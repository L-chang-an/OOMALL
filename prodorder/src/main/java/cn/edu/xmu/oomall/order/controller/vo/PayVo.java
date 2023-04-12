package cn.edu.xmu.oomall.order.controller.vo;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class PayVo {
    @Min(value = 0,message = "积分点不能为负数")
    private Long points;

    @NotNull
    private Long shopChannelId;

    @NotEmpty(message = "至少要有一个活动id")
    private List<Long> coupons;
}
