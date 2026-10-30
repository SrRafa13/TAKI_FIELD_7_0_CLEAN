package br.com.takirastreadores.field;

import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class InstallationActivityV703 extends InstallationActivityV70 {
    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        replace703(list);
    }

    void replace703(View v){
        if(v instanceof TextView){
            TextView t=(TextView)v;
            String s=String.valueOf(t.getText());
            t.setText(s.replace("V7.0.2","V7.0.3").replace("V7.0.1","V7.0.3"));
        }
        if(v instanceof ViewGroup){
            ViewGroup g=(ViewGroup)v;
            for(int i=0;i<g.getChildCount();i++)replace703(g.getChildAt(i));
        }
    }
}
