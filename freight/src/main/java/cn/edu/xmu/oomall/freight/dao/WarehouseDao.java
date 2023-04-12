package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.aop.checkor.warehouses.WarehouseExist;
import cn.edu.xmu.oomall.freight.dao.bo.Period;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import cn.edu.xmu.oomall.freight.mapper.WarehousePoMapper;
import cn.edu.xmu.oomall.freight.mapper.po.WarehousePeriodPo;
import cn.edu.xmu.oomall.freight.mapper.po.WarehousePo;
import cn.edu.xmu.oomall.freight.mapper.po.WarehouseRegionPo;
import cn.edu.xmu.oomall.freight.service.dto.WarehouseDto;
import net.bytebuddy.asm.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;


@Repository
@RefreshScope
public class WarehouseDao {
    private final static Logger logger = LoggerFactory.getLogger(WarehouseDao.class);
    private final static String KEY = "W%dS%d";
    @Value("${oomall.category.timeout}")
    private int timeout;
    private RedisUtil redisUtil;
    private WarehousePoMapper warehousePoMapper;
    private RegionDao regionDao;
    private WarehouseLogisticsDao warehouseLogisticsDao;
    private WarehouseRegionDao warehouseRegionDao;
    @Autowired
    public WarehouseDao(WarehouseLogisticsDao warehouseLogisticsDao, WarehouseRegionDao warehouseRegionDao, WarehousePoMapper warehousePoMapper, RegionDao regionDao, RedisUtil redisUtil) {
        this.warehousePoMapper = warehousePoMapper;
        this.warehouseLogisticsDao = warehouseLogisticsDao;
        this.warehouseRegionDao = warehouseRegionDao;
        this.regionDao = regionDao;
        this.redisUtil = redisUtil;
    }

    private void setBo(Warehouse bo){
        bo.setRegionDao(this.regionDao);
    }

    private Warehouse getBo(WarehousePo po, Optional<String> redisKey){
        Warehouse bo = Warehouse.builder().id(po.getId()).address(po.getAddress()).shopId(po.getShopId()).name(po.getName())
                .creatorName(po.getCreatorName()).creatorId(po.getCreatorId()).gmtCreate(po.getGmtCreate()).gmtModified(po.getGmtModified()).senderMobile(po.getSenderMobile()).regionId(po.getRegionId())
                .senderName(po.getSenderName()).priority(po.getPriority()).invalid(po.getInvalid()).modifierName(po.getModifierName())
                .modifierId(po.getModifierId()).build();
        this.setBo(bo);
        redisKey.ifPresent(key -> redisUtil.set(key, bo, timeout));
        return bo;
    }

    public void save(Warehouse warehouse, UserDto modifier) throws IllegalAccessException {
        // 检查修改的仓库是否存在 and 是否属于该商户
        Optional<WarehousePo> po = warehousePoMapper.findByShopIdAndId(warehouse.getShopId(), warehouse.getId());
        if (!po.isPresent())
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺仓库", warehouse.getId()));
        // 将warehouse中非空的字段注入要修改的po中
        WarehousePo po2 = cloneObj(warehouse, WarehousePo.class);
        Field fields[] = po2.getClass().getDeclaredFields();
        for (Field field: fields) {
            field.setAccessible(true);
            Object filedValue = null;
            filedValue = field.get(po2);
            if (null != filedValue) {
                field.set(po.get(), filedValue);
            }
        }
        putGmtFields(po.get(),"modified");
        putUserFields(po.get(),"modifier",modifier);
        warehousePoMapper.save(po.get());
        redisUtil.del(String.format(KEY, warehouse.getId(), warehouse.getShopId()));
    }
    
    public Warehouse insert(Warehouse warehouse, UserDto user) {
        WarehousePo warehousePo = cloneObj(warehouse, WarehousePo.class);
        putGmtFields(warehousePo,"create");
        putUserFields(warehousePo,"creator",user);
        WarehousePo savePo = warehousePoMapper.save(warehousePo);
        return cloneObj(savePo, Warehouse.class);
    }

