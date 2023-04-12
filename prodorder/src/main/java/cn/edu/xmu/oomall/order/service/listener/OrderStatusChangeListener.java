//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.service.listener;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.order.dao.OrderDao;
import cn.edu.xmu.oomall.order.dao.bo.Order;
import org.springframework.messaging.Message;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RocketMQMessageListener(topic = "Update-Order-Status", consumerGroup = "order-status-change", consumeThreadMax = 1)
public class OrderStatusChangeListener implements RocketMQListener<Message> {
    private static final Logger logger = LoggerFactory.getLogger(OrderStatusChangeListener.class);

    private OrderDao orderDao;

    @Autowired
    public OrderStatusChangeListener(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Override
    public void onMessage(Message message) {
        String content = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
        Order order = JacksonUtil.toObj(content, Order.class);
        UserDto user=new UserDto();
        if (null == order || null == order.getOrderItems()){
            logger.error("OrderConsumer: wrong format.... content = {}",content);
        }else{
            orderDao.save(order,user);
        }
    }
}
