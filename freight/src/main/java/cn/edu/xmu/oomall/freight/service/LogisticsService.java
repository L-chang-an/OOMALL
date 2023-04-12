package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.controller.vo.InfoVo;
import cn.edu.xmu.oomall.freight.controller.vo.ShopLogisticsVo;
import cn.edu.xmu.oomall.freight.dao.*;
import cn.edu.xmu.oomall.freight.dao.bo.*;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import cn.edu.xmu.oomall.freight.service.dto.*;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.MAX_RETURN;
import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;


@Service
public class LogisticsService {
    private static final Logger logger = LoggerFactory.getLogger(LogisticsService.class);
    private ShopLogisticsDao shopLogisticsDao;
    private WarehouseLogisticsDao warehouseLogisticsDao;
    private LogisticsDao logisticsDao;
    private ExpressDao expressDao;
    private WarehouseDao warehouseDao;
    private RegionDao regionDao;
    private UndeliverableDao undeliverableDao;

    public LogisticsService(UndeliverableDao undeliverableDao, ShopLogisticsDao shopLogisticsDao, WarehouseLogisticsDao warehouseLogisticsDao, LogisticsDao logisticsDao, ExpressDao expressDao, WarehouseDao warehouseDao, RegionDao regionDao) {
        this.undeliverableDao = undeliverableDao;
        this.shopLogisticsDao = shopLogisticsDao;
        this.warehouseLogisticsDao = warehouseLogisticsDao;
        this.logisticsDao = logisticsDao;
        this.expressDao = expressDao;
        this.warehouseDao = warehouseDao;
        this.regionDao = regionDao;
    }

    public PageDto<ShopLogisticsDto> retrieveShopLogisticsByShopId(Long shopId, Integer page, Integer pageSize) {

        PageDto<ShopLogistics> shopLogistics = shopLogisticsDao.retrieveShopLogisticsByShopIdOrderByPriorityAsc(shopId,page,pageSize);
        List<ShopLogisticsDto> ret = shopLogistics.getList().stream().map(bo->ShopLogisticsDto.builder()
                        .id(bo.getId())
                        .logistics(LogisticsDto.builder()
                                .id(bo.getLogistics().getId())
                                .name(bo.getLogistics().getName())
                                .build())
                        .invalid(bo.getInvalid())
                        .secret(bo.getSecret())
                        .priority(bo.getPriority())
                        .gmtCreate(bo.getGmtCreate())
                        .gmtModified(bo.getGmtModified())
                        .createdBy(SimpleAdminUserDto.builder()
                                .id(bo.getCreatorId())
                                .userName(bo.getCreatorName())
                                .build())
                        .modifiedBy(SimpleAdminUserDto.builder()
                                .id(bo.getModifierId())
                                .userName(bo.getModifierName())
                                .build())
                        .build()
                )
                .collect(Collectors.toList());
        return new PageDto<>(ret, page, pageSize);
    }

    public List<LogisticsDto> retrieveLogisticsByBillCode(String billCode) {
        List<Logistics> logisticsList;
        if(billCode==null){
            logisticsList = logisticsDao.retrieveLogistic();
        }
        else{
            Express express = expressDao.findExpressByBillCode(billCode);
            logisticsList = new ArrayList<>(1);
            logisticsList.add(express.getShopLogistics().getLogistics());
        }
        List<LogisticsDto> ret = logisticsList.stream().map(bo->LogisticsDto.builder()
                .id(bo.getId())
                .name(bo.getName())
                .build()).collect(Collectors.toList());
        return ret;
    }

