package frame.server.controller.warehouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import frame.server.annotation.OperationAnnotation;
import frame.server.controller.BaseController;
import frame.server.service.warehouse.WHomeService;
import frame.server.utils.ApiResultUtil;
import frame.server.vo.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "仓库管理系统：首页管理控制器")
@RequestMapping("/wHome")
@RestController
public class WHomeController extends BaseController {
	
	@Autowired
	private WHomeService wHomeService;

	@ApiOperation(value = "仓库管理系统：获取初始化数据")
	@OperationAnnotation(value = "仓库管理系统：获取初始化数据")
	@RequestMapping(value = "/getInitData", method = RequestMethod.GET)
	public ApiResult getInitData() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("userCount", wHomeService.getUserCount());
		jsonObject.put("warehouseCount", wHomeService.getWarehouseCount());
		jsonObject.put("alarmCount", wHomeService.getAlarmCount());
		jsonObject.put("typeCounts", wHomeService.getConsumableTypeCounts());
		jsonObject.put("consumableCount", wHomeService.getConsumableCount());
		jsonObject.put("recentOperations", wHomeService.getRecentOperations(5));
		jsonObject.put("pendingApprovalCount", wHomeService.getPendingApprovalCount());
		jsonObject.put("monthlyTrend", wHomeService.getMonthlyTrend());
		jsonObject.put("todayStats", wHomeService.getTodayStats());
		return ApiResultUtil.success(jsonObject);
	}
}
