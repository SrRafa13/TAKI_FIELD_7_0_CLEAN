package br.com.takirastreadores.field;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.text.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class FieldKitActivity extends Activity {
    static final int BG=Color.rgb(9,10,13), CARD=Color.rgb(24,25,30), TEXT=Color.rgb(246,246,248),
        MUTED=Color.rgb(165,168,175), RED=Color.rgb(231,39,53);
    LinearLayout list;
    String screen="travel",plate="";
    android.content.SharedPreferences prefs;

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        prefs=getSharedPreferences("taki",MODE_PRIVATE);
        screen=getIntent().getStringExtra("screen");
        if(screen==null)screen="travel";
        plate=FieldKitStore.key(getIntent().getStringExtra("plate"));
        if(plate.isEmpty())plate=FieldKitStore.key(FieldKitStore.p(this).getString("last_plate",""));
        show();
    }
    int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
    GradientDrawable bg(int c){GradientDrawable g=new GradientDrawable();g.setColor(c);g.setCornerRadius(dp(13));g.setStroke(dp(1),Color.rgb(56,57,65));return g;}
    TextView text(String s,int size,boolean bold){
        TextView t=new TextView(this);t.setText(s);t.setTextColor(TEXT);t.setTextSize(size);
        if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;
    }
    LinearLayout column(){
        LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);return l;
    }
    LinearLayout row(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.HORIZONTAL);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
    LinearLayout card(){
        LinearLayout l=column();l.setPadding(dp(15),dp(15),dp(15),dp(15));l.setBackground(bg(CARD));return l;
    }
    void addCard(LinearLayout c){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=dp(12);list.addView(c,p);}
    Button btn(String s,View.OnClickListener f){
        Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setTextColor(TEXT);b.setTextSize(12);
        b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);b.setBackground(bg(Color.rgb(44,45,53)));b.setOnClickListener(f);b.setMinHeight(dp(46));return b;
    }
    EditText input(String hint){
        EditText e=new EditText(this);e.setTextColor(TEXT);e.setHintTextColor(MUTED);e.setTextSize(15);e.setHint(hint);
        e.setSingleLine(true);e.setBackground(bg(Color.rgb(34,35,41)));e.setPadding(dp(12),dp(8),dp(12),dp(8));return e;
    }
    void show(){
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);
        list=column();list.setPadding(dp(14),dp(18),dp(14),dp(30));list.setBackgroundColor(BG);
        scroll.addView(list);setContentView(scroll);
        LinearLayout header=row();Button back=btn("‹ VOLTAR",v->finish());header.addView(back,new LinearLayout.LayoutParams(dp(105),dp(45)));
        TextView brand=text("TAKI FIELD • 7.1",16,true);brand.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);header.addView(brand,new LinearLayout.LayoutParams(0,dp(45),1));list.addView(header);
        TextView title=text("ROTINA DE CAMPO",23,true);title.setPadding(0,dp(18),0,dp(12));list.addView(title);
        LinearLayout nav=row();
        String[] labels={"VIAGEM","INSTALAÇÃO","MATERIAIS"};
        String[] values={"travel","install","materials"};
        for(int i=0;i<3;i++){final String value=values[i];Button b=btn(labels[i],v->{screen=value;show();});if(screen.equals(value))b.setBackground(bg(RED));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(48),1);if(i>0)p.leftMargin=dp(5);nav.addView(b,p);}
        list.addView(nav,new LinearLayout.LayoutParams(-1,dp(48)));
        TextView hint=text("Tudo fica salvo no aparelho, mesmo sem internet.",12,false);hint.setTextColor(MUTED);hint.setPadding(0,dp(11),0,dp(13));list.addView(hint);
        if("travel".equals(screen))travel();else if("install".equals(screen))installation();else materials();
    }
    String tech(){return prefs.getString("technician","Técnico");}
    void travel(){
        String key=FieldKitStore.travelKey(tech());
        LinearLayout c=card();c.addView(text("CHECKLIST DE SAÍDA • "+tech(),16,true));
        TextView status=text("",13,false);status.setTextColor(MUTED);c.addView(status);
        addCard(c);
        LinearLayout opt=card();opt.addView(text("ITENS CONDICIONAIS",14,true));
        CheckBox keyboard=check("Levar teclado de jornada neste serviço",FieldKitStore.flag(this,"need_keyboard"));
        CheckBox ibutton=check("Levar iButton neste serviço",FieldKitStore.flag(this,"need_ibutton"));
        keyboard.setOnCheckedChangeListener((b,on)->{FieldKitStore.flag(this,"need_keyboard",on);show();});
        ibutton.setOnCheckedChangeListener((b,on)->{FieldKitStore.flag(this,"need_ibutton",on);show();});
        opt.addView(keyboard);opt.addView(ibutton);addCard(opt);
        ArrayList<String> items=new ArrayList<>(FieldKitStore.list(FieldKitStore.TRAVEL,FieldKitStore.extra(this,"travel")));
        if(FieldKitStore.flag(this,"need_keyboard"))items.add("Teclado de jornada");
        if(FieldKitStore.flag(this,"need_ibutton"))items.add("iButton");
        LinearLayout checklist=card();checklist.addView(text("CONFERIR ANTES DE SAIR",15,true));
        for(String item:items){
            CheckBox ch=check(item,FieldKitStore.checked(this,key).contains(item));
            ch.setOnCheckedChangeListener((b,on)->{FieldKitStore.setChecked(this,key,item,on);progress(status,key,items);});
            checklist.addView(ch);
        }
        addCard(checklist);progress(status,key,items);
        LinearLayout custom=card();custom.addView(text("ACRESCENTAR ITEM",14,true));
        EditText e=input("Ex.: Alicate de crimpar");custom.addView(e);
        custom.addView(btn("ADICIONAR AO CHECKLIST",v->{FieldKitStore.addExtra(this,"travel",e.getText().toString());show();}));
        addCard(custom);
        list.addView(btn("INICIAR NOVA VIAGEM • ZERAR MARCAÇÕES",v->new AlertDialog.Builder(this)
            .setTitle("Começar outra viagem?").setMessage("As marcações da viagem atual serão zeradas. Os itens personalizados permanecem.")
            .setNegativeButton("Cancelar",null).setPositiveButton("ZERAR",(d,w)->{FieldKitStore.reset(this,key);show();}).show()));
    }
    void progress(TextView status,String key,List<String> items){
        Set<String> done=FieldKitStore.checked(this,key);int n=0;for(String s:items)if(done.contains(s))n++;
        status.setText(n+" de "+items.size()+" itens conferidos"+(n==items.size()?" • PRONTO PARA SAIR ✓":""));
    }
    CheckBox check(String s,boolean selected){
        CheckBox b=new CheckBox(this);b.setText(s);b.setTextSize(14);b.setTextColor(TEXT);b.setButtonTintList(android.content.res.ColorStateList.valueOf(RED));b.setChecked(selected);
        b.setPadding(dp(2),dp(5),0,dp(5));return b;
    }
    void plateSelector(){
        LinearLayout c=card();c.addView(text("PLACA DO VEÍCULO",14,true));
        EditText p=input("ABC1D23");p.setText(plate);p.setAllCaps(true);c.addView(p);
        c.addView(btn("ABRIR PLACA",v->{
            String x=FieldKitStore.key(p.getText().toString());
            if(x.isEmpty()){p.setError("Informe a placa");return;}
            plate=x;FieldKitStore.p(this).edit().putString("last_plate",plate).apply();show();
        }));addCard(c);
    }
    void installation(){
        plateSelector();if(plate.isEmpty()){list.addView(text("Informe a placa para salvar os testes individualmente.",13,false));return;}
        String key=FieldKitStore.installKey(plate);
        LinearLayout c=card();c.addView(text("TESTES • "+plate,17,true));
        TextView status=text("",13,false);status.setTextColor(MUTED);c.addView(status);
        CheckBox jc=check("Este veículo usa dashcam JC450",FieldKitStore.flag(this,"jc450_"+plate));
        jc.setOnCheckedChangeListener((b,on)->{FieldKitStore.flag(this,"jc450_"+plate,on);show();});
        c.addView(jc);addCard(c);
        ArrayList<String> items=new ArrayList<>(Arrays.asList(FieldKitStore.INSTALL));
        if(FieldKitStore.flag(this,"jc450_"+plate))items.addAll(Arrays.asList(FieldKitStore.JC450));
        LinearLayout tests=card();
        for(String item:items){
            CheckBox b=check(item,FieldKitStore.checked(this,key).contains(item));
            b.setOnCheckedChangeListener((button,on)->{FieldKitStore.setChecked(this,key,item,on);progress(status,key,items);});
            tests.addView(b);
        }
        addCard(tests);progress(status,key,items);
        LinearLayout info=card();info.addView(text("Os testes ficam associados à placa e podem aparecer no PDF do atendimento.",12,false));
        info.addView(btn("ZERAR TESTES DESTA PLACA",v->new AlertDialog.Builder(this).setTitle("Zerar testes de "+plate+"?")
            .setNegativeButton("Cancelar",null).setPositiveButton("ZERAR",(d,w)->{FieldKitStore.reset(this,key);show();}).show()));
        addCard(info);
    }
    void materials(){
        plateSelector();if(plate.isEmpty()){list.addView(text("Informe a placa para registrar o consumo por veículo.",13,false));return;}
        LinearLayout c=card();c.addView(text("MATERIAIS • "+plate,17,true));
        c.addView(text("Quantidades utilizadas neste atendimento. Não desconta estoque automaticamente.",12,false));addCard(c);
        ArrayList<String> items=new ArrayList<>(FieldKitStore.list(FieldKitStore.MATERIALS,FieldKitStore.extra(this,"materials")));
        LinearLayout box=card();
        for(String item:items){
            LinearLayout r=row();
            TextView name=text(item,13,false);r.addView(name,new LinearLayout.LayoutParams(0,dp(48),1));
            TextView n=text(String.valueOf(FieldKitStore.materials(this,plate).getOrDefault(item,0)),15,true);n.setGravity(Gravity.CENTER);
            Button minus=btn("−",v->{int next=Math.max(0,FieldKitStore.materials(this,plate).getOrDefault(item,0)-1);FieldKitStore.quantity(this,plate,item,next);n.setText(""+next);});
            Button plus=btn("+",v->{int next=FieldKitStore.materials(this,plate).getOrDefault(item,0)+1;FieldKitStore.quantity(this,plate,item,next);n.setText(""+next);});
            r.addView(minus,new LinearLayout.LayoutParams(dp(44),dp(44)));r.addView(n,new LinearLayout.LayoutParams(dp(38),dp(44)));r.addView(plus,new LinearLayout.LayoutParams(dp(44),dp(44)));box.addView(r);
        }
        addCard(box);
        LinearLayout custom=card();custom.addView(text("MATERIAL PERSONALIZADO",14,true));EditText e=input("Ex.: 2 metros de cabo");custom.addView(e);
        custom.addView(btn("ADICIONAR ITEM",v->{FieldKitStore.addExtra(this,"materials",e.getText().toString());show();}));addCard(custom);
        LinearLayout notes=card();notes.addView(text("OBSERVAÇÕES DE MATERIAIS",14,true));
        EditText note=input("Ex.: foram usados conectores adicionais");note.setSingleLine(false);note.setMinLines(3);note.setText(FieldKitStore.materialNotes(this,plate));
        note.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int b,int d){}public void onTextChanged(CharSequence s,int a,int b,int d){FieldKitStore.materialNotes(FieldKitActivity.this,plate,s.toString());}public void afterTextChanged(Editable e){}});
        notes.addView(note);addCard(notes);
    }
}
