package cn.edu.xmu.oomall.freight.controller.vo;


import cn.edu.xmu.oomall.freight.service.dto.LogisticsDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ShopLogisticsVo {

    private Long shopId;
    private Long logisticsId;
    @NotNull(message = "秘钥不能为空")
    private String secret;
    @NotNull(message = "优先级不能为空")
    private Long priority;

}
