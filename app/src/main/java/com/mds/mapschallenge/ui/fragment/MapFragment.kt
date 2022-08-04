package com.mds.mapschallenge.ui.fragment

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.mds.mapschallenge.R
import com.mds.mapschallenge.util.App
import com.mds.mapschallenge.viewmodel.MapViewModel
import com.mds.mapschallenge.viewmodel.MapViewModel.Companion.KEY_CAMERA_POSITION
import com.mds.mapschallenge.viewmodel.MapViewModel.Companion.KEY_LOCATION
import com.mds.mapschallenge.viewmodel.MapViewModel.Companion.lastKnownLocation
import com.mds.mapschallenge.viewmodel.MapViewModel.Companion.locationPermissionGranted
import kotlinx.android.synthetic.main.map_fragment.*

class MapFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = MapFragment()
    }

    private var flagButton : Boolean = false

    private var viewModel : MapViewModel = MapViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                App.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )}

    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                App.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )}else{
            if (viewModel.init(requireContext(), this.arguments)) {
                initUI(this.requireView())
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.map?.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            App.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                    if(locationPermissionGranted)
                        viewModel.init(this.requireContext(),this.arguments)
                        initUI(this.requireView())
                        viewModel.updateLocationUI()
                }
            }
        }
    }


    private fun initUI(view: View){

        val mapFragment : SupportMapFragment = childFragmentManager?.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val alertDialog: AlertDialog.Builder = alert()

        fabTrackingRecord.setOnClickListener(View.OnClickListener {
            var text = ""
            if (!flagButton){
                if(locationPermissionGranted) viewModel.startLocationUpdates()
                text = getString(R.string.start_location)
                flagButton = true
                viewModel.map?.addMarker(MarkerOptions().position(LatLng(lastKnownLocation!!.latitude,
                        lastKnownLocation!!.longitude))
                        .title("Inicio").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
            }
            else{
                viewModel.stopLocationUpdates()
                viewModel.counter(false)
                text = getString(R.string.end_location)
                viewModel.map?.addMarker(MarkerOptions().position(LatLng(lastKnownLocation!!.latitude,
                        lastKnownLocation!!.longitude))
                        .title("Fin").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                alertDialog.show()
                flagButton = false
            }
            Snackbar.make(view,text,Snackbar.LENGTH_LONG).show()
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        viewModel = MapViewModel(requireContext(),requireActivity(),googleMap)
        viewModel.updateLocationUI()
        viewModel.getDeviceLocation()
        viewModel.addPolyline(googleMap)
    }


    override fun onDestroy() {
        super.onDestroy()
        if (viewModel.stopLocationUpdates() != null){
          viewModel.stopLocationUpdates()
        }
    }

    private fun alert(): AlertDialog.Builder {

        var alertDialog = AlertDialog.Builder(requireContext())

        alertDialog.setMessage("Escribe el nombre de tu ruta a guardar")
        var input : EditText = EditText(context)
        var etName = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT)
        input.layoutParams = etName
        if (input.parent != null) {
            (input.parent as ViewGroup).removeView(input)
        }
        input.setText("")
        alertDialog.setView(input).setCancelable(false)
        alertDialog.setPositiveButton(R.string.save_button,
            DialogInterface.OnClickListener { dialog, id ->
                (input.parent as ViewGroup).removeView(input)
                viewModel.saveTrack(input.text.toString().trim()).let {
                    Snackbar.make(requireView(),"Guardado",Snackbar.LENGTH_LONG).show()
                }
                input.text.clear()
            })
            .setNegativeButton(R.string.cancel_button,
                DialogInterface.OnClickListener { dialog, id ->
                    viewModel.clearInfo()
                    (input.parent as ViewGroup).removeView(input)
                    input.text.clear()
                })
        alertDialog.create()

        return alertDialog
    }


}
