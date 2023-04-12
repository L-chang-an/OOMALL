package cn.edu.xmu.oomall.order.dao.openfeign.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PaymentDto {
    private LocalDateTime timeExpire;
    private LocalDateTime timeBegin;
    private String spOpenid;
    private Long amount;
    private Long divAmount;
    private Long shopChannelId;
}
