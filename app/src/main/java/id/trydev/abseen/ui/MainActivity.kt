package id.trydev.abseen.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import id.trydev.abseen.R
import kotlinx.android.synthetic.main.bottom_sheet.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        findNavController(R.id.nav_host_fragment).currentDestination
//        finish()
    }

}