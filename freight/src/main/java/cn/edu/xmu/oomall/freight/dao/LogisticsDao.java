package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.oomall.freight.dao.bo.Logistics;
import cn.edu.xmu.oomall.freight.mapper.LogisticsPoMapper;
import cn.edu.xmu.oomall.freight.mapper.po.LogisticsPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RefreshScope
public class LogisticsDao {
    private final static Logger logger = LoggerFactory.getLogger(LogisticsDao.class);

    private LogisticsPoMapper logisticsPoMapper;

    @Autowired
    public LogisticsDao(LogisticsPoMapper logisticsPoMapper) {
        this.logisticsPoMapper = logisticsPoMapper;
    }

    public Logistics findLogisticsById(Long id) throws RuntimeException {
        if (null == id){
            return null;
        }
        Optional<LogisticsPo> ret = logisticsPoMapper.findById(id);
        if (ret.isPresent()) {
            return Logistics.builder().id(ret.get().getId())
                    .name(ret.get().getName())
                    .appId(ret.get().getAppId())
                    .snPattern(ret.get().getSnPattern())
                    .secret(ret.get().getSecret())
                    .logisticsClass(ret.get().getLogisticsClass())
                    .creatorId(ret.get().getCreatorId())
                    .creatorName(ret.get().getCreatorName())
                    .gmtCreate(ret.get().getGmtCreate())
                    .gmtModified(ret.get().getGmtModified())
                    .modifierId(ret.get().getModifierId())
                    .modifierName(ret.get().getModifierName())
                    .build();
        } else {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "平台物流", id));
        }
    }
    public List<Logistics> retrieveLogistic(){
        List<LogisticsPo> logisticsPoList = logisticsPoMapper.findAll();
        return logisticsPoList.stream().map(po->Logistics.builder().id(po.getId())
                .name(po.getName())
                .appId(po.getAppId())
                .snPattern(po.getSnPattern())
                .secret(po.getSecret())
                .logisticsClass(po.getLogisticsClass())
                .creatorId(po.getCreatorId())
                .creatorName(po.getCreatorName())
                .gmtCreate(po.getGmtCreate())
                .gmtModified(po.getGmtModified())
                .modifierId(po.getModifierId())
                .modifierName(po.getModifierName())
                .build()).collect(Collectors.toList());
    }


}
