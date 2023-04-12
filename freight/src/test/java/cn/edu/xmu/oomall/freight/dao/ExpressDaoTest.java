package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.LogisticsCompany.JtDao;
import cn.edu.xmu.oomall.freight.dao.bo.Express;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class ExpressDaoTest {
    @Autowired
    private ExpressDao expressDao;

    @MockBean
    private JtDao jtDao;

    @Test
    public void findExpressByBillCode1(){
        String billCode = "SF1391971873939";
        Express express = expressDao.findExpressByBillCode(billCode);
        assertThat(express.getBillCode()).isEqualTo("SF1391971873939");
        assertThat(express.getId()).isEqualTo(1L);
        assertThat(express.getDeliverAddress()).isEqualTo("北京,朝阳,东坝,朝阳新城第二曙光路15号");
        assertThat(express.getSenderAddress()).isEqualTo("北京,朝阳,东坝,朝阳新城第二曙光路14号");
    }
    @Test
    public void findExpressById1() {
        Express express = expressDao.findExpressById(1L);
        assertThat(express.getBillCode()).isEqualTo("SF1391971873939");
        assertThat(express.getId()).isEqualTo(1L);
        assertThat(express.getDeliverAddress()).isEqualTo("北京,朝阳,东坝,朝阳新城第二曙光路15号");
        assertThat(express.getSenderAddress()).isEqualTo("北京,朝阳,东坝,朝阳新城第二曙光路14号");
    }
    @Test
    public void insert1() {
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
        Express express = expressDao.insert(bo, userDto);
        assertThat(express.getBillCode()).isNotNull();
    }
    @Test
    public void insert2() {
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
        Express express = expressDao.insert(bo, userDto);
        assertThat(express.getBillCode()).isEqualTo("UT0000547463164");
    }
    @Test
    public void cancel1(){
        Mockito.when(jtDao.cancel(Mockito.any())).thenReturn("UT0000547463164");
        Express bo = Express.builder()
                .id(8L)
                .shopId(1L)
                .build();
        expressDao.cancel(bo);
    }

    @Test
    public void save1() {
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
        String key = expressDao.save(express, userDto);
        assertThat(key).isEqualTo("E1");
    }

}
