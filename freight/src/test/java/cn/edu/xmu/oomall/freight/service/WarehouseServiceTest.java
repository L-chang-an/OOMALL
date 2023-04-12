package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.bo.Region;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseRegion;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import cn.edu.xmu.oomall.freight.service.dto.RegionWarehouseDto;
import cn.edu.xmu.oomall.freight.service.dto.WarehouseDto;
import cn.edu.xmu.oomall.freight.service.dto.WarehouseRegionDto;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class WarehouseServiceTest {
    @Autowired
    private WarehouseService warehouseService;

    @MockBean
    private RegionDao regionDao;
    @MockBean
    private RedisUtil redisUtil;

    @Test
    public void getWarehouses1() {
        List<Region> testRegions = Stream.of(new Region(1068L, "黑庄户村委会")
                , new Region(200397L, "徐溪村委会")
                , new Region(304646L, "大张村委会")
                , new Region(104859L, "五家子村委会")).collect(Collectors.toList());
        final int excpetLength = testRegions.size();
        testRegions.stream().forEach( region -> Mockito.when(regionDao.getRegionById(region.getId())).thenReturn(new InternalReturnObject<>(new Region(){
            {
                setId(region.getId());
                setName(region.getName());
            }
        })));
        List<WarehouseDto> list = warehouseService.getWarehouses(2L, 1, 15).getList();
        assertThat(list.size()).isEqualTo(excpetLength);
        IntStream.range(0, excpetLength-1).forEach(i -> assertThat(list.get(i+1).getPriority() >= list.get(i).getPriority()).isEqualTo(true));
        IntStream.range(0, excpetLength).forEach(i -> assertThat(list.get(i).getInvalid()).isEqualTo((byte)0));
        WarehouseDto dto = list.get(0);
        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getName()).isEqualTo("黑庄户仓库");
        assertThat(dto.getAddress()).isEqualTo("北京,朝阳,黑庄户,黑庄户曙光路14号");
        assertThat(dto.getSenderName()).isEqualTo("刘雨堡");
        assertThat(dto.getCreatedBy().getId()).isEqualTo(1);
        assertThat(dto.getCreatedBy().getUserName()).isEqualTo("admin");
        assertThat(dto.getRegion().getId()).isEqualTo(testRegions.get(0).getId());
        assertThat(dto.getRegion().getName()).isEqualTo(testRegions.get(0).getName());
    }

    @Test
    public void getWarehouses2() {
        List<WarehouseDto> list = warehouseService.getWarehouses(101L, 1, 10).getList();
        assertThat(list.size()).isEqualTo(0);
    }

    @Test
    public void updateWarehouseRegion() {
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
        WarehouseRegion wr2 = warehouseService.updateWarehouseRegion(2L, 2L, 1L, beginTime, endTime, userDto);
        assertThat(wr2.getRegionId()).isEqualTo(1L);
        assertThat(wr2.getWarehouseId()).isEqualTo(2L);
        assertThat(wr2.getModifierId()).isEqualTo(16L);
        assertThat(wr2.getModifierName()).isEqualTo("shop2");
        assertThat(wr2.getBeginTime()).isEqualTo(beginTime);
        assertThat(wr2.getEndTime()).isEqualTo(endTime);
    }

    @Test
    public void createWarehouseRegionByShopIdAndRegionIdAndWarehouseId1() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(regionDao.getRegionById(25L)).thenReturn(new InternalReturnObject<>(new Region(){
            {
                setId(25L);
            }
        }));
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
        WarehouseRegion warehouseRegion = warehouseService.createWarehouseRegion(2L,
                17L, 25L, beginTime, endTime, userDto);
        assertThat(warehouseRegion.getRegionId()).isEqualTo(25L);
        assertThat(warehouseRegion.getWarehouseId()).isEqualTo(17L);
        assertThat(warehouseRegion.getBeginTime()).isEqualTo(beginTime);
        assertThat(warehouseRegion.getEndTime()).isEqualTo(endTime);
    }

    @Test
    public void retrieveRegioneByShopIdAndWarehouseId() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        String nowTimeStr = "2022-12-15T16:30:30.145";
        DateTimeFormatter df = DateTimeFormatter.ISO_DATE_TIME;
        Mockito.when(regionDao.getRegionById(99537L)).thenReturn(new InternalReturnObject<>(new Region(){
            {
                setId(99537L);
                setName("内蒙古自治区");
                setCreatorId(1L);
                setCreatorName("admin");
            }
        }));
        LocalDateTime nowTime = LocalDateTime.parse(nowTimeStr, df);
        PageDto<WarehouseRegionDto> dtos = warehouseService.getWarehouseRegions(2L, 17L, nowTime, 1, 10);
        List<WarehouseRegionDto> list = dtos.getList();
        assertThat(list.size()).isEqualTo(1);
        WarehouseRegionDto dto = list.get(0);
        assertThat(dto.getCreator().getId()).isEqualTo(1L);
        assertThat(dto.getRegion().getName()).isEqualTo("内蒙古自治区");
        assertThat(dto.getRegion().getId()).isEqualTo(99537L);
    }

    @Test
    public void retrieveWarehouseByShopIdAndRegionId() {
        List<Region> testRegions = Stream.of(new Region(1068L, "黑庄户村委会")
                , new Region(200397L, "徐溪村委会")
                , new Region(304646L, "大张村委会")
                , new Region(104859L, "五家子村委会")).collect(Collectors.toList());
        final int excpetLength = testRegions.size();
        testRegions.stream().forEach( region -> Mockito.when(regionDao.getRegionById(region.getId())).thenReturn(new InternalReturnObject<>(new Region(){
            {
                setId(region.getId());
                setName(region.getName());
            }
        })));
        String nowTimeStr = "2022-12-15T16:30:30.145";
        DateTimeFormatter df = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime nowTime = LocalDateTime.parse(nowTimeStr, df);
        List<RegionWarehouseDto> warehouses = warehouseService.getRegionWarehouses(2L, 1L,nowTime, 1, 10).getList();
        assertThat(warehouses.size()).isEqualTo(1);
        RegionWarehouseDto warehouse = warehouses.get(0);
        assertThat(warehouse.getWarehouse().getId()).isEqualTo(2L);
        assertThat(warehouse.getWarehouse().getName()).isEqualTo("黑庄户仓库");
        assertThat(warehouse.getCreator().getId()).isEqualTo(1);
        assertThat(warehouse.getCreator().getUserName()).isEqualTo("admin");
        assertThat(warehouse.getStatus()).isEqualTo(0);
        assertThat(warehouse.getWarehouse().getInvalid()).isEqualTo(0);

        List<RegionWarehouseDto> warehouses2 = warehouseService.getRegionWarehouses(2L, 1L,nowTime, 2, 10).getList();
        assertThat(warehouses2.size()).isEqualTo(0);
    }
}
