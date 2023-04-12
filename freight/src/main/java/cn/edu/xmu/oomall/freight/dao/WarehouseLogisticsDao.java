package cn.edu.xmu.oomall.freight.dao;


import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseLogistics;
import cn.edu.xmu.oomall.freight.mapper.WarehouseLogisticsPoMapper;
import cn.edu.xmu.oomall.freight.mapper.po.WarehouseLogisticsPo;
import cn.edu.xmu.oomall.freight.service.dto.SimpleAdminUserDto;
import cn.edu.xmu.oomall.freight.service.dto.TotalPageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;


@Repository
@RefreshScope
public class WarehouseLogisticsDao {
    private final static Logger logger = LoggerFactory.getLogger(WarehouseLogisticsDao.class);

    private void setBo(WarehouseLogistics bo){
        bo.setShopLogisticsDao(this.shopLogisticsDao);
    }
    private WarehouseLogisticsPoMapper warehouseLogisticsPoMapper;
    private ShopLogisticsDao shopLogisticsDao;

    @Autowired
    public WarehouseLogisticsDao(WarehouseLogisticsPoMapper warehouseLogisticsPoMapper, ShopLogisticsDao shopLogisticsDao) {
        this.warehouseLogisticsPoMapper = warehouseLogisticsPoMapper;
        this.shopLogisticsDao = shopLogisticsDao;
    }

    public boolean existWarehouseLogistics(Long warehouseId, Long shopLogisticsId) {
        return warehouseLogisticsPoMapper.existsByWarehouseIdAndShopLogisticsId(warehouseId, shopLogisticsId);
    }

    public TotalPageDto<WarehouseLogistics> retrieveWarehouseLogisticsByWarehouseId(Long wrehouseId, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page-1, pageSize);
        logger.debug("retrieveWarehouseLogisticsByWarehouseId: page={}, pageSize={}", page, pageSize);
        List<WarehouseLogisticsPo> temp = warehouseLogisticsPoMapper.findByWarehouseId(wrehouseId, pageable);
        List<WarehouseLogistics> ret = temp.stream().map(po -> WarehouseLogistics.builder().warehouseId(po.getWarehouseId())
                .beginTime(po.getBeginTime()).endTime(po.getEndTime()).invalid(po.getInvalid())
                .shopLogisticsId(po.getShopLogisticsId()).creator(new SimpleAdminUserDto(po.getCreatorId(), po.getCreatorName()))
                .gmtCreate(po.getGmtCreate()).gmtModified(po.getGmtModified()).modifier(new SimpleAdminUserDto(po.getModifierId(), po.getModifierName()))
                .build()).collect(Collectors.toList());
        ret.forEach(this::setBo);
        Integer total = warehouseLogisticsPoMapper.countByWarehouseId(wrehouseId).intValue();
        return new TotalPageDto<>(ret, page, pageSize, total, total%page == 0 ? total/page : total/page+1);
    }

    public void insert(WarehouseLogistics warehouseLogistics, UserDto creator) {
        WarehouseLogisticsPo warehouseLogisticsPo = WarehouseLogisticsPo.builder().warehouseId(warehouseLogistics.getWarehouseId())
                .shopLogisticsId(warehouseLogistics.getShopLogisticsId()).beginTime(warehouseLogistics.getBeginTime()).endTime(warehouseLogistics.getEndTime())
                .invalid(0L).build();
        putGmtFields(warehouseLogisticsPo,"create");
        putUserFields(warehouseLogisticsPo,"creator",creator);
        warehouseLogisticsPoMapper.save(warehouseLogisticsPo);
    }

    public void save(WarehouseLogistics warehouseLogistics, UserDto modifier) {
        WarehouseLogisticsPo warehouseLogisticsPo = warehouseLogisticsPoMapper.findByWarehouseIdAndShopLogisticsId(warehouseLogistics.getWarehouseId(), warehouseLogistics.getShopLogisticsId());
        if (null != warehouseLogistics.getBeginTime())
            warehouseLogisticsPo.setBeginTime(warehouseLogistics.getBeginTime());
        if (null != warehouseLogistics.getEndTime())
            warehouseLogisticsPo.setEndTime(warehouseLogistics.getEndTime());
        putGmtFields(warehouseLogisticsPo,"modified");
        putUserFields(warehouseLogisticsPo,"modifier",modifier);
        warehouseLogisticsPoMapper.save(warehouseLogisticsPo);
    }

    public void delete(WarehouseLogistics warehouseLogistics) {
        warehouseLogisticsPoMapper.deleteByWarehouseIdAndShopLogisticsId(warehouseLogistics.getWarehouseId(), warehouseLogistics.getShopLogisticsId());
    }

    public void deleteByWarehouseId(Long warehouseId) {
        warehouseLogisticsPoMapper.deleteByWarehouseId(warehouseId);
    }

}
