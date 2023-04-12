package cn.edu.xmu.oomall.order.controller;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.order.OrderTestApplication;
import cn.edu.xmu.oomall.order.dao.bo.StatusNo;
import cn.edu.xmu.oomall.order.dao.openfeign.*;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.*;
import cn.edu.xmu.oomall.order.mapper.OrderItemPoMapper;
import cn.edu.xmu.oomall.order.mapper.OrderPoMapper;
import cn.edu.xmu.oomall.order.mapper.po.OrderItemPo;
import cn.edu.xmu.oomall.order.mapper.po.OrderPo;
import cn.edu.xmu.oomall.order.service.RocketMQService;
import cn.edu.xmu.oomall.order.service.dto.OrderItemDto;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(classes = OrderTestApplication.class)
@AutoConfigureMockMvc
@Transactional
public class CustomerControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    RocketMQService rocketMQService;

    @MockBean
    private ShopDao shopDao;

    @MockBean
    private FreightDao freightDao;

    @MockBean
    private CustomerDao customerDao;

    @MockBean
    private GoodsDao goodsDao;

    @MockBean
    PaymentDao paymentDao;
    @Autowired
    OrderPoMapper orderPoMapper;

    @Autowired
    OrderItemPoMapper orderItemPoMapper;

    @MockBean
    private RedisUtil redisUtil;

    private static final String ORDER_STATUS="/orders/states";
    private static final String ORDERS="/orders";
    private static final String ORDER="/orders/{id}";

    private static final String ORDER_PAY="/orders/{id}/pay";

    static String cusToken1;

    static String cusToken2;
    @BeforeAll
    public static void setup() {
        JwtHelper jwtHelper = new JwtHelper();
        cusToken1 = jwtHelper.createToken(7L, "test1", -100L, 2, 3600);
        cusToken2 = jwtHelper.createToken(1L, "test2", -100L, 2, 3600);
    }
    UserDto user1 = new UserDto(1L, "test2", -100L, 2);

    @Test
    public void getOrderStatus() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(ORDER_STATUS))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].code", is(101)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].name", is("新订单")))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void updateOrder1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        String body = "{\"consignee\": \"test\", \"regionId\": 1, \"address\": \"xmu\", \"mobile\": \"123456\"}";

        this.mockMvc.perform(MockMvcRequestBuilders.put(ORDER,1)
                        .header("authorization", cusToken2)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.AUTH_NO_RIGHT.getErrNo())))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void delOrderById1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        this.mockMvc.perform(MockMvcRequestBuilders.delete(ORDER,1)
                        .header("authorization", cusToken2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.ORDER_CHANGENOTALLOW.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void getOrdersByCustomer1() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(ORDERS)
                    .header("authorization", cusToken1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].id", is(13)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].status", is(201)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].originPrice", is(93930)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getOrderByIdForCustomer1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        InternalReturnObject<ShopDto> tmp1=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),ShopDto.builder().id(666L).status((byte)1).type((byte)1).name("test").build());
        InternalReturnObject<IdNameDto> tmp2=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),IdNameDto.builder().id(666L).name("test").build());
        InternalReturnObject<PackDto> tmp3=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),PackDto.builder().id(666L).billCode("123456789").build());

        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(tmp1);
        Mockito.when(customerDao.getCustomerById(Mockito.anyLong(),Mockito.anyLong())).thenReturn(tmp2);
        Mockito.when(freightDao.getPackById(Mockito.anyLong())).thenReturn(tmp3);

        this.mockMvc.perform(MockMvcRequestBuilders.get(ORDER,1)
                        .header("authorization", cusToken2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status", is(300)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.originPrice", is(799121)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void createOrder1() throws Exception{
        List<IdNameDto> actList=new ArrayList<>(){
            {
                add(IdNameDto.builder().id(1L).name("test").build());
                add(IdNameDto.builder().id(2L).name("test").build());
                add(IdNameDto.builder().id(3L).name("test").build());
            }
        };
        IdNameDto product=IdNameDto.builder().id(1L).name("testProduct").build();
        ShopDto shopDto=ShopDto.builder().id(1L).name("test").build();
        OnsaleDto onsaleDto=OnsaleDto.builder().id(1L).maxQuantity(10000).price(100L).type((byte)0).shop(shopDto).quantity(1000).actList(actList).product(product).build();
        InternalReturnObject<OnsaleDto> tmp1=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),onsaleDto);

        Mockito.when(goodsDao.getOnsaleById(0L,1L)).thenReturn(tmp1);

        String body="{\"items\": " +
                        "[{\"onsaleId\": 1, " +
                        "\"quantity\": 100," +
                        "\"actId\": 1,\"couponId\": 1 },{ \"onsaleId\": 1," +
                        "\"quantity\": 100," +
                        "\"actId\": 2,\"couponId\": 2},{\"onsaleId\": 1,\"quantity\": 100,\"actId\": 3,\"couponId\": 3}]," +
                "\"consignee\": \"string\"," +
                "\"mobile\": \"string\"," +
                "\"regionId\": 666," +
                "\"address\": \"string\","+
                "\"message\": \"string\"}";

        this.mockMvc.perform(MockMvcRequestBuilders.post(ORDERS)
                        .header("authorization", cusToken2)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.CREATED.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void payOrder1() throws Exception{
        OrderPo orderPo = OrderPo.builder().creatorId(user1.getId()).creatorName(user1.getName()).gmtCreate(LocalDateTime.now()).orderSn("123456")
                .status(StatusNo.NEW_ORDER.getStatusNo()).mobile("111111").address("xmu").shopId(1L).pid(0L).discountPrice(0L)
                .consignee(user1.getName()).customerId(user1.getId()).regionId(6L).message("null").build();
        OrderPo newPo=orderPoMapper.save(orderPo);

        OrderItemPo orderItemPo1 = OrderItemPo.builder().orderId(newPo.getId()).creatorId(user1.getId()).onsaleId(1L)
                .quantity(100).price(100L).discountPrice(0L).couponId(1L).activityId(123L).creatorName(user1.getName())
                .name("test").commented((byte)1).gmtCreate(LocalDateTime.now()).build();
        orderItemPoMapper.save(orderItemPo1);
        OrderItemPo orderItemPo2 = OrderItemPo.builder().orderId(newPo.getId()).creatorId(user1.getId()).onsaleId(1L)
                .quantity(100).price(100L).discountPrice(0L).couponId(2L).activityId(123L).creatorName(user1.getName())
                .name("test").commented((byte)1).gmtCreate(LocalDateTime.now()).build();
        orderItemPoMapper.save(orderItemPo2);
        OrderItemPo orderItemPo3 = OrderItemPo.builder().orderId(newPo.getId()).creatorId(user1.getId()).onsaleId(1L)
                .quantity(100).price(100L).discountPrice(0L).couponId(3L).activityId(123L).creatorName(user1.getName())
                .name("test").commented((byte)1).gmtCreate(LocalDateTime.now()).build();
        orderItemPoMapper.save(orderItemPo3);

        IdNameDto payment=IdNameDto.builder().id(111111L).name("123456").build();
        InternalReturnObject<IdNameDto> tmp1=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),payment);

        Mockito.when(paymentDao.createPayment(Mockito.any())).thenReturn(tmp1);

        FreightDto freight=FreightDto.builder().freightPrice(1L).build();
        InternalReturnObject<FreightDto> tmp2=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),freight);

        Mockito.when(shopDao.getExpressFee(Mockito.any())).thenReturn(tmp2);
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        OnsaleDto onsaleDto=OnsaleDto.builder().id(1L).maxQuantity(10000).price(100L).type((byte)0).type((byte)0).build();
        InternalReturnObject<OnsaleDto> tmp3=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),onsaleDto);
        Mockito.when(goodsDao.getOnsaleById(Mockito.anyLong(),Mockito.anyLong())).thenReturn(tmp3);


        String body="{\"points\": 1,\"shopChannelId\": 1, \"coupons\":[1,2,3]}";

        this.mockMvc.perform(MockMvcRequestBuilders.post(ORDER_PAY,newPo.getId())
                        .header("authorization", cusToken2)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }
}
