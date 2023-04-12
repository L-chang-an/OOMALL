package cn.edu.xmu.oomall.order.controller;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.order.OrderTestApplication;
import cn.edu.xmu.oomall.order.dao.openfeign.CustomerDao;
import cn.edu.xmu.oomall.order.dao.openfeign.FreightDao;
import cn.edu.xmu.oomall.order.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.order.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.IdNameDto;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.PackDto;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.ShopDto;
import cn.edu.xmu.oomall.order.service.RocketMQService;
import cn.edu.xmu.oomall.order.service.dto.ConsigneeDto;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(classes = OrderTestApplication.class)
@AutoConfigureMockMvc
@Transactional
public class AdminControllerTest {
    @Autowired
    MockMvc mockMvc;

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

    private static final String ORDERS_SHOP="/shops/{shopId}/orders";
    private static final String ORDER_SHOP="/shops/{shopId}/orders/{id}";
    private static final String ORDER_CONFIRM="/shops/{shopId}/orders/{id}/confirm";

    static String adminToken;
    static String shopToken;

    @BeforeAll
    public static void setup() {
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(7L, "13088admin", 0L, 1, 3600);
        shopToken = jwtHelper.createToken(15L, "shop1", 1L, 1, 3600);
    }

    @Test
    public void getOrdersByShop1() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(ORDERS_SHOP,1)
                        .header("authorization", shopToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].status", is(300)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].originPrice", is(799121)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getOrderByIdForShop1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        InternalReturnObject<ShopDto> tmp1=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),ShopDto.builder().id(666L).status((byte)1).type((byte)1).name("test").build());
        InternalReturnObject<IdNameDto> tmp2=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),IdNameDto.builder().id(666L).name("test").build());
        InternalReturnObject<PackDto> tmp3=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),PackDto.builder().id(666L).billCode("123456789").build());

        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(tmp1);
        Mockito.when(customerDao.getCustomerById(Mockito.anyLong(),Mockito.anyLong())).thenReturn(tmp2);
        Mockito.when(freightDao.getPackById(Mockito.anyLong())).thenReturn(tmp3);

        this.mockMvc.perform(MockMvcRequestBuilders.get(ORDER_SHOP,1,1)
                        .header("authorization", shopToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status", is(300)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.originPrice", is(799121)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateOrder1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        String body = "{\"message\":\"test\"}";

        this.mockMvc.perform(MockMvcRequestBuilders.put(ORDER_SHOP,1,1)
                        .header("authorization", shopToken)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void delOrderById1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        this.mockMvc.perform(MockMvcRequestBuilders.delete(ORDER_SHOP,1,1)
                        .header("authorization", shopToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.ORDER_CHANGENOTALLOW.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void confirmOrder1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        InternalReturnObject<ConsigneeDto> tmp1=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),ConsigneeDto.builder().consignee("test").regionId(666L).mobile("111111").address("厦门").build());
        InternalReturnObject<PackDto> tmp2=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),PackDto.builder().id(666L).billCode("123456789").build());
        InternalReturnObject<PackDto> tmp3=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),PackDto.builder().id(666L).billCode("123456789").build());

        Mockito.when(shopDao.getShopConsigneeById(Mockito.anyLong())).thenReturn(tmp1);
        Mockito.when(freightDao.createShipmentBill(Mockito.anyLong(),Mockito.any())).thenReturn(tmp2);
        Mockito.when(freightDao.getPackById(Mockito.anyLong())).thenReturn(tmp3);

        this.mockMvc.perform(MockMvcRequestBuilders.put(ORDER_CONFIRM,1,1016)
                        .header("authorization", shopToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }
}
