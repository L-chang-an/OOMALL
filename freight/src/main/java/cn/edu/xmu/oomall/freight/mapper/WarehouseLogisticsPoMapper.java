package cn.edu.xmu.oomall.freight.mapper;

import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.oomall.freight.mapper.po.ShopLogisticsPo;
import cn.edu.xmu.oomall.freight.mapper.po.WarehouseLogisticsPo;
import cn.edu.xmu.oomall.freight.mapper.po.WarehouseRegionPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseLogisticsPoMapper extends JpaRepository<WarehouseLogisticsPo, Long> {
    boolean existsByWarehouseIdAndShopLogisticsId(Long warehouseId, Long shopLogisticsId);
    WarehouseLogisticsPo findByWarehouseIdAndShopLogisticsId(Long warehouseId, Long shopLogisticsId);
    @Query(value = "select u from WarehouseLogisticsPo u, ShopLogisticsPo s where u.warehouseId = ?1 and u.shopLogisticsId = s.id order by s.priority asc")
    List<WarehouseLogisticsPo> findByWarehouseId(Long warehouseId, Pageable pageable);
    Long countByWarehouseId(Long warehouseId);
    void deleteByWarehouseIdAndShopLogisticsId(Long warehouseId, Long shopLogisticsId);
    void deleteByWarehouseId(Long warehouseId);
}
