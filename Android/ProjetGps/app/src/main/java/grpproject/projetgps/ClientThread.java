package grpproject.projetgps;

import android.support.annotation.UiThread;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

class ClientThread extends Thread {

    private volatile int PORT=55555;
    private volatile String IP="192.168.43.68";
    private volatile int REFRESH_TIME=1;
    private volatile FragmentOne fa;
    private volatile Socket socket;
    private volatile BufferedReader br;
    private volatile boolean vrai;
    private volatile String etat;


    ClientThread(FragmentOne fa){
        this.fa=fa;
        this.vrai=false;
        this.etat="DISCONNECTED";
    }


    @Override
    public void run() {

            while(true){

                //Test Start
                if(this.vrai) {

                    if(br==null || socket==null) {
                       if(connexion()) traitement();
                    }else {
                        traitement();
                    }
                }else {

                    //Test Stop
                    if (!this.vrai) {
                        br = null;
                        if (etat.equals("TRAITEMENT")) {
                            this.fa.setLog("Déconnecté...");
                            this.etat = "DISCONNECTED";
                        }
                    }
                }
            }
    }

    private void traitement(){

        try {
            if(br.ready()) {
                this.fa.setLog("Traitement...");
                this.etat="TRAITEMENT";
                String trame="";
                for(int i=0;i<18;i++) {
                    trame += br.readLine()+"\n";
                }
                analyseTrame(trame);
            }else{
               TimeUnit.SECONDS.sleep(REFRESH_TIME+1);
                Log.v("THREADTIME","SYNCHRO");
                if(!br.ready()) {
                    this.etat="CONNEXION_INTERRUPTED";
                    this.fa.setLog("Connexion interrompue...");
                    this.fa.stop();
                }
            }
        } catch (IOException e) {
            Log.v("ERRORREAD","READ");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void analyseTrame(String s){
        String lignes[]=s.split("\n");
        for(String ligne: lignes){
                if(ligne.startsWith("$GPRMC")) {
                    String[] datas=ligne.split(",");
                    if(datas[2].equals("A"))
                        this.fa.setMap(datas);
                }
        }
    }

    private boolean connexion(){
        try {
            this.fa.setLog("Connecting to host...");
            this.etat="SEARCH";
            this.fa.etatButtonStart(false);
            socket=new Socket(IP,PORT);
            br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.fa.setLog("Connecté...");
            this.etat="CONNECTED";
            this.fa.etatButtonStart(true);
            return true;
        } catch (IOException e) {
            Log.v("THREADTEST","NONCONNECTE");
            this.fa.setLog("Erreur de connexion...");
            this.etat="CONNEXION_ERROR";
            this.fa.etatButtonStart(true);
            this.fa.stop();
            return false;
        }
    }


    public void pause(){this.vrai=false; }
    public void play() {
        this.vrai = true;
    }
    public String getEtat(){return this.etat;}
    public void setPORT(int port){PORT=port;}
    public void setIP(String ip){IP=ip;}
    public void setRef(int time){REFRESH_TIME=time;}
    public String getIp(){ return IP; }
    public int getPort(){return PORT; }
    public int getRef(){return REFRESH_TIME; }
    public boolean getVrai(){ return vrai;}

}
