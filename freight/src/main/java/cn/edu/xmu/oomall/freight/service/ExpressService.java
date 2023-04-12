package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.service.dto.*;
import cn.edu.xmu.oomall.freight.dao.ExpressDao;
import cn.edu.xmu.oomall.freight.dao.bo.Express;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpressService {

    private static final Logger logger = LoggerFactory.getLogger(ExpressService.class);

    private ExpressDao expressDao;

    @Autowired
    public ExpressService(ExpressDao expressDao) {
        this.expressDao = expressDao;
    }

    public ExpressDto findExpressByBillCode(Long shopId, String billCode) {
        Express express = expressDao.findExpressByBillCode(billCode);
        if (!express.getShopId().equals(shopId)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW,
                    String.format(ReturnNo.STATENOTALLOW.getMessage(), "商家", shopId, "无权"));
        }
        return ExpressDto.builder()
                .id(express.getId())
                .billCode(express.getBillCode())
                .logistics(LogisticsDto.builder()
                        .id(express.getShopLogistics().getLogistics().getId())
                        .name(express.getShopLogistics().getLogistics().getName())
                        .build())
                .shipper(ExpressInfo.builder()
                        .name(express.getSenderName())
                        .mobile(express.getSenderMobile())
                        .regionId(express.getSenderRegionId())
                        .address(express.getSenderAddress())
                        .build())
                .receiver(ExpressInfo.builder()
                        .name(express.getDeliverName())
                        .mobile(express.getDeliverMobile())
                        .regionId(express.getDeliverRegionId())
                        .address(express.getDeliverAddress())
                        .build())
                .status(express.getStatus())
                .gmtCreate(express.getGmtCreate())
                .gmtModified(express.getGmtModified())
                .creator(SimpleAdminUserDto.builder()
                        .id(express.getCreatorId())
                        .userName(express.getCreatorName())
                        .build())
                .modifier(SimpleAdminUserDto.builder()
                        .id(express.getModifierId())
                        .userName(express.getModifierName())
                        .build())
                .build();
    }
    public ExpressDto findExpressById(Long id) {
        Express express = expressDao.findExpressById(id);
        return ExpressDto.builder()
                .id(express.getId())
                .billCode(express.getBillCode())
                .logistics(LogisticsDto.builder()
                        .id(express.getShopLogistics().getLogistics().getId())
                        .name(express.getShopLogistics().getLogistics().getName())
                        .build())
                .shipper(ExpressInfo.builder()
                        .name(express.getSenderName())
                        .mobile(express.getSenderMobile())
                        .regionId(express.getSenderRegionId())
                        .address(express.getSenderAddress())
                        .build())
                .receiver(ExpressInfo.builder()
                        .name(express.getDeliverName())
                        .mobile(express.getDeliverMobile())
                        .regionId(express.getDeliverRegionId())
                        .address(express.getDeliverAddress())
                        .build())
                .status(express.getStatus())
                .gmtCreate(express.getGmtCreate())
                .gmtModified(express.getGmtModified())
                .creator(SimpleAdminUserDto.builder()
                        .id(express.getCreatorId())
                        .userName(express.getCreatorName())
                        .build())
                .modifier(SimpleAdminUserDto.builder()
                        .id(express.getModifierId())
                        .userName(express.getModifierName())
                        .build())
                .build();
    }

    public void confirmExpress(Express express, UserDto user) {
        if(null==express.getStatus()||(express.getStatus().longValue()!=0L&&express.getStatus().longValue()!=1L)){
            throw new BusinessException(ReturnNo.FIELD_NOTVALID,
                    String.format(ReturnNo.FIELD_NOTVALID.getMessage(), "status"));
        }
        expressDao.save(express,user);
    }

    public SimpleExpressDto createExpress(Express express, UserDto user) {
        Express bo = expressDao.insert(express,user);
        SimpleExpressDto dto = SimpleExpressDto.builder()
                .id(bo.getId())
                .billCode(bo.getBillCode())
                .build();
        return dto;
    }

    public void cancelExpress(Express express) {
        expressDao.cancel(express);
    }
}
