package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Express extends OOMallObject implements Serializable {
    @ToString.Exclude
    @JsonIgnore
    private final static Logger logger = LoggerFactory.getLogger(Express.class);

    @Setter
    @JsonIgnore
    private RegionDao regionDao;

    @Setter
    @JsonIgnore
    private ShopLogisticsDao shopLogisticsDao;

    private String billCode;

    private Long shopLogisticsId;
    private ShopLogistics shopLogistics;
    public ShopLogistics getShopLogistics(){
        if (null == this.shopLogistics && null != this.shopLogisticsDao){
            this.shopLogistics = this.shopLogisticsDao.findShopLogisticsById(this.shopLogisticsId);
        }
        return this.shopLogistics;
    }


    private Long senderRegionId;
    private Region senderRegion;
    public Region getSenderRegion(){
        if (null == this.senderRegion && null != this.regionDao){
            this.senderRegion = this.regionDao.getRegionById(senderRegionId).getData();
        }
        return this.senderRegion;
    }

    private String senderAddress;

    private Long deliverRegionId;
    private Region deliverRegion;
    public Region getDeliverRegion(){
        if (null == this.deliverRegion && null != this.regionDao){
            this.deliverRegion = this.regionDao.getRegionById(deliverRegionId).getData();
        }
        return this.deliverRegion;
    }

    private String deliverAddress;

    private String senderName;

    private String senderMobile;

    private String deliverName;

    private Long status;

    private Long shopId;

    private String deliverMobile;

    @Builder
    public Express(Long id, Long creatorId, String creatorName, Long modifierId, String modifierName, LocalDateTime gmtCreate, LocalDateTime gmtModified, String billCode, Long shopLogisticsId, Long senderRegionId, String senderAddress, Long deliverRegionId, String deliverAddress, String senderName, String senderMobile, String deliverName, Long status, Long shopId, String deliverMobile) {
        super(id, creatorId, creatorName, modifierId, modifierName, gmtCreate, gmtModified);
        this.billCode = billCode;
        this.shopLogisticsId = shopLogisticsId;
        this.senderRegionId = senderRegionId;
        this.senderAddress = senderAddress;
        this.deliverRegionId = deliverRegionId;
        this.deliverAddress = deliverAddress;
        this.senderName = senderName;
        this.senderMobile = senderMobile;
        this.deliverName = deliverName;
        this.status = status;
        this.shopId = shopId;
        this.deliverMobile = deliverMobile;
    }
}

