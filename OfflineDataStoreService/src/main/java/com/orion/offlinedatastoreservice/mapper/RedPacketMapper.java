package com.orion.offlinedatastoreservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;


import com.orion.offlinedatastoreservice.model.RedPacket;
import org.apache.ibatis.annotations.Mapper;

/**
 * 红包主表 Mapper 接口
 */
@Mapper
public interface RedPacketMapper extends BaseMapper<RedPacket> {
}