package cn.edu.xmu.oomall.freight.dao.LogisticsCompany;

import cn.edu.xmu.oomall.freight.dao.bo.Express;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Repository;


@Repository
@RefreshScope
public interface LogisticsCompanyInf {
    /*
     调用快递公司API创建运单，返回的是运单号
     */
    String insert(Express bo) throws RuntimeException;

    /*
     调用快递公司API取消运单
     */
    String cancel(Express bo) throws RuntimeException;
}
