package id.trydev.abseen.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import id.trydev.abseen.R
import id.trydev.abseen.prefs.AppPreferences
import id.trydev.abseen.utils.State
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.content_main.*
import net.glxn.qrgen.android.QRCode
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    companion object {
        const val RC_LOCATION = 100
    }

    private lateinit var viewModel: MainViewModel

    private val fusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private val prefs: AppPreferences by lazy {
        AppPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        tv_greetings.text = String.format(resources.getString(R.string.greetings), prefs.name)

        logout.setOnClickListener {
            prefs.resetPrefs()
            findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
        }

        locationTask()

        val bottomSheet = BottomSheetBehavior.from(bottom_sheet)
        bottomSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                dialog_overlay.visibility = View.VISIBLE
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> dialog_overlay.visibility = View.VISIBLE
                    BottomSheetBehavior.STATE_COLLAPSED -> dialog_overlay.visibility = View.GONE
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        // do nothing
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        // do nothing
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        // do nothing
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        // do nothing
                    }
                }
            }

        })

        viewModel.getOfficeLocationState().observe(viewLifecycleOwner, Observer {
            when(it) {
                State.REQUEST_FAILED -> {
                    img_state.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.office_off))
                    swipe_refresh.isRefreshing = false
                    Snackbar.make(requireView(), viewModel.error, Snackbar.LENGTH_LONG).show()
                    qr_code.visibility = View.INVISIBLE
                    textView2.text = "Anda belum di kantor"
                    state_empty.visibility = View.VISIBLE
                    office_location.visibility = View.GONE
                }
                State.REQUEST_START -> swipe_refresh.isRefreshing = true
                State.REQUEST_SUCCESS -> {
                    swipe_refresh.isRefreshing = false
                    if (viewModel.officeLocation!=null) {
                        val qrInfo = listOf(
                            viewModel.officeLocation?.name,
                            viewModel.officeLocation?.location?.latitude.toString(),
                            viewModel.officeLocation?.location?.longitude.toString(),
                            prefs.name,
                            prefs.phone
                        ).joinToString("\n")

                        img_state.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.office_on))
                        val bitmap = QRCode.from(qrInfo).withSize(250,250).bitmap()
                        qr_code.setImageBitmap(bitmap)
                        office_location.text = "Anda berada di ${viewModel.officeLocation?.name}"
                        textView2.text = "Anda berada di ${viewModel.officeLocation?.name}"

                        qr_code.visibility = View.VISIBLE
                        state_empty.visibility = View.GONE
                        office_location.visibility = View.VISIBLE
                    } else {
                        // hide qr code
                        img_state.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.office_off))
                        qr_code.visibility = View.INVISIBLE
                        state_empty.visibility = View.VISIBLE
                        textView2.text = "Anda belum di kantor"
                        office_location.visibility = View.GONE
                    }
                }
                else -> {}
            }
        })

        swipe_refresh.setOnRefreshListener {
            locationTask()
        }

//        val bitmap = QRCode.from(prefs.phone).withSize(250,250).bitmap()
//        qr_code.setImageBitmap(bitmap)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.d("PERMISSION_GRANTED", "$perms")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(RC_LOCATION)
    private fun locationTask() {
        if (hasLocationPermission()) {
            // request location here
            swipe_refresh.isRefreshing = true

            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                swipe_refresh.isRefreshing = false
                if (it!=null) {
                    viewModel.compareLocation(it)
                }
            }.addOnFailureListener {
                swipe_refresh.isRefreshing = false
                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        } else {
            EasyPermissions.requestPermissions(this, "Permission", RC_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    private fun hasLocationPermission(): Boolean = EasyPermissions.hasPermissions(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)





}