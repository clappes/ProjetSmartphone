package grpproject.projetgps;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    private ViewGen vg;

    protected void onCreate(Bundle saveInstance){
        super.onCreate(saveInstance);
        final ActionBar actionBar = getActionBar();
        vg=new ViewGen(this);
        TextView tw=new TextView(this);
        tw.setText("Activity Home");
        vg.addView(tw);
        setContentView(vg);
    }

}
