package cn.edu.xmu.oomall.order.service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SimpleOrderDto {
    private Long id;

    private Integer status;

    private LocalDateTime gmtCreate;

    private Long originPrice;

    private Long discountPrice;

    private Long freightPrice;
}
