package cn.edu.xmu.oomall.order.dao.openfeign;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.IdNameDto;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.OnsaleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service")
public interface CustomerDao {
    @GetMapping("/shops/{shopId}/customers/{id}")
    InternalReturnObject<IdNameDto> getCustomerById(@PathVariable Long shopId, @PathVariable Long id);
}
