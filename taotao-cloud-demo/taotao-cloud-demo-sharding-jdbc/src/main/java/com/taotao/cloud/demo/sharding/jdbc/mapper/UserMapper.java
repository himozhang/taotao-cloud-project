package com.taotao.cloud.demo.sharding.jdbc.mapper;

import com.taotao.cloud.data.mybatis.plus.mapper.MpSuperMapper;
import com.taotao.cloud.demo.sharding.jdbc.model.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zlt
 */
@Mapper
public interface UserMapper extends MpSuperMapper<User> {

}
