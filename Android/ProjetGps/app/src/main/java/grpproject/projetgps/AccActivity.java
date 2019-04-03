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
    private int angle;


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

            double Xmin = 8.0;
            double part = 60.0 / 8.0;
            double Xmax = 0.0;
            double Z = 5.0;
            double Y = 0.0;
            double YMid = 4.0;
            double YMax = 9.0;
            double YMin = -9.0;


           if (Math.round(event.values[0]) < Xmin) {

                vit = ((Xmin - Math.round(event.values[0])) * part);

                if (vit >= 60.0) {
                    vit = 60.0;
                } else if (Math.round(event.values[2]) <= Z) {
                    vit = 0.0;
                }

                setVit(vit);
                vitesse.setText("Vitesse : " + getVit() + "km/h");

            }

            float seuil = 3;
            if ( event.values[0] < -seuil ||  event.values[0] > seuil || event.values[1] < -seuil || event.values[1] > seuil) {

                if(event.values[0] < Xmin){
                    setLatitude(getLatitude() + (event.values[0] / 100000));
                }
                else{
                    setLatitude(getLatitude() - (event.values[0] / 100000));
                }

                setLongitude(getLongitude() + (event.values[1] / 100000));

            }
        }

            lat.setText("Latitude : " + getLatitude());
            lon.setText("Longitude : " + getLongitude());
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

    /*@Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(AccActivity.this, senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }*/

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

    public TextView getVitesse() {
        return vitesse;
    }

    public double getVit() {
        return vit;
    }

    public TextView getLat() {
        return lat;
    }

    public TextView getLon() {
        return lon;
    }
    public void setVit(double vit) {
        this.vit = vit;
    }
    public void setVitesse(TextView vitesse) {
        this.vitesse = vitesse;
    }

    public MarkerOptions getBateau() {
        return bateau;
    }

    public void setBateau(MarkerOptions bateau) {
        this.bateau = bateau;
    }


}