package grpproject.projetgps;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentThree extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerDragListener {


    private View vue;
    private GoogleMap gmap;
    private ArrayList<Marker> markers;
    private int index;
    private boolean trace;

    public FragmentThree() {
        // Required empty public constructor
        markers=new ArrayList<Marker>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view= inflater.inflate(R.layout.fragment_three, container, false);
         vue=inflater.inflate(R.layout.marker,null);
         Button tracer=view.findViewById(R.id.frag3_tracer);
         Button clear=view.findViewById(R.id.frag3_clear);


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


        //Map
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frag3_map)).getMapAsync(this);

        return view;
    }

    private void clear() {
        gmap.clear();
        markers.clear();
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
    }


    @Override
    public void onMarkerDragStart(Marker marker) {
        index=markers.indexOf(marker);
        markers.remove(marker);
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        markers.add(index,marker);
        if(trace){
            gmap.clear();
            ArrayList<Marker> newMarkers=new ArrayList<Marker>();
            PolylineOptions po=new PolylineOptions();
            for(Marker l: markers){
                po.add(l.getPosition());
                Marker mark=gmap.addMarker(new MarkerOptions().position(l.getPosition()).draggable(true));
                newMarkers.add(mark);
            }
            gmap.addPolyline(po);
            markers=newMarkers;

        }
    }


    public void tracer(){
        PolylineOptions po=new PolylineOptions();
        ArrayList<Marker> newMarkers=new ArrayList<Marker>();
        for(Marker l: markers){
            po.add(l.getPosition());
        }
        gmap.addPolyline(po);
        trace=true;
    }


    public void retrace(){
        if(!markers.isEmpty()) {
            tracer();
            ArrayList<Marker> nmark = new ArrayList<Marker>();
            for (Marker l : markers) {

                Marker m = gmap.addMarker(new MarkerOptions().position(l.getPosition()).draggable(true));
                nmark.add(m);
            }
            markers = nmark;
        }
    }
}
