package cn.edu.xmu.oomall.order.dao.openfeign.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PackDto {
    private Long id;
    private String billCode;

    @JsonIgnore
    private Long status;
}
