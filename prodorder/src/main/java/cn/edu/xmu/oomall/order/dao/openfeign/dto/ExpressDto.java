package cn.edu.xmu.oomall.order.dao.openfeign.dto;

import cn.edu.xmu.oomall.order.service.dto.ConsigneeDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpressDto {
    @NotNull
    private Long shopLogisticsId;
    private ConsigneeDto sender;
    private ConsigneeDto delivery;
}