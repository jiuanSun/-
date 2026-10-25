package frame.server.controller.warehouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import frame.server.service.SmartAnalysisService;
import frame.server.utils.ApiResultUtil;
import frame.server.vo.ApiResult;

@RestController
@RequestMapping("/smart")
public class SmartAnalysisController {

    @Autowired
    private SmartAnalysisService smartAnalysisService;

    @RequestMapping(value = "/restock", method = RequestMethod.GET)
    public ApiResult getRestockSuggestions() {
        JSONObject res = new JSONObject();
        res.put("suggestions", smartAnalysisService.getRestockSuggestions());
        res.put("anomalies", smartAnalysisService.getAnomalyAlerts());
        return ApiResultUtil.success(res);
    }
}
