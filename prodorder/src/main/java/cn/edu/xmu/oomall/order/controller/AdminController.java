package cn.edu.xmu.oomall.order.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.order.dao.bo.Order;
import cn.edu.xmu.oomall.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController /*Restful的Controller对象*/
@RequestMapping(value="/shops/{shopId}",produces = "application/json;charset=UTF-8")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private OrderService orderService;

    @Autowired
    public AdminController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Audit(departName = "shops")
    @GetMapping("/orders")
    public ReturnObject getOrders(@PathVariable(value = "shopId")Long shopId,
                                  @RequestParam(value = "customerId",defaultValue = "0")Long customerId,
                                  @RequestParam(value = "orderSn",defaultValue = "")String orderSn,
                                  @RequestParam(required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beginTime,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
                                  @RequestParam(value = "page",defaultValue = "1")Integer page,
                                  @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize
    ){
        return new ReturnObject(orderService.retrieveOrdersByShop(shopId,customerId,orderSn,beginTime,endTime,page,pageSize));
    }

    @Audit(departName = "shops")
    @PutMapping("/orders/{id}")
    public ReturnObject updateOrder(@LoginUser UserDto user,
                                     @PathVariable(value = "shopId")Long shopId,
                                     @PathVariable(value = "id")Long id,
                                     @Validated @RequestBody String message
    ){
        logger.debug("updateOrder: user={},msgVo={}",user,message);
        Order bo=Order.builder().id(id).shopId(shopId).message(message).build();
        orderService.updateOrderMsg(bo,user);
        return new ReturnObject();
    }

    @Audit(departName = "shops")
    @GetMapping("/orders/{id}")
    public ReturnObject getOrderById(@PathVariable(value = "shopId")Long shopId,
                                     @PathVariable(value = "id")Long id
    ){
        return new ReturnObject(orderService.findOrderByIdForShop(shopId,id));
    }

    @Audit(departName = "shops")
    @DeleteMapping("/orders/{id}")
    public ReturnObject delOrderById(@PathVariable(value = "shopId")Long shopId,
                                     @PathVariable(value = "id")Long id,
                                     @LoginUser UserDto user
    ){
        orderService.delOrderByShopId(shopId,id,user);
        return new ReturnObject();
    }

    @Audit(departName = "shops")
    @PutMapping("/orders/{id}/confirm")
    public ReturnObject confirmOrder(@PathVariable(value = "shopId")Long shopId,
                                     @PathVariable(value = "id")Long id,
                                     @LoginUser UserDto user
    ){
        orderService.confirmOrder(shopId,id,user);
        return new ReturnObject();
    }
}
