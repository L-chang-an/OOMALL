//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.mapper;

import cn.edu.xmu.oomall.order.mapper.po.OrderItemPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemPoMapper extends JpaRepository<OrderItemPo, Long> {
    List<OrderItemPo> findByOrderId(Long orderId);
}
