package grpproject.projetgps;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Spinner;

import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */

public class FragmentOne extends Fragment implements OnMapReadyCallback{

    private TextView vitesse,lat,lon,log;
    private Button start;
    private ImageButton setting;
    private ClientThread ct;
    private GoogleMap gmap;
    private PolylineOptions trajet;
    private int nbrTrame;
    private MarkerOptions bateau;

    public FragmentOne() {
        // Required empty public constructor

        //Thread
        ct=new ClientThread(this);
        ct.start();
    }


    @Override

    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        nbrTrame=0;

        // Inflate the layout for this fragment
        final View view=  inflater.inflate(R.layout.fragment_one, container, false);
        Log.v("CREATEVIEW","NEW");

        //objet de la vue$
        vitesse = view.findViewById(R.id.frag1_vitesse);
        lon = view.findViewById(R.id.frag1_lon);
        lat = view.findViewById(R.id.frag1_lat);
        log = view.findViewById(R.id.frag1_log);
        log.setText("Déconnecté...");
        start = view.findViewById(R.id.frag1_start);
        setting = view.findViewById(R.id.frag1_setting);


        //Marker(drone)
        if(bateau==null) {
            bateau = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_boat_marker)).anchor(0.5f, 0.5f);
        }

        //Map
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frag1_map)).getMapAsync(this);

        //stop le thread
        stop();

        //boutton start/stop listener

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

        //boutton open setting
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Recupération valeur Thread
                View vvv=inflater.inflate(R.layout.setting_layout,null);
                EditText etip=vvv.findViewById(R.id.setting_ip);
                etip.setText(ct.getIp());
                EditText etport=vvv.findViewById(R.id.setting_port);
                etport.setText(""+ct.getPort());
                Spinner sp=vvv.findViewById(R.id.setting_spin);
                ArrayList a=new ArrayList();
                a.add(1); a.add(2); a.add(5); a.add(10);
                sp.setSelection(a.indexOf(ct.getRef()));

                //popup
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("SETTING");
                builder.setIcon(R.drawable.ic_action_setting);
                builder.setView(vvv);

                //boutton valider
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Objet
                        EditText etip=((AlertDialog) dialog).findViewById(R.id.setting_ip);
                        EditText etport=((AlertDialog) dialog).findViewById(R.id.setting_port);
                        Spinner spin=((AlertDialog) dialog).findViewById(R.id.setting_spin);
                        //Test format ip
                        if(etip.getText().toString().matches("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                                "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                                "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                                "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")  ){

                                //modification des paramètre
                                modifParam(etip.getText().toString(),etport.getText().toString(),
                                        spin.getSelectedItem().toString().split(" ")[0]);

                        }else if(!etip.getText().toString().equals("")) {

                            //erreur ip
                            AlertDialog.Builder erreur=new AlertDialog.Builder(getContext());
                            erreur.setTitle("Erreur Paramètre");
                            erreur.setMessage("Format IP invalid !");
                            erreur.setNegativeButton("close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    builder.show();
                                }
                            });
                            erreur.show();
                        }
                    }
                });

                //boutton cancel
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                //Afficher popup
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        return view;
    }

    public void stop(){
        this.setting.setClickable(true);

        start.setText("START");
        ct.pause();

    }

    public void start(){

        this.setting.setClickable(false);
        start.setText("STOP");
        trajet=new PolylineOptions().geodesic(true).color(Color.RED).width(8);
        gmap.clear();
        ct.play();
    }

    public Button getStart(){ return this.start; }
    public GoogleMap getMap(){ return this.gmap; }
    public TextView getLog(){return this.log; }
    public void setMap(final String[] datas){
        setVitesse(datas[7]);


       this.getActivity().runOnUiThread(new Runnable(){
            public void run(){
                double lat=Double.parseDouble(datas[3])/100;
                double lon=Double.parseDouble(datas[5])/100;

                //Conversion
                lat=((int)lat)+Double.parseDouble(datas[3].substring(datas[3].indexOf(".")-2))/60;
                lon=((int)lon)+Double.parseDouble(datas[5].substring(datas[5].indexOf(".")-2))/60;


                if(datas[4].equals("S")){
                    lat=-1*lat;
                }
                if(datas[6].equals("O")){
                    lon=-1*lon;
                }

                setLatitude(""+lat);
                setLongitude(""+lon);
                Log.v("CALCULCOOR",lat+" "+lon);

                trajet.add(new LatLng(lat,lon));
                gmap.clear();
                gmap.addPolyline(trajet);
                bateau.position(new LatLng(lat,lon));
                gmap.addMarker(bateau);

                if(nbrTrame%10==0) {
                    gmap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(lat, lon))
                            .zoom(17).build()));
                }
                nbrTrame++;
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        stop();
    }

    public void setVitesse(String s){vitesse.setText("Vitesse: "+s+" Km/h");}
    public void setLatitude(String s){lat.setText("Latitude: "+s);}
    public void setLongitude(String s){lon.setText("Longitude: "+s);}
    public void setLog(String s){log.setText(s);}
    public void setButton(String s){ start.setText(s); }
    public void etatButtonStart(boolean b){start.setClickable(b);}

    public void modifParam(String ip,String port,String time){
        ct.setIP(ip);
        if(!port.equals("")) {
            ct.setPORT(Integer.parseInt(port));
        }
        ct.setRef(Integer.parseInt(time));
    }
    public ClientThread getCt(){return ct;}


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if(trajet!=null){
            gmap.addPolyline(trajet);
            gmap.addMarker(bateau);
        }
    }
}
