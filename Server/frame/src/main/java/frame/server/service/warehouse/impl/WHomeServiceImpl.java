package frame.server.service.warehouse.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import frame.server.dao.ApplicationDao;
import frame.server.dao.ConsumableDao;
import frame.server.dao.OperationDao;
import frame.server.dao.UserDao;
import frame.server.dao.WarehouseDao;
import frame.server.domain.Operation;
import frame.server.dto.ConsumableDto;
import frame.server.service.warehouse.WHomeService;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("wHomeService")
public class WHomeServiceImpl implements WHomeService {
	
	@Autowired private UserDao userDao;
	@Autowired private WarehouseDao warehouseDao;
	@Autowired private ConsumableDao consumableDao;
	@Autowired private OperationDao operationDao;
	@Autowired private ApplicationDao applicationDao;

	@Override public int getUserCount() { return userDao.selectSystemAdminAndWarehouseManagerCount(); }
	@Override public int getWarehouseCount() { return warehouseDao.selectWarehousesCount(); }

	@Override public int getAlarmCount() {
		return warehouseDao.selectEnabledAll().stream().mapToInt(w -> consumableDao.c_selectAlarmByWarehouseId(w.getId()).size()).sum();
	}
	@Override public int getConsumableCount() {
		return warehouseDao.selectEnabledAll().stream().mapToInt(w -> consumableDao.c_selectCountByWarehouseId(w.getId())).sum();
	}
	@Override public int getPendingApprovalCount() {
		return warehouseDao.selectEnabledAll().stream().mapToInt(w -> applicationDao.selectCountByWarehouseId(w.getId())).sum();
	}

	@Override public JSONObject getTodayStats() {
		JSONObject stats = new JSONObject();
		List<Operation> recent = operationDao.selectRecent(50);
		String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		long count = recent.stream().filter(op -> op.getCreateTime().startsWith(today)).count();
		stats.put("totalOps", (int) count);
		return stats;
	}

	@Override public JSONArray getConsumableTypeCounts() {
		JSONArray result = new JSONArray();
		Map<String, Integer> map = new LinkedHashMap<>();
		warehouseDao.selectEnabledAll().forEach(w -> {
			consumableDao.c_selectByWarehouseId(w.getId()).forEach(c -> map.merge(c.getTypeName() != null ? c.getTypeName() : "未分类", c.getCount(), Integer::sum));
		});
		map.forEach((k, v) -> { JSONObject o = new JSONObject(); o.put("name", k); o.put("value", v); result.add(o); });
		return result;
	}

	@Override public List<JSONObject> getRecentOperations(int limit) {
		List<JSONObject> result = new ArrayList<>();
		for (Operation op : operationDao.selectRecent(limit)) {
			JSONObject item = new JSONObject();
			String opStr = op.getOperation();
			item.put("operation", opStr.length() > 30 ? opStr.substring(0, 30) + "..." : opStr);
			item.put("createTime", op.getCreateTime());
			item.put("ip", op.getIp());
			result.add(item);
		}
		return result;
	}

	@Override public JSONObject getMonthlyTrend() {
		JSONObject result = new JSONObject();
		JSONArray months = new JSONArray();
		JSONArray inbound = new JSONArray();
		JSONArray outbound = new JSONArray();
		List<Map<String, Object>> rows = operationDao.selectMonthlyTrend();
		Collections.reverse(rows);
		for (Map<String, Object> row : rows) {
			months.add(row.get("month").toString());
			inbound.add(Integer.parseInt(row.get("inbound").toString()));
			outbound.add(Integer.parseInt(row.get("outbound").toString()));
		}
		result.put("months", months);
		result.put("inbound", inbound);
		result.put("outbound", outbound);
		return result;
	}
}
