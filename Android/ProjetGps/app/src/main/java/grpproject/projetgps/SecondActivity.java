package grpproject.projetgps;

import android.app.Activity;
import android.os.Bundle;

public class SecondActivity extends Activity {

    private ViewTwo vt;

    protected void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);
        vt = new ViewTwo(this);
        setContentView(vt);

    }
}

