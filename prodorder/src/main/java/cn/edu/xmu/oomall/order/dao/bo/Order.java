//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.dao.bo;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.order.dao.openfeign.CustomerDao;
import cn.edu.xmu.oomall.order.dao.openfeign.FreightDao;
import cn.edu.xmu.oomall.order.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.order.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.FreightDto;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.IdNameDto;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.PackDto;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.ShopDto;
import cn.edu.xmu.oomall.order.service.RocketMQService;
import cn.edu.xmu.oomall.order.service.dto.ConsigneeDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@ToString(callSuper = true)
@NoArgsConstructor
public class Order extends OOMallObject implements Serializable {

    @Builder
    public Order(Long id, Long creatorId, String creatorName, Long modifierId, String modifierName, LocalDateTime gmtCreate, LocalDateTime gmtModified, Long customerId, Long shopId,Integer status, Long expressFee,Long discountPrice, Long originPrice,Long point, String orderSn, Long pid, String consignee, Long regionId, String address, String mobile, String message, Long activityId, Long packageId, List<OrderItem> orderItems) {
        super(id, creatorId, creatorName, modifierId, modifierName, gmtCreate, gmtModified);
        this.customerId = customerId;
        this.shopId = shopId;
        this.status=status;
        this.expressFee=expressFee;
        this.discountPrice=discountPrice;
        this.originPrice=originPrice;
        this.point=point;
        this.orderSn = orderSn;
        this.pid = pid;
        this.consignee = consignee;
        this.regionId = regionId;
        this.address = address;
        this.mobile = mobile;
        this.message = message;
        this.activityId = activityId;
        this.packageId = packageId;
        this.orderItems = orderItems;
    }

    @Setter
    private Integer status;

    public Integer getStatus(){
        if(status==StatusNo.HAS_SHIPMENTS.getStatusNo()){
            //查询运单状态
            if(pack!=null && pack.getStatus()==HAS_SIGNED){
                this.setStatus(StatusNo.COMPLETED.getStatusNo());
                rocketMQService.sendChangeOrderStatusMsg(this);
            }
        }
        if(status==StatusNo.WAIT_REFUND.getStatusNo()){
            //Todo: 查询是否退款成功,由于不知道要调用哪个api来查询，所以此处暂空置
            if(/*假设查询退款已成功*/true){
                this.setStatus(StatusNo.CANCELLED.getStatusNo());
                rocketMQService.sendChangeOrderStatusMsg(this);
            }
        }
        return status;
    }

    @Getter
    private Long expressFee;

    @Getter
    @Setter
    private Long discountPrice;

    @Getter
    @Setter
    private Long originPrice;

    @Getter
    @Setter
    private Long point;

    @Getter
    private Long customerId;

    @Getter
    private Long shopId;

    @Setter
    @Getter
    private String orderSn;

    @Setter
    @Getter
    private Long pid;

    @Setter
    @Getter
    private String consignee;

    @Setter
    @Getter
    private Long regionId;

    @Setter
    @Getter
    private String address;

    @Setter
    @Getter
    private String mobile;

    @Setter
    @Getter
    private String message;

    @Setter
    private Long activityId;

    @Setter
    @Getter
    private Long packageId;

    @Setter
    @Getter
    private List<OrderItem> orderItems;

    @Setter
    private List<OrderPayment> payments;

    @Setter
    private List<OrderRefund> refunds;

    private static final Long HAS_SIGNED=8L;

    @Setter
    @ToString.Exclude
    @JsonIgnore
    private ShopDao shopDao;

    @Setter
    @ToString.Exclude
    @JsonIgnore
    private CustomerDao customerDao;

    @Setter
    @ToString.Exclude
    @JsonIgnore
    private FreightDao freightDao;

    @Setter
    @ToString.Exclude
    @JsonIgnore
    private RocketMQService rocketMQService;


    @ToString.Exclude
    @JsonIgnore
    private ShopDto shop;

    public ShopDto getShop(){
        if (null != this.shopId && null == this.shop) {
            InternalReturnObject<ShopDto> ret=this.shopDao.getShopById(this.getShopId());
            if(ReturnNo.OK.getErrNo()==ret.getErrno())
                this.shop = ret.getData();
        }
        return this.shop;
    }

    @ToString.Exclude
    @JsonIgnore
    private ConsigneeDto shopConsignee;
    public ConsigneeDto getShopConsignee(){
        if (null != this.shopId && null == this.shopConsignee) {
            InternalReturnObject<ConsigneeDto> ret=this.shopDao.getShopConsigneeById(this.getShopId());
            if(ReturnNo.OK.getErrNo()==ret.getErrno())
                this.shopConsignee = ret.getData();
        }
        return this.shopConsignee;
    }

    @ToString.Exclude
    @JsonIgnore
    private IdNameDto customer;

    public IdNameDto getCustomer(){
        if (null != this.customerId && null == this.customer) {
            InternalReturnObject<IdNameDto> ret=this.customerDao.getCustomerById(this.shopId,this.customerId);
            if(ReturnNo.OK.getErrNo()==ret.getErrno())
                this.customer = ret.getData();
        }
        return this.customer;
    }

    @ToString.Exclude
    @JsonIgnore
    private PackDto pack;

    public PackDto getPack(){
        if (null != this.packageId && null == this.pack) {
            InternalReturnObject<PackDto> ret=this.freightDao.getPackById(this.packageId);
            if(ReturnNo.OK.getErrNo()==ret.getErrno())
                this.pack = ret.getData();
        }
        return this.pack;
    }

    public Long calTotalPrice(List<Long> coupons){
        this.setOriginPrice(0L);
        this.setDiscountPrice(0L);
        this.orderItems.forEach(orderItem ->
        {
            if(coupons.contains(orderItem.getCouponId())){
                this.setDiscountPrice(this.getDiscountPrice()+orderItem.getDiscountPrice()*orderItem.getQuantity());
            }
            this.setOriginPrice(this.getOriginPrice()+orderItem.getPrice()* orderItem.getQuantity());
        });
        Long amount=this.originPrice-this.discountPrice;
        if (null == this.expressFee) {
            InternalReturnObject<FreightDto> ret=shopDao.getExpressFee(orderItems);
            if(ReturnNo.OK.getErrNo()==ret.getErrno()){
                this.expressFee = ret.getData().getFreightPrice();
                amount-=this.expressFee;
            }
        }
        return amount;
    }
}
