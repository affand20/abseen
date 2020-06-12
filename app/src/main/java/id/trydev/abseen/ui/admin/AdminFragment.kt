package id.trydev.abseen.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import id.trydev.abseen.R
import id.trydev.abseen.adapter.AbsenAdapter
import id.trydev.abseen.prefs.AppPreferences
import id.trydev.abseen.utils.State
import kotlinx.android.synthetic.main.admin_fragment.*

class AdminFragment : Fragment() {

    private val adapter: AbsenAdapter by lazy {
        AbsenAdapter(requireContext())
    }

    private lateinit var viewModel: AdminViewModel
    private val prefs: AppPreferences by lazy {
        AppPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(AdminViewModel::class.java)

        viewModel.getData()

        rv_absen.layoutManager = LinearLayoutManager(requireContext())
        rv_absen.adapter = adapter

        viewModel.listenAbsenState().observe(viewLifecycleOwner, Observer {state ->
            when(state) {
                State.REQUEST_START -> {
                    swipe_refresh.isRefreshing = true
                    rv_absen.visibility = View.GONE
                    state_empty.visibility = View.GONE
                }
                State.REQUEST_SUCCESS -> {
                    swipe_refresh.isRefreshing = false
                    if (viewModel.listAbsen.size > 0) {
                        adapter.setData(viewModel.listAbsen)
                        rv_absen.adapter = adapter
                        rv_absen.visibility = View.VISIBLE
                        state_empty.visibility = View.GONE
                    } else {
                        rv_absen.visibility = View.GONE
                        state_empty.visibility = View.VISIBLE
                    }
                }
                State.REQUEST_FAILED -> {
                    swipe_refresh.isRefreshing = false
                    Snackbar.make(body, viewModel.error, Snackbar.LENGTH_LONG).show()
                }
                else -> {}
            }
        })

        swipe_refresh.setOnRefreshListener {
            viewModel.getData()
        }

        qr_scan.setOnClickListener {
            findNavController().navigate(R.id.action_adminFragment_to_qrCodeFragment)
        }

        logout.setOnClickListener {
            prefs.resetPrefs()
            findNavController().navigate(R.id.action_adminFragment_to_loginFragment)
        }

    }

}