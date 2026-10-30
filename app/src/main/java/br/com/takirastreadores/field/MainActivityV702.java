package br.com.takirastreadores.field;

import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivityV702 extends MainActivityV70 {
    static final String VERSION_702="7.0.3";

    @Override void replace70(View v){
        super.replace70(v);
        replace702(v);
    }

    void replace702(View v){
        if(v instanceof TextView){
            TextView t=(TextView)v;
            String s=String.valueOf(t.getText());
            s=s.replace("7.0.1","7.0.3").replace("7.0.2","7.0.3");
            t.setText(s);
        }
        if(v instanceof ViewGroup){
            ViewGroup g=(ViewGroup)v;
            for(int i=0;i<g.getChildCount();i++)replace702(g.getChildAt(i));
        }
    }

    // Corrige o bug em que a UI mostrava "Todos", mas os filtros esperavam "Todos os técnicos".
    @Override String[] dashboardTechFilters(ArrayList<ServiceStore.Entry> entries){
        LinkedHashSet<String> names=new LinkedHashSet<>();
        names.add("Todos os técnicos");
        names.add("Eduardo");
        names.add("Rafael");
        for(ServiceStore.Entry e:entries){
            if(e.technician!=null&&!e.technician.trim().isEmpty())names.add(e.technician.trim());
        }
        return names.toArray(new String[0]);
    }

    @Override void dashboardScreen(){
        screenHeader("DASHBOARD","Calendário sincronizado com a data do aparelho, filtros e pesquisa por placa.","DASHBOARD");
        ArrayList<ServiceStore.Entry> entries=ServiceStore.list(this);
        Calendar cursor=dashboardCursor();

        LinearLayout period=card();
        period.addView(label("MÊS DO CALENDÁRIO"),mb(7));
        LinearLayout monthRow=row();
        Button previous=button("‹",false,null);
        TextView monthTitle=txt(monthTitle(cursor),16,themeText(),true);
        monthTitle.setGravity(Gravity.CENTER);
        Button next=button("›",false,null);
        monthRow.addView(previous,new LinearLayout.LayoutParams(dp(54),dp(48)));
        gap(monthRow);
        monthRow.addView(monthTitle,new LinearLayout.LayoutParams(0,dp(48),1));
        gap(monthRow);
        monthRow.addView(next,new LinearLayout.LayoutParams(dp(54),dp(48)));
        period.addView(monthRow,mb(8));

        Calendar deviceNow=Calendar.getInstance();
        TextView deviceDate=txt("HOJE • "+new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(deviceNow.getTime())+" • data do aparelho",11,themeMuted(),false);
        deviceDate.setGravity(Gravity.CENTER);
        period.addView(deviceDate,mb(8));
        Button today=button("VOLTAR PARA O MÊS ATUAL",false,null);
        period.addView(today,new LinearLayout.LayoutParams(-1,dp(44)));
        list.addView(period,mb(11));

        LinearLayout filters=card();
        filters.addView(label("PESQUISAR POR PLACA"),mb(5));
        EditText plateSearch=input("Ex.: ABC1D23");
        plateSearch.setAllCaps(true);
        filters.addView(plateSearch,mb(9));
        filters.addView(label("TIPO DE SERVIÇO"));
        Spinner service=spinner(new String[]{"Todos","Instalação","Troca","Retirada","Manutenção"});
        filters.addView(service,mb(8));
        filters.addView(label("TÉCNICO"));
        Spinner tech=spinner(dashboardTechFilters(entries));
        filters.addView(tech);
        list.addView(filters,mb(11));

        LinearLayout holder=new LinearLayout(this);
        holder.setOrientation(LinearLayout.VERTICAL);
        list.addView(holder);

        final Runnable[] render=new Runnable[1];
        render[0]=()->{
            monthTitle.setText(monthTitle(cursor));
            renderDashboard702(holder,entries,cursor,service.getSelectedItem().toString(),tech.getSelectedItem().toString(),plateSearch.getText().toString());
        };

        previous.setOnClickListener(v->{cursor.add(Calendar.MONTH,-1);render[0].run();});
        next.setOnClickListener(v->{cursor.add(Calendar.MONTH,1);render[0].run();});
        today.setOnClickListener(v->{
            Calendar now=Calendar.getInstance();
            cursor.setTimeInMillis(now.getTimeInMillis());
            cursor.set(Calendar.DAY_OF_MONTH,1);
            cursor.set(Calendar.HOUR_OF_DAY,12);
            cursor.set(Calendar.MINUTE,0);
            cursor.set(Calendar.SECOND,0);
            cursor.set(Calendar.MILLISECOND,0);
            render[0].run();
        });

        plateSearch.addTextChangedListener(new TextWatcher(){
            public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            public void onTextChanged(CharSequence s,int a,int b,int c){render[0].run();}
            public void afterTextChanged(Editable e){}
        });
        AdapterView.OnItemSelectedListener listener=new AdapterView.OnItemSelectedListener(){
            @Override public void onNothingSelected(AdapterView<?> parent){}
            @Override public void onItemSelected(AdapterView<?> parent,View view,int position,long id){render[0].run();}
        };
        service.setOnItemSelectedListener(listener);
        tech.setOnItemSelectedListener(listener);
        render[0].run();
    }

    void renderDashboard702(LinearLayout holder,ArrayList<ServiceStore.Entry> all,Calendar cursor,String service,String tech,String plateQuery){
        String needle=plateQuery==null?"":plateQuery.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]","");
        if(needle.isEmpty()){
            renderDashboard611(holder,all,cursor,service,tech);
            return;
        }
        ArrayList<ServiceStore.Entry> byPlate=new ArrayList<>();
        for(ServiceStore.Entry e:all){
            String plate=e.plate==null?"":e.plate.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]","");
            if(plate.contains(needle))byPlate.add(e);
        }
        renderDashboard611(holder,byPlate,cursor,service,tech);
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
