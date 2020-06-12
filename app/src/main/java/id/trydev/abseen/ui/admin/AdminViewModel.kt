package id.trydev.abseen.ui.admin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import id.trydev.abseen.model.Absen
import id.trydev.abseen.utils.State

class AdminViewModel: ViewModel() {

    private val mFirestore = FirebaseFirestore.getInstance()

    private val absenState: MutableLiveData<State> by lazy {
        MutableLiveData<State>()
    }

    var error = ""
    val listAbsen = mutableListOf<Absen>()

    fun getData() {
        absenState.value = State.REQUEST_START
        mFirestore.collection("absen")
            .get()
            .addOnSuccessListener {
                listAbsen.clear()
                it.forEach {doc ->
                    listAbsen.add(doc.toObject(Absen::class.java))
                }
                absenState.value = State.REQUEST_SUCCESS
            }
            .addOnFailureListener {
                error = it.localizedMessage
                absenState.value = State.REQUEST_FAILED
            }
    }

    fun listenAbsenState(): MutableLiveData<State> = absenState

}