package br.com.takirastreadores.field;

import android.app.AlertDialog;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.URL;

public class MainActivityV702 extends MainActivityV70 {
    static final String VERSION_702="7.0.2";

    @Override void replace70(View v){
        super.replace70(v);
        replace702(v);
    }

    void replace702(View v){
        if(v instanceof TextView){
            TextView t=(TextView)v;
            String s=String.valueOf(t.getText());
            s=s.replace("7.0.1","7.0.2");
            t.setText(s);
        }
        if(v instanceof ViewGroup){
            ViewGroup g=(ViewGroup)v;
            for(int i=0;i<g.getChildCount();i++)replace702(g.getChildAt(i));
        }
    }

    @Override void checkUpdate(){
        Toast.makeText(this,"Consultando versão...",Toast.LENGTH_SHORT).show();
        new Thread(()->{
            try{
                URL u=new URL("https://raw.githubusercontent.com/SrRafa13/TAKI_FIELD_7_0_CLEAN/main/version.txt");
                BufferedReader br=new BufferedReader(new InputStreamReader(u.openStream()));
                String latest=br.readLine();
                br.close();
                runOnUiThread(()->new AlertDialog.Builder(this)
                    .setTitle("TAKI FIELD • Atualização")
                    .setMessage("Versão instalada: "+VERSION_702+"\nVersão disponível: "+latest+(VERSION_702.equals(latest)?"\n\nSeu app está atualizado.":"\n\nHá uma versão diferente disponível."))
                    .setNegativeButton("Fechar",null)
                    .setPositiveButton("ABRIR VERSÕES",(d,w)->openUrl("https://github.com/SrRafa13/TAKI_FIELD_7_0_CLEAN/actions"))
                    .show());
            }catch(Exception e){runOnUiThread(()->Toast.makeText(this,"Não foi possível consultar atualização",Toast.LENGTH_LONG).show());}
        }).start();
    }
}
