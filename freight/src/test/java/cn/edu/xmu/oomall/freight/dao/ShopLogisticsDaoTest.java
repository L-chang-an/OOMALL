package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.bo.Logistics;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseRegion;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class ShopLogisticsDaoTest {
    @Autowired
    private ShopLogisticsDao shopLogisticsDao;
    @Autowired
    private LogisticsDao logisticsDao;

    @Test
    public void retrieveShopLogisticsByShopIdOrderByPriorityAsc1() {
        List<ShopLogistics> shopLogisticsList = shopLogisticsDao
                .retrieveShopLogisticsByShopIdOrderByPriorityAsc(1L, 1, 10).getList();
        assertThat(shopLogisticsList.size()).isEqualTo(3L);
        //按照优先级排序从小到大返回
        assertThat(shopLogisticsList.get(0).getLogisticsId()).isEqualTo(3L);
        assertThat(shopLogisticsList.get(0).getLogistics().getName()).isEqualTo("极兔速递");
        assertThat(shopLogisticsList.get(0).getPriority()).isEqualTo(2L);

        assertThat(shopLogisticsList.get(1).getLogisticsId()).isEqualTo(1L);
        assertThat(shopLogisticsList.get(1).getLogistics().getName()).isEqualTo("顺丰快递");
        assertThat(shopLogisticsList.get(1).getPriority()).isEqualTo(3L);

        assertThat(shopLogisticsList.get(2).getLogisticsId()).isEqualTo(2L);
        assertThat(shopLogisticsList.get(2).getLogistics().getName()).isEqualTo("中通快递");
        assertThat(shopLogisticsList.get(2).getPriority()).isEqualTo(113L);

    }
    @Test
    public void retrieveShopLogisticsByShopIdOrderByPriorityAsc2() {
        List<ShopLogistics> shopLogisticsList = shopLogisticsDao
                .retrieveShopLogisticsByShopIdOrderByPriorityAsc(57197187L, 1, 10).getList();
        assertThat(shopLogisticsList.size()).isEqualTo(0L);
    }
    @Test
    public void findShopLogisticsById1(){
        ShopLogistics shopLogistics = shopLogisticsDao.findShopLogisticsById(10L);
        assertThat(shopLogistics.getShopId()).isEqualTo(4L);
        assertThat(shopLogistics.getLogisticsId()).isEqualTo(1L);
        assertThat(shopLogistics.getPriority()).isEqualTo(3L);
    }

    @Test
    public void findShopLogisticsById2(){
        ShopLogistics shopLogistics = shopLogisticsDao.findShopLogisticsById(4L);
        assertThat(shopLogistics.getShopId()).isEqualTo(2L);
        assertThat(shopLogistics.getLogisticsId()).isEqualTo(1L);
        assertThat(shopLogistics.getPriority()).isEqualTo(3L);
    }
    @Test
    public void insert1(){
        UserDto userDto = new UserDto();
        userDto.setId(3L);
        userDto.setName("shop2");
        userDto.setDepartId(2L);
        userDto.setUserLevel(1);

        ShopLogistics shopLogistics = ShopLogistics.builder()
                .logisticsId(2L)
                .secret("secret2")
                .priority(10L)
                .shopId(2L)
                .build();

        ShopLogistics bo = shopLogisticsDao.insert(shopLogistics, userDto);
        assertThat(bo.getLogisticsId()).isEqualTo(2L);
        assertThat(bo.getShopId()).isEqualTo(2L);
        assertThat(bo.getSecret()).isEqualTo("secret2");
        assertThat(bo.getPriority()).isEqualTo(10L);
        assertThat(bo.getCreatorId()).isEqualTo(3L);
        assertThat(bo.getCreatorName()).isEqualTo("shop2");

    }
}
