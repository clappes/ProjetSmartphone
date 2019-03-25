package grpproject.projetgps;

import android.app.Activity;
import android.os.Bundle;

public class ThirdActivity extends Activity {

    private ViewThree vth;

    protected void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);
        vth = new ViewThree(this);
        setContentView(vth);

    }
}