    public ShopLogisticsDto createShoplogistics(ShopLogisticsVo shopLogisticsVo, UserDto user) {
        try{
            if (null == logisticsDao.findLogisticsById(shopLogisticsVo.getLogisticsId())) {
                throw new BusinessException(ReturnNo.FIELD_NOTVALID, String.format
                        (ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "平台物流", shopLogisticsVo.getLogisticsId()));
            }
        }
        catch (BusinessException e){
            throw new BusinessException(ReturnNo.FIELD_NOTVALID, String.format
                    (ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "平台物流", shopLogisticsVo.getLogisticsId()));
        }
        retrieveShopLogisticsByShopId(shopLogisticsVo.getShopId(),1,MAX_RETURN).getList().forEach(
                bo->{
                    if(bo.getLogistics().getId().equals(shopLogisticsVo.getLogisticsId())){
                        throw new BusinessException(ReturnNo.ORDER_CHANGENOTALLOW,
                                "物流合作已经存在，无法重复新增");
                    }
                }
        );
        ShopLogistics shopLogistics = ShopLogistics.builder()
                .shopId(shopLogisticsVo.getShopId())
                .logisticsId(shopLogisticsVo.getLogisticsId())
                .secret(shopLogisticsVo.getSecret())
                .priority(shopLogisticsVo.getPriority())
                .build();
        ShopLogistics bo = shopLogisticsDao.insert(shopLogistics, user);
        return ShopLogisticsDto.builder()
                .id(bo.getId())
                .logistics(LogisticsDto.builder()
                        .id(bo.getLogistics().getId())
                        .name(bo.getLogistics().getName())
                        .build())
                .invalid(bo.getInvalid())
                .secret(bo.getSecret())
                .priority(bo.getPriority())
                .gmtCreate(bo.getGmtCreate())
                .gmtModified(bo.getGmtModified())
                .createdBy(SimpleAdminUserDto.builder()
                        .id(bo.getCreatorId())
                        .userName(bo.getCreatorName())
                        .build())
                .modifiedBy(SimpleAdminUserDto.builder()
                        .id(bo.getModifierId())
                        .userName(bo.getModifierName())
                        .build())
                .build();
    }

    public void createWarehouseLogistics(Long shopId, Long warehouseId, Long shopLogisticsId, InfoVo infoVo, UserDto user) {
        // 判断仓库是否属于商户
        warehouseDao.retrieveWarehouseByShopIdAndWarehouseId(shopId, warehouseId);
        // 判断商铺物流是否存在|商铺物流是否属于该商铺
        if (!shopLogisticsDao.existsByIdAndShopId(shopLogisticsId, shopId)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,
                    String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺物流", shopLogisticsId));
        }
        // 判断该仓库是否已有指定的商铺物流
        // 没找到仓库物流已存在的错误码，用商铺的代替
        if (warehouseLogisticsDao.existWarehouseLogistics(warehouseId, shopLogisticsId)) {
            throw new BusinessException(ReturnNo.FREIGHT_LOGISTIC_EXIST,
                    String.format(ReturnNo.FREIGHT_LOGISTIC_EXIST.getMessage(), shopLogisticsId));
        }
        WarehouseLogistics warehouseLogistics = WarehouseLogistics.builder().warehouseId(warehouseId).shopLogisticsId(shopLogisticsId)
                        .beginTime(infoVo.getBeginTime()).endTime(infoVo.getEndTime()).build();
        warehouseLogisticsDao.insert(warehouseLogistics, user);
    }

