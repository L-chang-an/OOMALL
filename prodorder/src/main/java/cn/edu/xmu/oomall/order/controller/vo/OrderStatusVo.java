package cn.edu.xmu.oomall.order.controller.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderStatusVo {
    /*
        状态码
     */
    private Integer code;

    /*
        状态名称
     */
    private String name;
}
