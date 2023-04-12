package cn.edu.xmu.oomall.freight.mapper;

import cn.edu.xmu.oomall.freight.mapper.po.LogisticsPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogisticsPoMapper extends JpaRepository<LogisticsPo, Long> {
}
