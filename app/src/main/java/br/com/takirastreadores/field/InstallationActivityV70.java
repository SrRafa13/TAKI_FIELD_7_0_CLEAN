package br.com.takirastreadores.field;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.view.*;
import android.widget.*;
import java.util.*;

public class InstallationActivityV70 extends InstallationActivityV61 {
    String registeredPlate="";
    String registeredService="";
    String registeredFinishedAt="";
    long registeredEpoch=0L;

    @Override public void onCreate(Bundle b){super.onCreate(b);replace70(list);}

    void replace70(View v){
        if(v instanceof TextView){
            TextView t=(TextView)v;
            String s=String.valueOf(t.getText());
            if(s.contains("TAKI FIELD • V6.1"))t.setText(s.replace("TAKI FIELD • V6.1","TAKI FIELD • V7.0.2"));
            if(s.contains("TAKI FIELD • V7.0.1"))t.setText(s.replace("TAKI FIELD • V7.0.1","TAKI FIELD • V7.0.2"));
            if(s.contains("TAKI FIELD • V7.0"))t.setText(s.replace("TAKI FIELD • V7.0","TAKI FIELD • V7.0.2"));
            if(s.contains("EVIDÊNCIAS FOTOGRÁFICAS"))t.setText("FOTOS DO ATENDIMENTO • OPCIONAL");
            if(s.contains("Registre o painel no início e o resultado final. Fotos extras entram no mesmo relatório."))t.setText("Adicione fotos somente quando forem úteis. Nenhuma foto é obrigatória para registrar o atendimento.");
        }
        if(v instanceof ViewGroup){ViewGroup g=(ViewGroup)v;for(int i=0;i<g.getChildCount();i++)replace70(g.getChildAt(i));}
    }

    @Override Button button(String s,boolean red,View.OnClickListener l){Button b=super.button(s,red,null);b.setOnClickListener(v->{feedback(false);if(l!=null)l.onClick(v);});return b;}
    @Override Button segment(String s,boolean selected,View.OnClickListener l){Button b=super.segment(s,selected,null);b.setOnClickListener(v->{feedback(false);if(l!=null)l.onClick(v);});return b;}
    void feedback(boolean important){try{if(appPrefs==null)appPrefs=getSharedPreferences("taki",MODE_PRIVATE);if(appPrefs.getBoolean("haptic_feedback_v70",true))getWindow().getDecorView().performHapticFeedback(important?HapticFeedbackConstants.LONG_PRESS:HapticFeedbackConstants.KEYBOARD_TAP);if(appPrefs.getBoolean("sound_feedback_v70",false)){AudioManager a=(AudioManager)getSystemService(AUDIO_SERVICE);if(a!=null)a.playSoundEffect(important?AudioManager.FX_KEYPRESS_RETURN:AudioManager.FX_KEY_CLICK,0.35f);}}catch(Exception ignored){}}

    @Override void renderActions(){
        actionsPanel.removeAllViews();
        TextView info=txt("REGISTRO LOCAL • independente do PDF",11,themeMuted(),true);
        info.setGravity(Gravity.CENTER);
        actionsPanel.addView(info,mb(8));
        actionsPanel.addView(button("REGISTRAR INSTALAÇÃO",true,v->registerCurrentManual()),new LinearLayout.LayoutParams(-1,dp(58)));
        Space sp=new Space(this);actionsPanel.addView(sp,new LinearLayout.LayoutParams(1,dp(9)));

        if(!batchMode){
            actionsPanel.addView(button("GERAR PDF • OPCIONAL",false,v->generateIndividual()),new LinearLayout.LayoutParams(-1,dp(54)));
            TextView h=txt("Primeiro registre. O PDF é somente o relatório do atendimento.",11,themeMuted(),false);h.setGravity(Gravity.CENTER);actionsPanel.addView(h,mtWrap(9));
            return;
        }

        actionsPanel.addView(button("FINALIZAR PLACA NO LOTE",false,v->finishCurrentPlate()),new LinearLayout.LayoutParams(-1,dp(54)));
        Space sp2=new Space(this);actionsPanel.addView(sp2,new LinearLayout.LayoutParams(1,dp(9)));
        actionsPanel.addView(button("FINALIZAR LOTE • GERAR PDF ÚNICO",false,v->finalizeBatchRequest()),new LinearLayout.LayoutParams(-1,dp(54)));
        TextView saved=txt("Registrar salva imediatamente no Histórico, Dashboard e Calendário. Finalizar placa também registra antes de avançar.",11,themeMuted(),false);saved.setGravity(Gravity.CENTER);actionsPanel.addView(saved,mtWrap(10));
    }

