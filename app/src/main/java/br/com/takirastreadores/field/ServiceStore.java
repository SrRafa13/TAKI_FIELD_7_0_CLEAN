package br.com.takirastreadores.field;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

final class ServiceStore {
    static final String FILE_NAME = "service_history_v6.json";

    static class Entry {
        String plate = "";
        String service = "";
        String brand = "";
        String module = "";
        String technician = "";
        String finishedAt = "";
        String batchId = "";
        String reportFile = "";
        String reportType = "";
        int reportCount = 0;
        long epoch = 0L;

        JSONObject toJson() {
            JSONObject o = new JSONObject();
            try {
                o.put("plate", plate);
                o.put("service", service);
                o.put("brand", brand);
                o.put("module", module);
                o.put("technician", technician);
                o.put("finishedAt", finishedAt);
                o.put("batchId", batchId);
                o.put("reportFile", reportFile);
                o.put("reportType", reportType);
                o.put("reportCount", reportCount);
                o.put("epoch", epoch);
            } catch (Exception ignored) {}
            return o;
        }

        static Entry fromJson(JSONObject o) {
            Entry e = new Entry();
            e.plate = o.optString("plate", "");
            e.service = o.optString("service", "");
            e.brand = o.optString("brand", "");
            e.module = o.optString("module", "");
            e.technician = o.optString("technician", "");
            e.finishedAt = o.optString("finishedAt", "");
            e.batchId = o.optString("batchId", "");
            e.reportFile = o.optString("reportFile", "");
            e.reportType = o.optString("reportType", e.batchId.isEmpty() ? "individual" : "batch");
            e.reportCount = o.optInt("reportCount", e.batchId.isEmpty() ? 1 : 0);
            e.epoch = o.optLong("epoch", 0L);
            return e;
        }

        String key() {
            return (plate + "|" + service + "|" + technician + "|" + finishedAt).toUpperCase(Locale.ROOT);
        }
    }

