package cn.edu.xmu.oomall.freight.dao.logisticsCompany;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.LogisticsCompany.JtDao;
import cn.edu.xmu.oomall.freight.dao.bo.Express;
import cn.edu.xmu.oomall.freight.dao.bo.Region;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class JtDaoTest {

    @Autowired
    private JtDao jtDao;

    @Test
    public void insert1() throws IOException {
        Express bo = Express.builder()
                .shopLogisticsId(3L)
                .senderName("阮杰")
                .senderMobile("13919718739")
                .senderRegionId(1043L)
                .senderAddress("北京,朝阳,东坝,朝阳新城第二曙光路14号")
                .deliverName("杰阮")
                .deliverMobile("11871973939")
                .deliverRegionId(1043L)
                .deliverAddress("北京,朝阳,东坝,朝阳新城第二曙光路15号")
                .id(8L)
                .build();
        try {
            String billCode = jtDao.insert(bo);
            assertThat(billCode).isEqualTo("UT0000547463164");
        }
        catch (Exception e){
            assertThat(e.getMessage().equals("已取消状态不可修改"));
        }
    }
    @Test
    public void cancel1() throws IOException {
        Express bo = Express.builder()
                .id(8L)
                .build();
        try {
            String billCode = jtDao.cancel(bo);
            assertThat(billCode).isEqualTo("UT0000547463164");
        }
        catch (Exception e){
            assertThat(e.getMessage().equals("存在不可取消的数据，请重新确认数据"));
        }
    }

}
