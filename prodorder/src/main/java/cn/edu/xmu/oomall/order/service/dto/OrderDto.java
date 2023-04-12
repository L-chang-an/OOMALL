package cn.edu.xmu.oomall.order.service.dto;

import cn.edu.xmu.oomall.order.dao.openfeign.dto.IdNameDto;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.ShopDto;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.PackDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderDto {
    private Long id;
    private String orderSn;
    private IdNameDto customer;
    private ShopDto shop;
    private Long pid;
    private Integer status;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private Long originPrice;
    private Long discountPrice;
    private Long expressFee;
    private String message;
    private ConsigneeDto consignee;
    private PackDto pack;
    private List<OrderItemDto> orderItems;
}
