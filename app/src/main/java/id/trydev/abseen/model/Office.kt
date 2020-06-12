package id.trydev.abseen.model

import com.google.firebase.firestore.GeoPoint

data class Office (
    val name: String? = null,
    val location: GeoPoint? = null
)