package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseLogistics;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import cn.edu.xmu.oomall.freight.mapper.WarehouseLogisticsPoMapper;
import cn.edu.xmu.oomall.freight.mapper.po.WarehouseLogisticsPo;
import cn.edu.xmu.oomall.freight.service.dto.TotalPageDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class WarehouseLogisticsDaoTest {
    @Autowired
    private WarehouseLogisticsDao warehouseLogisticsDao;

    @Autowired
    private WarehouseLogisticsPoMapper warehouseLogisticsPoMapper;

    @MockBean
    private ShopLogisticsDao shopLogisticsDao;

    private String nowTimeStr1 = "2021-12-15T16:30:30";
    private String nowTimeStr2 = "2027-12-15T16:30:30";
    private DateTimeFormatter df = DateTimeFormatter.ISO_DATE_TIME;
    private LocalDateTime beginTime = LocalDateTime.parse(nowTimeStr1, df);
    private LocalDateTime endTime = LocalDateTime.parse(nowTimeStr2, df);

    @Test
    public void existWarehouseLogistics() {
        assertThat(warehouseLogisticsDao.existWarehouseLogistics(25L, 1L)).isEqualTo(true);
        assertThat(warehouseLogisticsDao.existWarehouseLogistics(1L, 2L)).isEqualTo(true);
        assertThat(warehouseLogisticsDao.existWarehouseLogistics(1L, 1L)).isEqualTo(true);
        assertThat(warehouseLogisticsDao.existWarehouseLogistics(21L, 3L)).isEqualTo(true);
        assertThat(warehouseLogisticsDao.existWarehouseLogistics(1L, 11L)).isEqualTo(false);
        assertThat(warehouseLogisticsDao.existWarehouseLogistics(2L, 1L)).isEqualTo(false);
        assertThat(warehouseLogisticsDao.existWarehouseLogistics(111L, 11L)).isEqualTo(false);
    }

    @Test
    public void retrieveAllWarehouseLogisticsByWarehouseId() {
        TotalPageDto<WarehouseLogistics> dto = warehouseLogisticsDao.retrieveWarehouseLogisticsByWarehouseId(1L, 2, 2);
        List<WarehouseLogistics> list = dto.getList();
        assertThat(list.size()).isEqualTo(1);
        assertThat(dto.getTotal()).isEqualTo(3);
        assertThat(dto.getPages()).isEqualTo(2);
        WarehouseLogistics bo = list.get(0);
        assertThat(bo.getShopLogisticsId()).isEqualTo(2);
    }

    @Test
    public void saveTest1() {
        WarehouseLogistics warehouseLogistics = WarehouseLogistics.builder().warehouseId(14L).shopLogisticsId(3L)
                .beginTime(null).endTime(endTime).build();
        UserDto userDto = new UserDto();
        userDto.setId(16L);
        userDto.setUserLevel(1);
        userDto.setName("shop2");
        userDto.setDepartId(2L);
        warehouseLogisticsDao.save(warehouseLogistics, userDto);

        WarehouseLogisticsPo po = warehouseLogisticsPoMapper.findByWarehouseIdAndShopLogisticsId(14L, 3L);
        assertThat(po.getId()).isEqualTo(826);
        assertThat(po.getBeginTime()).isEqualTo("2022-12-02T13:57:43");
        assertThat(po.getEndTime()).isEqualTo("2027-12-15T16:30:30");
    }

    @Test
    public void saveTest2() {
        WarehouseLogistics warehouseLogistics = WarehouseLogistics.builder().warehouseId(9L).shopLogisticsId(7L)
                .beginTime(beginTime).endTime(null).build();
        UserDto userDto = new UserDto();
        userDto.setId(16L);
        userDto.setUserLevel(1);
        userDto.setName("shop2");
        userDto.setDepartId(2L);
        warehouseLogisticsDao.save(warehouseLogistics, userDto);

        WarehouseLogisticsPo po = warehouseLogisticsPoMapper.findByWarehouseIdAndShopLogisticsId(9L, 7L);
        assertThat(po.getId()).isEqualTo(876);
        assertThat(po.getBeginTime()).isEqualTo("2021-12-15T16:30:30");
        assertThat(po.getEndTime()).isEqualTo("2023-04-02T13:58:09");
    }
}
