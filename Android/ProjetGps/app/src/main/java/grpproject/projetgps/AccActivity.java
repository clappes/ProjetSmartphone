package grpproject.projetgps;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AccActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Button quit;
    private TextView vitesse,lat,lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc);

        vitesse=findViewById(R.id.vitesse);
        lon=findViewById(R.id.lon);
        lat=findViewById(R.id.lat);

        vitesse.setText("Vitesse : X");
        lon.setText("Longitude : Y");
        lat.setText("Latitude : Y");

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(AccActivity.this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

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
        System.out.println("X : "+event.values[0]+" Y : "+event.values[1]+" Z : "+event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
