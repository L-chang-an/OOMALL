package cn.edu.xmu.oomall.freight.mapper.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehousePeriodPo {
    WarehousePo warehousePo;
    LocalDateTime beginTime;
    LocalDateTime endTime;
}
