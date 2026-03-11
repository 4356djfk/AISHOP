package com.shop.aishop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.aishop.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 系统用户基础信息表 Mapper 接口
 * </p>
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
