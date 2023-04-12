//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.order.dao.bo.Order;
import cn.edu.xmu.oomall.order.dao.bo.OrderItem;
import cn.edu.xmu.oomall.order.dao.bo.OrderPayment;
import cn.edu.xmu.oomall.order.dao.bo.StatusNo;
import cn.edu.xmu.oomall.order.dao.openfeign.CustomerDao;
import cn.edu.xmu.oomall.order.dao.openfeign.FreightDao;
import cn.edu.xmu.oomall.order.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.order.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.order.mapper.OrderItemPoMapper;
import cn.edu.xmu.oomall.order.mapper.OrderPaymentPoMapper;
import cn.edu.xmu.oomall.order.mapper.OrderPoMapper;
import cn.edu.xmu.oomall.order.mapper.po.OrderItemPo;
import cn.edu.xmu.oomall.order.mapper.po.OrderPaymentPo;
import cn.edu.xmu.oomall.order.mapper.po.OrderPo;
import cn.edu.xmu.oomall.order.service.RocketMQService;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;
import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

@Repository
public class OrderDao {
    private static final Logger logger = LoggerFactory.getLogger(OrderDao.class);

    private OrderPoMapper orderPoMapper;

    @Value("${oomall.order.timeout}")
    private Long timeout;

    private RocketMQService rocketMQService;

    private ShopDao shopDao;

    private FreightDao freightDao;

    private CustomerDao customerDao;

    private RedisUtil redisUtil;

    private OrderItemPoMapper orderItemPoMapper;
    private OrderPaymentPoMapper orderPaymentPoMapper;

    private static final String ORDER_KEY = "O%d";

    @Autowired
    public OrderDao(OrderPoMapper orderPoMapper,
                    OrderItemPoMapper orderItemPoMapper,
                    RedisUtil redisUtil,
                    RocketMQService rocketMQService,
                    ShopDao shopDao,
                    FreightDao freightDao,
                    OrderPaymentPoMapper orderPaymentPoMapper,
                    CustomerDao customerDao
    ){
        this.orderPoMapper = orderPoMapper;
        this.orderItemPoMapper = orderItemPoMapper;
        this.redisUtil=redisUtil;
        this.rocketMQService=rocketMQService;
        this.freightDao=freightDao;
        this.customerDao=customerDao;
        this.orderPaymentPoMapper=orderPaymentPoMapper;
        this.shopDao=shopDao;
    }

    private void setBo(Order bo){
        bo.setShopDao(this.shopDao);
        bo.setCustomerDao(this.customerDao);
        bo.setFreightDao(this.freightDao);
        bo.setRocketMQService(this.rocketMQService);
    }

    public void createOrder(Order order)throws RuntimeException{
        this.setBo(order);
        OrderPo orderPo = OrderPo.builder().creatorId(order.getCreatorId()).creatorName(order.getCreatorName()).gmtCreate(order.getGmtCreate()).orderSn(order.getOrderSn())
                .status(order.getStatus()).mobile(order.getMobile()).address(order.getAddress()).shopId(order.getShopId()).pid(order.getPid())
                .consignee(order.getConsignee()).customerId(order.getCustomerId()).regionId(order.getRegionId()).message(order.getMessage()).expressFee(order.getExpressFee()).build();
        OrderPo newPo=orderPoMapper.save(orderPo);
        order.setId(newPo.getId());
        String key = String.format(ORDER_KEY, order.getId());
        redisUtil.set(key,order,timeout);

        order.getOrderItems().stream().forEach(orderItem -> {
            OrderItemPo orderItemPo = OrderItemPo.builder().orderId(newPo.getId()).creatorId(orderItem.getCreatorId()).onsaleId(orderItem.getOnsaleId())
                    .quantity(orderItem.getQuantity()).price(orderItem.getPrice()).discountPrice(orderItem.getDiscountPrice()).couponId(orderItem.getCouponId())
                    .activityId(orderItem.getActId()).creatorName(orderItem.getCreatorName()).name(orderItem.getName()).commented(orderItem.getCommented())
                    .gmtCreate(orderItem.getGmtCreate()).build();
            orderItemPoMapper.save(orderItemPo);
        });
    }

