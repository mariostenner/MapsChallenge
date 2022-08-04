package com.mds.mapschallenge.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.mds.mapschallenge.R
import com.mds.mapschallenge.model.Routes
import com.mds.mapschallenge.viewmodel.DetailsViewModel
import com.mds.mapschallenge.viewmodel.MapViewModel
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var viewModel : DetailsViewModel = DetailsViewModel()
    companion object{
        var trackingRoute : String = ""
        var id : Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        initUI()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        viewModel = DetailsViewModel()
        viewModel.uploadTrackPoints(trackingRoute,googleMap)
    }


    fun initUI(){

        var intent = getIntent()

         id      = intent.extras!!.getInt("Id").toInt()
        var name = intent.extras!!.getString("RouteName")
        var km   = intent.extras!!.getString("KmTraveled")
        var time = intent.extras!!.getString("TimeTraveled")
        var route       = intent.extras!!.getString("TrackPoints")//"20.6239849,-103.4730227|20.623971,-103.4730509|20.6239868,-103.4730218|20.6239868,-103.4730218"//intent.extras!!.getString("RouteTrack")

        tvNameRoute.text    = name
        tvKmTraveled.text   = km.toString()
        tvTimeTraveled.text = time

        trackingRoute = route.toString()

        var senderText : String = "$km, $time"

        btnShare.setOnClickListener(View.OnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT,senderText )
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        })

        btnDelete.setOnClickListener(View.OnClickListener {
            viewModel.deleteRoute(Routes(id,"",0.0,"",""),application).let {
                if(it){
                    finish()
                }
            }
        })

        val mapFragment : SupportMapFragment = supportFragmentManager?.findFragmentById(R.id.mapDetails) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }
}
