//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
public class ConsigneeDto implements Serializable {

    @JsonProperty(value = "name")
    private String consignee;

    private String address;

    private Long regionId;

    private String mobile;
}