    boolean sameRegistered(ServiceRecord r){
        return r!=null && !registeredFinishedAt.isEmpty() && registeredPlate.equalsIgnoreCase(r.plate==null?"":r.plate) && registeredService.equals(r.service==null?"":r.service);
    }

    boolean ensureRegistered(ServiceRecord r){
        if(r==null||r.plate==null||r.plate.trim().isEmpty())return false;
        if(sameRegistered(r)){
            r.finishedAt=registeredFinishedAt;
        }else{
            registeredPlate=r.plate;
            registeredService=r.service==null?"":r.service;
            registeredFinishedAt=r.finishedAt;
            registeredEpoch=System.currentTimeMillis();
        }
        boolean ok=ServiceStore.addVerified(this,r.plate,r.service,r.brand,r.module,currentTech(),registeredFinishedAt,registeredEpoch,"","","individual",1);
        if(!ok){
            registeredPlate="";registeredService="";registeredFinishedAt="";registeredEpoch=0L;
            return false;
        }
        return ServiceStore.exists(this,r.plate,r.service,currentTech(),registeredFinishedAt);
    }

    void registerCurrentManual(){
        String p=cleanPlate();
        if(p.isEmpty()){plate.setError("Informe a placa");return;}
        ServiceRecord r=captureCurrent();
        boolean ok=ensureRegistered(r);
        if(!ok){
            new AlertDialog.Builder(this).setTitle("ERRO AO REGISTRAR").setMessage("O atendimento não foi confirmado no armazenamento local. Nada foi marcado como concluído. Tente novamente.").setPositiveButton("OK",null).show();
            return;
        }
        feedback(true);
        int total=ServiceStore.list(this).size();
        new AlertDialog.Builder(this)
            .setTitle("INSTALAÇÃO REGISTRADA ✓")
            .setMessage(r.plate+" • "+r.service+"\n"+registeredFinishedAt+"\n\nRegistro confirmado no aparelho.\nEle alimenta Histórico, Dashboard e Calendário.\n\nRegistros salvos: "+total)
            .setNegativeButton("CONTINUAR EDITANDO",null)
            .setPositiveButton("OK",null)
            .show();
    }

    void removeCompletedService(ServiceRecord r){
        if(r==null)return;
        ServiceStore.Entry e=new ServiceStore.Entry();
        e.plate=r.plate==null?"":r.plate;
        e.service=r.service==null?"":r.service;
        e.technician=currentTech();
        e.finishedAt=r.finishedAt==null?"":r.finishedAt;
        ServiceStore.remove(this,e);
    }

    @Override void generateIndividual(){
        String p=cleanPlate();if(p.isEmpty()){plate.setError("Informe a placa");return;}
        ServiceRecord r=captureCurrent();
        if(!ensureRegistered(r)){
            new AlertDialog.Builder(this).setTitle("REGISTRO NÃO SALVO").setMessage("O PDF não será tratado como conclusão enquanto o registro local não for confirmado. Toque em REGISTRAR INSTALAÇÃO e tente novamente.").setPositiveButton("OK",null).show();
            return;
        }
        feedback(true);
        generateSinglePdf(r);
    }

    @Override void finishCurrentPlate(){
        String p=cleanPlate();if(p.isEmpty()){plate.setError("Informe a placa");return;}
        feedback(true);
        commitCurrentPlate();
    }

    @Override void commitCurrentPlate(){
        ServiceRecord r=captureCurrent();
        if(!ensureRegistered(r)){
            new AlertDialog.Builder(this).setTitle("NÃO FOI POSSÍVEL REGISTRAR").setMessage("A placa não será adicionada ao lote porque o registro local falhou.").setPositiveButton("OK",null).show();
            return;
        }
        batchRecords.add(r);
        int keepService=serviceType.getSelectedItemPosition(),keepBrand=moduleBrand.getSelectedItemPosition();
        clearCurrent();
        serviceType.setSelection(keepService);
        moduleBrand.setSelection(keepBrand);
        saveDraft(false);
        updateBatchStatus();
        new AlertDialog.Builder(this)
            .setTitle("Placa registrada e adicionada ✓")
            .setMessage(r.plate+" já está salva no histórico e entrou no lote.\n\nO que deseja fazer agora?")
            .setNegativeButton("FINALIZAR LOTE",(d,w)->generateBatchPdf())
            .setPositiveButton("PRÓXIMA PLACA",(d,w)->{plate.requestFocus();Toast.makeText(this,"Próxima placa #"+(batchRecords.size()+1),Toast.LENGTH_SHORT).show();})
            .show();
    }

