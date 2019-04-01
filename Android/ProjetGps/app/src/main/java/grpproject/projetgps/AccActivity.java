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
    private Sensor Sensor2;
    private Button quit;
    private Button home;
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
    public float frameTime = 0.666f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc);

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

        //Map
        SupportMapFragment mapFragment = (SupportMapFragment) AccActivity.this.getSupportFragmentManager()
                .findFragmentById(R.id.frag2_map);
        mapFragment.getMapAsync(this);

        trajet = new PolylineOptions().geodesic(true).color(Color.RED).width(8);
        //mMap.clear();

        quit = findViewById(R.id.frag2_quit);
        quit.setText("QUITTER");

        home = findViewById(R.id.frag2_home);
        home.setText("HOME");

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                senSensorManager.unregisterListener(AccActivity.this);
                finish();

            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLatitude(46.14986608208221);
                setLongitude(-1.1737993547569658);
                setMap(getLatitude(),getLongitude());

            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        bateau.rotation(event.values[1]);

        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.out.println("X : " + event.values[0] + " Y : " + event.values[1] + " Z : " + event.values[2]);

            double Xmin = 8.0;
            double part = 60.0/8.0;
            double Xmax = 0.0;
            double Z = 5.0;
            double Y = 0.0;
            double YMid = 4.0;
            double YMax = 9.0;
            double YMin = -9.0;

            if (Math.round(event.values[0]) < Xmin) {

                vit =  ((Xmin-Math.round(event.values[0]))*part);

                if(vit >= 60.0){
                    vit = 60.0;
                }
                else if(Math.round(event.values[2]) <= Z){
                    vit = 0.0;
                }

                setVit(vit);

            }

            if(vit > 0){
                if (Math.round(event.values[1]) > Y) {

                    double angle = ((YMax-Math.round(event.values[1]))*(90/9));
                    double var = Math.round(event.values[1])/YMax;

                    System.out.println(var);
                    System.out.println("A DROIIIITE");
                    if(Math.round(event.values[1]) > YMid){
                        setLongitude(getLongitude() + angle/2000);
                        setLatitude(getLatitude() - angle/2000);
                    }
                    else{
                        setLongitude(getLongitude() + (event.values[1]*event.values[0])/2000);
                        setLatitude(getLatitude() + (event.values[1]*event.values[0])/2000);
                    }


                } else if (Math.round(event.values[1]) < Math.round(Y)) {

                    double var = Math.round(event.values[1])/YMax;
                    System.out.println("A GAUCHHHEEE");
                    setLongitude(getLongitude() - (vit/2000000)*-event.values[1]);
                    setLatitude(getLatitude() + (vit/200000)*-event.values[1]);
                }

                else{
                    if(Math.round(event.values[2]) > Z) {
                        setLatitude(getLatitude() + (vit / 20000));
                    }
                    else{
                        setLatitude(getLatitude() - (vit / 20000));
                    }

                }
            }

            vitesse.setText("Vitesse : "+getVit()+"km/h");
            lat.setText("Latitude : " + getLatitude());
            lon.setText("Longitude : " + getLongitude());
            setMap(getLatitude(), getLongitude());
        }
        else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            double mOrientation = 0.0;
            mOrientation = event.values[0];
           // draw(mOrientation);
        }

    }

    public void draw(float angle) {
        // Take the relevant Marker from the marker list where available in map
      /*  AndroidMapGoogleOverlayItem myself = (AndroidMapGoogleOverlayItem) getOverlayItem(0);

        if (myself == null) {
            return;
        }
        myself.getMarker().setRotation(mOrientation);  // set the orientation value returned from the senserManager*/
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
            mMap.addPolyline(trajet);
            mMap.addMarker(bateau);
        }
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(AccActivity.this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        //Sensor2 = senSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

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

    public void setMap(double latit, double longi) {

        trajet.add(new LatLng(latit, longi));
        mMap.clear();
        mMap.addPolyline(trajet);
        bateau.position(new LatLng(latit, longi));
        mMap.addMarker(bateau);

        if(cpt%5==0) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(latit, longi))
                    .zoom(11).build()));
        }
        cpt++;

    }

}