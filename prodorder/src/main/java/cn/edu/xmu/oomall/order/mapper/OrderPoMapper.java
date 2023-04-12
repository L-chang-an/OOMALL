//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.mapper;

import cn.edu.xmu.oomall.order.mapper.po.OrderPo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderPoMapper extends JpaRepository<OrderPo, Long> {

    List<OrderPo> findByCustomerIdAndOrderSnLikeAndStatusBetweenAndGmtCreateBetween(Long customerId,String orderSn, Integer status1, Integer status2, LocalDateTime begin, LocalDateTime end, Pageable pageable);
    List<OrderPo> findByCustomerIdAndOrderSnLikeAndGmtCreateBetween(Long customerId,String orderSn,LocalDateTime begin, LocalDateTime end, Pageable pageable);
    List<OrderPo> findByShopIdAndOrderSnLikeAndGmtCreateBetween(Long shopId,String orderSn,LocalDateTime begin, LocalDateTime end, Pageable pageable);
    List<OrderPo> findByOrderSnLikeAndGmtCreateBetween(String orderSn,LocalDateTime begin, LocalDateTime end, Pageable pageable);
    List<OrderPo> findByShopIdAndCustomerIdAndOrderSnLikeAndGmtCreateBetween(Long shopId, Long customerId,String orderSn,LocalDateTime begin, LocalDateTime end, Pageable pageable);
}
