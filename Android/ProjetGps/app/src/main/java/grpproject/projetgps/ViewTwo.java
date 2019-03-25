package grpproject.projetgps;

import android.content.Context;
import android.widget.TableLayout;

public class ViewTwo extends TableLayout {

    private ViewGen vg;

    public ViewTwo(Context context) {
        super(context);
        vg=new ViewGen(context);
        this.addView(vg);
    }
}
