package com.frontleaves.greenchaincarbonledger.mapper;

import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * UserMapper
 * <hr/>
 * 用于用户的数据访问对象, 用于Mybatis
 *
 * @author xiao_feng
 */
@Mapper
public interface UserMapper {
    @Select("SELECT * FROM fy_carbon.fy_user WHERE uuid = #{uuid}")
    UserDO getUserByUuid(String uuid);
}