    public void savePayOrder(Order order,UserDto user)throws RuntimeException{
        String key = String.format(ORDER_KEY, order.getId());
        if(redisUtil.hasKey(key)){
            redisUtil.del(key);
        }
        OrderPo orderPo = OrderPo.builder().id(order.getId()).originPrice(order.getOriginPrice()).discountPrice(order.getDiscountPrice()).modifierId(user.getId())
                .modifierName(user.getName()).point(order.getPoint()).status(order.getStatus()).build();
        orderPoMapper.save(orderPo);
    }

    public void save(Order order, UserDto user)throws RuntimeException{
        String key = String.format(ORDER_KEY, order.getId());
        if(redisUtil.hasKey(key)){
            redisUtil.del(key);
        }
        OrderPo orderPo = OrderPo.builder().id(order.getId()).shopId(order.getShopId()).modifierId(user.getId()).modifierName(user.getName()).message(order.getMessage())
                .consignee(order.getConsignee()).address(order.getAddress()).regionId(order.getRegionId()).mobile(order.getMobile()).status(order.getStatus()).packageId(order.getPackageId()).build();
        orderPoMapper.save(orderPo);
    }

    public Order findOrderById(Long id)throws RuntimeException{
        String key = String.format(ORDER_KEY, id);
        Order bo=null;
        if (redisUtil.hasKey(key)) {
            bo=(Order)redisUtil.get(key);
        } else {
            Optional<OrderPo> ret=this.orderPoMapper.findById(id);
            if (ret.isEmpty()) {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "支付渠道", id));
            }else{
                OrderPo po=ret.get();
                bo = Order.builder().id(po.getId()).creatorId(po.getCreatorId()).creatorName(po.getCreatorName()).gmtCreate(po.getGmtCreate())
                        .modifierId(po.getModifierId()).modifierName(po.getModifierName()).gmtModified(po.getGmtModified()).orderSn(po.getOrderSn())
                        .consignee(po.getConsignee()).customerId(po.getCustomerId()).address(po.getAddress()).message(po.getMessage()).customerId(po.getCustomerId())
                        .mobile(po.getMobile()).pid(po.getPid()).packageId(po.getPackageId()).shopId(po.getShopId()).expressFee(po.getExpressFee())
                        .regionId(po.getRegionId()).status(po.getStatus()).discountPrice(po.getDiscountPrice()).originPrice(po.getOriginPrice()).build();
                bo.setOrderItems(this.retrieveByOrderId(bo.getId()));
                redisUtil.set(key,bo,timeout);
            }
        }
        this.setBo(bo);
        return bo;
    }

    public List<OrderItem> retrieveByOrderId(Long orderId) {
        List<OrderItemPo> orderItemPoList = orderItemPoMapper.findByOrderId(orderId);
        List<OrderItem> orderItems = orderItemPoList.stream().map(tmp -> OrderItem.builder().id(tmp.getId()).orderId(tmp.getOrderId()).actId(tmp.getActivityId()).couponId(tmp.getCouponId())
                .discountPrice(tmp.getDiscountPrice()).price(tmp.getPrice()).creatorId(tmp.getCreatorId()).creatorName(tmp.getCreatorName()).modifierName(tmp.getModifierName()).name(tmp.getName())
                .modifierId(tmp.getModifierId()).gmtModified(tmp.getGmtModified()).quantity(tmp.getQuantity()).onsaleId(tmp.getOnsaleId()).gmtCreate(tmp.getGmtCreate()).point(tmp.getPoint()).commented(tmp.getCommented()).build()
        ).collect(Collectors.toList());
        return orderItems;
    }

    public List<Order> retrieveOrdersByCustomer(Long customerId,String orderSn, Integer status, LocalDateTime beginTime,LocalDateTime endTime,Integer page,Integer pageSize){
        Pageable pageable= PageRequest.of(page-1,pageSize);
        orderSn=new StringBuilder(orderSn).append("%").toString();
        List<OrderPo> poList=null;
        if(status == StatusNo.ALL){
            poList=orderPoMapper.findByCustomerIdAndOrderSnLikeAndGmtCreateBetween(customerId,orderSn,beginTime,endTime,pageable);
        }else{
            poList=orderPoMapper.findByCustomerIdAndOrderSnLikeAndStatusBetweenAndGmtCreateBetween(customerId,orderSn,status,status+StatusNo.INTERNAL,beginTime,endTime,pageable);
        }

        return poList.stream().map(po->Order.builder().id(po.getId()).creatorId(po.getCreatorId()).creatorName(po.getCreatorName())
                .gmtCreate(po.getGmtCreate()).modifierId(po.getModifierId()).modifierName(po.getModifierName()).gmtModified(po.getGmtModified())
                .orderSn(po.getOrderSn()).consignee(po.getConsignee()).customerId(po.getCustomerId()).address(po.getAddress()).expressFee(po.getExpressFee())
                .message(po.getMessage()).mobile(po.getMobile()).pid(po.getPid()).packageId(po.getPackageId()).shopId(po.getShopId())
                .regionId(po.getRegionId()).status(po.getStatus()).discountPrice(po.getDiscountPrice()).originPrice(po.getOriginPrice()).build()).collect(Collectors.toList());
    }

    public List<Order> retrieveOrdersByShop(Long shopId,Long customerId,String orderSn, LocalDateTime beginTime,LocalDateTime endTime,Integer page,Integer pageSize){
        Pageable pageable= PageRequest.of(page-1,pageSize);
        orderSn=new StringBuilder(orderSn).append("%").toString();
        List<OrderPo> poList=null;
        if(shopId == PLATFORM&&customerId==PLATFORM){
            poList=orderPoMapper.findByOrderSnLikeAndGmtCreateBetween(orderSn,beginTime,endTime,pageable);
        }else if(shopId != PLATFORM&&customerId==PLATFORM){
            poList=orderPoMapper.findByShopIdAndOrderSnLikeAndGmtCreateBetween(shopId,orderSn,beginTime,endTime,pageable);
        }else if(shopId == PLATFORM&&customerId!=PLATFORM){
            poList=orderPoMapper.findByCustomerIdAndOrderSnLikeAndGmtCreateBetween(customerId,orderSn,beginTime,endTime,pageable);
        }else{
            poList=orderPoMapper.findByShopIdAndCustomerIdAndOrderSnLikeAndGmtCreateBetween(shopId,customerId,orderSn,beginTime,endTime,pageable);
        }
        return poList.stream().map(po->Order.builder().id(po.getId()).creatorId(po.getCreatorId()).creatorName(po.getCreatorName())
                .gmtCreate(po.getGmtCreate()).modifierId(po.getModifierId()).modifierName(po.getModifierName()).gmtModified(po.getGmtModified())
                .orderSn(po.getOrderSn()).consignee(po.getConsignee()).customerId(po.getCustomerId()).address(po.getAddress()).expressFee(po.getExpressFee())
                .message(po.getMessage()).mobile(po.getMobile()).pid(po.getPid()).packageId(po.getPackageId()).shopId(po.getShopId())
                .regionId(po.getRegionId()).status(po.getStatus()).discountPrice(po.getDiscountPrice()).originPrice(po.getOriginPrice()).build()).collect(Collectors.toList());
    }

    public void createOrderPayment(OrderPayment payment, UserDto user)throws RuntimeException{
        OrderPaymentPo po = OrderPaymentPo.builder().orderId(payment.getOrderId()).paymentId(payment.getPaymentId()).creatorId(user.getId()).creatorName(user.getName()).gmtCreate(LocalDateTime.now()).build();
        orderPaymentPoMapper.save(po);
    }
}
