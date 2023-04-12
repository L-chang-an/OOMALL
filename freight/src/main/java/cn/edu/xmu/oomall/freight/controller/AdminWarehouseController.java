package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.controller.vo.InfoVo;
import cn.edu.xmu.oomall.freight.controller.vo.WarehouseInfoVo;
import cn.edu.xmu.oomall.freight.service.LogisticsService;
import cn.edu.xmu.oomall.freight.service.WarehouseService;
import cn.edu.xmu.oomall.freight.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;


/**
 * 运费控制器
 */
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/shops", produces = "application/json;charset=UTF-8")
public class AdminWarehouseController {

    private final Logger logger = LoggerFactory.getLogger(AdminWarehouseController.class);
    private WarehouseService warehouseService;

    private LogisticsService logisticsService;

    @Autowired
    public AdminWarehouseController(WarehouseService warehouseService, LogisticsService logisticsService) {
        this.warehouseService = warehouseService;
        this.logisticsService = logisticsService;
    }

    /*
     * 总的说明：
     * 1. 所有有用到user的api，都没有在update和save前检查user是否存在，如果检查，分支覆盖的时候无法覆盖到
     *    因为aop似乎会确保用户登录后才能操作api，所以我不知道如何构造一个样例覆盖user不存在的分支。因此没有检查user的存在性
     * 2. 所有put都没有检查传入vo的是否非空，如果字段为空则代表不修改，如果有必要检查min(0)等，则另外开一个不validate空的vo给put使用
     */
//    @GetMapping("/{shopId}/regions/{id}/warehouses")
//    @Audit(departName = "shops")
//    public ReturnObject getRegionWarehouses(
//            @PathVariable(value = "shopId") Long shopId,
//            @PathVariable(value = "id") Long regionId,
//            @RequestParam(defaultValue = "1") Integer page,
//            @RequestParam(defaultValue = "10") Integer pageSize
//    ){
//        /*
//         * duizhaoapi说明："商户或管理员查询某个地区可以配送的所有仓库 - 地区可以用下级地区查询  - 仓库按优先级从高到低返回"
//         * 1. 获取该商家的所有仓库
//         * 2. 获取该地区所有可发送的仓库id（当前时间在有效时间范围内 且 仓库invalid = 0）
//         * 3. 使用2.获得的id过滤1.获得的仓库
//         * 4. 手动分页返回结果
//         */
//        LocalDateTime nowTime = LocalDateTime.now();
//        PageDto<RegionWarehouseDto> pageDto = warehouseService.getRegionWarehouses(shopId, regionId, nowTime, page, pageSize);
//        return new ReturnObject(pageDto);
//    }

    @GetMapping("/{shopId}/regions/{id}/warehouses")
    @Audit(departName = "shops")
    public ReturnObject getRegionWarehouses(
            @PathVariable(value = "shopId") Long shopId,
            @PathVariable(value = "id") Long regionId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ){
        /*
         * 对照mapper版api说明："商户或管理员查询某个地区可以配送的所有仓库 - 地区可以用下级地区查询  - 仓库按优先级从高到低返回"
         * 1. 获取该商家的所有仓库
         * 2. 获取该地区所有可发送的仓库id（当前时间在有效时间范围内 且 仓库invalid = 0）
         * 3. 使用2.获得的id过滤1.获得的仓库
         * 4. 手动分页返回结果
         */
        LocalDateTime nowTime = LocalDateTime.now();
        PageDto<RegionWarehouseDto> pageDto = warehouseService.getRegionWarehouses(shopId, regionId, nowTime, page, pageSize);
        return new ReturnObject(pageDto);
    }
    @GetMapping("/{shopId}/warehouses/{id}/regions")
    @Audit(departName = "shops")
    public ReturnObject getWarehouseRegions(
            @PathVariable(value = "shopId") Long shopId,
            @PathVariable(value = "id") Long warehouseId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ){
        /*
         * api说明： "商户或管理员查询某个仓库的配送地区"
         * 1. 验证该仓库存在且是否属于该商家（可以用一条exist实现，就都抛出不存在错误，后期有必要细分再拆开）
         * 2. 使用仓库和当前时间，查询所有可配送地区返回结果
         */
        LocalDateTime nowTime = LocalDateTime.now();
        PageDto<WarehouseRegionDto> pageDto = warehouseService.getWarehouseRegions(shopId, warehouseId, nowTime, page, pageSize);
        return new ReturnObject(pageDto);
    }


