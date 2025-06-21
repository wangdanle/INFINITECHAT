package com.orion.messagingService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.messagingService.model.RedPacketReceive;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【red_packet_receive(红包领取记录表)】的数据库操作Mapper
 * @createDate 2025-06-06 21:44:12
 * @Entity generator.domain.RedPacketReceive
 */
@Mapper
public interface RedPacketReceiveMapper extends BaseMapper<RedPacketReceive> {
    List<RedPacketReceive> selectByRedPacketId(@Param("redPacketId") Long redPacketId,
                                               @Param("pageNum") Integer pageNum,
                                               @Param("pageSize") Integer pageSize
    );

}




