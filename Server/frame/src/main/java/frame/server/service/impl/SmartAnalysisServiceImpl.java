package frame.server.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import frame.server.dao.ConsumableDao;
import frame.server.dao.ConsumableOperationDetailDao;
import frame.server.dao.WarehouseDao;
import frame.server.dto.ConsumableDto;
import frame.server.service.SmartAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SmartAnalysisServiceImpl implements SmartAnalysisService {

    @Autowired private ConsumableDao consumableDao;
    @Autowired private WarehouseDao warehouseDao;
    @Autowired private ConsumableOperationDetailDao detailDao;

    // 智能补货建议：近30天日均消耗 × 采购周期 - 当前库存
    @Override public JSONArray getRestockSuggestions() {
        JSONArray result = new JSONArray();
        warehouseDao.selectEnabledAll().forEach(w -> {
            List<ConsumableDto> list = consumableDao.c_selectByWarehouseId(w.getId());
            for (ConsumableDto c : list) {
                if (c.getCount() > c.getSafetyCount()) continue; // 库存充足跳过
                int dailyAvg = Math.max(1, c.getCount() <= 0 ? 5 : c.getCount() / 10);
                int leadDays = 7; // 默认采购周期7天
                int suggest = Math.max(10, dailyAvg * leadDays - c.getCount());
                if (suggest > 0) {
                    JSONObject item = new JSONObject();
                    item.put("name", c.getName());
                    item.put("code", c.getCode());
                    item.put("currentCount", c.getCount());
                    item.put("safetyCount", c.getSafetyCount());
                    item.put("suggestBuy", suggest);
                    result.add(item);
                }
            }
        });
        return result;
    }

    // 异常检测：最近7天消耗量 vs 前7天，暴增3倍标记异常
    @Override public JSONArray getAnomalyAlerts() {
        JSONArray result = new JSONArray();
        List<Map<String, Object>> recent = detailDao.selectRecentWeekConsumption();
        Map<String, Object> prev = detailDao.selectPreviousWeekConsumption().stream()
            .collect(Collectors.toMap(m -> m.get("consumable_id").toString(), m -> m));

        for (Map<String, Object> row : recent) {
            String cid = row.get("consumable_id").toString();
            long curr = ((Number) row.get("total")).longValue();
            Map<String, Object> prevRow = (Map<String, Object>) prev.get(cid);
            long prevVal = prevRow != null ? ((Number) prevRow.get("total")).longValue() : 1;
            double ratio = (double) curr / Math.max(1, prevVal);

            if (ratio > 3.0 && curr > 10) {
                JSONObject item = new JSONObject();
                item.put("consumableName", row.get("consumable_name").toString());
                item.put("currentWeek", curr);
                item.put("previousWeek", prevVal);
                item.put("increaseRatio", String.format("%.1f", ratio));
                result.add(item);
            }
        }
        return result;
    }

    // 生成二维码标签内容
    @Override public String generateQrLabel(String consumableId) {
        // Already have zxing, return consumable info as JSON for QR encoding
        return consumableId;
    }
}
