package cn.edu.xmu.oomall.freight.controller.vo;

import cn.edu.xmu.oomall.freight.service.dto.ExpressInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpressVo {

    private Long shopLogisticsId;
    /*
    ExpressInfo这个类定义在dto里，因为创建传入的参数也有这个类
    */
    private ExpressInfo sender;
    private ExpressInfo delivery;
    private Long status;
}
