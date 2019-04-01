package grpproject.projetgps;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class AccActivity extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Button quit;
    private TextView vitesse, lat, lon;
    private GoogleMap mMap;
    private MarkerOptions bateau;
    private PolylineOptions trajet;
    private double latitude;
    private double longitude;
    private double X;
    private double Y;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc);

        X = 0.0;
        Y = 0.0;

        setLatitude(46.14986608208221);
        setLongitude(-1.1737993547569658);

        vitesse = findViewById(R.id.vitesse);
        lon = findViewById(R.id.lon);
        lat = findViewById(R.id.lat);

        vitesse.setText("Vitesse : X");
        lon.setText("Longitude : Y");
        lat.setText("Latitude : Y");

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(AccActivity.this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        if (bateau == null) {
            bateau = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_boat_marker)).anchor(0.5f, 0.5f);
        }

        //Map
        SupportMapFragment mapFragment = (SupportMapFragment) AccActivity.this.getSupportFragmentManager()
                .findFragmentById(R.id.frag2_map);
        mapFragment.getMapAsync(this);

        trajet = new PolylineOptions().geodesic(true).color(Color.RED).width(8);
        //mMap.clear();

        quit = findViewById(R.id.frag2_quit);
        quit.setText("QUITTER");

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                senSensorManager.unregisterListener(AccActivity.this);
                finish();

            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        System.out.println("X : " + event.values[0] + " Y : " + event.values[1] + " Z : " + event.values[2]);

        if (Math.round(event.values[1]) > Math.round(Y)) {
            System.out.println("A DROIIIITE");
            setLatitude(getLatitude() + 0.0005);
        } else if (Math.round(event.values[1]) < Math.round(Y)) {
            System.out.println("A GAUCHHHEEE");
            setLatitude(getLatitude() - 0.0005);
        }
        X = event.values[0];
        Y = event.values[1];

        setMap(getLatitude(),getLongitude());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        bateau.position(new LatLng(getLatitude(), getLongitude()));
        mMap.addMarker(bateau);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(getLatitude(), getLongitude()))
                .zoom(15).build()));
        if(trajet!=null){
            mMap.addPolyline(trajet);
            mMap.addMarker(bateau);
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public void setMap(double latit, double longi) {

        trajet.add(new LatLng(latit, longi));
        mMap.clear();
        mMap.addPolyline(trajet);
        bateau.position(new LatLng(latit, longi));
        mMap.addMarker(bateau);

       // mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(latit, longi))
                //.zoom(17).build()));
    }
}