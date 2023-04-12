package cn.edu.xmu.oomall.freight.dao;


import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.dao.bo.Period;
import cn.edu.xmu.oomall.freight.dao.bo.SimpleWarehouseRegion;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseRegion;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import cn.edu.xmu.oomall.freight.mapper.WarehousePoMapper;
import cn.edu.xmu.oomall.freight.mapper.WarehouseRegionPoMapper;
import cn.edu.xmu.oomall.freight.mapper.po.WarehousePo;
import cn.edu.xmu.oomall.freight.mapper.po.WarehouseRegionPo;
import cn.edu.xmu.oomall.freight.service.dto.WarehouseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
@RefreshScope
public class WarehouseRegionDao {
    private final static Logger logger = LoggerFactory.getLogger(WarehouseRegionDao.class);

    private WarehouseRegionPoMapper warehouseRegionPoMapper;
    private RegionDao regionDao;
    @Autowired
    public WarehouseRegionDao(WarehouseRegionPoMapper warehouseRegionPoMapper, RegionDao regionDao) {
        this.warehouseRegionPoMapper = warehouseRegionPoMapper;
        this.regionDao = regionDao;
    }

    public Map<Long, Period> retrieveValidWarehouseByRegionId(Long regionId, LocalDateTime nowTime, List<Warehouse> warehouses) {
        /*
         * 以集合形式返回该地区当前时间可以配送的所有仓库id
         */
        Map<Long, Period> ret = warehouseRegionPoMapper.findByRegionIdAndBeginTimeLessThanEqualAndEndTimeGreaterThanEqualAndWarehouseIdIn(regionId,
                        nowTime, nowTime, warehouses.stream().map(warehouse -> warehouse.getId()).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(WarehouseRegionPo::getWarehouseId, WarehouseRegionPo::createPeriod));
        return ret;
    }

    public List<SimpleWarehouseRegion> retrieveValidRegionsByWarehouseId(Long warehouseId, LocalDateTime nowTime, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page-1, pageSize);
        List<SimpleWarehouseRegion> ret = warehouseRegionPoMapper.findByWarehouseIdAndBeginTimeLessThanEqualAndEndTimeGreaterThanEqual(warehouseId,
                        nowTime, nowTime, pageable)
                .stream().map(wrpo -> new SimpleWarehouseRegion(wrpo.getRegionId(), wrpo.getBeginTime(), wrpo.getEndTime())).collect(Collectors.toList());
        return ret;
    }

    public WarehouseRegion findWarehouseRegionByWarehouseIdAndRegionId(Long warehouseId, Long regionId) {
        WarehouseRegionPo po = warehouseRegionPoMapper.findByWarehouseIdAndRegionId(warehouseId, regionId);
        if (null == po)
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "地区配送规则", -1));
        return cloneObj(po, WarehouseRegion.class);
    }

    public void deleteByWarehouseIdAndRegionId(Long warehouseId, Long regionId) {
        WarehouseRegion bo = findWarehouseRegionByWarehouseIdAndRegionId(warehouseId, regionId);
        warehouseRegionPoMapper.deleteById(bo.getId());
    }

    public void deleteByWarehouseId(Long warehouseId) {
        warehouseRegionPoMapper.deleteByWarehouseId(warehouseId);
    }

    public WarehouseRegion save(WarehouseRegion warehouseRegion, UserDto modifier) {
        WarehouseRegionPo po = warehouseRegionPoMapper.findByWarehouseIdAndRegionId(warehouseRegion.getWarehouseId(), warehouseRegion.getRegionId());
        if (null == po)
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "地区配送规则", -1));
        if (null != warehouseRegion.getBeginTime())
            po.setBeginTime(warehouseRegion.getBeginTime());
        if (null != warehouseRegion.getEndTime())
            po.setEndTime(warehouseRegion.getEndTime());
        putGmtFields(po,"modified");
        putUserFields(po,"modifier",modifier);
        logger.debug("save: Po {}", po);
        WarehouseRegionPo savePo = warehouseRegionPoMapper.save(po);
        return cloneObj(savePo, WarehouseRegion.class);
    }

    public WarehouseRegion insert(WarehouseRegion warehouseRegion, UserDto user) {
        // 不确定是否要判断仓库配送地区是否已经存在，且ReturnNo中没有合适的错误码返回，暂用物流重复错误码代替
        if (null != warehouseRegionPoMapper.findByWarehouseIdAndRegionId(warehouseRegion.getWarehouseId(), warehouseRegion.getRegionId()))
            throw new BusinessException(ReturnNo.FREIGHT_LOGISTIC_EXIST);
        WarehouseRegionPo warehouseRegionPo = cloneObj(warehouseRegion, WarehouseRegionPo.class);
        putGmtFields(warehouseRegionPo,"create");
        putUserFields(warehouseRegionPo,"creator",user);
        WarehouseRegionPo savePo = warehouseRegionPoMapper.save(warehouseRegionPo);
        return cloneObj(savePo, WarehouseRegion.class);
    }
}
