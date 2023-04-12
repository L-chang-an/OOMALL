package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import cn.edu.xmu.oomall.freight.mapper.ShopLogisticsPoMapper;
import cn.edu.xmu.oomall.freight.mapper.po.ExpressPo;
import cn.edu.xmu.oomall.freight.mapper.po.ShopLogisticsPo;
import cn.edu.xmu.oomall.freight.mapper.po.WarehousePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;
import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

@Repository
@RefreshScope
public class ShopLogisticsDao {
    private final static Logger logger = LoggerFactory.getLogger(ShopLogisticsDao.class);

    private static final String KEY = "SL%d";

    private ShopLogisticsPoMapper shopLogisticsPoMapper;

    private LogisticsDao logisticsDao;

    @Autowired
    public ShopLogisticsDao(ShopLogisticsPoMapper shopLogisticsPoMapper, LogisticsDao logisticsDao) {
        this.shopLogisticsPoMapper = shopLogisticsPoMapper;
        this.logisticsDao = logisticsDao;
    }
    private void setBo(ShopLogistics bo){
        bo.setLogisticsDao(this.logisticsDao);
    }

    public PageDto<ShopLogistics> retrieveShopLogisticsByShopIdOrderByPriorityAsc(Long shopId, Integer page, Integer pageSize) {
        List<ShopLogistics> ret = new ArrayList<>();
        Pageable pageable = PageRequest.of(page-1, pageSize);
        Page<ShopLogisticsPo> shopLogisticsPos;
        shopLogisticsPos = this.shopLogisticsPoMapper.findByShopIdOrderByPriorityAsc(shopId, pageable);
        if (shopLogisticsPos.getSize() > 0) {
            ret = shopLogisticsPos.stream().map(po -> ShopLogistics.builder().id(po.getId())
                            .shopId(po.getShopId())
                            .logisticsId(po.getLogisticsId())
                            .secret(po.getSecret())
                            .invalid(po.getInvalid())
                            .priority(po.getPriority())
                            .creatorId(po.getCreatorId())
                            .creatorName(po.getCreatorName())
                            .gmtCreate(po.getGmtCreate())
                            .gmtModified(po.getGmtModified())
                            .modifierId(po.getModifierId())
                            .modifierName(po.getModifierName())
                            .build())
                    .collect(Collectors.toList());
            ret.forEach(this::setBo);
        }
        return new PageDto<>(ret, page, pageSize);
    }
    public ShopLogistics findShopLogisticsById(Long id) {
        Optional<ShopLogisticsPo> ret = shopLogisticsPoMapper.findById(id);
        if (ret.isEmpty() ){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺物流", id));
        }else{

            ShopLogisticsPo po = ret.get();
            ShopLogistics bo = ShopLogistics.builder().id(po.getId())
                    .shopId(po.getShopId())
                    .logisticsId(po.getLogisticsId())
                    .secret(po.getSecret())
                    .invalid(po.getInvalid())
                    .priority(po.getPriority())
                    .creatorId(po.getCreatorId())
                    .creatorName(po.getCreatorName())
                    .gmtCreate(po.getGmtCreate())
                    .gmtModified(po.getGmtModified())
                    .modifierId(po.getModifierId())
                    .modifierName(po.getModifierName())
                    .build();
            setBo(bo);
            return bo;
        }
    }

    public ShopLogistics insert(ShopLogistics shopLogistics, UserDto user) {
        ShopLogisticsPo shopLogisticsPo = ShopLogisticsPo.builder()
                .logisticsId(shopLogistics.getLogisticsId())
                .secret(shopLogistics.getSecret())
                .priority(shopLogistics.getPriority())
                .shopId(shopLogistics.getShopId())
                .build();
        if(null!=user){
            putGmtFields(shopLogisticsPo,"create");
            putUserFields(shopLogisticsPo,"creator",user);
        }
        shopLogisticsPo.setId(null);
        shopLogisticsPo.setInvalid(0L);
        logger.debug("shopLogisticsPo:{}",shopLogisticsPo);
        ShopLogisticsPo ret = this.shopLogisticsPoMapper.save(shopLogisticsPo);
        ShopLogistics bo = ShopLogistics.builder()
                .id(ret.getId())
                .logisticsId(ret.getLogisticsId())
                .shopId(ret.getShopId())
                .invalid(ret.getInvalid())
                .secret(ret.getSecret())
                .priority(ret.getPriority())
                .modifierName(ret.getModifierName())
                .modifierId(ret.getModifierId())
                .gmtModified(ret.getGmtModified())
                .gmtCreate(ret.getGmtCreate())
                .creatorName(ret.getCreatorName())
                .creatorId(ret.getCreatorId())
                .build();
        setBo(bo);
        return bo;
    }

    public String save(ShopLogistics shopLogistics, UserDto user) throws RuntimeException {

        Optional<ShopLogisticsPo> ret = shopLogisticsPoMapper.findById(shopLogistics.getId());
        if(!ret.isPresent()){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺物流", shopLogistics.getId()));
        }
        ShopLogisticsPo po = ret.get();
        if(shopLogistics.getSecret()!=null) po.setSecret(shopLogistics.getSecret());
        if(shopLogistics.getPriority()!=null) po.setPriority(shopLogistics.getPriority());
        if(shopLogistics.getInvalid()!=null) po.setInvalid(shopLogistics.getInvalid());
        if(null!=user){
            putGmtFields(po,"create");
            putUserFields(po,"creator",user);
        }
        ShopLogisticsPo newPo = this.shopLogisticsPoMapper.save(po);
        logger.debug("newPo:{}",newPo);
        return String.format(KEY,shopLogistics.getId());
    }

    public boolean existsByIdAndShopId(Long shopLogisticsId, Long shopId) {
        return shopLogisticsPoMapper.existsByIdAndShopId(shopLogisticsId, shopId);
    }

}
