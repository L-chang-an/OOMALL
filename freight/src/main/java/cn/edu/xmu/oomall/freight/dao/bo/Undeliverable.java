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
public class Undeliverable extends OOMallObject implements Serializable {
    @ToString.Exclude
    @JsonIgnore
    private final static Logger logger = LoggerFactory.getLogger(Undeliverable.class);

    @Setter
    @JsonIgnore
    private RegionDao regionDao;

    private Long regionId;

    private Region region;

    public Region getRegion(){
        if (null == this.region && null != this.regionDao){
            this.region = this.regionDao.getRegionById(regionId).getData();
            logger.debug("getRegion: {}", this.regionId);
        }
        return this.region;
    }


    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    @Setter
    @JsonIgnore
    private ShopLogisticsDao shopLogisticsDao;

    private Long shopLogisticsId;

    private ShopLogistics shopLogistics;

    public ShopLogistics getShopLogistics(){
        if (null == this.shopLogistics && null != this.shopLogisticsDao){
            this.shopLogistics = this.shopLogisticsDao.findShopLogisticsById(this.shopLogisticsId);
        }
        return this.shopLogistics;
    }

    @Builder
    public Undeliverable(Long id, Long creatorId, String creatorName, Long modifierId, String modifierName, LocalDateTime gmtCreate, LocalDateTime gmtModified, RegionDao regionDao, Long regionId, Region region, LocalDateTime beginTime, LocalDateTime endTime, ShopLogisticsDao shopLogisticsDao, Long shopLogisticsId, ShopLogistics shopLogistics) {
        super(id, creatorId, creatorName, modifierId, modifierName, gmtCreate, gmtModified);
        this.regionDao = regionDao;
        this.regionId = regionId;
        this.region = region;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.shopLogisticsDao = shopLogisticsDao;
        this.shopLogisticsId = shopLogisticsId;
        this.shopLogistics = shopLogistics;
    }
}
