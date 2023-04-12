//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.Common;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.order.dao.OrderDao;
import cn.edu.xmu.oomall.order.dao.bo.Order;
import cn.edu.xmu.oomall.order.dao.bo.OrderItem;
import cn.edu.xmu.oomall.order.dao.bo.OrderPayment;
import cn.edu.xmu.oomall.order.dao.bo.StatusNo;
import cn.edu.xmu.oomall.order.dao.openfeign.FreightDao;
import cn.edu.xmu.oomall.order.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.order.dao.openfeign.PaymentDao;
import cn.edu.xmu.oomall.order.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.*;
import cn.edu.xmu.oomall.order.service.dto.ConsigneeDto;
import cn.edu.xmu.oomall.order.service.dto.OrderDto;
import cn.edu.xmu.oomall.order.service.dto.OrderItemDto;
import cn.edu.xmu.oomall.order.service.dto.SimpleOrderDto;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;

@Repository
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    @Value("${oomall.order.server-num}")
    private int serverNum;
    private static final Byte ADVANCED=3;
    private static final Byte GROUPON=2;
    private GoodsDao goodsDao;

    private FreightDao freightDao;

    private PaymentDao paymentDao;

    private OrderDao orderDao;

    private RocketMQService rocketMQService;

    @Autowired
    public OrderService(GoodsDao goodsDao,
                        OrderDao orderDao,
                        FreightDao freightDao,
                        PaymentDao paymentDao,
                        RocketMQService rocketMQService
    ) {
        this.goodsDao = goodsDao;
        this.orderDao = orderDao;
        this.rocketMQService= rocketMQService;
        this.freightDao=freightDao;
        this.paymentDao=paymentDao;
    }
    @Transactional
    public Map<Long, List<OrderItem>> packOrder(List<OrderItemDto> items, UserDto customer){
        Map<Long, List<OrderItem>> packs = new HashMap<>();
        items.stream().forEach(item -> {
            OnsaleDto onsaleDto = this.goodsDao.getOnsaleById(PLATFORM, item.getOnsaleId()).getData();
            OrderItem orderItem = OrderItem.builder().onsaleId(onsaleDto.getId()).price(onsaleDto.getPrice()).name(onsaleDto.getProduct().getName()).creatorId(customer.getId()).creatorName(customer.getName()).gmtCreate(LocalDateTime.now()).build();
            if (null != onsaleDto.getActList() && null != item.getActId()){
                if (onsaleDto.getActList().stream().filter(activity -> activity.getId() == item.getActId()).count()  > 0){
                    orderItem.setActId(item.getActId());
                    //TODO:需要查看优惠卷id所属的活动是否在onsale的活动列表中，并且优惠卷是有效的，才能设置到orderItem中
                    /*
                        若要对coupon进行有效性检查
                        1. 根据couponId，利用OpenFeign向customer模块查询coupon对应的活动详情是否能和onsaleDto中的ActList对应上
                        2. 查看couponId的有效期是否过期
                        3. 若都没问题，则将couponId设置到orderItem中
                     */
                    //由于取消集成测试，所以此处优惠券进行有效性检查的代码就不补了，直接假设都有效
                    orderItem.setCouponId(item.getCouponId());
                    /*
                       如果优惠券有效，则向goods模块查询discountPrice
                       同样地，集成测试取消，此处不补OpenFeign的代码
                       直接认为discountPrice为0
                    */
                    orderItem.setDiscountPrice(0L);
                }
            }
            if (item.getQuantity() <= onsaleDto.getMaxQuantity()){
                //不能超过最大可购买数量
                orderItem.setQuantity(item.getQuantity());
            }else{
                throw new BusinessException(ReturnNo.ITEM_OVERMAXQUANTITY, String.format(ReturnNo.ITEM_OVERMAXQUANTITY.getMessage(), onsaleDto.getId(), item.getQuantity(), onsaleDto.getMaxQuantity()));
            }
            Long shopId = onsaleDto.getShop().getId();
            List<OrderItem> pack = packs.get(shopId);
            if (null == pack){
                packs.put(shopId, new ArrayList<>(){
                    {
                        add(orderItem);
                    }
                });
            }else{
                pack.add(orderItem);
            }
        });
        return packs;
    }

    @Transactional
    public void createOrder(List<OrderItemDto> items, ConsigneeDto consignee, String message, UserDto customer) {
        Map<Long, List<OrderItem>> packs = this.packOrder(items, customer);

        packs.keySet().stream().forEach(shopId -> {
                    Order order = Order.builder().creatorId(customer.getId()).customerId(customer.getId()).creatorName(customer.getName()).gmtCreate(LocalDateTime.now()).shopId(shopId).
                            consignee(consignee.getConsignee()).address(consignee.getAddress()).mobile(consignee.getMobile()).regionId(consignee.getRegionId()).
                            orderSn(Common.genSeqNum(serverNum)).message(message).orderItems(packs.get(shopId)).pid(0L).status(StatusNo.NEW_ORDER.getStatusNo()).build();
                    //先要减去货品数量,发送异步消息给goods模块
                    rocketMQService.sendDelGoodsMsg(order);
                    this.orderDao.createOrder(order);
                }
        );
    }

    public void payOrder(UserDto user,Long id,Long points,List<Long> coupons,Long shopChannelId){
        Order order=orderDao.findOrderById(id);
        LocalDateTime timeBegin=LocalDateTime.now();
        if(user.getId()!=order.getCustomerId()){
            throw new BusinessException(ReturnNo.AUTH_NO_RIGHT,ReturnNo.AUTH_NO_RIGHT.getMessage());
        }
        order.setPoint(points);
        Long amount= order.calTotalPrice(coupons);

        //通知cutomer模块将已使用的优惠券coupons扣除
        rocketMQService.sendDelCouponsMsg(coupons);

        OnsaleDto onsaleDto = this.goodsDao.getOnsaleById(PLATFORM, order.getOrderItems().get(0).getOnsaleId()).getData();
        if(onsaleDto.getType()==ADVANCED){
            order.setStatus(StatusNo.WAIT_BALANCE.getStatusNo());
        }else if(onsaleDto.getType()==GROUPON){
            order.setStatus(StatusNo.WAIT_GROUP.getStatusNo());
        }else{
            order.setStatus(StatusNo.HAS_PAID.getStatusNo());
        }
        orderDao.savePayOrder(order,user);
        LocalDateTime timeExpire=LocalDateTime.now();
        //调用payment模块生成支付单
        PaymentDto pay=PaymentDto.builder().timeBegin(timeBegin).timeExpire(timeExpire).amount(amount).divAmount(amount/10).shopChannelId(shopChannelId).spOpenid("test").build();
        InternalReturnObject<IdNameDto> payInfo=paymentDao.createPayment(pay);
        if(payInfo.getErrno()==ReturnNo.OK.getErrNo()){
            OrderPayment payment=OrderPayment.builder().orderId(order.getId()).paymentId(payInfo.getData().getId()).build();
            orderDao.createOrderPayment(payment,user);
        }
    }

    public PageDto<SimpleOrderDto> retrieveOrdersByShop(Long shopId,Long customerId,String orderSn,LocalDateTime beginTime,LocalDateTime endTime,Integer page,Integer pageSize){
        List<Order> orders=orderDao.retrieveOrdersByShop(customerId,shopId,orderSn,beginTime,endTime,page,pageSize);
        List<SimpleOrderDto> dtoList=orders.stream().map(bo->SimpleOrderDto.builder().id(bo.getId()).status(bo.getStatus()).discountPrice(bo.getDiscountPrice()).originPrice(bo.getOriginPrice()).freightPrice(bo.getExpressFee()).gmtCreate(bo.getGmtCreate())
                .build()).collect(Collectors.toList());
        return new PageDto<>(dtoList,page,pageSize);
    }
    public PageDto<SimpleOrderDto> retrieveOrdersByCustomer(Long customerId,String orderSn,Integer status,LocalDateTime beginTime,LocalDateTime endTime,Integer page,Integer pageSize){
        List<Order> orders=orderDao.retrieveOrdersByCustomer(customerId,orderSn,status,beginTime,endTime,page,pageSize);
        List<SimpleOrderDto> dtoList=orders.stream().map(bo->SimpleOrderDto.builder().id(bo.getId()).status(bo.getStatus()).discountPrice(bo.getDiscountPrice()).originPrice(bo.getOriginPrice()).freightPrice(bo.getExpressFee()).gmtCreate(bo.getGmtCreate())
                .build()).collect(Collectors.toList());
        return new PageDto<>(dtoList,page,pageSize);
    }

    public OrderDto findOrderByIdForCustomer(UserDto customer,Long id){
        Order order=orderDao.findOrderById(id);
        if(customer.getId()!=order.getCustomerId()){
            throw new BusinessException(ReturnNo.AUTH_NO_RIGHT,ReturnNo.AUTH_NO_RIGHT.getMessage());
        }
        List<OrderItem> orderItems=order.getOrderItems();
        List<OrderItemDto> orderItemDtos=orderItems.stream().map(tmp->OrderItemDto.builder().orderId(tmp.getOrderId()).actId(tmp.getActId()).discountPrice(tmp.getDiscountPrice())
                        .price(tmp.getPrice()).name(tmp.getName()).quantity(tmp.getQuantity()).onsaleId(tmp.getOnsaleId()).couponId(tmp.getCouponId()).build())
                .collect(Collectors.toList());
        OrderDto dto=OrderDto.builder().id(order.getId()).pid(order.getPid()).orderSn(order.getOrderSn()).originPrice(order.getOriginPrice())
                .discountPrice(order.getDiscountPrice()).status(order.getStatus()).gmtModified(order.getGmtModified()).gmtCreate(order.getGmtCreate())
                .expressFee(order.getExpressFee()).message(order.getMessage()).customer(IdNameDto.builder().id(customer.getId()).name(customer.getName()).build())
                .consignee(ConsigneeDto.builder().consignee(order.getConsignee()).address(order.getAddress()).regionId(order.getRegionId()).mobile(order.getMobile()).build())
                .shop(order.getShop()).pack(order.getPack()).orderItems(orderItemDtos).build();
        return dto;
    }

    public OrderDto findOrderByIdForShop(Long shopId,Long id){
        Order order=orderDao.findOrderById(id);
        if(shopId!=PLATFORM&&shopId!=order.getShopId()){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE,String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(),"订单",id,shopId));
        }
        List<OrderItem> orderItems=order.getOrderItems();
        List<OrderItemDto> orderItemDtos=orderItems.stream().map(tmp->OrderItemDto.builder().orderId(tmp.getOrderId()).actId(tmp.getActId()).discountPrice(tmp.getDiscountPrice())
                        .price(tmp.getPrice()).name(tmp.getName()).quantity(tmp.getQuantity()).onsaleId(tmp.getOnsaleId()).couponId(tmp.getCouponId()).build())
                .collect(Collectors.toList());
        OrderDto dto=OrderDto.builder().id(order.getId()).pid(order.getPid()).orderSn(order.getOrderSn()).originPrice(order.getOriginPrice())
                .discountPrice(order.getDiscountPrice()).status(order.getStatus()).gmtModified(order.getGmtModified()).gmtCreate(order.getGmtCreate())
                .expressFee(order.getExpressFee()).message(order.getMessage()).customer(order.getCustomer()).pack(order.getPack()).shop(order.getShop())
                .consignee(ConsigneeDto.builder().consignee(order.getConsignee()).address(order.getAddress()).regionId(order.getRegionId()).mobile(order.getMobile()).build())
                .orderItems(orderItemDtos).build();
        return dto;
    }

    @Transactional
    public void updateOrderMsg(Order bo,UserDto user){
        Order oldBo=orderDao.findOrderById(bo.getId());
        if(bo.getShopId()!=PLATFORM&&oldBo.getShopId()!=bo.getShopId()){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE,String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(),"订单",bo.getId(),bo.getShopId()));
        }
        bo.setStatus(oldBo.getStatus());
        orderDao.save(bo,user);
    }

    @Transactional
    public void updateOrderConsignee(Order bo,UserDto user){
        Order oldBo=orderDao.findOrderById(bo.getId());
        if((oldBo.getCustomerId()!=user.getId())||(oldBo.getStatus()>=StatusNo.HAS_SHIPMENTS.getStatusNo())){
            throw new BusinessException(ReturnNo.AUTH_NO_RIGHT);
        }
        bo.setStatus(oldBo.getStatus());
        orderDao.save(bo,user);
    }

    @Transactional
    public void confirmOrder(Long shopId,Long id,UserDto user){
        Order bo=orderDao.findOrderById(id);
        if(shopId!=PLATFORM&&bo.getShopId()!=shopId){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE,String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(),"订单",bo.getId(),bo.getShopId()));
        }
        if(StatusNo.allowStatus(bo.getStatus(),StatusNo.HAS_SHIPMENTS.getStatusNo())){
            ExpressDto info=ExpressDto.builder().shopLogisticsId(0L).sender(bo.getShopConsignee()).delivery(ConsigneeDto.builder().consignee(bo.getConsignee()).address(bo.getAddress()).mobile(bo.getMobile()).regionId(bo.getRegionId()).build()).build();
            InternalReturnObject<PackDto> pack=freightDao.createShipmentBill(shopId,info);
            if(ReturnNo.OK.getErrNo()!=pack.getErrno())
                throw new BusinessException(ReturnNo.getReturnNoByCode(pack.getErrno()));
            bo.setPackageId(pack.getData().getId());
            bo.setStatus(StatusNo.HAS_SHIPMENTS.getStatusNo());
            orderDao.save(bo,user);
        }else{
            throw new BusinessException(ReturnNo.ORDER_CHANGENOTALLOW);
        }
    }

    @Transactional
    public void delOrderByShopId(Long shopId,Long id,UserDto user){
        Order bo=orderDao.findOrderById(id);
        if(shopId!=PLATFORM&&bo.getShopId()!=shopId){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE,String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(),"订单",bo.getId(),bo.getShopId()));
        }
        if(StatusNo.allowStatus(bo.getStatus(),StatusNo.WAIT_REFUND.getStatusNo())){
            bo.setStatus(StatusNo.WAIT_REFUND.getStatusNo());
            //通知商品模块恢复商品数量
            rocketMQService.sendRevokeOrderMsg(bo);
            orderDao.save(bo,user);
        }else{
            throw new BusinessException(ReturnNo.ORDER_CHANGENOTALLOW);
        }
    }

    @Transactional
    public void delOrderByCustomer(Long id,UserDto user){
        Order bo=orderDao.findOrderById(id);
        if(bo.getCustomerId()!=user.getId()){
            throw new BusinessException(ReturnNo.AUTH_NO_RIGHT,ReturnNo.AUTH_NO_RIGHT.getMessage());
        }
        if(bo.getStatus()>=StatusNo.NEW&&bo.getStatus()<StatusNo.SHIPMENT){
            bo.setStatus(StatusNo.CANCELLED.getStatusNo());
        }else{
            if(StatusNo.allowStatus(bo.getStatus(),StatusNo.WAIT_REFUND.getStatusNo())){
                bo.setStatus(StatusNo.WAIT_REFUND.getStatusNo());
            }else{
                throw new BusinessException(ReturnNo.ORDER_CHANGENOTALLOW);
            }
        }
        //通知商品模块恢复商品数量
        rocketMQService.sendRevokeOrderMsg(bo);
        orderDao.save(bo,user);
    }
}
