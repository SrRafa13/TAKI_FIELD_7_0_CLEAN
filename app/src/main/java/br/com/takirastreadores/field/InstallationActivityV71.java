package br.com.takirastreadores.field;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.*;
import android.graphics.pdf.PdfDocument;
import android.view.*;
import android.widget.*;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;
import org.json.*;
import java.util.*;

public class InstallationActivityV71 extends InstallationActivityV703 {
    static final String SINGLE_DRAFT="individual_draft_v71";

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        Spinner replacement=spinner(new String[]{"Não informar","Suntech","X3Tech","Queclink","Quanta","Jimi IoT JC450","Outro"});
        String selected=moduleBrand.getSelectedItem()==null?"":moduleBrand.getSelectedItem().toString();
        moduleBrand.setAdapter(replacement.getAdapter());selectSpinner(moduleBrand,selected);
        addPasteRow(plate,false);
        addPasteRow(moduleNumber,true);
        LinearLayout panel=card();
        panel.addView(txt("ROTINA DE CAMPO • V7.1",15,themeText(),true),mb(5));
        panel.addView(txt("Checklist de testes e materiais salvos por placa. Essas informações entram no PDF quando preenchidas.",12,themeMuted(),false),mb(8));
        panel.addView(button("CHECKLIST DE INSTALAÇÃO",false,v->field("install")),new LinearLayout.LayoutParams(-1,dp(48)));
        panel.addView(button("MATERIAIS UTILIZADOS",false,v->field("materials")),mtWrap(8));
        list.addView(panel,list.indexOfChild(actionsPanel),mb(12));
        replace71(list);
        list.post(this::offerSingleDraft);
    }

    void replace71(View v){
        if(v instanceof TextView){
            TextView t=(TextView)v;String s=String.valueOf(t.getText());
            if(s.contains("V7.0.3"))t.setText(s.replace("V7.0.3","V7.1.0"));
        }
        if(v instanceof ViewGroup){
            ViewGroup g=(ViewGroup)v;for(int i=0;i<g.getChildCount();i++)replace71(g.getChildAt(i));
        }
    }
    void field(String screen){
        String p=cleanPlate();
        if(p.isEmpty()){plate.setError("Informe a placa primeiro");return;}
        Intent i=new Intent(this,FieldKitActivity.class);i.putExtra("screen",screen);i.putExtra("plate",p);
        if("Jimi IoT JC450".equals(moduleBrand.getSelectedItem().toString()))FieldKitStore.flag(this,"jc450_"+FieldKitStore.key(p),true);
        startActivity(i);
    }
    void addPasteRow(EditText e,boolean scan){
        if(e==null||!(e.getParent() instanceof LinearLayout))return;
        LinearLayout parent=(LinearLayout)e.getParent();
        int idx=parent.indexOfChild(e);ViewGroup.LayoutParams old=e.getLayoutParams();parent.removeView(e);
        LinearLayout r=row();r.addView(e,new LinearLayout.LayoutParams(0,dp(52),1));gap(r);
        r.addView(button("COLAR",false,v->paste(e)),new LinearLayout.LayoutParams(dp(73),dp(52)));
        if(scan){gap(r);r.addView(button("LER",false,v->scan(e)),new LinearLayout.LayoutParams(dp(58),dp(52)));}
        parent.addView(r,idx,old);
    }
    void paste(EditText field){
        try{
            android.content.ClipboardManager cm=(android.content.ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
            if(cm==null||!cm.hasPrimaryClip()){Toast.makeText(this,"Prancheta vazia",Toast.LENGTH_SHORT).show();return;}
            android.content.ClipData clip=cm.getPrimaryClip();
            if(clip==null||clip.getItemCount()==0)return;
            CharSequence val=clip.getItemAt(0).coerceToText(this);
            if(val==null||val.toString().trim().isEmpty())return;
            field.setText(val.toString().trim());field.setSelection(field.length());
        }catch(Exception e){Toast.makeText(this,"Não foi possível colar",Toast.LENGTH_SHORT).show();}
    }
    void scan(EditText field){
        try{
            GmsBarcodeScanning.getClient(this).startScan().addOnSuccessListener(code->{
                String v=code.getRawValue();
                if(v==null||v.trim().isEmpty())return;
                new AlertDialog.Builder(this).setTitle("Etiqueta identificada")
                    .setMessage(v+"\n\nConfirme se o número é o ESN/IMEI correto.")
                    .setNegativeButton("Cancelar",null)
                    .setPositiveButton("USAR NÚMERO",(d,w)->{field.setText(v.trim());field.setSelection(field.length());}).show();
            }).addOnFailureListener(e->Toast.makeText(this,"Leitura indisponível",Toast.LENGTH_LONG).show());
        }catch(Exception e){Toast.makeText(this,"Leitor indisponível neste aparelho",Toast.LENGTH_LONG).show();}
    }

    @Override protected void onPause(){
        if(!batchMode)saveSingleDraft();
        super.onPause();
    }
    void saveSingleDraft(){
        try{
            if(plate==null)return;
            String p=cleanPlate(),m=moduleNumber.getText().toString(),n=notes.getText().toString();
            if(p.isEmpty()&&m.trim().isEmpty()&&n.trim().isEmpty())return;
            JSONObject o=new JSONObject();o.put("plate",p);o.put("module",m);o.put("notes",n);
            o.put("service",serviceType.getSelectedItem().toString());o.put("brand",moduleBrand.getSelectedItem().toString());
            FieldKitStore.p(this).edit().putString(SINGLE_DRAFT,o.toString()).apply();
        }catch(Exception ignored){}
    }
    void offerSingleDraft(){
        if(batchMode||draftFile().exists())return;
        String s=FieldKitStore.p(this).getString(SINGLE_DRAFT,"");
        if(s.isEmpty())return;
        try{
            JSONObject o=new JSONObject(s);String p=o.optString("plate","");
            if(p.isEmpty())return;
            new AlertDialog.Builder(this).setTitle("Retomar atendimento?")
                .setMessage("Existe um rascunho individual salvo para "+p+".")
                .setNegativeButton("DESCARTAR",(d,w)->FieldKitStore.p(this).edit().remove(SINGLE_DRAFT).apply())
                .setPositiveButton("RETOMAR",(d,w)->{
                    plate.setText(o.optString("plate",""));
                    moduleNumber.setText(o.optString("module",""));
                    notes.setText(o.optString("notes",""));
                    selectSpinner(serviceType,o.optString("service","Instalação"));
                    selectSpinner(moduleBrand,o.optString("brand","Não informar"));
                }).show();
        }catch(Exception ignored){}
    }

    @Override int writeRecord(PdfDocument doc,ServiceRecord r,int pageNo,boolean batch){
        int next=super.writeRecord(doc,r,pageNo,batch);
        if(!FieldKitStore.hasReportData(this,r.plate))return next;
        PdfDocument.Page page=newPage(doc,next++);
        Canvas c=page.getCanvas();int y=drawHeader(c,r.plate,48,"TESTES E MATERIAIS");
        Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);p.setColor(Color.DKGRAY);p.setTextSize(12);
        Paint h=new Paint(Paint.ANTI_ALIAS_FLAG);h.setColor(Color.BLACK);h.setTextSize(15);h.setTypeface(Typeface.DEFAULT_BOLD);
        c.drawText("CHECKLIST DE INSTALAÇÃO",48,y+20,h);y+=44;
        Set<String> checked=FieldKitStore.checked(this,FieldKitStore.installKey(r.plate));
        ArrayList<String> tests=new ArrayList<>(Arrays.asList(FieldKitStore.INSTALL));
        if(FieldKitStore.flag(this,"jc450_"+FieldKitStore.key(r.plate)))tests.addAll(Arrays.asList(FieldKitStore.JC450));
        for(String item:tests){
            if(y>760){doc.finishPage(page);page=newPage(doc,next++);c=page.getCanvas();y=drawHeader(c,r.plate,48,"TESTES E MATERIAIS")+18;}
            y=drawWrapped(c,(checked.contains(item)?"[OK] ":"[PENDENTE] ")+item,48,y,490,p)+4;
        }
        y+=12;
        if(y>735){doc.finishPage(page);page=newPage(doc,next++);c=page.getCanvas();y=drawHeader(c,r.plate,48,"MATERIAIS")+24;}
        c.drawText("MATERIAIS UTILIZADOS",48,y,h);y+=25;
        LinkedHashMap<String,Integer> materials=FieldKitStore.materials(this,r.plate);
        if(materials.isEmpty()){c.drawText("Nenhum material informado.",48,y,p);y+=20;}
        for(Map.Entry<String,Integer> e:materials.entrySet()){
            if(y>760){doc.finishPage(page);page=newPage(doc,next++);c=page.getCanvas();y=drawHeader(c,r.plate,48,"MATERIAIS")+18;}
            y=drawWrapped(c,e.getKey()+" • "+e.getValue()+" un.",48,y,490,p)+4;
        }
        String notes=FieldKitStore.materialNotes(this,r.plate);
        if(!notes.trim().isEmpty()){
            if(y>735){doc.finishPage(page);page=newPage(doc,next++);c=page.getCanvas();y=drawHeader(c,r.plate,48,"OBSERVAÇÕES")+18;}
            y=drawWrapped(c,"Observações: "+notes,48,y+10,490,p);
        }
        doc.finishPage(page);
        return next;
    }
}
