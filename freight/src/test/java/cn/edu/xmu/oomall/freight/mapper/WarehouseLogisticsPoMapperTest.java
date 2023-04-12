package cn.edu.xmu.oomall.freight.mapper;

import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.mapper.po.WarehouseLogisticsPo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class WarehouseLogisticsPoMapperTest {
    @Autowired
    WarehouseLogisticsPoMapper warehouseLogisticsPoMapper;

    @Test
    public void findByWarehouseId() {
        Pageable pageable = PageRequest.of(0, 10);
        List<WarehouseLogisticsPo> pos = warehouseLogisticsPoMapper.findByWarehouseId(1L, pageable);
        assertThat(pos.size()).isEqualTo(3);
        assertThat(pos.get(2).getShopLogisticsId()).isEqualTo(2);
    }
    @Test
    public void countWarehouseLogistics() {
        assertThat(warehouseLogisticsPoMapper.countByWarehouseId(1L)).isEqualTo(3);
        assertThat(warehouseLogisticsPoMapper.countByWarehouseId(25L)).isEqualTo(3);
    }
}
