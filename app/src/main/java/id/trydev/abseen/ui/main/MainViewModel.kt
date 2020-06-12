package id.trydev.abseen.ui.main

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.QueryDocumentSnapshot
import id.trydev.abseen.model.Office
import id.trydev.abseen.utils.State

class MainViewModel : ViewModel() {

    private val mFirestore = FirebaseFirestore.getInstance()

    private val locationState: MutableLiveData<State> by lazy {
        MutableLiveData<State>()
    }

    var error = ""
    var officeLocation: Office? = null

    fun compareLocation(location: Location) {
        locationState.value = State.REQUEST_START
        val currentPoint = GeoPoint(location.latitude, location.longitude)

        mFirestore.collection("office")
            .get()
            .addOnSuccessListener {snapshot ->

                val listOffice = HashMap<Float, Office>()
                snapshot.forEach {
                    val office = it.toObject(Office::class.java)
                    val officeLoc = Location(office.name)

                    office.location?.let { loc ->
                        officeLoc.longitude = loc.longitude
                        officeLoc.latitude = loc.latitude
                    }

                    if (location.distanceTo(officeLoc)<=100) {
                        listOffice[location.distanceTo(officeLoc)] = office
                    }

                    Log.d("COMPARE GEOPOINT", """
                        Compare distanceTo => ${location.distanceTo(officeLoc)} =====> pake yang ini
                    ========================================================
                    """.trimIndent())
                }

                if (listOffice.size>0) {
                    officeLocation = listOffice.minBy {
                        it.key
                    }?.value
                    locationState.value = State.REQUEST_SUCCESS
                } else {
                    error = "Anda belum di kantor"
                    locationState.value = State.REQUEST_FAILED
                }

                Log.d("LIST OFFICE", "${listOffice.minBy {
                    it.key
                }}")

            }
            .addOnFailureListener {
                error = it.localizedMessage
                locationState.value = State.REQUEST_FAILED
            }
    }

    fun getOfficeLocationState(): MutableLiveData<State> = locationState

}