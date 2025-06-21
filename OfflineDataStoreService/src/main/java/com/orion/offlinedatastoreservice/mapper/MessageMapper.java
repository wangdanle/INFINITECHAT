package com.orion.offlinedatastoreservice.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.offlinedatastoreservice.model.Message;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Zzw
* @description 针对表【message】的数据库操作Mapper
* @createDate 2024-09-20 16:39:30
* @Entity generator.domain.Message
*/
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

}




