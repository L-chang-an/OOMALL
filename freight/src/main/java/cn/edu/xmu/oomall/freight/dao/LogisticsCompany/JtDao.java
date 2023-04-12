package cn.edu.xmu.oomall.freight.dao.LogisticsCompany;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.oomall.freight.dao.ExpressDao;
import cn.edu.xmu.oomall.freight.dao.LogisticsCompany.LogisticsCompanyInf;
import cn.edu.xmu.oomall.freight.dao.bo.Express;
import cn.edu.xmu.oomall.freight.dao.bo.Region;
import com.alibaba.fastjson.JSONObject;
import com.yl.jms.sdk.JtExpressApi;
import com.yl.jms.sdk.auth.ClientConfiguration;
import com.yl.jms.sdk.client.JtExpressApiOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Repository
@RefreshScope
public class JtDao implements LogisticsCompanyInf {

    private final static Logger logger = LoggerFactory.getLogger(JtDao.class);
    public static final String apiAccount="178337126125932605";
    public static final String privateKey="0258d71b55fc45e3ad7a7f38bf4b201a";
    public static final String customerCode="J0086474299";
    public static final String orderPassword="H5CD3zE6";
    public static final String createUrl = "https://uat-openapi.jtexpress.com.cn/webopenplatformapi/api/order/v2/addOrder";
    public static final String cancelUrl = "https://uat-openapi.jtexpress.com.cn/webopenplatformapi/api/order/cancelOrder";

    @Override
    public String insert(Express bo) throws RuntimeException {
        ClientConfiguration clientConfiguration = new ClientConfiguration(apiAccount,privateKey);
        clientConfiguration.setCustomerCode(customerCode);
        clientConfiguration.setCustomerPwd(orderPassword);
        JtExpressApi jtExpressApi = new JtExpressApiOperator(clientConfiguration);
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> sender = new HashMap<>();
        Map<String, Object> receiver = new HashMap<>();
        map.put("txlogisticId",bo.getId());
        logger.debug("txlogisticId:{}",bo.getId());
        map.put("expressType","EZ");
        map.put("orderType","1");
        map.put("serviceType","01");
        map.put("deliveryType","03");
        map.put("goodsType","bm000006");
        map.put("weight","1");

        sender.put("name",bo.getSenderName());
        sender.put("mobile",bo.getSenderMobile());
        sender.put("phone","");
        sender.put("countryCode","CHN");
        String[] senderStrings = bo.getSenderAddress().split(",");
        if(senderStrings.length<3){
            throw new BusinessException(ReturnNo.PARAMETER_MISSED, "发货地址缺少省/市/区");
        }
        sender.put("prov",senderStrings[0]);
        sender.put("city",senderStrings[1]);
        sender.put("area",senderStrings[2]);
        sender.put("address",bo.getSenderAddress());

        map.put("sender",sender);
        receiver.put("name",bo.getDeliverName());
        receiver.put("mobile",bo.getDeliverMobile());
        receiver.put("phone","");
        receiver.put("countryCode","CHN");

        String[] deliverStrings = bo.getSenderAddress().split(",");
        if(deliverStrings.length<3){
            throw new BusinessException(ReturnNo.PARAMETER_MISSED, "收货地址缺少省/市/区");
        }
        receiver.put("prov",deliverStrings[0]);
        receiver.put("city",deliverStrings[1]);
        receiver.put("area",deliverStrings[2]);

        receiver.put("address",bo.getDeliverAddress());
        map.put("receiver",receiver);
        map.put("digest","qonqb4O1eNr6VCWS07Ieeg==");

        try {
            JSONObject location = jtExpressApi.postByCustom(map,createUrl);
            if(location.get("code").toString().equals("1")) {
                return (String) (((JSONObject) location.get("data")).get("billCode"));
            }
            throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR,
                    (String)location.get("msg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String cancel(Express bo) throws RuntimeException {
        ClientConfiguration clientConfiguration = new ClientConfiguration(apiAccount,privateKey);
        clientConfiguration.setCustomerCode(customerCode);
        clientConfiguration.setCustomerPwd(orderPassword);
        JtExpressApi jtExpressApi = new JtExpressApiOperator(clientConfiguration);
        Map<String, Object> map = new HashMap<>();
        map.put("txlogisticId",bo.getId());
        map.put("orderType","2");
        map.put("reason","取消订单");
        map.put("digest","qonqb4O1eNr6VCWS07Ieeg==");
        try {
            JSONObject location = jtExpressApi.postByCustom(map,cancelUrl);
            if(location.get("code").toString().equals("1")) {
                return (String) (((JSONObject) location.get("data")).get("billCode"));
            }
            throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR,
                    (String)location.get("msg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
