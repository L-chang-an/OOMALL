package cn.edu.xmu.oomall.freight.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegionWarehouseDto {
    private SimpleWarehouseDto warehouse;
    private LocalDateTime beginTime;
    // 似乎也是仓库的invalid
    private Long status;
    private LocalDateTime endTime;
    private SimpleAdminUserDto creator;
    private SimpleAdminUserDto modifier;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
