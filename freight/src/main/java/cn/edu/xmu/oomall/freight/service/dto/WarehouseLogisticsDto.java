package cn.edu.xmu.oomall.freight.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseLogisticsDto {
    private ShopLogisticsDto shopLogistics;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Long status;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private SimpleAdminUserDto creator;
    private SimpleAdminUserDto modifier;
}
