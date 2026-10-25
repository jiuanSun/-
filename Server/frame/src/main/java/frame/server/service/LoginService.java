package frame.server.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import frame.server.domain.Permission;
import frame.server.domain.Warehouse;
import frame.server.dto.UserDto;
import frame.server.vo.LoginController.RegisterModel;

public interface LoginService {

	UserDto getUserInfo(String account);
	List<Permission> getSystemPermission();
	List<Warehouse> getWarehouses(String userId, boolean isSystemManager);
	@Transactional
	void register(RegisterModel model);
	int getAlarmCount();
	int getPendingApprovalCount();
}
