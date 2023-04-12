package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.freight.dao.LogisticsDao;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import cn.edu.xmu.oomall.freight.service.dto.SimpleAdminUserDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarehouseLogistics implements Serializable {
    @ToString.Exclude
    @JsonIgnore
    private final static Logger logger = LoggerFactory.getLogger(WarehouseLogistics.class);

    @Setter
    @JsonIgnore
    private ShopLogisticsDao shopLogisticsDao;
    private Long shopLogisticsId;
    private Long warehouseId;
    private ShopLogistics shopLogistics;
    public ShopLogistics getShopLogistics(){
        logger.debug("getShopLogistics: dao = {}", null == this.shopLogisticsDao);
        if (null == this.shopLogistics && null != this.shopLogisticsDao){
            logger.debug("getShopLogistics: shopLogisticsId = {}", shopLogisticsId);
            this.shopLogistics = this.shopLogisticsDao.findShopLogisticsById(shopLogisticsId);
            logger.debug("getShopLogistics: bo = {}", null == this.shopLogistics);
        }
        return this.shopLogistics;
    }
    private Long invalid;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private SimpleAdminUserDto creator;
    private SimpleAdminUserDto modifier;
    protected LocalDateTime gmtCreate;
    protected LocalDateTime gmtModified;
}
