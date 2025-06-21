package com.orion.contactservice.mapper;

import com.orion.contactservice.model.Session;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【session(会话表)】的数据库操作Mapper
* @createDate 2025-06-13 11:46:17
* @Entity com.orion.contactservice.model.Session
*/
@Mapper
public interface SessionMapper extends BaseMapper<Session> {

}




