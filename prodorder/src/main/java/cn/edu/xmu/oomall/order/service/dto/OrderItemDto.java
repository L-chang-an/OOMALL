//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.service.dto;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
public class OrderItemDto implements Serializable {
    private Long orderId;

    private Long onsaleId;

    private Integer quantity;

    private Long price;

    private Long discountPrice;

    private String name;

    private Long actId;

    private Long couponId;
}
