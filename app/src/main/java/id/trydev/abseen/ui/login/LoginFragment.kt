package id.trydev.abseen.ui.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import id.trydev.abseen.R
import id.trydev.abseen.prefs.AppPreferences
import id.trydev.abseen.utils.State
import kotlinx.android.synthetic.main.login_fragment.*

class LoginFragment : Fragment() {

    private val prefs: AppPreferences by lazy {
        AppPreferences(requireContext())
    }

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        Log.d("PREFS", "${prefs.name}, ${prefs.phone}")

        if (prefs.name==null) {
            imageView.animate().translationY(0f).setStartDelay(1000)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        textInputLayout.visibility = View.VISIBLE
                        textInputLayout2.visibility = View.VISIBLE
                        btn_login.visibility = View.VISIBLE
                        textInputLayout.animate().translationY(0f)
                            .alpha(1f)
                        textInputLayout2.animate().translationY(0f)
                            .alpha(1f)
                        btn_login.animate().translationY(0f)
                            .alpha(1f)

                    }
                })
        } else {
            Handler().postDelayed({
                if (prefs.role==0) {
                    findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                } else {
                    findNavController().navigate(R.id.action_loginFragment_to_adminFragment)
                }
//                findNavController().navigate(R.id.action_loginFragment_to_adminFragment)
            }, 1500)
        }

        viewModel.loginState.observe(viewLifecycleOwner, Observer {
            when (it) {
                State.REQUEST_START -> {
                    btn_login.visibility = View.INVISIBLE
                    progress_bar.visibility = View.VISIBLE
                }
                State.REQUEST_SUCCESS -> {
                    progress_bar.visibility = View.GONE
                    btn_login.visibility = View.VISIBLE
//                    prefs.name = edt_fullname.text.toString()
//                    prefs.phone = edt_phone_number.text.toString()
                    prefs.name = viewModel.employee?.name
                    prefs.phone = viewModel.employee?.phone
                    prefs.role = viewModel.employee?.role ?:
                    Log.d("PREFS", "${prefs.name}, ${prefs.phone}, ${prefs.role}")
                    if (prefs.role == 0) {
                        findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                    } else {
                        findNavController().navigate(R.id.action_loginFragment_to_adminFragment)
                    }
                }
                State.REQUEST_FAILED -> {
                    progress_bar.visibility = View.GONE
                    btn_login.visibility = View.VISIBLE
                    Snackbar.make(requireView(), viewModel.error, Snackbar.LENGTH_LONG).show()
                }
                else -> {}
            }
        })

        btn_login.setOnClickListener {
            if (validate()) {
                viewModel.registerUser(edt_fullname.text.toString(), edt_phone_number.text.toString())
            }
        }
    }

    private fun validate(): Boolean {
        if (edt_fullname.text.toString().isEmpty()) {
            edt_fullname.requestFocus()
            edt_fullname.error = "Wajib diisi!"
            return false
        }
        if (edt_phone_number.text.toString().isEmpty()) {
            edt_phone_number.requestFocus()
            edt_phone_number.error = "Wajib diisi!"
            return false
        }
        return true
    }


}