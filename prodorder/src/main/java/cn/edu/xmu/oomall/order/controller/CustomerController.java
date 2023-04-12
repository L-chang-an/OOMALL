//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.order.controller.vo.OrderStatusVo;
import cn.edu.xmu.oomall.order.controller.vo.OrderVo;
import cn.edu.xmu.oomall.order.controller.vo.PayVo;
import cn.edu.xmu.oomall.order.dao.bo.Order;
import cn.edu.xmu.oomall.order.dao.bo.StatusNo;
import cn.edu.xmu.oomall.order.service.OrderService;
import cn.edu.xmu.oomall.order.service.dto.ConsigneeDto;
import cn.edu.xmu.oomall.order.service.dto.OrderItemDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;

@RestController /*Restful的Controller对象*/
@RequestMapping(value="/orders",produces = "application/json;charset=UTF-8")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private OrderService orderService;

    @Autowired
    public CustomerController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/states")
    public ReturnObject getOrderStatus(){
        logger.debug("getOrderStatus: ");
        return new ReturnObject(Arrays.stream(StatusNo.values()).map(statusNo -> OrderStatusVo.builder().code(statusNo.getStatusNo()).name(statusNo.getMessage()).build()).collect(Collectors.toList()));
    }

    @Audit
    @GetMapping("")
    public ReturnObject getOrders(@LoginUser UserDto user,
                                  @RequestParam(value = "orderSn",defaultValue = "")String orderSn,
                                  @RequestParam(value = "status",defaultValue = "1")Integer status,
                                  @RequestParam(required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beginTime,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
                                  @RequestParam(value = "page",defaultValue = "1")Integer page,
                                  @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize
    ){
        return new ReturnObject(orderService.retrieveOrdersByCustomer(user.getId(),orderSn,status,beginTime,endTime,page,pageSize));
    }

    @Audit
    @PostMapping("")
    public ReturnObject createOrder(@RequestBody @Validated OrderVo orderVo, @LoginUser UserDto user) {
        orderService.createOrder(orderVo.getItems().stream().map(item -> OrderItemDto.builder().onsaleId(item.getOnsaleId()).quantity(item.getQuantity()).actId(item.getActId()).couponId(item.getCouponId()).build()).collect(Collectors.toList()),
                ConsigneeDto.builder().consignee(orderVo.getConsignee()).address(orderVo.getAddress()).regionId(orderVo.getRegionId()).mobile(orderVo.getMobile()).build(),
                orderVo.getMessage(), user);
        return new ReturnObject(ReturnNo.CREATED,ReturnNo.CREATED.getMessage());
    }

    @Audit
    @GetMapping("{id}")
    public ReturnObject findOrderById(@LoginUser UserDto user, @PathVariable(value = "id")Long id){
        return new ReturnObject(orderService.findOrderByIdForCustomer(user,id));
    }

    @Audit
    @PutMapping("{id}")
    public ReturnObject updateOrderById(@LoginUser UserDto user,
                                        @Validated @RequestBody OrderVo vo,
                                        @PathVariable(value = "id")Long id){
        Order bo=Order.builder().id(id).consignee(vo.getConsignee()).address(vo.getAddress()).regionId(vo.getRegionId()).mobile(vo.getMobile()).build();
        orderService.updateOrderConsignee(bo,user);
        return new ReturnObject(ReturnNo.OK);
    }

    @Audit
    @DeleteMapping("{id}")
    public ReturnObject delOrderById(@LoginUser UserDto user, @PathVariable(value = "id")Long id){
        orderService.delOrderByCustomer(id,user);
        return new ReturnObject(ReturnNo.OK);
    }

    @Audit
    @PostMapping("/{id}/pay")
    public ReturnObject payOrder(@LoginUser UserDto user,
                                 @Validated @RequestBody PayVo vo,
                                 @PathVariable(value = "id")Long id
    ){
        orderService.payOrder(user,id,vo.getPoints(),vo.getCoupons(),vo.getShopChannelId());
        return new ReturnObject(ReturnNo.OK);
    }
}


