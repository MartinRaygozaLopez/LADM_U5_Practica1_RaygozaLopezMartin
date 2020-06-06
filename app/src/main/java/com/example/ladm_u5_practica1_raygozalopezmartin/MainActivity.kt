package com.example.ladm_u5_practica1_raygozalopezmartin

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.type.LatLng
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var baseRemota = FirebaseFirestore.getInstance()
    var posicion = ArrayList<Data>()
    lateinit var locacion : LocationManager

    var p1 : Location = Location("puntoA")
    var p2 : Location = Location("puntoB")
    var p3 : Location = Location("puntoC")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        var arreglo : ArrayList<String> = ArrayList()

        baseRemota.collection("tecnologico")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException != null) {
                    Toast.makeText(this, "ERROR: " + firebaseFirestoreException.message, Toast.LENGTH_SHORT)
                    return@addSnapshotListener
                }

                arreglo.clear()
                posicion.clear()

                for(document in querySnapshot!!){
                    var data = Data()
                    data.nombre = document.getString("nombre").toString()
                    data.posicion1 = document.getGeoPoint("posicion1")!!
                    data.posicion2 = document.getGeoPoint("posicion2")!!
                    posicion.add(data)
                    arreglo.add(data.nombre)
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arreglo)
                spinner.adapter = adapter
            }

        locacion = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var oyente = Oyente(this)
        locacion.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 01f, oyente)

        btn_buscar.setOnClickListener {
            baseRemota.collection("tecnologico")
                .whereEqualTo("nombre", spinner.selectedItem.toString())
                .addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                    if(firebaseFirestoreException != null){
                        txt_busqueda.setText("ERROR, NO HAY CONEXION")
                        return@addSnapshotListener
                    }


                    var e = ""

                    for(document in  querySnapshot!!){
                        p1.longitude = document.getGeoPoint("posicion1")!!.longitude
                        p1.latitude = document.getGeoPoint("posicion1")!!.latitude

                        p3.longitude = document.getGeoPoint("posicion2")!!.longitude
                        p3.latitude = document.getGeoPoint("posicion2")!!.latitude
                        e = document.getString("extras")!!
                    }

                    var res = "COORDENADAS DEL EDIFICIO: \n          De (${(p1.latitude)}, ${p1.longitude})\n          a (${p3.latitude}, ${p3.longitude})"
                    res = res + "\n\n Extras: ${e}"

                    var dis = p2.distanceTo(p1)

                    res = res + "\n\nDistancia aproximada en m: " + dis + " m"

                    txt_busqueda.setText(res)
                }
        }

        btn_Mapa.setOnClickListener {
            var otraVentana = Intent(this, MapsActivity::class.java)
            otraVentana.putExtra("coordenadasLugarDeInteres", p3)
            otraVentana.putExtra("coordenadasEstas", p2)
            otraVentana.putExtra("nombre", spinner.selectedItem.toString())
            startActivity(otraVentana)
        }

    }
}

class Oyente(puntero : MainActivity) : LocationListener {
    var p = puntero

    override fun onLocationChanged(location: Location) {
        var geoPosicionGPS = GeoPoint(location.latitude, location.longitude)
        for(item in p.posicion) {
            if(item.estoyEn(geoPosicionGPS)) {
                p.txt_estas.setText("ESTAS EN:\n       -> " + item.nombre)
            }
        }
        p.txt_coor.setText("UBICACIÓN ACTUAL:\n        Latitud: ${location.latitude}\n        Longitud: ${location.longitude}")
        p.p2.latitude = location.latitude
        p.p2.longitude = location.longitude
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }
}
