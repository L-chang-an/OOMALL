package cn.edu.xmu.oomall.freight.dao;


import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.bo.SimpleWarehouseRegion;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseRegion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class WarehouseRegionDaoTest {
    @Autowired
    private WarehouseRegionDao warehouseRegionDao;

    @Test
    public void testSave1() {
        UserDto userDto = new UserDto();
        userDto.setId(16L);
        userDto.setUserLevel(1);
        userDto.setName("shop2");
        userDto.setDepartId(2L);
        String nowTimeStr1 = "2021-12-15T16:30:30.145";
        String nowTimeStr2 = "2027-12-15T16:30:30.145";
        DateTimeFormatter df = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime beginTime = LocalDateTime.parse(nowTimeStr1, df);
        LocalDateTime endTime = LocalDateTime.parse(nowTimeStr2, df);
        WarehouseRegion wr = WarehouseRegion.builder().regionId(1L).warehouseId(2L).beginTime(beginTime).endTime(endTime).build();
        WarehouseRegion wr2 = warehouseRegionDao.save(wr, userDto);
        assertThat(wr2.getRegionId()).isEqualTo(1L);
        assertThat(wr2.getWarehouseId()).isEqualTo(2L);
        assertThat(wr2.getModifierId()).isEqualTo(16L);
        assertThat(wr2.getModifierName()).isEqualTo("shop2");
        assertThat(wr2.getBeginTime()).isEqualTo(beginTime);
        assertThat(wr2.getEndTime()).isEqualTo(endTime);
    }

    @Test
    public void testInsert1() {
        UserDto userDto = new UserDto();
        userDto.setId(16L);
        userDto.setUserLevel(1);
        userDto.setName("shop2");
        userDto.setDepartId(2L);
        String nowTimeStr1 = "2022-12-15T16:30:30.145";
        String nowTimeStr2 = "2025-12-15T16:30:30.145";
        DateTimeFormatter df = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime beginTime = LocalDateTime.parse(nowTimeStr1, df);
        LocalDateTime endTime = LocalDateTime.parse(nowTimeStr2, df);
        WarehouseRegion wr = WarehouseRegion.builder().regionId(5L).warehouseId(10L).beginTime(beginTime).endTime(endTime).build();
        WarehouseRegion wr2 = warehouseRegionDao.insert(wr, userDto);
        assertThat(wr2.getRegionId()).isEqualTo(5L);
        assertThat(wr2.getWarehouseId()).isEqualTo(10L);
        assertThat(wr2.getCreatorId()).isEqualTo(16L);
        assertThat(wr2.getCreatorName()).isEqualTo("shop2");
        assertThat(wr2.getBeginTime()).isEqualTo(beginTime);
        assertThat(wr2.getEndTime()).isEqualTo(endTime);
    }

//    @Test
//    public void retrieveWarehouseByRegionId1() {
//        String nowTimeStr = "2022-12-15T16:30:30.145";
//        DateTimeFormatter df = DateTimeFormatter.ISO_DATE_TIME;
//        LocalDateTime nowTime = LocalDateTime.parse(nowTimeStr, df);
//        List<Warehouse> pd = Stream.of(1L, 2L, 3L, 5L, 7L, 22L).map(i -> new Warehouse(){
//            {
//                setId(i);
//            }
//        }).collect(Collectors.toList());
//        Set<Long> set = warehouseRegionDao.retrieveValidWarehouseByRegionId(1L, nowTime, pd);
//        assertThat(set.size()).isEqualTo(4);
//        Stream.of(1L,2L, 3L,5L).forEach(i -> assertThat(set.contains(i)).isEqualTo(true));
//    }
//
//    @Test
//    public void retrieveWarehouseByRegionId2() {
//        String nowTimeStr = "2025-12-15T16:30:30.145";
//        DateTimeFormatter df = DateTimeFormatter.ISO_DATE_TIME;
//        LocalDateTime nowTime = LocalDateTime.parse(nowTimeStr, df);
//        List<Warehouse> pd = Stream.of(1L, 3L, 5L, 7L, 22L).map(i -> new Warehouse(){
//            {
//                setId(i);
//            }
//        }).collect(Collectors.toList());
//        Set<Long> set = warehouseRegionDao.retrieveValidWarehouseByRegionId(1L, nowTime, pd);
//        assertThat(set.size()).isEqualTo(0);
//    }

    @Test
    public void retrieveValidRegionsByWaarehouseId() {
        String nowTimeStr = "2022-12-15T16:30:30.145";
        DateTimeFormatter df = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime nowTime = LocalDateTime.parse(nowTimeStr, df);
        List<SimpleWarehouseRegion> set = warehouseRegionDao.retrieveValidRegionsByWarehouseId(17L, nowTime, 1, 10);
        assertThat(set.size()).isEqualTo(1);
        List<SimpleWarehouseRegion> list = set.stream().collect(Collectors.toList());
        assertThat(list.get(0).getRegionId()).isEqualTo(99537L);

        String nowTimeStr2 = "2023-04-02T14:20:30.145";
        LocalDateTime nowTime2 = LocalDateTime.parse(nowTimeStr2, df);
        List<SimpleWarehouseRegion> set2 = warehouseRegionDao.retrieveValidRegionsByWarehouseId(17L, nowTime2, 1, 10);
        assertThat(set2.size()).isEqualTo(0);
    }
}
