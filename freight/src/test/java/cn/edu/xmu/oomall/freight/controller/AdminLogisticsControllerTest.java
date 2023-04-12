package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.controller.vo.InfoVo;
import cn.edu.xmu.oomall.freight.controller.vo.ShopLogisticsVo;
import cn.edu.xmu.oomall.freight.dao.bo.Region;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import cn.edu.xmu.oomall.freight.mapper.UndeliverablePoMapper;
import cn.edu.xmu.oomall.freight.mapper.po.UndeliverablePo;
import cn.edu.xmu.oomall.freight.mapper.po.WarehouseLogisticsPo;
import cn.edu.xmu.oomall.freight.service.dto.WarehouseLogisticsDto;
import com.alibaba.fastjson.JSONObject;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest(classes = FreightApplication.class)
@AutoConfigureMockMvc
@Transactional
public class AdminLogisticsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RegionDao regionDao;
    @Autowired
    private UndeliverablePoMapper undeliverablePoMapper;

    private static final String Freight_Logistics = "/logistics";
    private static final String Freight_Shop_All_Logistics = "/shops/{shopId}/shoplogistics";
    private static final String Freight_Shop_Logistics = "/shops/{shopId}/shoplogistics/{id}";
    private static final String Freight_Shop_Logistics_Suspend = "/shops/{shopId}/shoplogistics/{id}/suspend";
    private static final String Freight_Shop_Logistics_Resume = "/shops/{shopId}/shoplogistics/{id}/resume";
    private static final String Freight_Shop_Logistics_UndeliverableRegion = "/shops/{shopId}/shoplogistics/{id}/undeliverableregions";
    private static final String Freight_Shop_Logistics_Region_Undeliverable = "/shops/{shopId}/shoplogistics/{id}/regions/{rid}/undeliverable";
    private static String adminToken;
    private static String shop1Token;
    private static String shop2Token;

    String nowTimeStr1 = "2021-12-15T16:30:30";
    String nowTimeStr2 = "2027-12-15T16:30:30";
    DateTimeFormatter df = DateTimeFormatter.ISO_DATE_TIME;
    LocalDateTime beginTime = LocalDateTime.parse(nowTimeStr1, df);
    LocalDateTime endTime = LocalDateTime.parse(nowTimeStr2, df);


    @BeforeAll
    public static void setup() {
        JwtHelper jwtHelper = new JwtHelper();
        shop1Token = jwtHelper.createToken(2L, "shop1", 1L, 1, 3600);
        shop2Token = jwtHelper.createToken(3L, "shop2", 2L, 1, 3600);
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
    }

    @Test
    public void getAllShopLogisticsUndeliverable1() throws Exception {
        Mockito.when(regionDao.getRegionById(483250L)).thenReturn(new InternalReturnObject<>(
                new Region() {
                    {
                        setId(483250L);
                        setName("广东省");
                    }
                }
        ));
        InfoVo vo = new InfoVo();
        vo.setBeginTime(beginTime);
        vo.setEndTime(endTime);
        this.mockMvc.perform(MockMvcRequestBuilders.get(Freight_Shop_Logistics_UndeliverableRegion, 1L, 1L)
                        .header("authorization", shop1Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list.length()",is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].region.id",is(483250)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].region.name",is("广东省")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].beginTime",is("2022-12-02T22:28:43")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].endTime",is("2023-12-02T22:28:49")));
    }

    @Test
    public void getAllShopLogisticsUndeliverable2() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(Freight_Shop_Logistics_UndeliverableRegion, 1L, 2L)
                        .header("authorization", shop1Token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list.length()",is(0)));

        this.mockMvc.perform(MockMvcRequestBuilders.get(Freight_Shop_Logistics_UndeliverableRegion, 1L, 4L)
                        .header("authorization", shop1Token))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));
    }


    @Test
    public void createUndeliverable1() throws Exception {
        Mockito.when(regionDao.getRegionById(4L)).thenReturn(new InternalReturnObject<>(
                new Region() {
                    {
                        setId(4L);
                        setName("东华门街道办事处");
                    }
                }
        ));
        InfoVo vo = new InfoVo();
        vo.setBeginTime(beginTime);
        vo.setEndTime(endTime);
        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Shop_Logistics_Region_Undeliverable, 2L, 4L, 4L)
                        .header("authorization", shop2Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));

        UndeliverablePo po = undeliverablePoMapper.findByRegionIdAndShopLogisticsId(4L, 4L);
        assertThat(po.getCreatorId()).isEqualTo(3L);
        assertThat(po.getCreatorName()).isEqualTo("shop2");
        assertThat(po.getShopLogisticsId()).isEqualTo(4L);
        assertThat(po.getRegionId()).isEqualTo(4L);
        assertThat(po.getBeginTime()).isEqualTo(beginTime);
        assertThat(po.getEndTime()).isEqualTo(endTime);
    }

    @Test
    public void createUndeliverable2() throws Exception {
        InfoVo vo = new InfoVo();
        vo.setEndTime(beginTime);
        vo.setBeginTime(endTime);
        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Shop_Logistics_Region_Undeliverable, 2L, 4L, 4L)
                        .header("authorization", shop2Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.LATE_BEGINTIME.getErrNo())));

        vo.setEndTime(null);
        vo.setBeginTime(beginTime);
        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Shop_Logistics_Region_Undeliverable, 2L, 4L, 4L)
                        .header("authorization", shop2Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.PARAMETER_MISSED.getErrNo())));

        vo.setEndTime(endTime);
        vo.setBeginTime(null);
        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Shop_Logistics_Region_Undeliverable, 2L, 4L, 4L)
                        .header("authorization", shop2Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.PARAMETER_MISSED.getErrNo())));
    }

    @Test
    public void createUndeliverable3() throws Exception {
        InfoVo vo = new InfoVo();
        vo.setEndTime(endTime);
        vo.setBeginTime(beginTime);
        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Shop_Logistics_Region_Undeliverable, 2L, 4L, 499999999L)
                        .header("authorization", shop2Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));

        Mockito.when(regionDao.getRegionById(4L)).thenReturn(new InternalReturnObject<>(
                new Region() {
                    {
                        setId(4L);
                        setName("东华门街道办事处");
                    }
                }
        ));

        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Shop_Logistics_Region_Undeliverable, 2L, 99L, 4L)
                        .header("authorization", shop2Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));
    }

    @Test
    public void updateUndeliverable1() throws Exception {
        Mockito.when(regionDao.getRegionById(483250L)).thenReturn(new InternalReturnObject<>(
                new Region() {
                    {
                        setId(483250L);
                        setName("广东省");
                    }
                }
        ));
        InfoVo vo = new InfoVo();
        vo.setBeginTime(beginTime);
        vo.setEndTime(endTime);
        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Shop_Logistics_Region_Undeliverable, 1L, 1L, 483250L)
                        .header("authorization", shop1Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));

        UndeliverablePo po = undeliverablePoMapper.findByRegionIdAndShopLogisticsId(483250L, 1L);
        assertThat(po.getModifierId()).isEqualTo(2L);
        assertThat(po.getModifierName()).isEqualTo("shop1");
        assertThat(po.getShopLogisticsId()).isEqualTo(1L);
        assertThat(po.getRegionId()).isEqualTo(483250L);
        assertThat(po.getBeginTime()).isEqualTo(beginTime);
        assertThat(po.getEndTime()).isEqualTo(endTime);
    }

    @Test
    public void updateUndeliverable2() throws Exception {
        Mockito.when(regionDao.getRegionById(483250L)).thenReturn(new InternalReturnObject<>(
                new Region() {
                    {
                        setId(483250L);
                        setName("广东省");
                    }
                }
        ));
        InfoVo vo = new InfoVo();
        vo.setEndTime(beginTime);
        vo.setBeginTime(endTime);
        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Shop_Logistics_Region_Undeliverable, 1L, 1L, 483250L)
                        .header("authorization", shop1Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.LATE_BEGINTIME.getErrNo())));

        vo.setEndTime(null);
        vo.setBeginTime(beginTime);
        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Shop_Logistics_Region_Undeliverable, 1L, 1L, 483250L)
                        .header("authorization", shop1Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));

        vo.setEndTime(endTime);
        vo.setBeginTime(null);
        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Shop_Logistics_Region_Undeliverable, 1L, 1L, 483250L)
                        .header("authorization", shop1Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));
    }

    @Test
    public void updateUndeliverable3() throws Exception {
        Mockito.when(regionDao.getRegionById(483250L)).thenReturn(new InternalReturnObject<>(
                new Region() {
                    {
                        setId(483250L);
                        setName("广东省");
                    }
                }
        ));
        InfoVo vo = new InfoVo();
        vo.setEndTime(endTime);
        vo.setBeginTime(beginTime);
        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Shop_Logistics_Region_Undeliverable, 1L, 7L, 483250L)
                        .header("authorization", shop1Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));

        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Shop_Logistics_Region_Undeliverable, 1L, 1L, 999994250L)
                        .header("authorization", shop1Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));

        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Shop_Logistics_Region_Undeliverable, 1L, 2L, 483250L)
                        .header("authorization", shop1Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));

    }

    @Test
    public void deleteUndeliverable1() throws Exception {
        Mockito.when(regionDao.getRegionById(483250L)).thenReturn(new InternalReturnObject<>(
                new Region() {
                    {
                        setId(483250L);
                        setName("广东省");
                    }
                }
        ));
        this.mockMvc.perform(MockMvcRequestBuilders.delete(Freight_Shop_Logistics_Region_Undeliverable, 1L, 1L, 483250L)
                        .header("authorization", shop1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));

        UndeliverablePo po = undeliverablePoMapper.findByRegionIdAndShopLogisticsId(483250L, 1L);
        assertThat(po).isEqualTo(null);
    }

    @Test
    public void deleteUndeliverable2() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete(Freight_Shop_Logistics_Region_Undeliverable, 1L, 9L, 483250L)
                        .header("authorization", shop1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));

        this.mockMvc.perform(MockMvcRequestBuilders.delete(Freight_Shop_Logistics_Region_Undeliverable, 1L, 1L, 4834214220L)
                        .header("authorization", shop1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));

        Mockito.when(regionDao.getRegionById(483250L)).thenReturn(new InternalReturnObject<>(
                new Region() {
                    {
                        setId(483250L);
                        setName("广东省");
                    }
                }
        ));

        this.mockMvc.perform(MockMvcRequestBuilders.delete(Freight_Shop_Logistics_Region_Undeliverable, 1L, 2L, 483250L)
                        .header("authorization", shop1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));
    }

    @Test
    public void retrieveShoplogisticsByShopId1() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(Freight_Shop_All_Logistics, 2L)
                        .header("authorization", shop2Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].logistics.name",is("顺丰快递")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[1].logistics.name",is("极兔速递")));

    }
    @Test
    public void retrieveShoplogisticsByShopId2() throws Exception {
        JwtHelper jwtHelper = new JwtHelper();
        String shop11Token = jwtHelper.createToken(12L, "shop11", 11L, 1, 3600);
        this.mockMvc.perform(MockMvcRequestBuilders.get(Freight_Shop_All_Logistics, "11")
                        .header("authorization", shop11Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list.length()",is(0)));

    }

    @Test
    public void createShoplogistics1() throws Exception {
        ShopLogisticsVo shopLogisticsVo = new ShopLogisticsVo();
        shopLogisticsVo.setLogisticsId(2L);
        shopLogisticsVo.setSecret("secret2");
        shopLogisticsVo.setPriority(10L);
        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Shop_All_Logistics, 2L)
                        .header("authorization", shop2Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(shopLogisticsVo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.logistics.name",is("中通快递")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.secret",is("secret2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.priority",is(10)));
    }
    @Test
    public void createShoplogistics2() throws Exception {
        ShopLogisticsVo shopLogisticsVo = new ShopLogisticsVo();
        shopLogisticsVo.setLogisticsId(4L);
        shopLogisticsVo.setSecret("secret2");
        shopLogisticsVo.setPriority(10L);
        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Shop_All_Logistics, 2L)
                        .header("authorization", shop2Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(shopLogisticsVo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(3)));
    }
    @Test
    public void createShoplogistics3() throws Exception {
        ShopLogisticsVo shopLogisticsVo = new ShopLogisticsVo();
        shopLogisticsVo.setLogisticsId(3L);
        shopLogisticsVo.setSecret("secret2");
        shopLogisticsVo.setPriority(10L);
        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Shop_All_Logistics, 2L)
                        .header("authorization", shop2Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(shopLogisticsVo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(801)));
    }

    @Test
    public void retrieveLogisticsByBillCode1() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(Freight_Logistics)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("billCode", "SF1391971873939"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()",is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].name",is("顺丰快递")));
    }
    @Test
    public void retrieveLogisticsByBillCode2() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(Freight_Logistics)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()",is(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].name",is("顺丰快递")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].name",is("中通快递")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].name",is("极兔速递")));
    }
    @Test
    public void retrieveLogisticsByBillCode3() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(Freight_Logistics)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("billCode", "SF2330123456789"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));
    }

    @Test
    public void updateShoplogistics1() throws Exception {
        ShopLogisticsVo shopLogisticsVo = new ShopLogisticsVo();
        shopLogisticsVo.setSecret("secret2");
        shopLogisticsVo.setPriority(10L);
        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Shop_Logistics,"1","2")
                        .header("authorization", shop1Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(shopLogisticsVo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));
    }
    @Test
    public void updateShoplogistics2() throws Exception {
        ShopLogisticsVo shopLogisticsVo = new ShopLogisticsVo();
        shopLogisticsVo.setSecret("secret2");
        shopLogisticsVo.setPriority(10L);
        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Shop_Logistics,"2","5")
                        .header("authorization", shop2Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(shopLogisticsVo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));
    }
    @Test
    public void updateShoplogistics3() throws Exception {
        ShopLogisticsVo shopLogisticsVo = new ShopLogisticsVo();
        shopLogisticsVo.setSecret("secret2");
        shopLogisticsVo.setPriority(10L);
        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Shop_Logistics,"1","2")
                        .header("authorization", shop2Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(shopLogisticsVo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));
    }
    @Test
    public void suspendShoplogistics1() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Shop_Logistics_Suspend,"1","2")
                        .header("authorization", shop1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));
    }

    @Test
    public void suspendShoplogistics2() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Shop_Logistics_Suspend,"2","4")
                        .header("authorization", shop1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));
    }
    @Test
    public void resumeShoplogistics1() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Shop_Logistics_Resume,"1","2")
                        .header("authorization", shop1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));
    }

    @Test
    public void resumeShoplogistics2() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Shop_Logistics_Resume,"2","4")
                        .header("authorization", shop1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));
    }

}
