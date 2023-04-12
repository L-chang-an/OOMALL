package cn.edu.xmu.oomall.order.dao.openfeign;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.ExpressDto;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.PackDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "freight-service")
public interface FreightDao {
    @GetMapping("/internal/packages/{id}")
    InternalReturnObject<PackDto> getPackById(@PathVariable Long id);

    @PostMapping("/internal/shops/{shopId}/packages")
    InternalReturnObject<PackDto> createShipmentBill(@PathVariable Long shopId, @Validated @RequestBody ExpressDto expInfo);
}
