package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.controller.vo.InfoVo;
import cn.edu.xmu.oomall.freight.controller.vo.WarehouseInfoVo;
import cn.edu.xmu.oomall.freight.dao.LogisticsDao;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.WarehouseDao;
import cn.edu.xmu.oomall.freight.dao.WarehouseRegionDao;
import cn.edu.xmu.oomall.freight.dao.bo.Region;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import cn.edu.xmu.oomall.freight.mapper.WarehouseLogisticsPoMapper;
import cn.edu.xmu.oomall.freight.mapper.WarehousePoMapper;
import cn.edu.xmu.oomall.freight.mapper.WarehouseRegionPoMapper;
import cn.edu.xmu.oomall.freight.mapper.po.WarehouseLogisticsPo;
import cn.edu.xmu.oomall.freight.mapper.po.WarehousePo;
import cn.edu.xmu.oomall.freight.mapper.po.WarehouseRegionPo;
import cn.edu.xmu.oomall.freight.service.dto.ShopLogisticsDto;
import cn.edu.xmu.oomall.freight.service.dto.WarehouseDto;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = FreightApplication.class)
@AutoConfigureMockMvc
@Transactional
public class AdminWarehouseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegionDao regionDao;

    @MockBean
    private ShopLogisticsDao shopLogisticsDao;

    @Autowired
    private WarehouseRegionDao warehouseRegionDao;

    @Autowired
    private WarehousePoMapper warehousePoMapper;

    @Autowired
    private WarehouseRegionPoMapper warehouseRegionPoMapper;

    @Autowired
    private WarehouseDao warehouseDao;

    @Autowired
    private WarehouseLogisticsPoMapper warehouseLogisticsPoMapper;

    @MockBean
    private RedisUtil redisUtil;
    private static String adminToken;

    static String shopToken;

    private static final String Freight_Shop_Warehouse_All_Region = "/shops/{shopId}/warehouses/{id}/regions";

    private static final String Freight_Shop_Warehouse_Region = "/shops/{shopId}/warehouses/{wid}/regions/{id}";

    private static final String Freight_Shop_All_Warehouse = "/shops/{shopId}/warehouses";

    private static final String Frieght_Shop_Warehouse = "/shops/{shopId}/warehouses/{id}";

    private static final String Freight_Shop_Warehouse_Resume = "/shops/{shopId}/warehouses/{id}/resume";
    
    private static final String Freight_Shop_Warehouse_Suspend = "/shops/{shopId}/warehouses/{id}/suspend";
    private static final String Freight_Shop_Region_All_Warehouse = "/shops/{shopId}/regions/{regionId}/warehouses";
    private static final String Freight_Warehouse_Logistics = "/shops/{shopId}/warehouses/{id}/shoplogistics/{lid}";


    @BeforeAll
    public static void setup() {
        JwtHelper jwtHelper = new JwtHelper();
        String qaq = jwtHelper.createToken(2L, "shop3", 3L, 1, 3600);
        System.out.println(qaq);
        shopToken = jwtHelper.createToken(2L, "shop1", 2L, 1, 3600);
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
    }

    @Test
    public void createWarehouseLogistics1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        String nowTimeStr1 = "2021-12-15T16:30:30";
        String nowTimeStr2 = "2027-12-15T16:30:30";
        DateTimeFormatter df = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime beginTime = LocalDateTime.parse(nowTimeStr1, df);
        LocalDateTime endTime = LocalDateTime.parse(nowTimeStr2, df);
        Mockito.when(shopLogisticsDao.existsByIdAndShopId(31L, 2L)).thenReturn(true);
        InfoVo vo = new InfoVo();
        vo.setBeginTime(beginTime);
        vo.setEndTime(endTime);
        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Warehouse_Logistics, 2L, 17L, 31L)
                        .header("authorization", shopToken)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));

        WarehouseLogisticsPo warehouseLogisticsPo = warehouseLogisticsPoMapper.findByWarehouseIdAndShopLogisticsId(17L, 31L);
        assertThat(warehouseLogisticsPo.getShopLogisticsId()).isEqualTo(31L);
        assertThat(warehouseLogisticsPo.getBeginTime()).isEqualTo(nowTimeStr1);
        assertThat(warehouseLogisticsPo.getEndTime()).isEqualTo(nowTimeStr2);
        assertThat(warehouseLogisticsPo.getCreatorName()).isEqualTo("shop1");
    }

    @Test
    public void getRegionWarehouses1() throws Exception {
        List<Region> testRegions = Stream.of(new Region(1068L, "黑庄户村委会")
                , new Region(200397L, "徐溪村委会")
                , new Region(304646L, "大张村委会")
                , new Region(104859L, "五家子村委会")).collect(Collectors.toList());
        testRegions.stream().forEach( region -> Mockito.when(regionDao.getRegionById(region.getId())).thenReturn(new InternalReturnObject<>(new Region(){
            {
                setId(region.getId());
                setName(region.getName());
            }
        })));
        this.mockMvc.perform(MockMvcRequestBuilders.get(Freight_Shop_Region_All_Warehouse,2L, 1L)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].warehouse.id",is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].warehouse.name",is("黑庄户仓库")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].warehouse.invalid",is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].status",is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].creator.id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].creator.userName", is("admin")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list.length()", is(1)));
    }

    @Test
    public void getRegionWarehouses2() throws Exception {
        List<Region> testRegions = Stream.of(new Region(1068L, "黑庄户村委会")
                , new Region(200397L, "徐溪村委会")
                , new Region(304646L, "大张村委会")
                , new Region(104859L, "五家子村委会")).collect(Collectors.toList());
        testRegions.stream().forEach( region -> Mockito.when(regionDao.getRegionById(region.getId())).thenReturn(new InternalReturnObject<>(new Region(){
            {
                setId(region.getId());
                setName(region.getName());
            }
        })));
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Shop_Warehouse_Suspend,2L, 2L)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));

        this.mockMvc.perform(MockMvcRequestBuilders.get(Freight_Shop_Region_All_Warehouse,2L, 1L)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list.length()",is(0)));
    }

    @Test
    public void getWarehouses() throws Exception {
        List<Region> testRegions = Stream.of(new Region(1068L, "黑庄户村委会")
                , new Region(200397L, "徐溪村委会")
                , new Region(304646L, "大张村委会")
                , new Region(104859L, "五家子村委会")).collect(Collectors.toList());
        testRegions.stream().forEach( region -> Mockito.when(regionDao.getRegionById(region.getId())).thenReturn(new InternalReturnObject<>(new Region(){
            {
                setId(region.getId());
                setName(region.getName());
            }
        })));
        this.mockMvc.perform(MockMvcRequestBuilders.get(Freight_Shop_All_Warehouse, 2L)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].id",is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].name",is("黑庄户仓库")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].region.id",is(1068)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].region.name",is("黑庄户村委会")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].createdBy.id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].createdBy.userName", is("admin")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].priority", is(1000)));
    }

    @Test 
    public void updateWarehouseStatus() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Shop_Warehouse_Suspend,2L, 2L)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));
        
        WarehousePo po1 = warehousePoMapper.findByShopIdAndId(2L, 2L).get();
        assertThat(po1.getInvalid()).isEqualTo((byte)1);
        assertThat(po1.getModifierName()).isEqualTo("shop1");
        assertThat(po1.getId()).isEqualTo(2);
        assertThat(po1.getModifierId()).isEqualTo(2);
        assertThat(po1.getName()).isEqualTo("黑庄户仓库");
        assertThat(po1.getAddress()).isEqualTo("北京,朝阳,黑庄户,黑庄户曙光路14号");
        assertThat(po1.getSenderName()).isEqualTo("刘雨堡");
        assertThat(po1.getCreatorId()).isEqualTo(1);
        assertThat(po1.getCreatorName()).isEqualTo("admin");

        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Shop_Warehouse_Resume,2L, 2L)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));

        WarehousePo po2 = warehousePoMapper.findByShopIdAndId(2L, 2L).get();
        assertThat(po2.getInvalid()).isEqualTo((byte)0);
        assertThat(po2.getModifierName()).isEqualTo("shop1");
        assertThat(po2.getId()).isEqualTo(2);
        assertThat(po2.getModifierId()).isEqualTo(2);
        assertThat(po2.getName()).isEqualTo("黑庄户仓库");
        assertThat(po2.getAddress()).isEqualTo("北京,朝阳,黑庄户,黑庄户曙光路14号");
        assertThat(po2.getSenderName()).isEqualTo("刘雨堡");
        assertThat(po2.getCreatorId()).isEqualTo(1);
        assertThat(po2.getCreatorName()).isEqualTo("admin");

        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Shop_Warehouse_Resume,2L, 99L)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));
    }


    @Test
    void deleteWarehouse1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        assertThat(warehouseRegionPoMapper.findByWarehouseIdAndRegionId(2L, 1L)).isNotEqualTo(null);
        assertThat(warehouseLogisticsPoMapper.findByWarehouseIdAndShopLogisticsId(2L, 4L)).isNotEqualTo(null);
        assertThat(warehouseLogisticsPoMapper.findByWarehouseIdAndShopLogisticsId(2L, 6L)).isNotEqualTo(null);
        this.mockMvc.perform(MockMvcRequestBuilders.delete(Frieght_Shop_Warehouse,2L, 2L)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));

        assertThrows(BusinessException.class, () -> warehouseDao.retrieveWarehouseByShopIdAndWarehouseId(2L, 2L));
        assertThat(warehouseRegionPoMapper.findByWarehouseIdAndRegionId(2L, 1L)).isEqualTo(null);
        assertThat(warehouseLogisticsPoMapper.findByWarehouseIdAndShopLogisticsId(2L, 4L)).isEqualTo(null);
        assertThat(warehouseLogisticsPoMapper.findByWarehouseIdAndShopLogisticsId(2L, 6L)).isEqualTo(null);
    }

    @Test
    void deleteWarehouse3() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(regionDao.getRegionById(11144L)).thenReturn(new InternalReturnObject<>(new Region(){
            {
                setId(11144L);
                setName("后坨里社区居委会");
            }
        }));
        WarehouseInfoVo vo = new WarehouseInfoVo();
        vo.setAddress("河南,商丘,宁陵,阳驿,汤林王曙光路14号");
        vo.setPriority(1000L);
        vo.setName("黑庄户仓库22");
        vo.setRegionId(11144L);
        vo.setSenderMobile("139206174517");
        vo.setSenderName("出版社");
        String result = this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Shop_All_Warehouse, 2L)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.region.id", is(11144)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name", is("黑庄户仓库22")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.createdBy.id", is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.region.name", is("后坨里社区居委会")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.invalid", is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.priority", is(1000)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.senderName", is("出版社")))
                        .andReturn().getResponse().getContentAsString();


        Long warehouseId = JSONObject.parseObject(JSONObject.parseObject(result).get("data").toString(), WarehouseDto.class).getId();
        assertThat(warehousePoMapper.findByShopIdAndId(2L, warehouseId).isPresent()).isEqualTo(true);
        this.mockMvc.perform(MockMvcRequestBuilders.delete(Frieght_Shop_Warehouse, 2L, warehouseId)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));
        assertThat(warehousePoMapper.findByShopIdAndId(2L, warehouseId).isPresent()).isEqualTo(false);
    }
    @Test
    void deleteWarehouse2() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete(Frieght_Shop_Warehouse,2L, 99L)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));

    }

    @Test
    void updateWarehouse1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(regionDao.getRegionById(11144L)).thenReturn(new InternalReturnObject<>(new Region(){
            {
                setId(11144L);
                setName("后坨里社区居委会");
            }
        }));
        WarehouseInfoVo vo = new WarehouseInfoVo();
        vo.setAddress("河南,商丘,宁陵,阳驿,汤林王曙光路14号");
        vo.setPriority(11111L);
        vo.setName("黑庄户仓库22");
        vo.setRegionId(11144L);
        vo.setSenderMobile("139206174517");
        vo.setSenderName("出版社");
        this.mockMvc.perform(MockMvcRequestBuilders.put(Frieght_Shop_Warehouse,2L, 17L)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));

        WarehousePo warehouse = warehousePoMapper.findByShopIdAndId(2L, 17L).get();
        assertThat(warehouse.getPriority()).isEqualTo(11111L);
        assertThat(warehouse.getAddress()).isEqualTo("河南,商丘,宁陵,阳驿,汤林王曙光路14号");
        assertThat(warehouse.getRegionId()).isEqualTo(11144L);
        assertThat(warehouse.getSenderName()).isEqualTo("出版社");
        assertThat(warehouse.getModifierName()).isEqualTo("shop1");
        assertThat(warehouse.getModifierId()).isEqualTo(2L);
    }

    @Test void updateWarehouse2() throws Exception {
        Mockito.when(regionDao.getRegionById(11144L)).thenReturn(new InternalReturnObject<>(new Region(){
            {
                setId(11144L);
                setName("后坨里社区居委会");
            }
        }));
        WarehouseInfoVo vo = new WarehouseInfoVo();
        vo.setAddress("河南,商丘,宁陵,阳驿,汤林王曙光路14号");
        vo.setPriority(11111L);
        vo.setName("黑庄户仓库22");
        vo.setRegionId(11144L);
        vo.setSenderMobile("139206174517");
        vo.setSenderName("出版社");
        this.mockMvc.perform(MockMvcRequestBuilders.put(Frieght_Shop_Warehouse,2L, 18L)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));

        vo.setRegionId(11145L);

        this.mockMvc.perform(MockMvcRequestBuilders.put(Frieght_Shop_Warehouse,2L, 17L)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));
    }

    @Test void updateWarehouse3() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        WarehouseInfoVo vo = new WarehouseInfoVo();
        vo.setAddress("河南,商丘,宁陵,阳驿,汤林王曙光路14号");
        vo.setSenderMobile("139206174517");
        vo.setSenderName("出版社");
        this.mockMvc.perform(MockMvcRequestBuilders.put(Frieght_Shop_Warehouse,2L, 17L)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));

        WarehousePo warehouse = warehousePoMapper.findByShopIdAndId(2L, 17L).get();
        assertThat(warehouse.getPriority()).isEqualTo(1000L);
        assertThat(warehouse.getAddress()).isEqualTo("河南,商丘,宁陵,阳驿,汤林王曙光路14号");
        assertThat(warehouse.getRegionId()).isEqualTo(104859L);
        assertThat(warehouse.getName()).isEqualTo("五家子仓库");
        assertThat(warehouse.getSenderName()).isEqualTo("出版社");
        assertThat(warehouse.getModifierName()).isEqualTo("shop1");
        assertThat(warehouse.getModifierId()).isEqualTo(2L);
    }

    @Test
    public void createWarehouse1() throws Exception {
        Mockito.when(regionDao.getRegionById(11144L)).thenReturn(new InternalReturnObject<>(new Region(){
            {
                setId(11144L);
                setName("后坨里社区居委会");
            }
        }));
        WarehouseInfoVo vo = new WarehouseInfoVo();
        vo.setAddress("河南,商丘,宁陵,阳驿,汤林王曙光路14号");
        vo.setPriority(1000L);
        vo.setName("黑庄户仓库22");
        vo.setRegionId(11144L);
        vo.setSenderMobile("139206174517");
        vo.setSenderName("出版社");
        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Shop_All_Warehouse,2L)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.region.id",is(11144)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name",is("黑庄户仓库22")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.createdBy.id",is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.region.name",is("后坨里社区居委会")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.invalid",is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.priority",is(1000)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.senderName",is("出版社")));
    }

    @Test
    public void createWarehouse2() throws Exception {
        WarehouseInfoVo vo = new WarehouseInfoVo();
        vo.setAddress("河南,商丘,宁陵,阳驿,汤林王曙光路14号");
        vo.setPriority(1000L);
        vo.setName("黑庄户仓库22");
        vo.setRegionId(1111111144L);
        vo.setSenderMobile("139206174517");
        vo.setSenderName("出版社");
        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Shop_All_Warehouse,2L)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));

        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Shop_All_Warehouse,2L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.AUTH_NO_RIGHT.getErrNo())));

    }

    @Test
    public void getWarehouseRegions() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(regionDao.getRegionById(99537L)).thenReturn(new InternalReturnObject<>(new Region(){
            {
                setId(99537L);
                setName("内蒙古自治区");
                setCreatorId(1L);
                setCreatorName("admin");
            }
        }));
        this.mockMvc.perform(MockMvcRequestBuilders.get(Freight_Shop_Warehouse_All_Region,2L, 17L)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].region.id",is(99537)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].region.name",is("内蒙古自治区")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].creator.id",is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].beginTime",is("2022-12-02T14:20:05")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].endTime",is("2023-04-02T14:20:05")));
    }
    @Test
    public void deleteWarehouseRegion1() throws Exception {
        // 测试是否删除成功
        this.mockMvc.perform(MockMvcRequestBuilders.delete(Freight_Shop_Warehouse_Region,2L, 2L, 1L)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.OK.getErrNo())));

        assertThrows(BusinessException.class, () -> warehouseRegionDao.findWarehouseRegionByWarehouseIdAndRegionId(2L, 1L));
    }

    @Test
    public void deleteWarehouseRegion2() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete(Freight_Shop_Warehouse_Region,2L, 2L, 99L)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));
    }

    @Test
    public void putWarehouseRegion1() throws Exception {
        InfoVo vo = new InfoVo();
        vo.setBeginTime(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        vo.setEndTime(LocalDateTime.parse("2025-11-09T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Shop_Warehouse_Region,2L, 2L, 1L)
                        .header("authorization", shopToken)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.OK.getErrNo())));
    }
    @Test
    public void putWarehouseRegion2() throws Exception {
        InfoVo vo = new InfoVo();
        vo.setBeginTime(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        vo.setEndTime(LocalDateTime.parse("2025-11-09T12:00:00", DateTimeFormatter.ISO_DATE_TIME));

        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Shop_Warehouse_Region,2L, 17L, 1L)
                        .header("authorization", shopToken)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));

        vo.setBeginTime(LocalDateTime.parse("2028-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));

        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Shop_Warehouse_Region,2L, 2L, 1L)
                        .header("authorization", shopToken)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.LATE_BEGINTIME.getErrNo())));
    }

    @Test
    public void putWarehouseRegion3() throws Exception {
        InfoVo vo0 = new InfoVo();
        vo0.setBeginTime(LocalDateTime.parse("2022-11-06T12:00:09", DateTimeFormatter.ISO_DATE_TIME));
        vo0.setEndTime(LocalDateTime.parse("2025-11-09T12:00:09", DateTimeFormatter.ISO_DATE_TIME));


        InfoVo vo = new InfoVo();
        vo.setBeginTime(LocalDateTime.parse("2022-11-06T12:00:09", DateTimeFormatter.ISO_DATE_TIME));
        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Shop_Warehouse_Region,2L, 2L, 1L)
                        .header("authorization", shopToken)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));

        WarehouseRegionPo warehouseRegionPo = warehouseRegionPoMapper.findByWarehouseIdAndRegionId(2L, 1L);
        assertThat(warehouseRegionPo.getBeginTime()).isEqualTo(LocalDateTime.parse("2022-11-06T12:00:09", DateTimeFormatter.ISO_DATE_TIME));
        assertThat(warehouseRegionPo.getEndTime()).isEqualTo(LocalDateTime.parse("2023-04-02T14:20:05", DateTimeFormatter.ISO_DATE_TIME));

        vo.setBeginTime(null);
        vo.setEndTime(LocalDateTime.parse("2025-11-09T12:00:09", DateTimeFormatter.ISO_DATE_TIME));
        this.mockMvc.perform(MockMvcRequestBuilders.put(Freight_Shop_Warehouse_Region,2L, 2L, 1L)
                        .header("authorization", shopToken)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));

        WarehouseRegionPo warehouseRegionPo2 = warehouseRegionPoMapper.findByWarehouseIdAndRegionId(2L, 1L);
        assertThat(warehouseRegionPo2.getBeginTime()).isEqualTo(LocalDateTime.parse("2022-11-06T12:00:09", DateTimeFormatter.ISO_DATE_TIME));
        assertThat(warehouseRegionPo2.getEndTime()).isEqualTo(LocalDateTime.parse("2025-11-09T12:00:09", DateTimeFormatter.ISO_DATE_TIME));
    }
    @Test
    public void createWarehouseRegionByShopIdAndRegionId1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(regionDao.getRegionById(25L)).thenReturn(new InternalReturnObject<>(new Region(){
            {
                setId(25L);
            }
        }));
        InfoVo vo = new InfoVo();
        vo.setBeginTime(LocalDateTime.parse("2022-11-06T12:00:09", DateTimeFormatter.ISO_DATE_TIME));
        vo.setEndTime(LocalDateTime.parse("2025-11-09T12:00:09", DateTimeFormatter.ISO_DATE_TIME));
        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Shop_Warehouse_Region,2L, 17L, 25L)
                        .header("authorization", shopToken)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.CREATED.getErrNo())));
    }

    @Test
    public void createWarehouseRegionByShopIdAndRegionId2() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(regionDao.getRegionById(25L)).thenReturn(new InternalReturnObject<>(new Region(){
            {
                setId(25L);
            }
        }));
        InfoVo vo = new InfoVo();
        vo.setBeginTime(LocalDateTime.parse("2022-11-06T12:00:09", DateTimeFormatter.ISO_DATE_TIME));
        vo.setEndTime(LocalDateTime.parse("2025-11-09T12:00:09", DateTimeFormatter.ISO_DATE_TIME));


        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Shop_Warehouse_Region,2L, 917L, 25L)
                        .header("authorization", shopToken)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));

        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Shop_Warehouse_Region,2L, 17L, 11111125L)
                        .header("authorization", shopToken)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));

        vo.setBeginTime(LocalDateTime.parse("2028-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));

        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Shop_Warehouse_Region,2L, 917L, 25L)
                        .header("authorization", shopToken)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.LATE_BEGINTIME.getErrNo())));
    }

    @Test
    public void createWarehouseRegionByShopIdAndRegionId3() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(regionDao.getRegionById(25L)).thenReturn(new InternalReturnObject<>(new Region(){
            {
                setId(25L);
            }
        }));
        InfoVo vo = new InfoVo();
        vo.setBeginTime(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        vo.setEndTime(LocalDateTime.parse("2025-11-09T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Shop_Warehouse_Region,2L, 17L, 25L)
                        .header("authorization", shopToken)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.CREATED.getErrNo())));
    }

    @Test
    public void createWarehouseRegionByShopIdAndRegionId4() throws Exception {
        InfoVo vo = new InfoVo();
        vo.setBeginTime(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Shop_Warehouse_Region,2L, 17L, 25L)
                        .header("authorization", shopToken)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.PARAMETER_MISSED.getErrNo())));

        vo.setBeginTime(null);
        vo.setEndTime(LocalDateTime.parse("2025-11-09T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        this.mockMvc.perform(MockMvcRequestBuilders.post(Freight_Shop_Warehouse_Region,2L, 17L, 25L)
                        .header("authorization", shopToken)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.PARAMETER_MISSED.getErrNo())));
    }
}