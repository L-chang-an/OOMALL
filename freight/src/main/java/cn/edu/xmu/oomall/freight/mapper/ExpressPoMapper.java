package cn.edu.xmu.oomall.freight.mapper;

import cn.edu.xmu.oomall.freight.mapper.po.ExpressPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpressPoMapper extends JpaRepository<ExpressPo, Long> {
    Optional<ExpressPo> findByBillCode(String billCode);
}
