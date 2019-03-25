package grpproject.projetgps;

import android.content.Context;
import android.widget.TableLayout;

public class ViewThree extends TableLayout {

    private ViewGen vg;

    public ViewThree(Context context) {
        super(context);
        vg=new ViewGen(context);
        this.addView(vg);
    }
}
