package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.order.OrderTestApplication;
import cn.edu.xmu.oomall.order.dao.OrderDao;
import cn.edu.xmu.oomall.order.dao.bo.Order;
import cn.edu.xmu.oomall.order.dao.bo.OrderItem;
import cn.edu.xmu.oomall.order.dao.bo.StatusNo;
import cn.edu.xmu.oomall.order.dao.openfeign.*;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.*;
import cn.edu.xmu.oomall.order.mapper.OrderItemPoMapper;
import cn.edu.xmu.oomall.order.mapper.OrderPoMapper;
import cn.edu.xmu.oomall.order.mapper.po.OrderItemPo;
import cn.edu.xmu.oomall.order.mapper.po.OrderPo;
import cn.edu.xmu.oomall.order.service.dto.ConsigneeDto;
import cn.edu.xmu.oomall.order.service.dto.OrderItemDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = OrderTestApplication.class)
@Transactional
public class OrderServiceTest {
    @MockBean
    RocketMQService rocketMQService;

    @MockBean
    ShopDao shopDao;

    @MockBean
    FreightDao freightDao;

    @MockBean
    CustomerDao customerDao;

    @MockBean
    PaymentDao paymentDao;

    @MockBean
    GoodsDao goodsDao;

    @MockBean
    RedisUtil redisUtil;

    @Autowired
    OrderDao orderDao;

    @Autowired
    OrderPoMapper orderPoMapper;

    @Autowired
    OrderItemPoMapper orderItemPoMapper;

    @Autowired
    OrderService orderService;

    UserDto user1 = new UserDto(3L, "test1", -100L, 1);
    UserDto user2 = new UserDto(2L, "test2", 1L, 1);
    UserDto user3 = new UserDto(1L, "test3", -100L, 1);

    @Test
    public void updateOrderMsg1(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        Order bo=Order.builder().id(1L).shopId(1L).message("test").build();
        orderService.updateOrderMsg(bo,user2);
        bo=orderDao.findOrderById(1L);
        assertThat(bo).isNotNull();
    }

    @Test
    public void updateOrderConsignee1(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        Order bo=Order.builder().id(39L).regionId(1L).consignee("test").address("xmu").mobile("123456").build();
        orderService.updateOrderConsignee(bo,user1);
        bo=orderDao.findOrderById(39L);
        assertThat(bo).isNotNull();
    }

    @Test
    public void packOrder1(){
        List<OrderItemDto> items=new ArrayList<>(){
            {
                add(OrderItemDto.builder().onsaleId(1L).actId(1L).quantity(100).build());
                add(OrderItemDto.builder().onsaleId(1L).actId(2L).quantity(100).build());
                add(OrderItemDto.builder().onsaleId(1L).actId(3L).quantity(100).build());
            }
        };
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

        Map<Long, List<OrderItem>> packs= orderService.packOrder(items,user3);
        List<OrderItem> pack=packs.get(1L);

        assertThat(pack.size()).isEqualTo(3);
        assertThat(pack.get(0).getActId()).isEqualTo(1L);
        assertThat(pack.get(0).getQuantity()).isEqualTo(100);
        assertThat(pack.get(0).getPrice()).isEqualTo(100L);
    }

    @Test
    public void createOrder1(){
        List<OrderItemDto> items=new ArrayList<>(){
            {
                add(OrderItemDto.builder().onsaleId(1L).actId(1L).quantity(100).build());
                add(OrderItemDto.builder().onsaleId(1L).actId(2L).quantity(100).build());
                add(OrderItemDto.builder().onsaleId(1L).actId(3L).quantity(100).build());
            }
        };
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

        ConsigneeDto consigneeDto=ConsigneeDto.builder().consignee("testname").regionId(123L).address("xmu").mobile("123456").build();
        String msg="test";
        orderService.createOrder(items,consigneeDto,msg,user3);
    }

    @Test
    public void payOrder1(){
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

        List<Long> coupons=new ArrayList<>(){
            {
                add(1L);
                add(2L);
                add(3L);
            }
        };

        IdNameDto payment=IdNameDto.builder().id(111111L).name("123456").build();
        InternalReturnObject<IdNameDto> tmp1=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),payment);

        Mockito.when(paymentDao.createPayment(Mockito.any())).thenReturn(tmp1);

        FreightDto freight=FreightDto.builder().freightPrice(1L).build();
        InternalReturnObject<FreightDto> tmp2=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),freight);

        OnsaleDto onsaleDto=OnsaleDto.builder().id(1L).maxQuantity(10000).price(100L).type((byte)0).type((byte)0).build();
        InternalReturnObject<OnsaleDto> tmp3=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),onsaleDto);

        Mockito.when(shopDao.getExpressFee(Mockito.any())).thenReturn(tmp2);
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(goodsDao.getOnsaleById(Mockito.anyLong(),Mockito.anyLong())).thenReturn(tmp3);

        orderService.payOrder(user1, newPo.getId(), 1L,coupons,1L);

    }
}
