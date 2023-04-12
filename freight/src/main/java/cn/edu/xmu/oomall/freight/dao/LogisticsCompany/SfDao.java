package cn.edu.xmu.oomall.freight.dao.LogisticsCompany;

import cn.edu.xmu.oomall.freight.dao.bo.Express;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Repository;

@Repository
@RefreshScope
public class SfDao implements LogisticsCompanyInf{
    @Override
    public String insert(Express bo) throws RuntimeException {
        return "SF1391971873939";
    }

    @Override
    public String cancel(Express bo) throws RuntimeException {
        return "SF1391971873939";
    }
}