    // 仓库的增删改查
    @GetMapping("/{shopId}/warehouses")
    @Audit(departName = "shops")
    public ReturnObject getWarehouses(
            @PathVariable(value = "shopId") Long shopId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ){
        /*
         *  api 说明：“获得仓库 - 需要登录 - 按照优先级排序”
         *  1. 获取该商家的所有仓库（传入page和pageSize）
         *  2. 封装为Dto后返回
         */
        PageDto<WarehouseDto> pageDto = warehouseService.getWarehouses(shopId, page, pageSize);
        return new ReturnObject(pageDto);
    }

    @PostMapping("/{shopId}/warehouses")
    @Audit(departName = "shops")
    public ReturnObject createWarehouse(
            @PathVariable(value = "shopId") Long shopId,
            @Validated @RequestBody WarehouseInfoVo warehouseInfoVo,
            @LoginUser UserDto user
    ){
        /*
         * api说明： “商户新建仓库 - 需要登录 - 只有商家才能新建warehouse”
         * 1. 检查登录商户是否是商户，是平台则抛错
         * 2. 创建仓库，返回仓库dto
         */
        if (PLATFORM.equals(user.getDepartId()))
            throw new BusinessException(ReturnNo.AUTH_NO_RIGHT, "只有商铺能够新建仓库");
        WarehouseDto dto = warehouseService.createWarehouse(shopId, warehouseInfoVo, user);
        return new ReturnObject(ReturnNo.CREATED, "创建成功", dto);
    }

    @PutMapping("/{shopId}/warehouses/{id}")
    @Audit(departName = "shops")
    public ReturnObject updateWarehouse(
            @PathVariable(value = "shopId") Long shopId,
            @PathVariable(value = "id") Long warehouseId,
            @RequestBody WarehouseInfoVo warehouseInfoVo,
            @LoginUser UserDto user
    ){
        /*
         * api说明： "商户修改仓库信息。 -需要登录"
         * 1. 构造bo，传入dao层，取出po，修改，save po
         */
        logger.debug("updateWarehouse: shopId = {}, warehouseId = {}", shopId, warehouseId);
        warehouseService.updateWarehouse(shopId, warehouseId, warehouseInfoVo, user);
        return new ReturnObject();
    }

    @DeleteMapping("/{shopId}/warehouses/{id}")
    @Audit(departName = "shops")
    public ReturnObject deleteWarehouse(
            @PathVariable(value = "shopId") Long shopId,
            @PathVariable(value = "id") Long warehouseId
    ){
        warehouseService.deleteWarehouse(shopId, warehouseId);
        return new ReturnObject();
    }

    @PutMapping("/{shopId}/warehouses/{id}/suspend")
    @Audit(departName = "shops")
    public ReturnObject updateWarehouseStatus1(
            @PathVariable(value = "shopId",required = true) Long shopId,
            @PathVariable(value = "id", required = true) Long warehouseId,
            @LoginUser UserDto user
    ){
        /*
         * api说明: "商铺暂停某个仓库发货 - 需要登录" invalid = 1 表示暂停使用
         */
        warehouseService.updateWarehouseStatus(shopId, warehouseId, 1L, user);
        return new ReturnObject();
    }

    @PutMapping("/{shopId}/warehouses/{id}/resume")
    @Audit(departName = "shops")
    public ReturnObject updateWarehouseStatus2(
            @PathVariable(value = "shopId",required = true) Long shopId,
            @PathVariable(value = "id", required = true) Long warehouseId,
            @LoginUser UserDto user
    ){
        /*
         * api说明: "商铺恢复某个仓库发货 - 需要登录" invalid = 0 表示暂停使用
         */
        warehouseService.updateWarehouseStatus(shopId, warehouseId, 0L, user);
        return new ReturnObject();
    }

    // 仓库配送地区的增删改
    @PostMapping("/{shopId}/warehouses/{wid}/regions/{id}")
    @Audit(departName = "shops")
    public ReturnObject createWarehouseRegion(
            @PathVariable(value = "shopId") Long shopId,
            @PathVariable(value = "wid") Long warehouseId,
            @PathVariable(value = "id") Long regionId,
            @Validated @RequestBody InfoVo infoVo,
            @LoginUser UserDto user
    ){
        /*
         * api说明： "商户新增仓库配送地区"
         */
        if (null == infoVo.getEndTime() || null == infoVo.getBeginTime())
            throw new BusinessException(ReturnNo.PARAMETER_MISSED);
        if (infoVo.getBeginTime().isAfter(infoVo.getEndTime())){
            throw new BusinessException(ReturnNo.LATE_BEGINTIME);
        }
        warehouseService.createWarehouseRegion(shopId, warehouseId, regionId, infoVo.getBeginTime(), infoVo.getEndTime(), user);
        return new ReturnObject(ReturnNo.CREATED);
    }

