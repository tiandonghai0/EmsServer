package com.shmet.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shmet.entity.mysql.gen.SubProject;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author
 */
@Repository
public interface SubProjectMapper extends BaseMapper<SubProject> {

    @Select("<script> SELECT c.* from t_customer a LEFT JOIN t_project b on a.customer_id=b.customer_id LEFT JOIN t_sub_project c on c.project_id=b.project_id WHERE a.version='EMS2.0' " +
            "<if test='projectName != null'>  and b.project_name  like '%${projectName}%' </if> </script>")
    public List<SubProject> getV2Project(@Param("projectName") String projectName);

    @Select("<script> " +
            "SELECT a.customer_name customerName,c.sub_project_id subProjectId,c.project_id projectId,c.sub_project_name subProjectName,c.total_capacity totalCapacity ,c.battery_type batteryType,b.longitude,b.latitude,b.addr,b.sysType,a.customer_id customerId,a.no customerNo " +
            "from t_customer a " +
            "LEFT JOIN t_project b on a.customer_id=b.customer_id " +
            "LEFT JOIN t_sub_project c on c.project_id=b.project_id " +
            "WHERE a.version='EMS2.0' " +
            "<if test='projectName != null'>  and b.project_name  like '%${projectName}%' </if> " +
            "<if test='sysType != null'>  and b.sysType = '${sysType}' </if> " +
            "order by b.create_date desc" +
            "</script>")
    public List<Map<String,Object>> getV2ProjectInfo(@Param("projectName") String projectName, @Param("sysType") String sysType);
}
