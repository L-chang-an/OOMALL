package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleWarehouseRegion implements Serializable {
    @ToString.Exclude
    @JsonIgnore
    private final static Logger logger = LoggerFactory.getLogger(SimpleWarehouseRegion.class);
    private Long regionId;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;


    @Builder
    public SimpleWarehouseRegion(Long regionId, LocalDateTime beginTime, LocalDateTime endTime) {
        this.regionId = regionId;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }
}