package id.trydev.abseen.ui.qrcode

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.zxing.Result
import id.trydev.abseen.R
import id.trydev.abseen.model.Absen
import id.trydev.abseen.utils.State
import kotlinx.android.synthetic.main.qr_code_fragment.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.text.SimpleDateFormat
import java.util.*

class QrCodeFragment : Fragment(), ZXingScannerView.ResultHandler {

    private lateinit var viewModel: QrCodeViewModel
    private lateinit var mScanner: ZXingScannerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.qr_code_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(QrCodeViewModel::class.java)

        initScannerView()

        viewModel.listenAbsenState().observe(viewLifecycleOwner, Observer {state ->
            when(state) {
                State.REQUEST_START -> {
                    state_text.text = "Mengirim data..."
                    progress_bar.visibility = View.VISIBLE
                }
                State.REQUEST_SUCCESS -> {
                    state_text.text = "Absen telah direkam!"
                    progress_bar.visibility = View.GONE
                    findNavController().popBackStack()
                }
                State.REQUEST_FAILED -> {
                    state_text.text = "Gagal mengirim data, coba lagi."
                    progress_bar.visibility = View.GONE
                    mScanner.resumeCameraPreview(this)

                }
                else -> {}
            }
        })
    }

    private fun initScannerView() {
        mScanner = ZXingScannerView(requireContext())
        mScanner.setAutoFocus(true)
        mScanner.setResultHandler(this)
        qr_layout.addView(mScanner)
        mScanner.startCamera()
    }

    override fun onStart() {
        doRequestPermission()
        super.onStart()
    }

    private fun doRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(requireContext(),Manifest.permission.CAMERA) != PermissionChecker.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            100 -> {
                initScannerView()
            }
            else -> {
                /* nothing to do in here */
            }
        }
    }

    override fun onPause() {
        mScanner.stopCamera()
        super.onPause()
    }

    override fun handleResult(rawResult: Result?) {
//        text_view_qr_code_value.text = rawResult?.text
//        button_reset.visibility = View.VISIBLE
        Log.d("HANDLE RESULT", rawResult?.text)
        val absenBuilder = rawResult?.text?.split("\n")

        absenBuilder?.let {
            val absen = Absen(
                absenBuilder[0],
                absenBuilder[3],
                SimpleDateFormat("dd/MM/yyyy").format(Date()),
                SimpleDateFormat("HH:mm").format(Date())
            )
            Log.d("ABSEN", "$absen")
            viewModel.insertAbsen(absen)
        }
    }

}