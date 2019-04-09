package grpproject.projetgps;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;

public class AccActivity extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Button home;
    private Button arret;
    private TextView vitesse;
    private TextView lat;
    private TextView lon;
    private GoogleMap mMap;
    private MarkerOptions bateau;
    private PolylineOptions trajet;
    private double latitude;
    private double longitude;
    private double X;
    private double Y;
    private double Z;
    private double vit;
    private int cpt;
    private int cpt2;
    Polyline polyline;
    private double angle = 0;
    private static DecimalFormat df3 = new DecimalFormat(".###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc);

        cpt2 = 1;

        X = 0.0;
        Y = 0.0;
        Z = 0.0;
        vit = 0.0;

        setLatitude(46.14986608208221);
        setLongitude(-1.1737993547569658);

        vitesse = findViewById(R.id.vitesse);
        lon = findViewById(R.id.lon);
        lat = findViewById(R.id.lat);

        vitesse.setText("Vitesse : ");
        lon.setText("Longitude : ");
        lat.setText("Latitude : ");


        if (bateau == null) {
            bateau = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_boat_marker)).anchor(0.5f, 0.5f);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) AccActivity.this.getSupportFragmentManager()
                .findFragmentById(R.id.frag2_map);
        mapFragment.getMapAsync(this);

        trajet = new PolylineOptions().geodesic(true).color(Color.RED).width(8);

        home = findViewById(R.id.frag2_home);
        home.setText("HOME");

        arret = findViewById(R.id.frag2_arret);
        arret.setText("ARRET");

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLatitude(46.14986608208221);
                setLongitude(-1.1737993547569658);
                setMap(getLatitude(),getLongitude());
                polyline.remove();
                trajet = new PolylineOptions().geodesic(true).color(Color.RED).width(8);
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(getLatitude(), getLongitude()))
                        .zoom(15).build()));
            }
        });

        arret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cpt2%2==0) {
                    senSensorManager.registerListener(AccActivity.this, senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
                    arret.setText("ARRET");
                }
                else{
                    senSensorManager.unregisterListener(AccActivity.this);
                    arret.setText("RELANCER");
                }
                cpt2++;

            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.out.println("X : " + event.values[0] + " Y : " + event.values[1] + " Z : " + event.values[2]);

            if(event.values[2] >= 0 && event.values[0] > 0) {
                vit = (event.values[2]*(60))/10;
            } else if(event.values[0] < 0) {
                vit = 60;
            }
            if(event.values[1] < -2.5) {
                    angle = angle - event.values[1]/100;

                } else if(event.values[1] > 2.5) {
                    angle = angle - event.values[1]/100;
                }

                setLatitude(getLatitude()+( Math.sin(angle) * (vit / 0.5))/1000000);
                setLongitude(getLongitude() + (Math.cos(angle) * (vit / 0.5))/1000000);
        }

            vitesse.setText("Vitesse : " + df3.format(vit) + "km/h");
            lat.setText("Latitude : " + df3.format(getLatitude()));
            lon.setText("Longitude : " + df3.format(getLongitude()));

            setMap(getLatitude(), getLongitude());

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        bateau.position(new LatLng(getLatitude(), getLongitude()));
        bateau.flat(true);
        mMap.addMarker(bateau);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(getLatitude(), getLongitude()))
                .zoom(15).build()));
        if(trajet!=null){
            polyline = mMap.addPolyline(trajet);
            mMap.addMarker(bateau);
        }

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(AccActivity.this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void setMap(double latit, double longi) {

        trajet.add(new LatLng(latit, longi));
        mMap.clear();
        polyline = mMap.addPolyline(trajet);
        bateau.position(new LatLng(latit, longi));

        mMap.addMarker(bateau.flat(true));

        if(cpt%5==0) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(latit, longi))
                    .zoom(15).build()));
        }


        cpt++;

    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(AccActivity.this);
        finish();
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

}