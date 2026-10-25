package frame.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import frame.server.config.mybatis.SimpleInsertExtendedLanguageDriver;
import frame.server.domain.Operation;
import frame.server.dto.OperationDto;

/**
 * 操作履历
 * 
 * @author Administrator
 *
 */
@Mapper
public interface OperationDao {
	
	/**
	 * 添加操作履历
	 * 
	 * @param operation
	 * @return
	 */
	@Insert("insert into operation (#{operation})")
	@Lang(SimpleInsertExtendedLanguageDriver.class)
	Integer insert(Operation operation);
	
	/**
	 * 清空操作履历
	 * 
	 * @return
	 */
	@Delete("delete from operation")
	Integer clear();
	
	/**
	 * 获取所有操作履历
	 * 
	 * @return
	 */
	@Select("select t1.*, t2.name from operation as t1, user as t2 where t1.user_id = t2.id order by create_time desc")
	List<OperationDto> selectAll();


	@Select("select * from operation order by create_time desc limit #{limit}")
	java.util.List<frame.server.domain.Operation> selectRecent(int limit);


	@Select("select date_format(h.create_time, '%Y-%m') as month, sum(case when d.operation_type in (2,3) then d.operation_count else 0 end) as inbound, sum(case when d.operation_type = 0 then d.operation_count else 0 end) as outbound from consumable_operation_detail d join consumable_operation_history h on d.consumable_operation_history_id = h.id group by date_format(h.create_time, '%Y-%m') order by month desc limit 6")
	java.util.List<java.util.Map<String, Object>> selectMonthlyTrend();
}