    @Override void finalizeBatchRequest(){
        if(!cleanPlate().isEmpty()){
            new AlertDialog.Builder(this)
                .setTitle("Placa atual ainda aberta")
                .setMessage("Deseja registrar e incluir "+cleanPlate()+" no lote antes de gerar o PDF?")
                .setNegativeButton("Não incluir",(d,w)->generateBatchPdf())
                .setPositiveButton("REGISTRAR, INCLUIR E GERAR",(d,w)->{
                    ServiceRecord r=captureCurrent();
                    if(!ensureRegistered(r)){Toast.makeText(this,"Falha ao registrar a placa. PDF do lote não gerado.",Toast.LENGTH_LONG).show();return;}
                    batchRecords.add(r);
                    clearCurrent();
                    saveDraft(false);
                    generateBatchPdf();
                }).show();
            return;
        }
        generateBatchPdf();
    }

    @Override void clearCurrent(){
        super.clearCurrent();
        registeredPlate="";
        registeredService="";
        registeredFinishedAt="";
        registeredEpoch=0L;
    }

    @Override void applyCurrent(ServiceRecord r){
        super.applyCurrent(r);
        registeredPlate="";
        registeredService="";
        registeredFinishedAt="";
        registeredEpoch=0L;
    }

    void undoLast(){
        if(batchRecords.isEmpty()){Toast.makeText(this,"Nenhuma placa para desfazer",Toast.LENGTH_SHORT).show();return;}
        ServiceRecord r=batchRecords.remove(batchRecords.size()-1);
        removeCompletedService(r);
        applyCurrent(r);
        saveDraft(false);
        updateBatchStatus();
        Toast.makeText(this,r.plate+" voltou para edição e saiu temporariamente do histórico",Toast.LENGTH_LONG).show();
    }

    @Override void updateBatchStatus(){if(batchStatus==null)return;int current=batchRecords.size()+1;batchStatus.setText(batchRecords.size()+" placas concluídas  •  atual #"+current);batchStatus.setTextColor(batchRecords.isEmpty()?themeMuted():theme().accent);}

    @Override void showBatchList(){
        if(batchRecords.isEmpty()){Toast.makeText(this,"O lote ainda não tem placas finalizadas",Toast.LENGTH_SHORT).show();return;}
        LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(dp(12),dp(6),dp(12),dp(6));
        ScrollView scroll=new ScrollView(this);scroll.addView(box);
        for(int i=0;i<batchRecords.size();i++){final int index=i;ServiceRecord r=batchRecords.get(i);LinearLayout line=new LinearLayout(this);line.setOrientation(LinearLayout.HORIZONTAL);line.setGravity(Gravity.CENTER_VERTICAL);line.setPadding(dp(10),dp(7),dp(10),dp(7));TextView n=txt(String.format(Locale.getDefault(),"%02d",i+1),12,theme().accent,true);line.addView(n,new LinearLayout.LayoutParams(dp(38),dp(44)));LinearLayout info=new LinearLayout(this);info.setOrientation(LinearLayout.VERTICAL);info.addView(txt(r.plate,14,themeText(),true));info.addView(txt(r.service+(r.module.isEmpty()?"":" • "+r.module),11,themeMuted(),false));line.addView(info,new LinearLayout.LayoutParams(0,-2,1));Button edit=button("EDITAR",false,v->editBatchRecord(index));line.addView(edit,new LinearLayout.LayoutParams(dp(82),dp(42)));box.addView(line);}
        new AlertDialog.Builder(this).setTitle("LOTE • "+batchRecords.size()+" placas").setView(scroll).setNegativeButton("Fechar",null).show();
    }

    void editBatchRecord(int index){
        if(index<0||index>=batchRecords.size())return;
        ServiceRecord r=batchRecords.remove(index);
        removeCompletedService(r);
        applyCurrent(r);
        saveDraft(false);
        updateBatchStatus();
        Toast.makeText(this,r.plate+" aberta para edição. Registre novamente ao concluir.",Toast.LENGTH_LONG).show();
    }
}
