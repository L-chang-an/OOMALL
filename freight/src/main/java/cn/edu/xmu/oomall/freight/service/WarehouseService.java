package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.controller.vo.WarehouseInfoVo;
import cn.edu.xmu.oomall.freight.dao.WarehouseDao;
import cn.edu.xmu.oomall.freight.dao.WarehouseRegionDao;
import cn.edu.xmu.oomall.freight.dao.bo.*;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import cn.edu.xmu.oomall.freight.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

@Service
public class WarehouseService {
    private static final Logger logger = LoggerFactory.getLogger(WarehouseService.class);
    private WarehouseDao warehouseDao;

    private WarehouseRegionDao warehouseRegionDao;

    private RegionDao regionDao;

    @Autowired
    public WarehouseService(WarehouseDao warehouseDao, WarehouseRegionDao warehouseRegionDao, RegionDao regionDao) {
        this.warehouseDao = warehouseDao;
        this.warehouseRegionDao = warehouseRegionDao;
        this.regionDao = regionDao;
    }

    @Transactional
    public PageDto<WarehouseDto> getWarehouses(Long shopId, Integer page, Integer pageSize) throws RuntimeException {
        PageDto<Warehouse> warehouses = warehouseDao.retrieveWarehouseByShopIdOrderByPriorityAsc(shopId,page,pageSize);
        List<WarehouseDto>  ret = warehouses.getList().stream().map(warehouse -> cloneObj(warehouse, WarehouseDto.class))
                .collect(Collectors.toList());
        IntStream.range(0, warehouses.getList().size())
                .forEach(i -> {
                    ret.get(i).setCreatedBy(new SimpleAdminUserDto(warehouses.getList().get(i).getCreatorId(), warehouses.getList().get(i).getCreatorName()));
                    ret.get(i).setModifiedBy(new SimpleAdminUserDto(warehouses.getList().get(i).getModifierId(), warehouses.getList().get(i).getModifierName()));
                    ret.get(i).setRegion(SimpleRegionDto.builder().id(warehouses.getList().get(i).getRegion().getId()).name(warehouses.getList().get(i).getRegion().getName()).build());
                });
        return new PageDto<>(ret, page, pageSize);
    }

    @Transactional
    public WarehouseDto createWarehouse(Long shopId, WarehouseInfoVo warehouseInfoVo, UserDto creator) throws RuntimeException {
        /*
         *  1. 检查新建仓库对象所在的 region 是否存在
         *  2. 不确定是否存在指定地区只有一个仓库的限制，没有检查仓库是否重复的步骤
         */
        InternalReturnObject<Region> itr = regionDao.getRegionById(warehouseInfoVo.getRegionId());
        if (null == itr)
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "地区", warehouseInfoVo.getRegionId()));
        Region region = itr.getData();
        Warehouse temp = Warehouse.builder().shopId(shopId).priority(warehouseInfoVo.getPriority()).regionId(warehouseInfoVo.getRegionId())
                .senderMobile(warehouseInfoVo.getSenderMobile()).senderName(warehouseInfoVo.getSenderName())
                .address(warehouseInfoVo.getAddress()).name(warehouseInfoVo.getName()).invalid(0L).build();
        Warehouse warehouse = warehouseDao.insert(temp, creator);
        return WarehouseDto.builder().id(warehouse.getId()).name(warehouse.getName()).address(warehouse.getAddress())
                .region(new SimpleRegionDto(warehouse.getRegionId(), region.getName())).senderName(warehouse.getSenderName()).senderMobile(warehouse.getSenderMobile())
                .invalid(warehouse.getInvalid()).priority(warehouse.getPriority()).createdBy(new SimpleAdminUserDto(warehouse.getCreatorId(), warehouse.getCreatorName()))
                .modifiedBy(new SimpleAdminUserDto(warehouse.getModifierId(), warehouse.getModifierName())).gmtModified(warehouse.getGmtModified()).gmtCreate(warehouse.getGmtCreate()).build();
    }

