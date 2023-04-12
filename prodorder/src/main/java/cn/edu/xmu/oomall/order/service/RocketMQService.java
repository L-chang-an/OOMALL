package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.order.dao.bo.Order;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RocketMQService {

    private static final Logger logger = LoggerFactory.getLogger(RocketMQService.class);

    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    public RocketMQService(RocketMQTemplate rocketMQTemplate){this.rocketMQTemplate=rocketMQTemplate;};

    @Value("${oomall.order.order-check-top.delay-level}")
    private int delayLevel;

    @Value("${oomall.order.order-check-top.timeout}")
    private long timeout;

    public void sendDelGoodsMsg(Order order){
        logger.info("sendNewOrderMsg: send message order = "+order+" delay ="+delayLevel+" time =" +LocalDateTime.now());
        String orderStr= JacksonUtil.toJson(order);
        Message message=MessageBuilder.withPayload(orderStr).build();
        rocketMQTemplate.asyncSend("New-Order", message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                logger.info("sendOrderPayMessage: onSuccess result = "+ sendResult+" time ="+LocalDateTime.now());
            }
            @Override
            public void onException(Throwable throwable) {
                logger.info("sendOrderPayMessage: onException e = "+ throwable.getMessage()+" time ="+LocalDateTime.now());
            }
        }, timeout * 1000, delayLevel);
    }

    public void sendRevokeOrderMsg(Order order){
        logger.info("sendRevokeOrderMsg: send message order = "+order+" delay ="+delayLevel+" time =" +LocalDateTime.now());
        String orderStr= JacksonUtil.toJson(order);
        Message message=MessageBuilder.withPayload(orderStr).build();
        rocketMQTemplate.asyncSend("Revoke-Order", message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                logger.info("sendOrderPayMessage: onSuccess result = "+ sendResult+" time ="+LocalDateTime.now());
            }
            @Override
            public void onException(Throwable throwable) {
                logger.info("sendOrderPayMessage: onException e = "+ throwable.getMessage()+" time ="+LocalDateTime.now());
            }
        }, timeout * 1000, delayLevel);
    }

    public void sendChangeOrderStatusMsg(Order order){
        logger.info("sendUpateOrderStatusMsg: send message order = "+order+" delay ="+delayLevel+" time =" +LocalDateTime.now());
        String orderStr= JacksonUtil.toJson(order);
        Message message=MessageBuilder.withPayload(orderStr).build();
        rocketMQTemplate.asyncSend("Update-Order-Status", message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                logger.info("sendOrderPayMessage: onSuccess result = "+ sendResult+" time ="+LocalDateTime.now());
            }
            @Override
            public void onException(Throwable throwable) {
                logger.info("sendOrderPayMessage: onException e = "+ throwable.getMessage()+" time ="+LocalDateTime.now());
            }
        }, timeout * 1000, delayLevel);
    }

    public void sendDelCouponsMsg(List<Long> coupons){
        logger.info("sendDelCouponsMsg: send message coupons = "+coupons+" delay ="+delayLevel+" time =" +LocalDateTime.now());
        String couponsStr= JacksonUtil.toJson(coupons);
        Message message=MessageBuilder.withPayload(couponsStr).build();
        rocketMQTemplate.asyncSend("Del-Coupons", message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                logger.info("sendOrderPayMessage: onSuccess result = "+ sendResult+" time ="+LocalDateTime.now());
            }
            @Override
            public void onException(Throwable throwable) {
                logger.info("sendOrderPayMessage: onException e = "+ throwable.getMessage()+" time ="+LocalDateTime.now());
            }
        }, timeout * 1000, delayLevel);
    }
}