    public void updateWarehouseLogistics(Long shopId, Long warehouseId, Long shopLogisticsId, InfoVo infoVo, UserDto modifier) {
        // 判断仓库是否属于商户
        warehouseDao.retrieveWarehouseByShopIdAndWarehouseId(shopId, warehouseId);
        // 判断商铺物流是否存在|商铺物流是否属于该商铺
        if (!shopLogisticsDao.existsByIdAndShopId(shopLogisticsId, shopId)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,
                    String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺物流", shopLogisticsId));
        }
        // api上要求抛出504错误，但是core中没有，暂用not exist代替
        if (!warehouseLogisticsDao.existWarehouseLogistics(warehouseId, shopLogisticsId)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,
                    String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "仓库物流", -1));
        }
        WarehouseLogistics warehouseLogistics = WarehouseLogistics.builder().warehouseId(warehouseId).shopLogisticsId(shopLogisticsId)
                .beginTime(infoVo.getBeginTime()).endTime(infoVo.getEndTime()).build();
        warehouseLogisticsDao.save(warehouseLogistics, modifier);
    }

    public void deleteWarehouseLogistics(Long shopId, Long warehouseId, Long shopLogisticsId) {
        // 判断仓库是否属于商户
        warehouseDao.retrieveWarehouseByShopIdAndWarehouseId(shopId, warehouseId);
        // 判断仓库物流是否存在
        if (!warehouseLogisticsDao.existWarehouseLogistics(warehouseId, shopLogisticsId)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,
                    String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "仓库物流", -1));
        }
        WarehouseLogistics warehouseLogistics = WarehouseLogistics.builder().warehouseId(warehouseId).shopLogisticsId(shopLogisticsId)
                .build();
        warehouseLogisticsDao.delete(warehouseLogistics);
    }

    public TotalPageDto<WarehouseLogisticsDto> getAllWarehouseLogistics(Long shopId, Long warehouseId, Integer page, Integer pageSize) {
        // 判断仓库是否属于商户
        warehouseDao.retrieveWarehouseByShopIdAndWarehouseId(shopId, warehouseId);
        TotalPageDto<WarehouseLogistics> totalPageDto = warehouseLogisticsDao.retrieveWarehouseLogisticsByWarehouseId(warehouseId, page, pageSize);
        logger.debug("getAllWarehouseLogistics： {}", totalPageDto.getList().get(0).getShopLogisticsDao().findShopLogisticsById(4L));
        List<WarehouseLogisticsDto> list = totalPageDto.getList().stream().map(
                bo -> WarehouseLogisticsDto.builder()
                        .shopLogistics(ShopLogisticsDto.builder()
                        .id(bo.getShopLogistics().getId())
                        .logistics(LogisticsDto.builder()
                                .id(bo.getShopLogistics().getLogistics().getId())
                                .name(bo.getShopLogistics().getLogistics().getName())
                                .build())
                        .invalid(bo.getShopLogistics().getInvalid())
                        .secret(bo.getShopLogistics().getSecret())
                        .priority(bo.getShopLogistics().getPriority())
                        .gmtCreate(bo.getShopLogistics().getGmtCreate())
                        .gmtModified(bo.getShopLogistics().getGmtModified())
                        .createdBy(SimpleAdminUserDto.builder()
                                .id(bo.getShopLogistics().getCreatorId())
                                .userName(bo.getShopLogistics().getCreatorName())
                                .build())
                        .modifiedBy(SimpleAdminUserDto.builder()
                                .id(bo.getShopLogistics().getModifierId())
                                .userName(bo.getShopLogistics().getModifierName())
                                .build())
                        .build()).beginTime(bo.getBeginTime()).endTime(bo.getEndTime())
                        .status(bo.getInvalid()).creator(bo.getCreator()).modifier(bo.getModifier())
                        .gmtModified(bo.getGmtModified()).gmtCreate(bo.getGmtCreate()).build()
        ).collect(Collectors.toList());
        return new TotalPageDto<>(list, totalPageDto.getPage(), totalPageDto.getPageSize()
                , totalPageDto.getTotal(), totalPageDto.getPages());
    }

    public void createUndeliverable(Long shopId, Long shopLogisticsId, Long regionId, InfoVo infoVo, UserDto creator) {
        // 判断商铺物流是否存在|商铺物流是否属于该商铺
        logger.debug("createUndeliverable: shopLogistics {}, {}", shopLogisticsId, shopId);
        if (!shopLogisticsDao.existsByIdAndShopId(shopLogisticsId, shopId)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,
                    String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺物流", shopLogisticsId));
        }
        logger.debug("createUndeliverable: shopLogistics exist");
        // 检查地区是否存在
        InternalReturnObject<Region> itr = regionDao.getRegionById(regionId);
        if (null == itr)
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "地区", regionId));
        logger.debug("createUndeliverable: region exist");
        // 检查商铺物流 与 指定地区之间是否已经存在不可达信息, 找不到错误码，用商铺物流已存在代替
        if (undeliverableDao.existsByRegionIdAndShopLogisticsId(regionId, shopLogisticsId))
            throw new BusinessException(ReturnNo.FREIGHT_LOGISTIC_EXIST);
        logger.debug("createUndeliverable: sl-region not exist");
        undeliverableDao.insert(Undeliverable.builder().beginTime(infoVo.getBeginTime()).endTime(infoVo.getEndTime())
                .shopLogisticsId(shopLogisticsId).regionId(regionId).build(), creator);
    }

    public void updateUndeliverable(Long shopId, Long shopLogisticsId, Long regionId, InfoVo infoVo, UserDto modifier) {
        // 判断商铺物流是否存在|商铺物流是否属于该商铺
        logger.debug("createUndeliverable: shopLogistics {}, {}", shopLogisticsId, shopId);
        if (!shopLogisticsDao.existsByIdAndShopId(shopLogisticsId, shopId)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,
                    String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺物流", shopLogisticsId));
        }
        logger.debug("createUndeliverable: shopLogistics exist");
        // 检查地区是否存在
        InternalReturnObject<Region> itr = regionDao.getRegionById(regionId);
        if (null == itr)
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "地区", regionId));
        logger.debug("createUndeliverable: region exist");
        // 检查商铺物流 与 指定地区之间是否存在不可达信息
        if (!undeliverableDao.existsByRegionIdAndShopLogisticsId(regionId, shopLogisticsId))
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,
                    String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺物流不可达信息", -1));
        logger.debug("createUndeliverable: sl-region not exist");
        undeliverableDao.update(Undeliverable.builder().beginTime(infoVo.getBeginTime()).endTime(infoVo.getEndTime())
                .shopLogisticsId(shopLogisticsId).regionId(regionId).build(), modifier);
    }

    public void deleteUndeliverable(Long shopId, Long shopLogisticsId, Long regionId) {
        // 判断商铺物流是否存在|商铺物流是否属于该商铺
        if (!shopLogisticsDao.existsByIdAndShopId(shopLogisticsId, shopId)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,
                    String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺物流", shopLogisticsId));
        }
        // 检查地区是否存在
        InternalReturnObject<Region> itr = regionDao.getRegionById(regionId);
        if (null == itr)
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "地区", regionId));
        // 检查商铺物流 与 指定地区之间是否存在不可达信息
        if (!undeliverableDao.existsByRegionIdAndShopLogisticsId(regionId, shopLogisticsId))
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,
                    String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺物流不可达信息", -1));
        undeliverableDao.delete(Undeliverable.builder().shopLogisticsId(shopLogisticsId).regionId(regionId).build());
    }
    public TotalPageDto<UndeliverableDto> getAllShopLogisticsUndeliverable(Long shopId, Long shopLogisticsId, Integer page, Integer pageSize) {
        // 判断商铺物流是否存在|商铺物流是否属于该商铺
        if (!shopLogisticsDao.existsByIdAndShopId(shopLogisticsId, shopId)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,
                    String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺物流", shopLogisticsId));
        }
        TotalPageDto<Undeliverable> totalPageDto = undeliverableDao.retrieveUndeliverableByShopLogisticsId(shopLogisticsId, page, pageSize);
        List<UndeliverableDto> list = totalPageDto.getList().stream().map(bo -> UndeliverableDto.builder().id(bo.getId())
                .region(SimpleRegionDto.builder().id(bo.getRegionId()).name(bo.getRegion().getName()).build())
                .beginTime(bo.getBeginTime()).endTime(bo.getEndTime())
                .gmtCreate(bo.getGmtCreate()).gmtModified(bo.getGmtModified())
                .creator(SimpleAdminUserDto.builder().id(bo.getCreatorId()).userName(bo.getCreatorName()).build())
                .modifier(SimpleAdminUserDto.builder().id(bo.getModifierId()).userName(bo.getModifierName()).build())
                .build()).collect(Collectors.toList());
        return new TotalPageDto<>(list, totalPageDto.getPage(), totalPageDto.getPageSize()
                , totalPageDto.getTotal(), totalPageDto.getPages());
    }

    public void saveShopLogistics(Long shopId, Long id, ShopLogistics shopLogistics, UserDto user) {
        ShopLogistics oldShopLogistics = shopLogisticsDao.findShopLogisticsById(id);
        if(PLATFORM != shopId && shopId != oldShopLogistics.getShopId()){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商铺物流", id, shopId));
        }
        shopLogistics.setId(id);
        String key = this.shopLogisticsDao.save(shopLogistics, user);
//        if (redisUtil.hasKey(key)){
//            redisUtil.del(key);
//        }
    }
}