//    @Transactional
//    public PageDto<RegionWarehouseDto> getRegionWarehouses(Long shopId, Long regionId, LocalDateTime nowTime, Integer page, Integer pageSize) throws RuntimeException {
//        int fromIndex = 0, toIndex = 0;
//        List<Warehouse> warehouses = warehouseDao.retrieveWarehouseByShopIdOrderByPriorityAsc(shopId, 1, Integer.MAX_VALUE).getList();
//        Map<Long, Period> validWarehouseIds = warehouseRegionDao.retrieveValidWarehouseByRegionId(regionId, nowTime, warehouses);
//        List<RegionWarehouseDto> ret = warehouses.stream().filter(bo -> ((bo.getInvalid() == (byte)0)&&validWarehouseIds.keySet().contains(bo.getId())))
//                .map(bo -> RegionWarehouseDto.builder().warehouse(SimpleWarehouseDto.builder().id(bo.getId()).name(bo.getName()).invalid(bo.getInvalid()).build())
//                        .creator(SimpleAdminUserDto.builder().id(bo.getCreatorId()).userName(bo.getCreatorName()).build())
//                        .modifier(SimpleAdminUserDto.builder().id(bo.getModifierId()).userName(bo.getModifierName()).build())
//                        .beginTime(validWarehouseIds.get(bo.getId()).getBeginTime())
//                        .endTime(validWarehouseIds.get(bo.getId()).getEndTime())
//                        .gmtCreate(bo.getGmtCreate()).gmtModified(bo.getGmtModified())
//                        .status(bo.getInvalid()).build())
//                .collect(Collectors.toList());
//        int len = ret.size();
//        if (len < (page-1)*pageSize)
//            return new PageDto<>(new ArrayList<>(), page, pageSize);
//        fromIndex = (page-1)*pageSize;
//        toIndex = Math.min(len-(page-1)*pageSize, fromIndex+pageSize);
//        return new PageDto<>(ret.subList(fromIndex, toIndex), page, pageSize);
//    }

    @Transactional
    public PageDto<RegionWarehouseDto> getRegionWarehouses(Long shopId, Long regionId, LocalDateTime nowTime, Integer page, Integer pageSize) throws RuntimeException {
        List<Warehouse> warehouses = warehouseDao.retrieveWarehouseByShopIdOrderByPriorityAscDuiZhao(shopId, regionId, nowTime, page, pageSize).getList();
        List<RegionWarehouseDto> ret = warehouses.stream()
                .map(bo -> RegionWarehouseDto.builder().warehouse(SimpleWarehouseDto.builder().id(bo.getId()).name(bo.getName()).invalid(bo.getInvalid()).build())
                        .creator(SimpleAdminUserDto.builder().id(bo.getCreatorId()).userName(bo.getCreatorName()).build())
                        .modifier(SimpleAdminUserDto.builder().id(bo.getModifierId()).userName(bo.getModifierName()).build())
                        .beginTime(bo.getPeriod().getBeginTime())
                        .endTime(bo.getPeriod().getEndTime())
                        .gmtCreate(bo.getGmtCreate()).gmtModified(bo.getGmtModified())
                        .status(bo.getInvalid()).build())
                .collect(Collectors.toList());
        return new PageDto<>(ret, page, pageSize);
    }

    @Transactional
    public void updateWarehouse(Long shopId, Long warehouseId, WarehouseInfoVo warehouseInfoVo, UserDto creator) throws RuntimeException {
        /*
         * 如果有传入region id的话，那么检查实际的region是否存在
         */
        if (null != warehouseInfoVo.getRegionId() && null == regionDao.getRegionById(warehouseInfoVo.getRegionId()))
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "地区", warehouseInfoVo.getRegionId()));
        Warehouse temp = Warehouse.builder().shopId(shopId).id(warehouseId).priority(warehouseInfoVo.getPriority()).regionId(warehouseInfoVo.getRegionId())
                .senderMobile(warehouseInfoVo.getSenderMobile()).senderName(warehouseInfoVo.getSenderName())
                .address(warehouseInfoVo.getAddress()).name(warehouseInfoVo.getName()).build();
        try {
            warehouseDao.save(temp, creator);
        } catch (IllegalAccessException e){
            throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    @Transactional
    public void deleteWarehouse(Long shopId, Long warehouseId) throws RuntimeException {
        warehouseDao.delete(shopId, warehouseId);
    }

    @Transactional
    public void updateWarehouseStatus(Long shopId, Long warehouseId, Long invalid, UserDto modifier) throws RuntimeException {
        try {
            warehouseDao.save(Warehouse.builder().shopId(shopId).id(warehouseId).invalid(invalid).build(), modifier);
        } catch (IllegalAccessException e){
            throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    public PageDto<WarehouseRegionDto> getWarehouseRegions(Long shopId, Long warehouseId, LocalDateTime nowTime, Integer page, Integer pageSize) throws RuntimeException {
        warehouseDao.retrieveWarehouseByShopIdAndWarehouseId(shopId, warehouseId);
        List<SimpleWarehouseRegion> validRegions = warehouseRegionDao.retrieveValidRegionsByWarehouseId(warehouseId, nowTime, page, pageSize);
        List<Region> regions = validRegions.stream().map(i -> regionDao.getRegionById(i.getRegionId()).getData()).collect(Collectors.toList());
        List<WarehouseRegionDto> ret = validRegions.stream().map(i -> new WarehouseRegionDto(){
            {
                setBeginTime(i.getBeginTime());
                setEndTime(i.getEndTime());
            }
        }).collect(Collectors.toList());
        IntStream.range(0, ret.size())
                .forEach(i -> {
                    ret.get(i).setRegion(new SimpleRegionDto(regions.get(i).getId(), regions.get(i).getName()));
                    ret.get(i).setCreator(new SimpleAdminUserDto(regions.get(i).getCreatorId(), regions.get(i).getCreatorName()));
                    ret.get(i).setModifier(new SimpleAdminUserDto(regions.get(i).getModifierId(), regions.get(i).getModifierName()));
                    ret.get(i).setGmtCreate(regions.get(i).getGmtCreate());
                    ret.get(i).setGmtModified(regions.get(i).getGmtModified());
                });
        return new PageDto<>(ret, page, pageSize);
    }

    @Transactional
    public WarehouseRegion createWarehouseRegion(Long shopId, Long warehouseId, Long regionId, LocalDateTime beginTime, LocalDateTime endTime, UserDto user) throws RuntimeException {
        warehouseDao.retrieveWarehouseByShopIdAndWarehouseId(shopId, warehouseId);
        if (null == regionDao.getRegionById(regionId))
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "仓库地区配送-地区", regionId, shopId));
        WarehouseRegion wr = WarehouseRegion.builder().warehouseId(warehouseId).regionId(regionId).beginTime(beginTime).endTime(endTime).build();
        return warehouseRegionDao.insert(wr, user);
    }

    @Transactional
    public void deleteWarehouseRegion(Long shopId, Long warehouseId, Long regionId) throws RuntimeException {
        // 检查仓库是否归该商铺所有
//        if (null == warehouseDao.retrieveWarehouseByShopIdAndWarehouseId(shopId, warehouseId))
//            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(
//                    ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "仓库", warehouseId, shopId));
        warehouseRegionDao.deleteByWarehouseIdAndRegionId(warehouseId, regionId);
    }

    @Transactional
    public WarehouseRegion updateWarehouseRegion(Long shopId, Long warehouseId, Long regionId, LocalDateTime beginTime, LocalDateTime endTime, UserDto modifier) throws RuntimeException {
        logger.debug("updateWarehouseRegion: shopId {}, warehouseId {}", shopId, warehouseId);
        // 检查仓库是否归该商铺所有
//        if (null == warehouseDao.retrieveWarehouseByShopIdAndWarehouseId(shopId, warehouseId))
//            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(
//                    ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "仓库", warehouseId, shopId));
        WarehouseRegion wr = WarehouseRegion.builder().warehouseId(warehouseId).regionId(regionId).beginTime(beginTime).endTime(endTime).build();
        logger.debug("updateWarehouseRegion: wr {}", wr);
        return warehouseRegionDao.save(wr, modifier);
    }



}
