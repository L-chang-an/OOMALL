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
@Builder
public class WarehouseDto {
    @NotNull
    private Long id;
    private String name;
    private String address;
    private SimpleRegionDto region;
    private String senderName;
    private String senderMobile;
    /*
        API中的status疑似为invalid，因为表中只有invalid没有status
     */
    private Long invalid;
    private Long priority;

    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private SimpleAdminUserDto createdBy;
    private SimpleAdminUserDto modifiedBy;

    public WarehouseDto(Long id, String name, String address, SimpleRegionDto region, String senderName, String senderMobile, Long invalid, Long priority, LocalDateTime gmtCreate, LocalDateTime gmtModified, SimpleAdminUserDto createdBy, SimpleAdminUserDto modifiedBy) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.region = region;
        this.senderName = senderName;
        this.senderMobile = senderMobile;
        this.invalid = invalid;
        this.priority = priority;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        this.createdBy = createdBy;
        this.modifiedBy = modifiedBy;
    }

}
