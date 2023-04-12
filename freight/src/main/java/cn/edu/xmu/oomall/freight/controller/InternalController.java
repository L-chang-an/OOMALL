package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.controller.vo.ExpressVo;
import cn.edu.xmu.oomall.freight.controller.vo.InfoVo;
import cn.edu.xmu.oomall.freight.dao.bo.Express;
import cn.edu.xmu.oomall.freight.service.ExpressService;
import cn.edu.xmu.oomall.freight.service.LogisticsService;
import cn.edu.xmu.oomall.freight.service.dto.ExpressDto;
import cn.edu.xmu.oomall.freight.service.dto.LogisticsDto;
import cn.edu.xmu.oomall.freight.service.dto.ShopLogisticsDto;
import cn.edu.xmu.oomall.freight.service.dto.SimpleExpressDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/internal", produces = "application/json;charset=UTF-8")
public class InternalController {
    private final Logger logger = LoggerFactory.getLogger(InternalController.class);
    private ExpressService expressService;

    @Autowired
    public InternalController(ExpressService expressService) {
        this.expressService = expressService;
    }

    @GetMapping("/shops/{shopId}/packages")
    @Audit(departName = "shops")
    public ReturnObject findExpressByBillCode(
            @PathVariable(value = "shopId",required = true) Long shopId,
            @RequestParam(required = true) String billCode
    ){
        ExpressDto express = expressService.findExpressByBillCode(shopId, billCode);
        return new ReturnObject(express);
    }

    @PostMapping("/shops/{shopId}/packages")
    @Audit(departName = "shops")
    public ReturnObject insertExpress(
            @PathVariable(value = "shopId",required = true) Long shopId,
            @Validated @RequestBody ExpressVo expressVo,
            @LoginUser UserDto user
    ){
        Express express = Express.builder()
                .shopId(shopId)
                .shopLogisticsId(expressVo.getShopLogisticsId())
                .senderName(expressVo.getSender().getName())
                .senderAddress(expressVo.getSender().getAddress())
                .senderMobile(expressVo.getSender().getMobile())
                .senderRegionId(expressVo.getSender().getRegionId())
                .deliverName(expressVo.getDelivery().getName())
                .deliverAddress(expressVo.getDelivery().getAddress())
                .deliverMobile(expressVo.getDelivery().getMobile())
                .deliverRegionId(expressVo.getDelivery().getRegionId())
                .build();
        SimpleExpressDto ret = expressService.createExpress(express, user);
        return new ReturnObject(ret);
    }

    @GetMapping("/packages/{id}")
    @Audit(departName = "shops")
    public ReturnObject findExpressById(
            @PathVariable(value = "id",required = true) Long id
    ){
        ExpressDto express = expressService.findExpressById(id);
        return new ReturnObject(express);
    }

    @PutMapping("/shops/{shopId}/packages/{id}/confirm")
    @Audit(departName = "shops")
    public ReturnObject confirmExpress(
            @PathVariable(value = "shopId",required = true) Long shopId,
            @PathVariable(value = "id",required = true) Long id,
            @Validated @RequestBody ExpressVo expressVo,
            @LoginUser UserDto user
    ){
        Express express = Express.builder()
                .id(id).shopId(shopId).status(expressVo.getStatus())
                .build();
        expressService.confirmExpress(express,user);
        return new ReturnObject();
    }

    /*
     我不是太看得懂这个API需求，取消订单的话是调快递公司API，然后要删数据库记录吗，用的是Put很奇怪
     */
    @PutMapping("/shops/{shopId}/packages/{id}/cancel")
    @Audit(departName = "shops")
    public ReturnObject cancelExpress(
            @PathVariable(value = "shopId",required = true) Long shopId,
            @PathVariable(value = "id",required = true) Long id,
            @LoginUser UserDto user
    ){
        Express express = Express.builder()
                .id(id).shopId(shopId)
                .build();
        expressService.cancelExpress(express);
        return new ReturnObject();
    }



}
