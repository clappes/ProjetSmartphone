package grpproject.projetgps;


import android.content.Intent;
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

    private Button quit;

    public FragmentTwo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_two, container, false);

        Intent intent = new Intent(getActivity(), AccActivity.class);
        startActivity(intent);


        quit = view.findViewById(R.id.frag2_quit);
        quit.setText("QUITTER");

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AccActivity)getContext()).finish();
                System.out.println("fini");
            }
        });

        return view;

    }

    public Button getQuit() {
        return quit;
    }

    public void setQuit(Button quit) {
        this.quit = quit;
    }


}
