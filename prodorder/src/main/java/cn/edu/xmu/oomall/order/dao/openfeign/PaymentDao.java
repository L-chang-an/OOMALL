package cn.edu.xmu.oomall.order.dao.openfeign;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.IdNameDto;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.PaymentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentDao {
    @PostMapping("/internal/payments")
    InternalReturnObject<IdNameDto> createPayment(@Validated @RequestBody PaymentDto pay);

}
