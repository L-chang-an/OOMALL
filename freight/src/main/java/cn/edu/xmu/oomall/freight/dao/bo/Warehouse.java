//School of Informatics Xiamen University, GPL-3.0 license
package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Data
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Warehouse extends OOMallObject implements Serializable{
    @ToString.Exclude
    @JsonIgnore
    private final static Logger logger = LoggerFactory.getLogger(Warehouse.class);

    @Setter
    @JsonIgnore
    private RegionDao regionDao;


    private String address;

    private Long shopId;

    private String name;

    private String senderName;

    private Long regionId;


    private Region region;



    public Region getRegion(){
        if (null == this.region && null != this.regionDao){
            this.region = this.regionDao.getRegionById(regionId).getData();
            logger.debug("getRegion: {}", this.regionId);
        }
        return this.region;
    }

    private String senderMobile;

    private Long priority;

    private Long invalid;

    private Period period;
    @Builder
    public Warehouse(String address, Period period, Long id, Long shopId, String name, String senderName, Long creatorId, String creatorName, Long modifierId, String modifierName, LocalDateTime gmtCreate, LocalDateTime gmtModified, Long regionId, String senderMobile, Long priority, Long invalid) {
        this.address = address;
        this.id = id;
        this.period = period;
        this.shopId = shopId;
        this.name = name;
        this.senderName = senderName;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.modifierId = modifierId;
        this.modifierName = modifierName;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        this.regionId = regionId;
        this.senderMobile = senderMobile;
        this.priority = priority;
        this.invalid = invalid;
    }
}