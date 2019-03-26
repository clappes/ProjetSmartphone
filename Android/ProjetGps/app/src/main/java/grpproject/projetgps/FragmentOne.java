package grpproject.projetgps;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentOne extends Fragment {

    private TextView vitesse,lat,lon;
    private TextView log;
    private Button start;
    private ClientThread ct;

    public FragmentOne() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_one, container, false);


        vitesse=view.findViewById(R.id.frag1_vitesse);

        lon=view.findViewById(R.id.frag1_lon);

        lat=view.findViewById(R.id.frag1_lat);

        log=view.findViewById(R.id.frag1_log);
        log.setText("Déconnecté...");

        start=view.findViewById(R.id.frag1_start);
        start.setText("START");

        //Thread
        ct=new ClientThread(this);
        ct.start();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(start.getText().equals("START")){
                    start();
                }else{
                    stop();
                }
            }
        });

        return view;
    }
    public void stop(){
        start.setText("START");
        ct.pause();

    }

    public void start(){
        start.setText("STOP");
        ct.play();
    }

    public Button getStart(){
        return this.start;
    }
    //public TextView getMap(){ return this.map; }
    public TextView getLog(){return this.log; }
    //public void setMap(String s){ map.setText(s); }
    public void setVitesse(String s){vitesse.setText("Vitesse: "+s+" Km/h");}
    public void setLatitude(String s){lat.setText("Latitude: "+s);}
    public void setLongitude(String s){lon.setText("Longitude: "+s);}
    public void setLog(String s){log.setText(s);}
    public void setButton(String s){ start.setText(s); }
    public void etatButtonStart(boolean b){start.setClickable(b);}


}
