package grpproject.projetgps;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTwo extends Fragment {

    private Button lancer;

    public FragmentTwo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_two, container, false);


        lancer = view.findViewById(R.id.frag2_simu);
        lancer.setTextColor(Color.parseColor("white"));
        lancer.setText("Cliquez ici pour lancer la simulation");

        lancer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), AccActivity.class);
                startActivity(intent);

            }
        });

        return view;

    }

    public Button getLancer() {
        return lancer;
    }

    public void setLancer(Button lancer) {
        this.lancer = lancer;
    }


}
