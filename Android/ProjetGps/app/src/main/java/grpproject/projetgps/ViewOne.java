package grpproject.projetgps;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

public class ViewOne extends TableLayout {

    private ViewGen vg;
    private Context context;
    private ClientThread ct;
    private TextView map;
    private TextView log;
    private Button start;

    public ViewOne(Context context) {
        super(context);
        this.context=context;
        vg=new ViewGen(context);
        this.addView(vg);

        map=new TextView(context);
        map.setText("App GPS Project");
        this.addView(map);

        log=new TextView(context);
        log.setText("Déconnecté...");
        this.addView(log);

        start=new Button(context);
        start.setText("START");
        this.addView(start);

        //Thread
        ct=new ClientThread((FirstActivity)this.context);
        ct.start();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(start.getText().equals("START")){
                    start();
                }else{
                    stop();
                }
            }
        });
    }

    public void stop(){
        start.setText("START");
        ct.pause();
    }

    public void start(){
        start.setText("STOP");
        ct.play();
    }

    public Button getStart(){
        return this.start;
    }
    public TextView getMap(){ return this.map; }
    public TextView getLog(){return this.log; }
    public void setMap(String s){ map.setText(s); }
    public void setLog(String s){log.setText(s);}
    public void setButton(String s){ start.setText(s); }
    public void etatButtonStart(boolean b){start.setClickable(b);}

}
