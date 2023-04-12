package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.LogisticsCompany.JtDao;
import cn.edu.xmu.oomall.freight.dao.bo.Express;
import cn.edu.xmu.oomall.freight.service.dto.ExpressDto;
import cn.edu.xmu.oomall.freight.service.dto.SimpleExpressDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class ExpressServiceTest {
    @Autowired
    private ExpressService expressService;

    @MockBean
    private JtDao jtDao;

    @Test
    public void findExpressByBillCode1(){
        ExpressDto express = expressService.findExpressByBillCode(1L, "SF1391971873939");
        assertThat(express.getId()).isEqualTo(1L);
        assertThat(express.getBillCode()).isEqualTo("SF1391971873939");
        assertThat(express.getLogistics().getId()).isEqualTo(1L);
        assertThat(express.getLogistics().getName()).isEqualTo("顺丰快递");

        assertThat(express.getShipper().getName()).isEqualTo("阮杰");
        assertThat(express.getShipper().getMobile()).isEqualTo("13919718739");
        assertThat(express.getShipper().getRegionId()).isEqualTo(1043L);
        assertThat(express.getShipper().getAddress()).isEqualTo("北京,朝阳,东坝,朝阳新城第二曙光路14号");

        assertThat(express.getReceiver().getName()).isEqualTo("杰阮");
        assertThat(express.getReceiver().getMobile()).isEqualTo("11871973939");
        assertThat(express.getReceiver().getRegionId()).isEqualTo(1043L);
        assertThat(express.getReceiver().getAddress()).isEqualTo("北京,朝阳,东坝,朝阳新城第二曙光路15号");

    }
    @Test
    public void findExpressById1() {
        ExpressDto express = expressService.findExpressById(1L);
        assertThat(express.getId()).isEqualTo(1L);
        assertThat(express.getBillCode()).isEqualTo("SF1391971873939");
        assertThat(express.getLogistics().getId()).isEqualTo(1L);
        assertThat(express.getLogistics().getName()).isEqualTo("顺丰快递");

        assertThat(express.getShipper().getName()).isEqualTo("阮杰");
        assertThat(express.getShipper().getMobile()).isEqualTo("13919718739");
        assertThat(express.getShipper().getRegionId()).isEqualTo(1043L);
        assertThat(express.getShipper().getAddress()).isEqualTo("北京,朝阳,东坝,朝阳新城第二曙光路14号");

        assertThat(express.getReceiver().getName()).isEqualTo("杰阮");
        assertThat(express.getReceiver().getMobile()).isEqualTo("11871973939");
        assertThat(express.getReceiver().getRegionId()).isEqualTo(1043L);
        assertThat(express.getReceiver().getAddress()).isEqualTo("北京,朝阳,东坝,朝阳新城第二曙光路15号");
    }

    @Test
    public void confirmExpress1(){
        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("shop1");
        userDto.setDepartId(2L);
        userDto.setUserLevel(1);

        Express express = Express.builder()
                .id(1L)
                .status(1L)
                .shopId(1L)
                .build();
        expressService.confirmExpress(express,userDto);
    }


    @Test
    public void createExpress1(){
        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("shop1");
        userDto.setDepartId(2L);
        userDto.setUserLevel(1);
        Express bo = Express.builder()
                .shopLogisticsId(2L)
                .senderName("张三")
                .senderMobile("12345678910")
                .senderAddress("北京,朝阳,东坝,朝阳新城第二曙光路16号")
                .senderRegionId(1043L)
                .deliverName("李四")
                .deliverMobile("10987654321")
                .deliverAddress("北京,朝阳,东坝,朝阳新城第二曙光路17号")
                .deliverRegionId(1043L)
                .build();
        SimpleExpressDto expressDto = expressService.createExpress(bo, userDto);
        assertThat(expressDto.getBillCode()).isNotNull();
    }

    @Test
    public void createExpress2(){
        Mockito.when(jtDao.insert(Mockito.any())).thenReturn("UT0000547463164");
        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("shop1");
        userDto.setDepartId(2L);
        userDto.setUserLevel(1);
        Express bo = Express.builder()
                .shopLogisticsId(3L)
                .senderName("张三")
                .senderMobile("12345678910")
                .senderAddress("北京,朝阳,东坝,朝阳新城第二曙光路16号")
                .senderRegionId(1043L)
                .deliverName("李四")
                .deliverMobile("10987654321")
                .deliverAddress("北京,朝阳,东坝,朝阳新城第二曙光路17号")
                .deliverRegionId(1043L)
                .build();
        SimpleExpressDto expressDto = expressService.createExpress(bo, userDto);
        assertThat(expressDto.getBillCode()).isEqualTo("UT0000547463164");
    }

    @Test
    public void cancelExpress1(){
        Mockito.when(jtDao.cancel(Mockito.any())).thenReturn("UT0000547463164");
        Express bo = Express.builder()
                .id(8L)
                .shopId(1L)
                .build();
        expressService.cancelExpress(bo);
    }


}
