package id.trydev.abseen.ui.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import id.trydev.abseen.model.Employee
import id.trydev.abseen.utils.State

class LoginViewModel : ViewModel() {

    private val mFirestore = FirebaseFirestore.getInstance()

    val loginState: MutableLiveData<State> by lazy {
        MutableLiveData<State>()
    }

    var employee: Employee? = null

    var error = ""

    fun registerUser(name: String, phone: String) {
        loginState.value = State.REQUEST_START
        mFirestore.collection("employee")
            .whereEqualTo("phone", phone)
            .get()
            .addOnSuccessListener {
                Log.d("SIZE QUERY", it.size().toString())
                if (it.size() > 0) {
                    employee = it.first { snapshot ->
                        snapshot.get("phone") == phone
                    }.toObject(Employee::class.java)
                    Log.d("FIND USER", "$employee")
                    loginState.value = State.REQUEST_SUCCESS
                } else {
                    Log.d("CREATE USER", "MASUK")
                    val emp = Employee(name, phone, 0)
                    mFirestore.collection("employee")
                        .document()
                        .set(emp)
                        .addOnSuccessListener {
                            employee = emp
                            loginState.value = State.REQUEST_SUCCESS
                        }
                        .addOnFailureListener {e ->
                            error = e.localizedMessage
                            loginState.value = State.REQUEST_FAILED
                        }
                }
            }
            .addOnFailureListener {e ->
                loginState.value = State.REQUEST_FAILED
                error = e.localizedMessage
            }
//        mFirestore.collection("employee")
//            .document()
//            .set(employee)
//            .addOnSuccessListener {
//                loginState.value = State.REQUEST_SUCCESS
//            }
//            .addOnFailureListener {
//                loginState.value = State.REQUEST_FAILED
//                error = it.localizedMessage
////            }
    }
}