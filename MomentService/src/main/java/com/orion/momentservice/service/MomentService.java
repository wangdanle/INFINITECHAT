package com.orion.momentservice.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.orion.momentservice.data.createMoment.CreateMomentRequest;
import com.orion.momentservice.data.createMoment.CreateMomentResponse;
import com.orion.momentservice.data.deleteMoment.DeleteMomentRequest;
import com.orion.momentservice.data.deleteMoment.DeleteMomentResponse;
import com.orion.momentservice.data.getMomentList.GetMomentListRequest;
import com.orion.momentservice.data.getMomentList.GetMomentListResponse;
import com.orion.momentservice.model.Moment;

/**
* @author Zzw
* @description 针对表【moment(朋友圈)】的数据库操作Service
* @createDate 2024-10-08 11:59:32
*/
public interface MomentService extends IService<Moment> {
    CreateMomentResponse createMoment(CreateMomentRequest request);

    DeleteMomentResponse deleteMoment(DeleteMomentRequest request);

    GetMomentListResponse getMomentList(GetMomentListRequest request);

    Long getMomentOwnerId(Long momentId);
}
