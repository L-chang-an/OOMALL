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
public class ExpressDto {
    @NotNull
    private Long id;
    private String billCode;
    private LogisticsDto logistics;
    private ExpressInfo shipper;
    private ExpressInfo receiver;
    private Long status;
    private SimpleAdminUserDto creator;
    private SimpleAdminUserDto modifier;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