    static synchronized ArrayList<Entry> list(Context c) {
        ArrayList<Entry> out = new ArrayList<>();
        try {
            File f = new File(c.getFilesDir(), FILE_NAME);
            if (!f.exists()) return out;
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            try (FileInputStream in = new FileInputStream(f)) {
                byte[] buf = new byte[4096];
                int n;
                while ((n = in.read(buf)) > 0) b.write(buf, 0, n);
            }
            JSONObject root = new JSONObject(new String(b.toByteArray(), "UTF-8"));
            JSONArray arr = root.optJSONArray("entries");
            if (arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.optJSONObject(i);
                    if (o != null) out.add(Entry.fromJson(o));
                }
            }
        } catch (Exception ignored) {}
        Collections.sort(out, new Comparator<Entry>() {
            @Override public int compare(Entry a, Entry b) { return Long.compare(b.epoch, a.epoch); }
        });
        return out;
    }

    static synchronized void add(Context c, Entry incoming) {
        addVerified(c, incoming);
    }

    static synchronized boolean addVerified(Context c, Entry incoming) {
        if (c == null || incoming == null || incoming.plate == null || incoming.plate.trim().isEmpty()) return false;
        ArrayList<Entry> entries = list(c);
        String key = incoming.key();
        boolean changed = false;
        boolean found = false;
        for (Entry e : entries) {
            if (e.key().equals(key)) {
                found = true;
                if (!incoming.reportFile.isEmpty() && (e.reportFile == null || e.reportFile.isEmpty())) {
                    e.reportFile = incoming.reportFile;
                    e.reportType = incoming.reportType;
                    e.reportCount = incoming.reportCount;
                    changed = true;
                }
                if (incoming.batchId != null && !incoming.batchId.isEmpty() && (e.batchId == null || e.batchId.isEmpty())) {
                    e.batchId = incoming.batchId;
                    changed = true;
                }
                if (incoming.epoch > 0 && e.epoch <= 0) {
                    e.epoch = incoming.epoch;
                    changed = true;
                }
                break;
            }
        }
        if (!found) {
            entries.add(0, incoming);
            changed = true;
        }
        if (entries.size() > 1500) entries = new ArrayList<>(entries.subList(0, 1500));
        if (changed && !writeChecked(c, entries)) return false;

        // Leitura de volta: só confirma sucesso se o registro realmente existir no arquivo persistido.
        ArrayList<Entry> check = list(c);
        for (Entry e : check) if (e.key().equals(key)) return true;
        return false;
    }

    static synchronized void add(Context c, String plate, String service, String brand, String module,
                                 String technician, String finishedAt, long epoch, String batchId) {
        addVerified(c, plate, service, brand, module, technician, finishedAt, epoch, batchId, "", batchId == null || batchId.isEmpty() ? "individual" : "batch", batchId == null || batchId.isEmpty() ? 1 : 0);
    }

    static synchronized void add(Context c, String plate, String service, String brand, String module,
                                 String technician, String finishedAt, long epoch, String batchId,
                                 String reportFile, String reportType, int reportCount) {
        addVerified(c, plate, service, brand, module, technician, finishedAt, epoch, batchId, reportFile, reportType, reportCount);
    }

    static synchronized boolean addVerified(Context c, String plate, String service, String brand, String module,
                                            String technician, String finishedAt, long epoch, String batchId,
                                            String reportFile, String reportType, int reportCount) {
        Entry e = new Entry();
        e.plate = plate == null ? "" : plate.trim().toUpperCase(Locale.ROOT);
        e.service = service == null ? "" : service;
        e.brand = brand == null ? "" : brand;
        e.module = module == null ? "" : module;
        e.technician = technician == null ? "" : technician;
        e.finishedAt = finishedAt == null ? "" : finishedAt;
        e.epoch = epoch;
        e.batchId = batchId == null ? "" : batchId;
        e.reportFile = reportFile == null ? "" : reportFile;
        e.reportType = reportType == null ? "" : reportType;
        e.reportCount = Math.max(0, reportCount);
        return addVerified(c, e);
    }

    static synchronized boolean exists(Context c, String plate, String service, String technician, String finishedAt) {
        Entry probe = new Entry();
        probe.plate = plate == null ? "" : plate.trim().toUpperCase(Locale.ROOT);
        probe.service = service == null ? "" : service;
        probe.technician = technician == null ? "" : technician;
        probe.finishedAt = finishedAt == null ? "" : finishedAt;
        String key = probe.key();
        for (Entry e : list(c)) if (e.key().equals(key)) return true;
        return false;
    }

    static synchronized void remove(Context c, Entry target) {
        ArrayList<Entry> entries = list(c);
        String key = target.key();
        for (int i = entries.size() - 1; i >= 0; i--) {
            if (entries.get(i).key().equals(key)) entries.remove(i);
        }
        writeChecked(c, entries);
    }

    static synchronized void clear(Context c) {
        try { new File(c.getFilesDir(), FILE_NAME).delete(); } catch (Exception ignored) {}
    }

    static synchronized void write(Context c, ArrayList<Entry> entries) {
        writeChecked(c, entries);
    }

    static synchronized boolean writeChecked(Context c, ArrayList<Entry> entries) {
        try {
            JSONObject root = new JSONObject();
            root.put("version", 3);
            JSONArray arr = new JSONArray();
            for (Entry e : entries) arr.put(e.toJson());
            root.put("entries", arr);
            byte[] data = root.toString().getBytes("UTF-8");

            File target = new File(c.getFilesDir(), FILE_NAME);
            File tmp = new File(c.getFilesDir(), FILE_NAME + ".tmp");
            try (FileOutputStream out = new FileOutputStream(tmp)) {
                out.write(data);
                out.flush();
                try { out.getFD().sync(); } catch (Exception ignored) {}
            }

            boolean moved = false;
            if (target.exists() && !target.delete()) moved = false;
            if (!target.exists()) moved = tmp.renameTo(target);
            if (!moved) {
                try (FileOutputStream out = new FileOutputStream(target, false)) {
                    out.write(data);
                    out.flush();
                    try { out.getFD().sync(); } catch (Exception ignored) {}
                }
                tmp.delete();
            }
            return target.exists() && target.length() > 0;
        } catch (Exception ignored) {
            return false;
        }
    }

    private ServiceStore() {}
}
