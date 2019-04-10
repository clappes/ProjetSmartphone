package grpproject.projetgps;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentThree extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerDragListener {


    private View mPopup;
    private GoogleMap gmap;
    private ArrayList<Marker> markers;
    private int index;
    private boolean trace;

    public FragmentThree() {
        // Required empty public constructor
        markers=new ArrayList<Marker>();
        trace= false;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_three, container, false);
        mPopup=inflater.inflate(R.layout.marker,null);
        Button tracer=view.findViewById(R.id.frag3_tracer);
        Button clear=view.findViewById(R.id.frag3_clear);
        Button send = view.findViewById(R.id.frag3_send);


        //Listener bouton
        tracer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tracer();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                send();
            }
        });

        //Map
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frag3_map)).getMapAsync(this);

        return view;
    }

    //function clear
    private void clear() {
        gmap.clear();
        markers.clear();
        trace=false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        gmap.setOnMapClickListener(this );
        gmap.setOnMarkerClickListener(this);
        gmap.setOnInfoWindowClickListener(this);
        gmap.setOnMarkerDragListener(this);
        retrace();
    }

    @Override
    public void onMapClick(LatLng latLng) {

        //Init marker
        Marker marker=gmap.addMarker(new MarkerOptions().draggable(true).position(latLng));
        marker.setSnippet("Vitesse:1");
        marker.setTitle("Waypoint");
        marker.setTag(0);
        markers.add(marker);
    }



    @Override
    public boolean onMarkerClick(final Marker marker) {

        Toast.makeText(getContext(),"Marker cliqué",Toast.LENGTH_SHORT).show();

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {

        //Popup vitesse
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Modifié Vitesse du Waypoint");
        builder.setView(mPopup);
        TextView vit=mPopup.findViewById(R.id.frag3_vit);
        vit.setText(marker.getSnippet().split(":")[1]);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Objet
                EditText etvit = ((AlertDialog) dialog).findViewById(R.id.frag3_vit);
                String vitesse=etvit.getText().toString();
                marker.setSnippet("Vitesse:"+vitesse);
                marker.hideInfoWindow();
                ((ViewGroup) mPopup.getParent()).removeView(mPopup);
            }
        });

        AlertDialog alert= builder.create();

        alert.setCanceledOnTouchOutside(false);

        //Afficher popup
        alert.show();

    }


    @Override
    public void onMarkerDragStart(Marker marker) {

        //Drag marker
        index=markers.indexOf(marker);
        markers.remove(marker);
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        //Drop marker
        markers.add(index,marker);
        if(trace){
            gmap.clear();
            ArrayList<Marker> newMarkers=new ArrayList<Marker>();
            PolylineOptions po=new PolylineOptions();
            for(Marker l: markers){
                po.add(l.getPosition());
                po.color(Color.parseColor("#FF0000"));
                Marker mark=gmap.addMarker(new MarkerOptions().position(l.getPosition()).draggable(true));
                mark.setSnippet(l.getSnippet());
                mark.setTitle(l.getTitle());
                mark.setTag(0);
                newMarkers.add(mark);
            }
            gmap.addPolyline(po);
            markers=newMarkers;
        }
    }


    //Fonction tracer trajet
    public void tracer(){
        PolylineOptions po=new PolylineOptions();
        ArrayList<Marker> newMarkers=new ArrayList<Marker>();
        for(Marker l: markers){
            po.add(l.getPosition());
            po.color(Color.parseColor("#FF0000"));
        }
        gmap.addPolyline(po);
        trace=true;
    }


    //Fonction retrace au retour sur la vue
    public void retrace(){
        if(!markers.isEmpty()) {
            if(trace) tracer();
            ArrayList<Marker> nmark = new ArrayList<Marker>();
            for (Marker l : markers) {

                Marker m = gmap.addMarker(new MarkerOptions().position(l.getPosition()).draggable(true));
                m.setSnippet(l.getSnippet());
                m.setTitle(l.getTitle());
                m.setTag(0);
                nmark.add(m);
            }
            markers = nmark;
        }
    }

    public void send(){

        // Mise en place du fichier gpx
        double lat;
        double lon;
        int k = 0;
        String entete = "<gpx\n" +
                " version=\"1.0\"\n" +
                "creator=\"Projet Aqua Drone\"\n" +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "xmlns=\"http://www.topografix.com/GPX/1/0\"\n" +
                "xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0\n" +
                "http://www.topografix.com/GPX/1/0/gpx.xsd\">";
        String point = "";
        String trac ="<trk><name>GPX</name><number>1</number><trkseg>";
        String file;
        for (Marker m : markers){
            lat = m.getPosition().latitude;
            lon = m.getPosition().longitude;
            point = point +"\n<wpt lat= \""+lat+"\" lon=\""+lon+"\">\n\t<ele>0</ele>\n\t<nom>"+k+"</nom>\n</wpt>";
            trac = trac + "\n<trkpt lat=\""+lat+"\" lon=\""+lon+"\"><ele>0</ele><time>2007-10-14T10:09:57Z</time></trkpt>";
            k++;
        }
        file = entete+point+trac+"</trkseg></trk>"+"</gpx>";

        //Creation du fichier gpx
        File fic = null;
        fic = new File(Environment.getExternalStorageDirectory().getPath() + "/Download/gps.gpx  ");
        try {
            fic.createNewFile();
            OutputStream output = new FileOutputStream(fic);
            output.write(file.getBytes());
            if(output != null)
                output.close();
            Toast.makeText(getContext(),"GPX Téléchargé ",Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Log.v("GPX",entete);
        Log.v("File",entete+point+trac+"</trkseg></trk>"+"</gpx>");


    }

}