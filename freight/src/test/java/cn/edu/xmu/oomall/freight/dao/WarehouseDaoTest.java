package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.bo.Region;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class WarehouseDaoTest {
    @Autowired
    private WarehouseDao warehouseDao;
    @MockBean
    private RegionDao regionDao;
    @Test
    public void retrieveWarehouseByShopId1() {
        Mockito.when(regionDao.getRegionById(1043L)).thenReturn(new InternalReturnObject<>(new Region(){
            {
                setId(1043L);
                setName("朝阳新城第二社区居民委员会");
            }
        }));
        List<Warehouse> list = warehouseDao.retrieveWarehouseByShopIdOrderByPriorityAsc(1L, 1, 15).getList();
        assertThat(list.size()).isEqualTo(11L);
        list.forEach(bo -> assertThat(bo.getShopId()).isEqualTo(1L));
        IntStream.range(0, list.size()-1).forEach(i -> assertThat(list.get(i+1).getPriority() >= list.get(i).getPriority()).isEqualTo(true));
        Warehouse warehouse = list.get(0);
        assertThat(warehouse.getId()).isEqualTo(1L);
        assertThat(warehouse.getName()).isEqualTo("朝阳新城第二仓库");
        assertThat(warehouse.getAddress()).isEqualTo("北京,朝阳,东坝,朝阳新城第二曙光路14号");
        assertThat(warehouse.getRegionId()).isEqualTo(1043L);
        assertThat(warehouse.getSenderName()).isEqualTo("阮杰");
        assertThat(warehouse.getCreatorId()).isEqualTo(1);
        assertThat(warehouse.getCreatorName()).isEqualTo("admin");
        assertThat(warehouse.getRegion().getId()).isEqualTo(1043L);
        assertThat(warehouse.getRegion().getName()).isEqualTo("朝阳新城第二社区居民委员会");
    }

    @Test
    public void retrieveWarehouseByShopId2() {
        List<Warehouse> list = warehouseDao.retrieveWarehouseByShopIdOrderByPriorityAsc(100L, 1, 10).getList();
        assertThat(list.size()).isEqualTo(0L);
    }

}
