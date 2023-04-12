package cn.edu.xmu.oomall.freight.controller.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarehouseInfoVo {
    @NotBlank(message="仓库信息任一字段均不能为空")
    private String name;
    @NotBlank(message="仓库信息任一字段均不能为空")
    private String address;
    @NotNull(message="仓库信息任一字段均不能为空")
    private Long regionId;
    @NotBlank(message="仓库信息任一字段均不能为空")
    private String senderName;
    @NotBlank(message="仓库信息任一字段均不能为空")
    private String senderMobile;
    @NotNull(message="仓库信息任一字段均不能为空")
    private Long priority;
}
