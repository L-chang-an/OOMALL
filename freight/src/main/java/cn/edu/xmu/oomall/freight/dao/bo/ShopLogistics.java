package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.freight.dao.LogisticsDao;
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
public class ShopLogistics extends OOMallObject implements Serializable {
    @ToString.Exclude
    @JsonIgnore
    private final static Logger logger = LoggerFactory.getLogger(ShopLogistics.class);

    @Setter
    @JsonIgnore
    private LogisticsDao logisticsDao;

    private Long shopId;

    private Long logisticsId;
    private Logistics logistics;

    public Logistics getLogistics(){
        if (null == this.logistics && null != this.logisticsDao){
            this.logistics = this.logisticsDao.findLogisticsById(this.logisticsId);
        }
        return this.logistics;
    }
    private String secret;

    private Long invalid;

    private Long priority;

    @Builder
    public ShopLogistics(Long id, Long creatorId, String creatorName, Long modifierId, String modifierName, LocalDateTime gmtCreate, LocalDateTime gmtModified, long shopId, long logisticsId, String secret, long invalid, long priority) {
        super(id, creatorId, creatorName, modifierId, modifierName, gmtCreate, gmtModified);
        this.shopId = shopId;
        this.logisticsId = logisticsId;
        this.secret = secret;
        this.invalid = invalid;
        this.priority = priority;
    }
}
