package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.dao.bo.Undeliverable;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseLogistics;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import cn.edu.xmu.oomall.freight.mapper.UndeliverablePoMapper;
import cn.edu.xmu.oomall.freight.mapper.WarehouseLogisticsPoMapper;
import cn.edu.xmu.oomall.freight.mapper.po.UndeliverablePo;
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
public class UndeliverableDao {

    private final static Logger logger = LoggerFactory.getLogger(UndeliverableDao.class);

    private void setBo(Undeliverable bo){
        bo.setRegionDao(this.regionDao);
        bo.setShopLogisticsDao(this.shopLogisticsDao);
    }
    private UndeliverablePoMapper undeliverablePoMapper;
    private ShopLogisticsDao shopLogisticsDao;
    private RegionDao regionDao;

    @Autowired
    public UndeliverableDao(UndeliverablePoMapper undeliverablePoMapper, ShopLogisticsDao shopLogisticsDao, RegionDao regionDao) {
        this.undeliverablePoMapper = undeliverablePoMapper;
        this.shopLogisticsDao = shopLogisticsDao;
        this.regionDao = regionDao;
    }

    public boolean existsByRegionIdAndShopLogisticsId(Long regionId, Long shopLogisticsId) {
        return undeliverablePoMapper.existsByRegionIdAndShopLogisticsId(regionId, shopLogisticsId);
    }

    public void insert(Undeliverable undeliverable, UserDto creator) {
        UndeliverablePo po = cloneObj(undeliverable, UndeliverablePo.class);
        putGmtFields(po,"create");
        putUserFields(po,"creator",creator);
        logger.debug("insert: before save={}", po);
        UndeliverablePo savepo = undeliverablePoMapper.save(po);
        logger.debug("insert: after save={}", savepo);
    }

    public void update(Undeliverable undeliverable, UserDto modifier) {
        UndeliverablePo po = undeliverablePoMapper.findByRegionIdAndShopLogisticsId(undeliverable.getRegionId(), undeliverable.getShopLogisticsId());
        if (null != undeliverable.getBeginTime())
            po.setBeginTime(undeliverable.getBeginTime());
        if (null != undeliverable.getEndTime())
            po.setEndTime(undeliverable.getEndTime());
        putGmtFields(po,"modified");
        putUserFields(po,"modifier",modifier);
        logger.debug("update: before save={}", po);
        UndeliverablePo savepo = undeliverablePoMapper.save(po);
        logger.debug("update: after save={}", savepo);
    }

    public void delete(Undeliverable undeliverable) {
        undeliverablePoMapper.deleteByRegionIdAndShopLogisticsId(undeliverable.getRegionId(), undeliverable.getShopLogisticsId());
    }

    public TotalPageDto<Undeliverable> retrieveUndeliverableByShopLogisticsId(Long shopLogisticsId, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page-1, pageSize);
        logger.debug("retrieveWarehouseLogisticsByWarehouseId: page={}, pageSize={}", page, pageSize);
        List<UndeliverablePo> temp = undeliverablePoMapper.findByShopLogisticsId(shopLogisticsId, pageable);
        List<Undeliverable> ret = temp.stream().map(po -> Undeliverable.builder().beginTime(po.getBeginTime()).endTime(po.getEndTime())
                .shopLogisticsId(po.getShopLogisticsId()).regionId(po.getRegionId())
                .id(po.getId()).gmtCreate(po.getGmtCreate()).gmtModified(po.getGmtModified())
                .creatorName(po.getCreatorName()).modifierName(po.getModifierName())
                .creatorId(po.getCreatorId()).modifierId(po.getModifierId())
                .build()).collect(Collectors.toList());
        ret.forEach(this::setBo);
        Integer total = undeliverablePoMapper.countByShopLogisticsId(shopLogisticsId).intValue();
        return new TotalPageDto<>(ret, page, pageSize, total, total%page == 0 ? total/page : total/page+1);
    }
}
