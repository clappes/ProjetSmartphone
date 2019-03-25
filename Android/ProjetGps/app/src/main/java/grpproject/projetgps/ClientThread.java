package grpproject.projetgps;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

class ClientThread extends Thread {

    private volatile FirstActivity fa;
    private volatile Socket socket;
    private volatile BufferedReader br;
    private volatile boolean vrai;
    private volatile String etat;


    ClientThread(FirstActivity fa){
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
                this.fa.setMap(trame);
                Log.v("THREADTRAME",trame);
            }else{
                TimeUnit.SECONDS.sleep(1);
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

    private boolean connexion(){
        try {
            this.fa.setLog("Search...");
            this.etat="SEARCH";
            this.fa.etatButtonStart(false);
            socket=new Socket("192.168.43.68",55555);
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

}
