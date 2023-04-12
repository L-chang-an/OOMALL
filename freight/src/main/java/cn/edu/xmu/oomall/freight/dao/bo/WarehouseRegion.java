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

@NoArgsConstructor
@Data
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarehouseRegion extends OOMallObject implements Serializable {
    @ToString.Exclude
    @JsonIgnore
    private final static Logger logger = LoggerFactory.getLogger(WarehouseRegion.class);
    private Long warehouseId;

    private Long regionId;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;


    @Builder
    public WarehouseRegion(Long id, Long warehouseId, Long creatorId, String creatorName, Long modifierId, String modifierName, LocalDateTime gmtCreate, LocalDateTime gmtModified, Long regionId, LocalDateTime beginTime, LocalDateTime endTime) {
        this.id = id;
        this.warehouseId = warehouseId;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.modifierId = modifierId;
        this.modifierName = modifierName;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        this.regionId = regionId;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }
}