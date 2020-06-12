package id.trydev.abseen.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class LocationUpdateService: Service() {

    private val PACKAGE_NAME = "id.trydev.abseen.service"
    private val TAG = LocationUpdateService::class.java.simpleName




    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}