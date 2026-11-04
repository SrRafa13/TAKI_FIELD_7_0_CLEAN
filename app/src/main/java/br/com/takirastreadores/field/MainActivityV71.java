package br.com.takirastreadores.field;

import android.app.*;
import android.os.*;
import android.content.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class MainActivityV71 extends MainActivityV702 {
    static final String VERSION_71 = "7.1.0";
    static final String JIMI = "Jimi IoT";

    @Override void seed() {
        super.seed();
        String[] models = {"JC450"};
        jc("Consultar IMEI","Identificação do equipamento. A015/A016 da lista oficial.","IMEI");
        jc("Consultar firmware","Versão do firmware; confira compatibilidade antes de configurar.","VERSION");
        jc("Consultar status","Estado de funcionamento informado pelo equipamento.","STATUS");
        jc("Consultar módulo 4G","Versão do módulo de comunicação.","COMVER");
        jc("Consultar WiFiKit","Versão do aplicativo WiFiKit embarcado.","APPV");
        jc("Consultar memória TF","Capacidade e espaço livre dos cartões.","TFCARD,MEMINFO");
        jc("Reiniciar","Reinicia a JC450; interrompe temporariamente a comunicação.","REBOOT");
        jc("Restaurar padrão de fábrica","ATENÇÃO: apaga configurações. Confirme com a equipe antes.","RESTORE");
        jc("Configurar APN","A=nome do perfil, B=APN. Dados da operadora devem ser informados pelo técnico.","APN,{0},{1}","Nome do perfil","APN do chip");
        jc("Configurar servidor","Servidor principal e porta; secundário e demais parâmetros como NA conforme o manual.","SERVER,{0},{1},NA,NA,NA,NA","Endereço IP ou domínio","Porta");
        jc("Tipo de identificação BCD","0=14 dígitos IMEI em HEX; 1=últimos 12 dígitos do IMEI.","BCD,{0}","0 ou 1");
        jc("Tipo de upload HTTP","1=HTTPS, 2=HTTP; escolha segundo o servidor.","URLTYPE,{0}","1 ou 2");
        jc("Intervalo de localização","Intervalo em segundos; confira faixa na documentação.","LOCATEREP,{0}","Segundos");
        jc("Reporte por giro","Ângulo de alteração de direção em graus.","ANGLEREP,{0}","Graus");
        jc("Sincronizar relógio","Fonte de sincronização GPS ou NETWORK.","TIMESYNC,{0}","GPS ou NETWORK");
        jc("Fuso horário","Formato +03:00 ou -03:00; reinicie para aplicar.","TIMEZONE,{0}","Ex.: -03:00");
        jc("Volume dos alertas","0=mudo, 1=baixo, 2=médio, 3=alto.","VOLUME,{0}","0 a 3");
        jc("Idioma da voz","0=inglês, 2=português, 3=espanhol; varia por firmware.","VOICESW,{0}","Código do idioma");
        jc("LEDs","Liga ou desliga os indicadores de estado.","LED,{0}","ON ou OFF");
        jc("Wi-Fi hotspot","-1=ativo enquanto ACC ON; ou duração conforme manual.","WIFIAPT,{0}","-1 ou duração");
        jc("Modo após ACC OFF","0, 101 ou 103, conforme firmware e operação.","WAKEMODE,{0}","0 / 101 / 103");
        jc("Tempo para desligar","Retardo em segundos após ACC OFF.","SHUTDOWNTIME,{0}","Segundos");
        jc("Senha WiFiKit","Troca senha local de 6 dígitos. Não use senha de exemplo.","PASSWORD,{0},{1}","Senha atual","Nova senha");
        jc("Formatar cartão TF","APAGA gravações! 1=TF1, 2=TF2.","FORMAT,{0}","1 ou 2");
        jc("Configuração de gravação","Ativa/desativa gravação por canal, conforme manual.","RECORDSW,{0},{1}","Canal","0 ou 1");
        jc("Gravação de áudio","Áudio associado ao vídeo; consulte política de privacidade aplicável.","RECORDVSW,{0},{1}","Canal","0 ou 1");
        jc("Rotação de câmera","Configuração de orientação de imagem.","RATATION,{0},{1}","Canal","Ângulo");
        jc("Qualidade de vídeo","Canal, resolução, FPS e bitrate.","VIDEORSL,{0},{1},{2},{3}","Canal","Resolução","FPS","Bitrate");
        jc("Unidade velocidade","Configura unidade no vídeo.","MILE,{0}","Código da unidade");
        jc("Armazenamento TF","Modo de gravação nos cartões.","TFMODE,{0}","Modo");
        jc("Fotos periódicas","Parâmetros de captura periódica, conforme manual.","PICTIMER,{0},{1},{2},{3},{4}","Câmeras","Parâmetro B","Intervalo","Parâmetro D","ON/OFF");
        jc("Intervalo eventos","Filtro de vídeos de eventos.","ALARMFLT,{0},{1}","Tipo de evento","Segundos");
        jc("Câmeras em evento","Seleciona canais para captura de evento.","SNAPNUM,{0},{1}","Evento","Câmeras");
        jc("Qualidade das fotos","Qualidade das imagens de eventos.","PICQ,{0}","Qualidade");
        jc("Alerta de velocidade","Limite de velocidade para alerta.","SPEED,{0}","Limite");
        jc("Alerta de vibração","Limiar de vibração.","SHAKETHD,{0}","Limiar");
        jc("Alerta de colisão","Limiar do alerta de colisão.","CRASHALM,{0}","Limiar");
        jc("Aceleração brusca","Limiar de aceleração brusca.","RAPIDACC,{0}","Limiar");
        jc("Frenagem brusca","Limiar de frenagem brusca.","RAPIDDEC,{0}","Limiar");
        jc("Curva brusca","Limiar de curva brusca.","RAPIDTURN,{0}","Limiar");
        jc("Corte remoto RELAY","AÇÃO CRÍTICA: pode imobilizar o veículo. Use apenas com veículo parado e autorização.","RELAY,{0}","Estado conforme manual");
        jc("Porta RS232","Parâmetros de UART/RS232. Confira o manual da versão.","UART,{0},{1},{2},{3},{4},{5},{6}","Porta","B","C","D","Baud rate","F","G");
        jc("Entradas digitais","Configuração de entrada I/O.","INPUTFUN,{0},{1},{2},{3}","Entrada","B","C","D");
        jc("Calibração ADAS","Parâmetros geométricos da câmera e veículo. Somente após medição.","ADAS,CALIBRATION,{0},{1},{2},{3},{4}","A","B","C","D","E");
        jc("Intervalo eventos DMS","Intervalo de upload de eventos ADAS/DMS.","DMSPI,{0},{1}","Evento","Segundos");
        jc("Intervalo voz DMS","Intervalo de avisos sonoros ADAS/DMS.","DMSVI,{0},{1}","Evento","Segundos");
        jc("Velocidade ADAS/DMS","Limiar para ativar eventos.","DMSSP,{0},{1}","Evento","Velocidade");
        jc("Ativar evento ADAS/DMS","Liga ou desliga um tipo de evento. No manual: DMSEP.","DMSEP,{0},{1}","Evento","0 ou 1");
        jc("Sensibilidade DMS","Parâmetros dependem do evento e do firmware.","DMSSEN,{0},{1},{2},{3},{4}","Evento","B","C","D","E");
        jc("Sensibilidade ADAS","Parâmetros dependem do evento.","ADASSEN,{0},{1},{2}","Evento","B","C");
        jc("Velocidade virtual para teste","Apenas simulação/teste de IA; não utilizar durante condução.","DMSVSP,{0}","Velocidade");
        jc("Velocidade virtual fixa","Recurso de teste da IA; use com cautela.","FDMSVSP,{0},{1}","ON/OFF","Velocidade");
        jc("Zona DMS","Região de vídeo para detecção de motorista.","DMSCROP,{0},{1},{2},{3}","ON/OFF","B","C","D");
    }

    void jc(String title,String desc,String template,String... fields){
        String[] models={"JC450"};
        if(fields.length==0)add(JIMI,title,desc,template,models);
        else gen(JIMI,title,desc,template,models,fields,fields);
    }

    @Override String[] models(String v){
        if(JIMI.equals(v))return new String[]{"JC450"};
        return super.models(v);
    }

    @Override void commandsScreen(){
        super.commandsScreen();
        vendor.setAdapter(adapter(new String[]{"Suntech","X3Tech","Queclink",RS232_VENDOR,QUANTA_VENDOR,JIMI}));
        addPaste(idField,"COLAR ESN / IMEI");
        LinearLayout shortcuts=card();
        shortcuts.addView(txt("JC450 • GUIA DE CONFIGURAÇÃO",14,themeText(),true),mb(7));
        shortcuts.addView(button("ABRIR PERFIS RÁPIDOS",false,v->profilesScreen()),new LinearLayout.LayoutParams(-1,dp(44)));
        list.addView(shortcuts,mb(10));
    }

    void addPaste(EditText field,String title){
        if(field==null || !(field.getParent() instanceof LinearLayout))return;
        LinearLayout parent=(LinearLayout)field.getParent();
        int index=parent.indexOfChild(field);
        ViewGroup.LayoutParams original=field.getLayoutParams();
        parent.removeView(field);
        LinearLayout r=row();
        r.addView(field,new LinearLayout.LayoutParams(0,dp(52),1));
        gap(r);
        Button paste=button("▣ COLAR",false,v->pasteInto(field));
        r.addView(paste,new LinearLayout.LayoutParams(dp(98),dp(52)));
        parent.addView(r,index,original);
    }

    void pasteInto(EditText field){
        try{
            android.content.ClipboardManager cm=(android.content.ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
            if(cm==null||!cm.hasPrimaryClip()){Toast.makeText(this,"Prancheta vazia",Toast.LENGTH_SHORT).show();return;}
            android.content.ClipData c=cm.getPrimaryClip();
            if(c==null||c.getItemCount()==0)return;
            CharSequence s=c.getItemAt(0).coerceToText(this);
            if(s==null||s.toString().trim().isEmpty())return;
            field.setText(s.toString().trim());field.setSelection(field.length());
        }catch(Exception e){Toast.makeText(this,"Não foi possível colar",Toast.LENGTH_SHORT).show();}
    }

    @Override void centralScreen(){
        super.centralScreen();
        featureCard("✓","CHECKLIST DE VIAGEM","Ferramentas, materiais e itens condicionais antes de sair.",()->openField("travel"),170);
        featureCard("☑","CHECKLIST DE INSTALAÇÃO","Testes por placa, inclusive câmeras da JC450.",()->openField("install"),190);
        featureCard("▤","MATERIAIS UTILIZADOS","Registre quantidades por placa; disponíveis no PDF.",()->openField("materials"),210);
    }

    void openField(String screen){
        Intent i=new Intent(this,FieldKitActivity.class);
        i.putExtra("screen",screen);startActivity(i);
    }

    @Override void profilesScreen(){
        super.profilesScreen();
        LinearLayout c=card();
        c.addView(txt("JIMI IOT JC450 • CONFIGURAÇÃO INICIAL",15,themeText(),true),mb(6));
        c.addView(txt("Usa APN, servidor e porta salvos nos Ajustes. Cada linha é um SMS separado; confirme os dados e a versão do firmware.",12,themeMuted(),false),mb(9));
        c.addView(button("GERAR ROTEIRO JC450",true,v->{
            String apn=prefs.getString("cfg_apn","").trim(),srv=prefs.getString("cfg_server","").trim(),port=prefs.getString("cfg_port","").trim();
            if(apn.isEmpty()||srv.isEmpty()||port.isEmpty()){
                new AlertDialog.Builder(this).setTitle("Configuração incompleta").setMessage("Preencha APN, servidor e porta em Ajustes. Não vamos usar servidores de exemplo.").setPositiveButton("OK",null).show();return;
            }
            String plan="JC450 • ENVIAR UM SMS POR VEZ\n\nBCD,1\nAPN,"+apn+","+apn+"\nSERVER,"+srv+","+port+",NA,NA,NA,NA\nREBOOT\nSTATUS\n\nConfirme os parâmetros com o provedor e o firmware.";
            new AlertDialog.Builder(this).setTitle("Roteiro JC450").setMessage(plan).setNegativeButton("Fechar",null).setPositiveButton("COPIAR", (d,w)->copy(plan)).show();
        }),new LinearLayout.LayoutParams(-1,dp(48)));
        list.addView(c,mb(10));
    }

    @Override String interpret(String raw){
        String base=super.interpret(raw);
        if(raw==null||raw.trim().isEmpty())return base;
        String up=raw.toUpperCase(Locale.ROOT);
        StringBuilder b=new StringBuilder(base);
        if(up.contains("APN"))b.append("\nJC450: resposta menciona APN; confira nome e operadora.");
        if(up.contains("SERVER"))b.append("\nJC450: resposta menciona servidor; confira endereço e porta.");
        if(up.contains("TF")||up.contains("SD CARD"))b.append("\nJC450: resposta menciona cartão de memória; confira capacidade/status.");
        if(up.contains("DMS")||up.contains("ADAS"))b.append("\nJC450: resposta menciona ADAS/DMS; confirme parâmetros no manual.");
        return b.toString();
    }

    @Override boolean dangerous(String s){
        if(super.dangerous(s))return true;
        String x=s==null?"":s.trim().toUpperCase(Locale.ROOT);
        return x.matches("(?s)^(RESTORE|FORMAT|RELAY|UPDATE|PASSWORD|WAKEMODE|SHUTDOWNTIME|DMSSP|DMSEP|FDMSVSP)(,|$).*");
    }

    @Override void sms(String s){
        if(dangerous(s)){
            new AlertDialog.Builder(this).setTitle("Confirmar SMS de configuração")
                .setMessage("Este comando pode alterar configurações, gravações ou funcionamento do equipamento. Verifique o veículo e os parâmetros antes de abrir o SMS.\n\n"+s)
                .setNegativeButton("Cancelar",null)
                .setPositiveButton("ABRIR SMS",(d,w)->super.sms(s)).show();
        }else super.sms(s);
    }

    @Override void openInstallations(){startActivity(new Intent(this,InstallationActivityV71.class));}

    @Override void replace70(View v){
        super.replace70(v);
        replace71(v);
    }
    void replace71(View v){
        if(v instanceof TextView){
            TextView t=(TextView)v;
            String s=String.valueOf(t.getText());
            if(s.contains("7.0.3"))t.setText(s.replace("7.0.3",VERSION_71));
        }
        if(v instanceof ViewGroup){
            ViewGroup g=(ViewGroup)v;
            for(int i=0;i<g.getChildCount();i++)replace71(g.getChildAt(i));
        }
    }

    @Override void checkUpdate(){
        Toast.makeText(this,"Consultando versão...",Toast.LENGTH_SHORT).show();
        new Thread(()->{
            try(BufferedReader br=new BufferedReader(new InputStreamReader(new URL("https://raw.githubusercontent.com/SrRafa13/TAKI_FIELD_7_0_CLEAN/main/version.txt").openStream()))){
                String latest=br.readLine();
                runOnUiThread(()->new AlertDialog.Builder(this).setTitle("TAKI FIELD • Atualização")
                    .setMessage("Instalada: "+VERSION_71+"\nDisponível: "+latest+(VERSION_71.equals(latest)?"\nSeu app está atualizado.":"\nConfira as versões publicadas."))
                    .setPositiveButton("FECHAR",null)
                    .setNeutralButton("VERSÕES",(d,w)->openUrl("https://github.com/SrRafa13/TAKI_FIELD_7_0_CLEAN/actions")).show());
            }catch(Exception e){runOnUiThread(()->Toast.makeText(this,"Consulta indisponível",Toast.LENGTH_LONG).show());}
        }).start();
    }
}
