package org.smartregister.pnc.pojo;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;

public class PncVisitSummary {

    private Map<String, Map<String, String>> visitInfoMap;
    private Map<String, List<Map<String, String>>> visitChildStatusMap;

    public PncVisitSummary(@NonNull Map<String, Map<String, String>> visitInfoMap, @NonNull Map<String, List<Map<String, String>>> visitChildStatusMap) {
        this.visitInfoMap = visitInfoMap;
        this.visitChildStatusMap = visitChildStatusMap;
    }

    @NonNull
    public Map<String, Map<String, String>> getVisitInfoMap() {
        return visitInfoMap;
    }

    @NonNull
    public Map<String, List<Map<String, String>>> getVisitChildStatusMap() {
        return visitChildStatusMap;
    }
}
