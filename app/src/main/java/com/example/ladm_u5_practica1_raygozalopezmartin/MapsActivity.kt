package com.example.ladm_u5_practica1_raygozalopezmartin

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var nombre = ""
    lateinit var estasEn : Location
    lateinit var lugar : Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        var extras = intent.extras
        nombre = extras!!.getString("nombre").toString()
        lugar = extras!!.get("coordenadasLugarDeInteres") as Location

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val lug = LatLng(lugar.latitude, lugar.longitude)
        mMap.addMarker(MarkerOptions().position(lug).title(nombre))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lug))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lug, 15f))

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.isMyLocationEnabled = true
    }
}
