package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.bo.Logistics;
import cn.edu.xmu.oomall.freight.mapper.po.LogisticsPo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class LogisticsDaoTest {
    @Autowired
    private LogisticsDao logisticsDao;
    @Test
    public void findLogisticsById1() {
        Logistics logistics = logisticsDao.findLogisticsById(1L);
        assertThat(logistics.getName()).isEqualTo("顺丰快递");
        assertThat(logistics.getAppId()).isEqualTo("SF1001");
        assertThat(logistics.getSnPattern()).isEqualTo("^SF[A-Za-z0-9-]{4,35}$");
        assertThat(logistics.getLogisticsClass()).isEqualTo("sfDao");
    }
    @Test
    public void findLogisticsById2() {
        Logistics logistics = logisticsDao.findLogisticsById(null);
        assertThat(logistics).isEqualTo(null);
    }
    @Test
    public void retrieveLogistic1(){
        List<Logistics> logisticsList = logisticsDao.retrieveLogistic();
        assertThat(logisticsList.size()).isEqualTo(3);
        assertThat(logisticsList.get(0).getName()).isEqualTo("顺丰快递");
        assertThat(logisticsList.get(1).getName()).isEqualTo("中通快递");
        assertThat(logisticsList.get(2).getName()).isEqualTo("极兔速递");
    }
}
