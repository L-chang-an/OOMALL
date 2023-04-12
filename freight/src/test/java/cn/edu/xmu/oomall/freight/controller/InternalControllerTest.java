package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.controller.vo.ExpressVo;
import cn.edu.xmu.oomall.freight.dao.LogisticsCompany.JtDao;
import cn.edu.xmu.oomall.freight.service.dto.ExpressInfo;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.hamcrest.Matchers.is;

@SpringBootTest(classes = FreightApplication.class)
@AutoConfigureMockMvc
@Transactional
public class InternalControllerTest {


    private static String Internal_Express = "/internal/shops/{shopId}/packages";

    private static String Internal_Get_By_Id = "/internal/packages/{id}";
    private static String Internal_Confirm = "/internal/shops/{shopId}/packages/{id}/confirm";
    private static String Internal_Cancel = "/internal/shops/{shopId}/packages/{id}/cancel";

    private static String adminToken;
    private static String shop1Token;
    private static String shop2Token;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JtDao jtDao;

    @BeforeAll
    public static void setup() {
        JwtHelper jwtHelper = new JwtHelper();
        shop1Token = jwtHelper.createToken(2L, "shop1", 1L, 1, 3600);
        shop2Token = jwtHelper.createToken(3L, "shop2", 2L, 1, 3600);
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
    }

    @Test
    public void insertExpress1() throws Exception{
        Mockito.when(jtDao.insert(Mockito.any())).thenReturn("UT0000547463165");
        ExpressVo vo = new ExpressVo();
        vo.setShopLogisticsId(3L);
        ExpressInfo sender = ExpressInfo.builder()
                .name("王五")
                .mobile("13919718739")
                .regionId(1043L)
                .address("北京,朝阳,东坝,朝阳新城第二曙光路20号")
                .build();
        vo.setSender(sender);
        ExpressInfo delivery = ExpressInfo.builder()
                .name("赵六")
                .mobile("13919718739")
                .regionId(1043L)
                .address("北京,朝阳,东坝,朝阳新城第二曙光路21号")
                .build();
        vo.setDelivery(delivery);
        this.mockMvc.perform(MockMvcRequestBuilders.post(Internal_Express, 1L)
                        .header("authorization", shop1Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.billCode",is("UT0000547463165")));

    }


    @Test
    public void findExpressByBillCode1() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(Internal_Express, 1L)
                        .header("authorization", shop1Token)
                        .param("billCode","SF1391971873939")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.billCode",is("SF1391971873939")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.shipper.address",is("北京,朝阳,东坝,朝阳新城第二曙光路14号")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.receiver.address",is("北京,朝阳,东坝,朝阳新城第二曙光路15号")));
    }
    @Test
    public void findExpressByBillCode2() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(Internal_Express, 1L)
                        .header("authorization", shop1Token)
                        .param("billCode","UT0000547463164")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.billCode",is("UT0000547463164")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.shipper.address",is("北京,朝阳,东坝,朝阳新城第二曙光路16号")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.receiver.address",is("北京,朝阳,东坝,朝阳新城第二曙光路17号")));
    }


    @Test
    public void findExpressById1() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(Internal_Get_By_Id, 1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.billCode",is("SF1391971873939")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.shipper.address",is("北京,朝阳,东坝,朝阳新城第二曙光路14号")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.receiver.address",is("北京,朝阳,东坝,朝阳新城第二曙光路15号")));
    }
    @Test
    public void findExpressById2() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(Internal_Get_By_Id, 123456789L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(4)));
    }

    @Test
    public void confirmExpress1() throws Exception {
        Mockito.when(jtDao.cancel(Mockito.any())).thenReturn("UT0000547463164");
        ExpressVo vo = new ExpressVo();
        vo.setStatus(1L);
        this.mockMvc.perform(MockMvcRequestBuilders.put(Internal_Confirm, 1L, 8L)
                        .header("authorization", shop1Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(0)));
    }
    @Test
    public void confirmExpress2() throws Exception {
        Mockito.when(jtDao.cancel(Mockito.any())).thenReturn("UT0000547463164");
        ExpressVo vo = new ExpressVo();
        vo.setStatus(2L);
        this.mockMvc.perform(MockMvcRequestBuilders.put(Internal_Confirm, 1L, 8L)
                        .header("authorization", shop1Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(3)));
    }
    @Test
    public void confirmExpress3() throws Exception {
        Mockito.when(jtDao.cancel(Mockito.any())).thenReturn("UT0000547463164");
        ExpressVo vo = new ExpressVo();
        this.mockMvc.perform(MockMvcRequestBuilders.put(Internal_Confirm, 1L, 8L)
                        .header("authorization", shop1Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(3)));
    }

    @Test
    public void cancelExpress1() throws Exception {
        Mockito.when(jtDao.cancel(Mockito.any())).thenReturn("UT0000547463164");
        this.mockMvc.perform(MockMvcRequestBuilders.put(Internal_Cancel, 1L, 8L)
                        .header("authorization", shop1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(0)));
    }
    @Test
    public void cancelExpress2() throws Exception {
        Mockito.when(jtDao.cancel(Mockito.any())).thenReturn("UT0000547463164");
        this.mockMvc.perform(MockMvcRequestBuilders.put(Internal_Cancel, 2L, 8L)
                        .header("authorization", shop1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(17)));
    }

}
