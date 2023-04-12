package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.controller.vo.ShopLogisticsVo;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import cn.edu.xmu.oomall.freight.service.dto.LogisticsDto;
import cn.edu.xmu.oomall.freight.service.dto.ShopLogisticsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class LogisticsServiceTest {
    @Autowired
    private LogisticsService logisticsService;
    @Test
    public void retrieveShopLogisticsByShopId1() {
        List<ShopLogisticsDto> shopLogisticsDtoList = logisticsService.retrieveShopLogisticsByShopId(1L, 1, 10).getList();
        assertThat(shopLogisticsDtoList.size()).isEqualTo(3);

        assertThat(shopLogisticsDtoList.get(0).getLogistics().getId()).isEqualTo(3L);
        assertThat(shopLogisticsDtoList.get(0).getLogistics().getName()).isEqualTo("极兔速递");
        assertThat(shopLogisticsDtoList.get(0).getPriority()).isEqualTo(2L);
        assertThat(shopLogisticsDtoList.get(0).getInvalid()).isEqualTo(1L);

        assertThat(shopLogisticsDtoList.get(1).getLogistics().getId()).isEqualTo(1L);
        assertThat(shopLogisticsDtoList.get(1).getLogistics().getName()).isEqualTo("顺丰快递");
        assertThat(shopLogisticsDtoList.get(1).getPriority()).isEqualTo(3L);
        assertThat(shopLogisticsDtoList.get(1).getInvalid()).isEqualTo(0L);

        assertThat(shopLogisticsDtoList.get(2).getLogistics().getId()).isEqualTo(2L);
        assertThat(shopLogisticsDtoList.get(2).getLogistics().getName()).isEqualTo("中通快递");
        assertThat(shopLogisticsDtoList.get(2).getPriority()).isEqualTo(113L);
        assertThat(shopLogisticsDtoList.get(2).getInvalid()).isEqualTo(0L);
    }
    @Test
    public void retrieveLogisticsByBillCode1(){
        List<LogisticsDto> LogisticsDto = logisticsService.retrieveLogisticsByBillCode("SF1391971873939");
        assertThat(LogisticsDto.size()).isEqualTo(1);
        assertThat(LogisticsDto.get(0).getId()).isEqualTo(1L);
        assertThat(LogisticsDto.get(0).getName()).isEqualTo("顺丰快递");
    }
    @Test
    public void retrieveLogisticsByBillCode2(){
        List<LogisticsDto> LogisticsDto = logisticsService.retrieveLogisticsByBillCode(null);
        assertThat(LogisticsDto.size()).isEqualTo(3);
        assertThat(LogisticsDto.get(0).getId()).isEqualTo(1L);
        assertThat(LogisticsDto.get(0).getName()).isEqualTo("顺丰快递");
        assertThat(LogisticsDto.get(1).getId()).isEqualTo(2L);
        assertThat(LogisticsDto.get(1).getName()).isEqualTo("中通快递");
        assertThat(LogisticsDto.get(2).getId()).isEqualTo(3L);
        assertThat(LogisticsDto.get(2).getName()).isEqualTo("极兔速递");
    }

    @Test
    public void createShoplogistics1(){
        UserDto userDto = new UserDto();
        userDto.setId(3L);
        userDto.setName("shop2");
        userDto.setDepartId(2L);
        userDto.setUserLevel(1);

        ShopLogisticsVo shopLogisticsVo = new ShopLogisticsVo();
        shopLogisticsVo.setLogisticsId(2L);
        shopLogisticsVo.setSecret("secret2");
        shopLogisticsVo.setPriority(10L);
        shopLogisticsVo.setShopId(2L);

        ShopLogisticsDto shoplogisticsDto = logisticsService.createShoplogistics(shopLogisticsVo, userDto);
        assertThat(shoplogisticsDto.getLogistics().getId()).isEqualTo(2L);
        assertThat(shoplogisticsDto.getSecret()).isEqualTo("secret2");
        assertThat(shoplogisticsDto.getPriority()).isEqualTo(10L);
        assertThat(shoplogisticsDto.getCreatedBy().getId()).isEqualTo(3L);
        assertThat(shoplogisticsDto.getCreatedBy().getUserName()).isEqualTo("shop2");

    }


}
