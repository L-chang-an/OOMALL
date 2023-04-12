package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.controller.vo.InfoVo;
import cn.edu.xmu.oomall.freight.controller.vo.ShopLogisticsVo;
import cn.edu.xmu.oomall.freight.dao.*;
import cn.edu.xmu.oomall.freight.dao.bo.Logistics;
import cn.edu.xmu.oomall.freight.dao.bo.Region;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseLogistics;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import cn.edu.xmu.oomall.freight.mapper.WarehouseLogisticsPoMapper;
import cn.edu.xmu.oomall.freight.mapper.WarehousePoMapper;
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
public class AdminWarehouseLogisticsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WarehouseLogisticsPoMapper warehouseLogisticsPoMapper;

    @MockBean
    private RedisUtil redisUtil;

    private static final String Freight_All_Warehouse_Logistics = "/shops/{shopId}/warehouses/{id}/shoplogistics";
    private static final String Freight_Warehouse_Logistics = "/shops/{shopId}/warehouses/{id}/shoplogistics/{lid}";
    private static String adminToken;
    private static String shopToken;
    private static String shopToken3;
    private String nowTimeStr1 = "2021-12-15T16:30:30";
    private String nowTimeStr2 = "2027-12-15T16:30:30";
    private DateTimeFormatter df = DateTimeFormatter.ISO_DATE_TIME;
    private LocalDateTime beginTime = LocalDateTime.parse(nowTimeStr1, df);
    private LocalDateTime endTime = LocalDateTime.parse(nowTimeStr2, df);

    @BeforeAll
    public static void setup() {
        JwtHelper jwtHelper = new JwtHelper();
        shopToken3 = jwtHelper.createToken(5L, "shop2", 3L, 1, 3600);
        shopToken = jwtHelper.createToken(2L, "shop1", 2L, 1, 3600);
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
    }

    @Test
    public void getAllWarehouseLogistics1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        String result = this.mockMvc.perform(MockMvcRequestBuilders.get(Freight_All_Warehouse_Logistics, 2L, 17L)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String list = JSONObject.parseObject(JSONObject.parseObject(result).get("data").toString()).get("list").toString();
        List<WarehouseLogisticsDto> dtos = JSONObject.parseArray(list, WarehouseLogisticsDto.class);
        assertThat(dtos.size()).isEqualTo(2);
        assertThat(dtos.get(0).getShopLogistics().getId()).isEqualTo(4);
        assertThat(dtos.get(1).getShopLogistics().getId()).isEqualTo(6);
        assertThat(dtos.get(0).getShopLogistics().getPriority()).isEqualTo(3);
        assertThat(dtos.get(1).getShopLogistics().getPriority()).isEqualTo(5);
    }

    @Test
    public void getAllWarehouseLogistics2() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.get(Freight_All_Warehouse_Logistics, 2L, 5L)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));
    }

    @Test
    public void createWarehouseLogistics2() throws Exception {
        InfoVo vo = new InfoVo();
        vo.setEndTime(beginTime);
        vo.setBeginTime(endTime);
        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Warehouse_Logistics, 2L, 17L, 31L)
                        .header("authorization", shopToken)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.LATE_BEGINTIME.getErrNo())));

        vo.setEndTime(null);
        vo.setBeginTime(beginTime);
        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Warehouse_Logistics, 2L, 17L, 31L)
                        .header("authorization", shopToken)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.PARAMETER_MISSED.getErrNo())));

        vo.setEndTime(endTime);
        vo.setBeginTime(null);
        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Warehouse_Logistics, 2L, 17L, 31L)
                        .header("authorization", shopToken)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.PARAMETER_MISSED.getErrNo())));
    }

    @Test
    public void createWarehouseLogistics3() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        InfoVo vo = new InfoVo();
        vo.setEndTime(endTime);
        vo.setBeginTime(beginTime);
        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Warehouse_Logistics, 2L, 9L, 31L)
                        .header("authorization", shopToken)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));

        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Warehouse_Logistics, 2L, 17L, 31L)
                        .header("authorization", shopToken)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));
    }

    @Test
    public void updateWarehouseLogistics1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        InfoVo vo = new InfoVo();
        vo.setBeginTime(beginTime);
        vo.setEndTime(endTime);
        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Warehouse_Logistics, 3L, 3L, 8L)
                        .header("authorization", shopToken3)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));

        WarehouseLogisticsPo warehouseLogisticsPo = warehouseLogisticsPoMapper.findByWarehouseIdAndShopLogisticsId(3L, 8L);
        assertThat(warehouseLogisticsPo.getId()).isEqualTo(880L);
        assertThat(warehouseLogisticsPo.getShopLogisticsId()).isEqualTo(8L);
        assertThat(warehouseLogisticsPo.getBeginTime()).isEqualTo(nowTimeStr1);
        assertThat(warehouseLogisticsPo.getEndTime()).isEqualTo(nowTimeStr2);
        assertThat(warehouseLogisticsPo.getModifierName()).isEqualTo("shop2");
    }

    @Test
    public void updateWarehouseLogistics2() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        InfoVo vo = new InfoVo();
        vo.setEndTime(beginTime);
        vo.setBeginTime(endTime);
        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Warehouse_Logistics, 2L, 17L, 31L)
                        .header("authorization", shopToken)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.LATE_BEGINTIME.getErrNo())));

        vo.setEndTime(null);
        vo.setBeginTime(beginTime);
        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Warehouse_Logistics, 2L, 2L, 4L)
                        .header("authorization", shopToken)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));

        vo.setEndTime(endTime);
        vo.setBeginTime(null);
        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Warehouse_Logistics, 2L, 2L, 4L)
                        .header("authorization", shopToken)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));
    }

    @Test
    public void updateWarehouseLogistics3() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        InfoVo vo = new InfoVo();
        vo.setEndTime(endTime);
        vo.setBeginTime(beginTime);
        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Warehouse_Logistics, 2L, 9L, 31L)
                        .header("authorization", shopToken)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));

        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Warehouse_Logistics, 2L, 17L, 31L)
                        .header("authorization", shopToken)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));

    }

    @Test
    public void deleteWarehouseLogistics1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.delete(Freight_Warehouse_Logistics, 3L, 3L, 8L)
                        .header("authorization", shopToken3)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));

        WarehouseLogisticsPo warehouseLogisticsPo = warehouseLogisticsPoMapper.findByWarehouseIdAndShopLogisticsId(3L, 8L);
        assertThat(warehouseLogisticsPo).isEqualTo(null);
    }

    @Test
    public void deleteWarehouseLogistics2() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.delete(Freight_Warehouse_Logistics, 3L, 1L, 8L)
                        .header("authorization", shopToken3)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));

        this.mockMvc.perform(MockMvcRequestBuilders.delete(Freight_Warehouse_Logistics, 3L, 3L, 11L)
                        .header("authorization", shopToken3)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));

    }

}
