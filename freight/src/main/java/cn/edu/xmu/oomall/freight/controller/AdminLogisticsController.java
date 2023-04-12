package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.controller.vo.InfoVo;
import cn.edu.xmu.oomall.freight.controller.vo.ShopLogisticsVo;
import cn.edu.xmu.oomall.freight.controller.vo.WarehouseInfoVo;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import cn.edu.xmu.oomall.freight.service.LogisticsService;
import cn.edu.xmu.oomall.freight.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;

@RestController /*Restful的Controller对象*/
@RequestMapping(produces = "application/json;charset=UTF-8")
public class AdminLogisticsController {
    private final Logger logger = LoggerFactory.getLogger(AdminLogisticsController.class);
    private LogisticsService logisticsService;

    @Autowired
    public AdminLogisticsController(LogisticsService logisticsService) {
        this.logisticsService = logisticsService;
    }
    @GetMapping("/shops/{shopId}/shoplogistics")
    @Audit(departName = "shops")
    public ReturnObject retrieveShoplogisticsByShopId(
            @PathVariable(value = "shopId",required = true) Long shopId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @LoginUser UserDto user
    ){
        logger.debug("Controller retrieveShoplogisticsByShopId: departId = {}, userid = {}", user.getDepartId(), user.getId());
        PageDto<ShopLogisticsDto> pageDto = logisticsService.retrieveShopLogisticsByShopId(shopId, page, pageSize);
        logger.debug("ShopLogisticsDtoPageDto:{}", pageDto);

        return new ReturnObject(pageDto);
    }

    @PostMapping("/shops/{shopId}/shoplogistics")
    @Audit(departName = "shops")
    public ReturnObject createShoplogistics(
            @PathVariable(value = "shopId",required = true) Long shopId,
            @Validated @RequestBody ShopLogisticsVo shopLogisticsVo,
            @LoginUser UserDto user
    ){
        shopLogisticsVo.setShopId(shopId);
        ShopLogisticsDto shopLogisticsDto = logisticsService.createShoplogistics(shopLogisticsVo,user);
        return new ReturnObject(shopLogisticsDto);
    }
    @PutMapping("/shops/{shopId}/shoplogistics/{id}")
    @Audit(departName = "shops")
    public ReturnObject updateShoplogistics(
            @PathVariable(value = "shopId",required = true) Long shopId,
            @PathVariable(value = "id",required = true) Long id,
            @Validated @RequestBody ShopLogisticsVo shopLogisticsVo,
            @LoginUser UserDto user
    ){
        ShopLogistics shopLogistics = ShopLogistics.builder()
                .secret(shopLogisticsVo.getSecret())
                .priority(shopLogisticsVo.getPriority())
                .build();
        logisticsService.saveShopLogistics(shopId,id,shopLogistics,user);
        return new ReturnObject();
    }

    @PutMapping("/shops/{shopId}/shoplogistics/{id}/suspend")
    @Audit(departName = "shops")
    public ReturnObject suspendShoplogistics(
            @PathVariable(value = "shopId",required = true) Long shopId,
            @PathVariable(value = "id",required = true) Long id,
            @LoginUser UserDto user
    ){
        ShopLogistics shopLogistics = ShopLogistics.builder()
                .invalid(1L)
                .build();
        logisticsService.saveShopLogistics(shopId,id,shopLogistics,user);
        return new ReturnObject();
    }

    @PutMapping("/shops/{shopId}/shoplogistics/{id}/resume")
    @Audit(departName = "shops")
    public ReturnObject resumeShoplogistics(
            @PathVariable(value = "shopId",required = true) Long shopId,
            @PathVariable(value = "id",required = true) Long id,
            @LoginUser UserDto user
    ){
        ShopLogistics shopLogistics = ShopLogistics.builder()
                .invalid(0L)
                .build();
        logisticsService.saveShopLogistics(shopId,id,shopLogistics,user);
        return new ReturnObject();
    }

