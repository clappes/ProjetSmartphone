package grpproject.projetgps;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class FirstActivity extends Activity {

    private ViewOne vo;
    private int nbTrame=1;

    protected void onCreate(Bundle saveInstance){
        super.onCreate(saveInstance);

        //View
        vo=new ViewOne(this);
        setContentView(vo);

    }

    public void start(){ vo.start();}

    public void stop(){ vo.stop(); }

    public void setMap(String s){
        vo.setMap("Trame"+nbTrame);
        nbTrame++;
    }
    public void setLog(String s){ vo.setLog(s); }
    public void setButton(String s){ vo.setButton(s); }
    public void etatButtonStart(boolean b){vo.etatButtonStart(b); }

}
