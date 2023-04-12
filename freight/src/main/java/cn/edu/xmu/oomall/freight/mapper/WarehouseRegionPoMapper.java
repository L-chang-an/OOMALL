package cn.edu.xmu.oomall.freight.mapper;

import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.mapper.po.WarehousePo;
import cn.edu.xmu.oomall.freight.mapper.po.WarehouseRegionPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WarehouseRegionPoMapper extends JpaRepository<WarehouseRegionPo, Long> {
    WarehouseRegionPo findByWarehouseIdAndRegionId(Long warehouseId, Long regionId);
    List<WarehouseRegionPo> findByRegionIdAndBeginTimeLessThanEqualAndEndTimeGreaterThanEqualAndWarehouseIdIn(Long regionId, LocalDateTime nowTime1, LocalDateTime nowTime2, List<Long> warehouseId);
    void deleteByWarehouseId(Long warehouseId);
    Page<WarehouseRegionPo> findByWarehouseIdAndBeginTimeLessThanEqualAndEndTimeGreaterThanEqual(Long warehouseId, LocalDateTime nowTime1, LocalDateTime nowTime2, Pageable pageable);
}