    @PutMapping("/{shopId}/warehouses/{wid}/regions/{id}")
    @Audit(departName = "shops")
    public ReturnObject putWarehouseRegion(
            @PathVariable(value = "shopId") Long shopId,
            @PathVariable(value = "wid") Long warehouseId,
            @PathVariable(value = "id") Long regionId,
            @Validated @RequestBody InfoVo infoVo,
            @LoginUser UserDto user
    ){
        /*
         * api说明： "商户新增仓库配送地区"
         * 传入的api中有priority，但数据库仓库配送地区表中没有，因此未使用
         * 已经存在一个新增仓库配送地区的api，因此该api大概是为了修改，因此允许beginTime和endTime为空，代表不修改
         */
        if (null != infoVo.getEndTime() && null != infoVo.getBeginTime() && infoVo.getBeginTime().isAfter(infoVo.getEndTime())){
            throw new BusinessException(ReturnNo.LATE_BEGINTIME);
        }
        warehouseService.updateWarehouseRegion(shopId, warehouseId, regionId, infoVo.getBeginTime(), infoVo.getEndTime(), user);
        return new ReturnObject();
    }

    @DeleteMapping("/{shopId}/warehouses/{wid}/regions/{id}")
    @Audit(departName = "shops")
    public ReturnObject deleteWarehouseRegion(
            @PathVariable(value = "shopId") Long shopId,
            @PathVariable(value = "wid") Long warehouseId,
            @PathVariable(value = "id") Long regionId
    ){
        /*
         * api说明： "商户或管理员取消仓库对某个地区的配送"
         */
        warehouseService.deleteWarehouseRegion(shopId, warehouseId, regionId);
        return new ReturnObject();
    }

    // 仓库物流 增删改查
    @PostMapping("/{shopId}/warehouses/{id}/shoplogistics/{lid}")
    @Audit(departName = "shops")
    public ReturnObject createWarehouseLogistics(
            @PathVariable(value = "shopId") Long shopId,
            @PathVariable(value = "id") Long warehouseId,
            @PathVariable(value = "lid") Long shopLogisticsId,
            @Validated @RequestBody InfoVo infoVo,
            @LoginUser UserDto user

    ){
        if (null == infoVo.getEndTime() || null == infoVo.getBeginTime())
            throw new BusinessException(ReturnNo.PARAMETER_MISSED);
        if (infoVo.getBeginTime().isAfter(infoVo.getEndTime())){
            throw new BusinessException(ReturnNo.LATE_BEGINTIME);
        }
        logisticsService.createWarehouseLogistics(shopId, warehouseId, shopLogisticsId, infoVo, user);
        return new ReturnObject();
    }

    @PutMapping("/{shopId}/warehouses/{id}/shoplogistics/{lid}")
    @Audit(departName = "shops")
    public ReturnObject updateWarehouseLogistics(
            @PathVariable(value = "shopId") Long shopId,
            @PathVariable(value = "id") Long warehouseId,
            @PathVariable(value = "lid") Long shopLogisticsId,
            @Validated @RequestBody InfoVo infoVo,
            @LoginUser UserDto user
    ){
        if (null != infoVo.getEndTime() && null != infoVo.getBeginTime() && infoVo.getBeginTime().isAfter(infoVo.getEndTime())){
            throw new BusinessException(ReturnNo.LATE_BEGINTIME);
        }
        logisticsService.updateWarehouseLogistics(shopId, warehouseId, shopLogisticsId, infoVo, user);
        return new ReturnObject();
    }

    @DeleteMapping("/{shopId}/warehouses/{id}/shoplogistics/{lid}")
    @Audit(departName = "shops")
    public ReturnObject deleteWarehouseLogistics(
            @PathVariable(value = "shopId") Long shopId,
            @PathVariable(value = "id") Long warehouseId,
            @PathVariable(value = "lid") Long shopLogisticsId
    ){
        logisticsService.deleteWarehouseLogistics(shopId, warehouseId, shopLogisticsId);
        return new ReturnObject();
    }

    @GetMapping("/{shopId}/warehouses/{id}/shoplogistics")
    @Audit(departName = "shops")
    public ReturnObject getAllWarehouseLogistics(
            @PathVariable(value = "shopId") Long shopId,
            @PathVariable(value = "id") Long warehouseId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ){
        /*
         * api说明： "商户或管理员查询某个仓库的配送地区"
         * 1. 仓库物流表中不存在priority字段，只能使用外键去商铺物流表中找
         * 2. 目前采取得实现是在mapper层中用连表查询一次实现连表排序分页，剩下得就是不断组装返回给前端
         */
        TotalPageDto<WarehouseLogisticsDto> totalPageDto = logisticsService.getAllWarehouseLogistics(shopId, warehouseId, page, pageSize);
        return new ReturnObject(totalPageDto);
    }
}
