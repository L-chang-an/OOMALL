package cn.edu.xmu.oomall.order.dao.openfeign;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.oomall.order.dao.bo.OrderItem;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.FreightDto;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.ShopDto;
import cn.edu.xmu.oomall.order.service.dto.ConsigneeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "shop-service")
public interface ShopDao {
    @GetMapping("/shops/{id}")
    InternalReturnObject<ShopDto> getShopById(@PathVariable Long id);

    @GetMapping("/shops/{id}")
    InternalReturnObject<ConsigneeDto> getShopConsigneeById(@PathVariable Long id);

    @PostMapping("/internal/templates/{id}/regions/{rid}/freightprice")
    InternalReturnObject<FreightDto> getExpressFee(@Validated @RequestBody List<OrderItem> orderItems);
}
