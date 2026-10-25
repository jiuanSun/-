package frame.server.service.warehouse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.List;

public interface WHomeService {
	int getUserCount();
	int getWarehouseCount();
	int getAlarmCount();
	int getConsumableCount();
	int getPendingApprovalCount();
	JSONObject getTodayStats();
	JSONArray getConsumableTypeCounts();
	List<JSONObject> getRecentOperations(int limit);
	JSONObject getMonthlyTrend();
}