    /*
    * API内定义billCode是required，但需求里是可以不给（此时返回所有平台物流）
    */
    @GetMapping("/logistics")
    @Audit(departName = "shops")
    public ReturnObject retrieveLogisticsByBillCode(
            @RequestParam(required = false) String billCode,
            @LoginUser UserDto user
    ){
        List<LogisticsDto> logisticsDtoList = logisticsService.retrieveLogisticsByBillCode(billCode);
        return new ReturnObject(logisticsDtoList);
    }

    // 快递公司不可达的增删改查
    @GetMapping("/shops/{shopId}/shoplogistics/{id}/undeliverableregions")
    @Audit(departName = "shops")
    public ReturnObject getAllShopLogisticsUndeliverable(
            @PathVariable(value = "shopId") Long shopId,
            @PathVariable(value = "id") Long shopLogisticsId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ){
        /*
         *  api 说明："商户查询快递公司无法配送的地区"
         *  没有说明要查询当前时间的不可达地区，暂时不过滤（warehouse中有个api，虽然没说明，但我也加入了当前时间）
         */
        TotalPageDto<UndeliverableDto> totalPageDto = logisticsService.getAllShopLogisticsUndeliverable(shopId, shopLogisticsId, page, pageSize);
        return new ReturnObject(totalPageDto);
    }

    @PostMapping("/shops/{shopId}/shoplogistics/{id}/regions/{rid}/undeliverable")
    @Audit(departName = "shops")
    public ReturnObject createUndeliverable(
            @PathVariable(value = "shopId") Long shopId,
            @PathVariable(value = "id") Long shopLogisticsId,
            @PathVariable(value = "rid") Long regionId,
            @Validated @RequestBody InfoVo infoVo,
            @LoginUser UserDto user
    ){
        /*
         * api说明："商户指定快递公司无法配送某个地区 - 需要登录”
         */
        if (null == infoVo.getEndTime() || null == infoVo.getBeginTime())
            throw new BusinessException(ReturnNo.PARAMETER_MISSED);
        if (infoVo.getBeginTime().isAfter(infoVo.getEndTime())){
            throw new BusinessException(ReturnNo.LATE_BEGINTIME);
        }
        logger.debug("createUndeliverable: {}, {}",shopLogisticsId, shopId);
        logisticsService.createUndeliverable(shopId, shopLogisticsId, regionId, infoVo, user);
        return new ReturnObject(ReturnNo.CREATED);
    }

    @PutMapping("/shops/{shopId}/shoplogistics/{id}/regions/{rid}/undeliverable")
    @Audit(departName = "shops")
    public ReturnObject updateUndeliverable(
            @PathVariable(value = "shopId") Long shopId,
            @PathVariable(value = "id") Long shopLogisticsId,
            @PathVariable(value = "rid") Long regionId,
            @Validated @RequestBody InfoVo infoVo,
            @LoginUser UserDto user
    ){
        /*
         * api说明： "商户更新不可达信息"
         */
        if (null != infoVo.getEndTime() && null != infoVo.getBeginTime() && infoVo.getBeginTime().isAfter(infoVo.getEndTime())){
            throw new BusinessException(ReturnNo.LATE_BEGINTIME);
        }
        logisticsService.updateUndeliverable(shopId, shopLogisticsId, regionId, infoVo, user);
        return new ReturnObject();
    }

    @DeleteMapping("/shops/{shopId}/shoplogistics/{id}/regions/{rid}/undeliverable")
    @Audit(departName = "shops")
    public ReturnObject deleteUndeliverable(
            @PathVariable(value = "shopId") Long shopId,
            @PathVariable(value = "id") Long shopLogisticsId,
            @PathVariable(value = "rid") Long regionId
    ){
        /*
         * api说明： "商户删除某个不可达信息"
         */
        logisticsService.deleteUndeliverable(shopId, shopLogisticsId, regionId);
        return new ReturnObject();
    }

}
