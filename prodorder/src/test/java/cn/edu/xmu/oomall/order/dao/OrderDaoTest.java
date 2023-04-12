package cn.edu.xmu.oomall.order.dao;

import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.order.OrderTestApplication;
import cn.edu.xmu.oomall.order.dao.bo.Order;
import cn.edu.xmu.oomall.order.dao.openfeign.CustomerDao;
import cn.edu.xmu.oomall.order.dao.openfeign.FreightDao;
import cn.edu.xmu.oomall.order.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.order.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.order.service.RocketMQService;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = OrderTestApplication.class)
@Transactional
public class OrderDaoTest {
    @MockBean
    RocketMQService rocketMQService;

    @MockBean
    ShopDao shopDao;

    @MockBean
    FreightDao freightDao;

    @MockBean
    CustomerDao customerDao;

    @MockBean
    GoodsDao goodsDao;

    @MockBean
    RedisUtil redisUtil;

    @Autowired
    OrderDao orderDao;

    private String nowTimeStr1 = "2001-12-15T16:30:30";
    private String nowTimeStr2 = "2027-12-15T16:30:30";
    private DateTimeFormatter df = DateTimeFormatter.ISO_DATE_TIME;
    private LocalDateTime beginTime = LocalDateTime.parse(nowTimeStr1, df);
    private LocalDateTime endTime = LocalDateTime.parse(nowTimeStr2, df);

    @Test
    public void retrieveOrdersByCustomer1(){
        List<Order> bos=orderDao.retrieveOrdersByCustomer(7L,"",200,beginTime,endTime,1,10);
        assertThat(bos.size()).isGreaterThan(1);
        assertThat(bos.get(0).getId()).isEqualTo(13L);
        assertThat(bos.get(0).getOrderSn()).isEqualTo("2016102399670");
    }
    @Test
    public void retrieveOrdersByShop1(){
        List<Order> bos=orderDao.retrieveOrdersByShop(1L,7L,"",beginTime,endTime,1,10);
        assertThat(bos.size()).isGreaterThan(1);
        assertThat(bos.get(0).getId()).isEqualTo(13L);
        assertThat(bos.get(0).getOrderSn()).isEqualTo("2016102399670");
    }
    @Test
    public void retrieveOrdersByShop2(){
        List<Order> bos=orderDao.retrieveOrdersByShop(0L,7L,"",beginTime,endTime,1,10);
        assertThat(bos.size()).isGreaterThan(1);
        assertThat(bos.get(0).getId()).isEqualTo(13L);
        assertThat(bos.get(0).getOrderSn()).isEqualTo("2016102399670");
    }
    @Test
    public void retrieveOrdersByShop3(){
        List<Order> bos=orderDao.retrieveOrdersByShop(0L,0L,"",beginTime,endTime,1,10);
        assertThat(bos.size()).isGreaterThan(1);
        assertThat(bos.get(0).getId()).isEqualTo(1L);
        assertThat(bos.get(0).getOrderSn()).isEqualTo("2016102361242");
    }
}
