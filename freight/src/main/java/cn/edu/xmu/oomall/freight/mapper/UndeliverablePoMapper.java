package cn.edu.xmu.oomall.freight.mapper;

import cn.edu.xmu.oomall.freight.mapper.po.UndeliverablePo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UndeliverablePoMapper extends JpaRepository<UndeliverablePo, Long> {
    boolean existsByRegionIdAndShopLogisticsId(Long regionId, Long shopLogisticsId);

    UndeliverablePo findByRegionIdAndShopLogisticsId(Long regionId, Long shopLogisticsId);
    void deleteByRegionIdAndShopLogisticsId(Long regionId, Long shopLogisticsId);
    Long countByShopLogisticsId(Long shopLogisticsId);
    List<UndeliverablePo> findByShopLogisticsId(Long shopLogisticsId, Pageable pageable);
}
