package grpproject.projetgps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.Toolbar;

public class ViewGen extends TableLayout {

    private  Toolbar toolbar;
    private ImageButton b1,b2,b3,b4;
    private Activity context;


    public ViewGen(Context context) {
        super(context);
        this.context=(Activity) context;
        toolbar=new Toolbar(context);


        b1=new ImageButton(context);
        b1.setImageResource(R.drawable.ic_action_home);
        b1.setBackgroundColor(Color.parseColor("#FAFAFA"));

        b2=new ImageButton(context);
        b2.setImageResource(R.drawable.ic_action_drawmap);
        b2.setBackgroundColor(Color.parseColor("#FAFAFA"));

        b3=new ImageButton(context);
        b3.setImageResource(R.drawable.ic_action_editmap);
        b3.setBackgroundColor(Color.parseColor("#FAFAFA"));

        b4=new ImageButton(context);
        b4.setImageResource(R.drawable.ic_action_control);
        b4.setBackgroundColor(Color.parseColor("#FAFAFA"));

        Space sp=new Space(context);
        Space sp1=new Space(context);
        Space sp2=new Space(context);
        Space sp3=new Space(context);
        Space sp4=new Space(context);
        sp.setMinimumWidth(50);
        sp2.setMinimumWidth(200);
        sp3.setMinimumWidth(200);
        sp1.setMinimumWidth(200);
        sp4.setMinimumWidth(100);

        toolbar.addView(sp);
        toolbar.addView(b1);
        toolbar.addView(sp1);
        toolbar.addView(b2);
        toolbar.addView(sp2);
        toolbar.addView(b3);
        toolbar.addView(sp3);
        toolbar.addView(b4);
        toolbar.addView(sp4);
        this.addView(toolbar);

        b1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent= new Intent(ViewGen.this.context,MainActivity.class);
                ViewGen.this.context.startActivity(mIntent);
                ViewGen.this.context.finish();
            }
        });

        b2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent= new Intent(ViewGen.this.context,FirstActivity.class);
                ViewGen.this.context.startActivity(mIntent);
                ViewGen.this.context.finish();
            }
        });

        b3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent= new Intent(ViewGen.this.context,SecondActivity.class);
                ViewGen.this.context.startActivity(mIntent);
            }
        });

        b4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent= new Intent(ViewGen.this.context,ThirdActivity.class);
                ViewGen.this.context.startActivity(mIntent);
                ViewGen.this.context.finish();
            }
        });

    }


}
