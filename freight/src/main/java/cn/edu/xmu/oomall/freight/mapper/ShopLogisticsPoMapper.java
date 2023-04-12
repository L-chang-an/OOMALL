package cn.edu.xmu.oomall.freight.mapper;

import cn.edu.xmu.oomall.freight.mapper.po.ShopLogisticsPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopLogisticsPoMapper extends JpaRepository<ShopLogisticsPo, Long> {
    Page<ShopLogisticsPo> findByShopIdOrderByPriorityAsc(Long shopId, Pageable pageable);
    boolean existsByIdAndShopId(Long shopLogisticsId, Long shopId);
}
