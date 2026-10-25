package frame.server.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface SmartAnalysisService {
    JSONArray getRestockSuggestions();
    JSONArray getAnomalyAlerts();
    String generateQrLabel(String consumableId);
}
