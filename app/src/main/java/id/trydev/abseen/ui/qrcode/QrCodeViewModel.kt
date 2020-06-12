package id.trydev.abseen.ui.qrcode

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import id.trydev.abseen.model.Absen
import id.trydev.abseen.utils.State

class QrCodeViewModel : ViewModel() {

    private val mFirestore = FirebaseFirestore.getInstance()

    private val absenState: MutableLiveData<State> by lazy {
        MutableLiveData<State>()
    }

    var error = ""

    fun listenAbsenState(): MutableLiveData<State> = absenState

    fun insertAbsen(absen: Absen) {
        absenState.value = State.REQUEST_START
        mFirestore.collection("absen")
            .document()
            .set(absen)
            .addOnSuccessListener {
                absenState.value = State.REQUEST_SUCCESS
            }
            .addOnFailureListener {
                absenState.value = State.REQUEST_FAILED
                error = it.localizedMessage
            }
    }

}