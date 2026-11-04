package br.com.takirastreadores.field;

import android.content.*;
import org.json.*;
import java.util.*;

final class FieldKitStore {
    static final String PREFS="taki_field_v71";
    static final String[] TRAVEL={
        "Bolsas de ferramentas","Caixa de fusíveis","Caixa de ferragens",
        "Fita isolante extra","Presilhas / abraçadeiras","Chaves de boca",
        "Rastreadores","Multímetro","Chaves de fenda / Phillips",
        "Alicate de corte","Fusíveis de reserva","Celular e carregador"
    };
    static final String[] INSTALL={
        "Alimentação positiva (B+) conferida","Aterramento (GND) conferido",
        "Ignição (ACC) testada","Módulo fixado com segurança",
        "Sinal GPS validado","Comunicação com plataforma validada",
        "Comandos/configuração conferidos","Teste final realizado"
    };
    static final String[] JC450={
        "JC450 • cartão microSD reconhecido","JC450 • LEDs de status conferidos",
        "JC450 • câmeras com imagem validada","JC450 • ADAS/DMS conferidos, se aplicável"
    };
    static final String[] MATERIALS={
        "Rastreador","Teclado de jornada","iButton","Fusível",
        "Presilha / abraçadeira","Fita isolante","Conector",
        "Terminal","Chicote","Cartão microSD","Chip M2M","Outros"
    };

    static android.content.SharedPreferences p(Context c){return c.getSharedPreferences(PREFS,Context.MODE_PRIVATE);}
    static String key(String plate){return (plate==null?"":plate.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]",""));}
    static String travelKey(String tech){return "travel_"+key(tech);}
    static String installKey(String plate){return "install_"+key(plate);}
    static String materialKey(String plate){return "materials_"+key(plate);}

    static Set<String> checked(Context c,String k){
        return new HashSet<>(p(c).getStringSet(k,Collections.emptySet()));
    }
    static void setChecked(Context c,String k,String item,boolean yes){
        Set<String> a=checked(c,k);
        if(yes)a.add(item);else a.remove(item);
        p(c).edit().putStringSet(k,a).apply();
    }
    static void reset(Context c,String k){p(c).edit().remove(k).apply();}
    static boolean flag(Context c,String k){return p(c).getBoolean(k,false);}
    static void flag(Context c,String k,boolean val){p(c).edit().putBoolean(k,val).apply();}

    static LinkedHashMap<String,Integer> materials(Context c,String plate){
        LinkedHashMap<String,Integer> m=new LinkedHashMap<>();
        try{
            JSONObject o=new JSONObject(p(c).getString(materialKey(plate),"{}"));
            Iterator<String> it=o.keys();
            while(it.hasNext()){String k=it.next();int n=o.optInt(k,0);if(n>0)m.put(k,n);}
        }catch(Exception ignored){}
        return m;
    }
    static void quantity(Context c,String plate,String item,int n){
        LinkedHashMap<String,Integer> m=materials(c,plate);
        if(n<=0)m.remove(item);else m.put(item,Math.min(n,9999));
        JSONObject o=new JSONObject();
        for(Map.Entry<String,Integer> e:m.entrySet())try{o.put(e.getKey(),e.getValue());}catch(Exception ignored){}
        p(c).edit().putString(materialKey(plate),o.toString()).apply();
    }
    static String materialNotes(Context c,String plate){return p(c).getString("material_notes_"+key(plate),"");}
    static void materialNotes(Context c,String plate,String notes){p(c).edit().putString("material_notes_"+key(plate),notes).apply();}
    static String extra(Context c,String type){return p(c).getString("extra_"+type,"");}
    static void addExtra(Context c,String type,String name){
        String old=extra(c,type);
        if(name==null||name.trim().isEmpty()||name.contains("\n"))return;
        String x=name.trim();
        for(String s:old.split("\n"))if(s.equalsIgnoreCase(x))return;
        p(c).edit().putString("extra_"+type,old.isEmpty()?x:old+"\n"+x).apply();
    }
    static List<String> list(String[] base,String extra){
        LinkedHashSet<String> items=new LinkedHashSet<>(Arrays.asList(base));
        if(extra!=null)for(String s:extra.split("\n"))if(!s.trim().isEmpty())items.add(s.trim());
        return new ArrayList<>(items);
    }
    static boolean hasReportData(Context c,String plate){
        return !checked(c,installKey(plate)).isEmpty()||!materials(c,plate).isEmpty()||!materialNotes(c,plate).trim().isEmpty();
    }
    private FieldKitStore(){}
}