    public PageDto<Warehouse> retrieveWarehouseByShopIdOrderByPriorityAsc(Long shopId, Integer page, Integer pageSize) {
        List<Warehouse> ret = new ArrayList<>();
        Pageable pageable = PageRequest.of(page-1, pageSize);
        Page<WarehousePo> warehousePos;
        warehousePos = this.warehousePoMapper.findByShopIdOrderByPriorityAsc(shopId, pageable);
        if (warehousePos.stream().findAny().isPresent()) {
            ret = warehousePos.stream().map(po -> Warehouse.builder().id(po.getId()).address(po.getAddress()).shopId(po.getShopId()).name(po.getName())
                    .creatorName(po.getCreatorName()).creatorId(po.getCreatorId()).gmtCreate(po.getGmtCreate()).gmtModified(po.getGmtModified()).senderMobile(po.getSenderMobile()).regionId(po.getRegionId())
                    .senderName(po.getSenderName()).priority(po.getPriority()).invalid(po.getInvalid()).modifierName(po.getModifierName())
                    .modifierId(po.getModifierId()).build()).collect(Collectors.toList());
            ret.forEach(this::setBo);
        }
        return new PageDto<>(ret, page, pageSize);
    }

    public PageDto<Warehouse> retrieveWarehouseByShopIdOrderByPriorityAscDuiZhao(Long shopId, Long regionId, LocalDateTime nowTime, Integer page, Integer pageSize) {
        List<Warehouse> ret = new ArrayList<>();
        Pageable pageable = PageRequest.of(page-1, pageSize);
        Page<WarehousePeriodPo> warehousePos;
        warehousePos = this.warehousePoMapper.findRegionWarehouses(shopId, regionId, nowTime, pageable);
        if (warehousePos.stream().findAny().isPresent()) {
            ret = warehousePos.stream().map(po -> Warehouse.builder().period(new Period(po.getBeginTime(), po.getEndTime())).id(po.getWarehousePo().getId()).address(po.getWarehousePo().getAddress()).shopId(po.getWarehousePo().getShopId()).name(po.getWarehousePo().getName())
                    .creatorName(po.getWarehousePo().getCreatorName()).creatorId(po.getWarehousePo().getCreatorId()).gmtCreate(po.getWarehousePo().getGmtCreate()).gmtModified(po.getWarehousePo().getGmtModified()).senderMobile(po.getWarehousePo().getSenderMobile()).regionId(po.getWarehousePo().getRegionId())
                    .senderName(po.getWarehousePo().getSenderName()).priority(po.getWarehousePo().getPriority()).invalid(po.getWarehousePo().getInvalid()).modifierName(po.getWarehousePo().getModifierName())
                    .modifierId(po.getWarehousePo().getModifierId()).build()).collect(Collectors.toList());
            ret.forEach(this::setBo);
        }
        return new PageDto<>(ret, page, pageSize);
    }

    public Warehouse retrieveWarehouseByShopIdAndWarehouseId(Long shopId, Long warehouseId) {
        String key = String.format(KEY, shopId, warehouseId);
        if (redisUtil.hasKey(key)){
            Warehouse bo = (Warehouse) redisUtil.get(key);
            setBo(bo);
            return bo;
        }
        Optional<WarehousePo> ret = warehousePoMapper.findByShopIdAndId(shopId, warehouseId);
        logger.debug("retrieveWarehouseByShopIdAndWarehouseId: ret={}", ret);
        if (ret.isPresent()) {
            return getBo(ret.get(), Optional.of(key));
        }else {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺仓库", warehouseId));
        }
    }

    public void delete(Long shopId, Long warehouseId) {
        // 检查修改的仓库是否存在 and 是否属于该商户
        Optional<WarehousePo> ret = warehousePoMapper.findByShopIdAndId(shopId, warehouseId);
        if (ret.isPresent()) {
            warehousePoMapper.deleteById(warehouseId);
            warehouseRegionDao.deleteByWarehouseId(warehouseId);
            warehouseLogisticsDao.deleteByWarehouseId(warehouseId);
            redisUtil.del(String.format(KEY, warehouseId, shopId));
        }else {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺仓库", warehouseId));
        }
    }


}
