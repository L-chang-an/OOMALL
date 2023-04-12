package cn.edu.xmu.oomall.freight.mapper;

import cn.edu.xmu.oomall.freight.mapper.po.WarehousePeriodPo;
import cn.edu.xmu.oomall.freight.mapper.po.WarehousePo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WarehousePoMapper extends JpaRepository<WarehousePo, Long> {
    boolean existsByIdAndShopId(Long warehouseId, Long shopId);
    Page<WarehousePo> findByShopIdOrderByPriorityAsc(Long shopId, Pageable pageable);
    Optional<WarehousePo> findByShopIdAndId(Long shopId, Long warehouseId);
    boolean existsByShopIdAndId(Long shopId, Long warehouseId);
    @Query(value = "select new cn.edu.xmu.oomall.freight.mapper.po.WarehousePeriodPo(u, s.beginTime, s.endTime) " +
            "from WarehousePo u, WarehouseRegionPo s where u.shopId = ?1 and u.invalid = 0 and u.id = s.warehouseId and s.regionId = ?2 " +
            "and s.beginTime <= ?3 and s.endTime >= ?3 order by u.priority asc")
    Page<WarehousePeriodPo> findRegionWarehouses(Long shopId, Long regionId, LocalDateTime nowTime, Pageable pageable);
}
