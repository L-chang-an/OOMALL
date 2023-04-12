package cn.edu.xmu.oomall.order.dao.openfeign.dto;

import cn.edu.xmu.oomall.order.service.dto.OrderItemDto;
import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class FreightDto {
    private Long freightPrice;

    private Collection<OrderItemDto> pack;
}
