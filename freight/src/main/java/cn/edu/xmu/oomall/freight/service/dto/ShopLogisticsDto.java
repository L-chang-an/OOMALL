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
public class ShopLogisticsDto {
    @NotNull
    private Long id;
    private LogisticsDto logistics;

    /*
        API中的status疑似为invalid，因为表中只有invalid没有status
     */
    private Long invalid;

    private String secret;

    private Long priority;

    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private SimpleAdminUserDto createdBy;
    private SimpleAdminUserDto modifiedBy;
}
