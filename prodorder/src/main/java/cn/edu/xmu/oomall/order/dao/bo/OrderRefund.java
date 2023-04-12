package cn.edu.xmu.oomall.order.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString(callSuper = true)
@NoArgsConstructor
public class OrderRefund extends OOMallObject implements Serializable {
    @Getter
    @Setter
    private Long orderId;
    @Getter
    @Setter
    private Long refundId;
    @Getter
    @Setter
    private Long point;

    @Builder
    public OrderRefund(Long id, Long creatorId, String creatorName, Long modifierId, String modifierName, LocalDateTime gmtCreate, LocalDateTime gmtModified, Long orderId, Long refundId,Long point) {
        super(id, creatorId, creatorName, modifierId, modifierName, gmtCreate, gmtModified);
        this.refundId=refundId;
        this.orderId=orderId;
        this.point=point;
    }
}
