package cn.edu.xmu.oomall.order.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString(callSuper = true)
@NoArgsConstructor
public class OrderPayment extends OOMallObject implements Serializable {

    @Getter
    @Setter
    private Long orderId;
    @Getter
    @Setter
    private Long paymentId;

    @Builder
    public OrderPayment(Long id, Long creatorId, String creatorName, Long modifierId, String modifierName, LocalDateTime gmtCreate, LocalDateTime gmtModified, Long orderId,Long paymentId) {
        super(id, creatorId, creatorName, modifierId, modifierName, gmtCreate, gmtModified);
        this.paymentId=paymentId;
        this.orderId=orderId;
    }
}
