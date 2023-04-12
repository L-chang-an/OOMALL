package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.dao.LogisticsCompany.LogisticsCompanyInf;
import cn.edu.xmu.oomall.freight.dao.bo.Express;
import cn.edu.xmu.oomall.freight.dao.bo.Logistics;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import cn.edu.xmu.oomall.freight.mapper.ExpressPoMapper;
import cn.edu.xmu.oomall.freight.mapper.po.ExpressPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static cn.edu.xmu.javaee.core.util.Common.putGmtFields;
import static cn.edu.xmu.javaee.core.util.Common.putUserFields;

@Repository
@RefreshScope
public class ExpressDao {
    private final static Logger logger = LoggerFactory.getLogger(ExpressDao.class);

    private ApplicationContext context;
    private ExpressPoMapper expressPoMapper;
    private ShopLogisticsDao shopLogisticsDao;
    private RegionDao regionDao;
    private static final String KEY = "E%d";

    @Autowired
    public ExpressDao(ApplicationContext context, ExpressPoMapper expressPoMapper, ShopLogisticsDao shopLogisticsDao, RegionDao regionDao) {
        this.context = context;
        this.expressPoMapper = expressPoMapper;
        this.shopLogisticsDao = shopLogisticsDao;
        this.regionDao = regionDao;
    }

    private void setBo(Express bo){
        bo.setShopLogisticsDao(shopLogisticsDao);
        bo.setRegionDao(regionDao);
    }
    private LogisticsCompanyInf findLogisticsCompanyDao(Logistics logistics){
        return (LogisticsCompanyInf) context.getBean(logistics.getLogisticsClass());
    }

    public Express findExpressByBillCode(String billCode) throws RuntimeException {
        if (null == billCode){
            return null;
        }
        Optional<ExpressPo> ret = expressPoMapper.findByBillCode(billCode);
        if (ret.isPresent()) {
            ExpressPo po = ret.get();
            Express bo = Express.builder()
                    .id(po.getId())
                    .billCode(po.getBillCode())
                    .shopLogisticsId(po.getShopLogisticsId())
                    .shopId(po.getShopId())
                    .deliverAddress(po.getDeliverAddress())
                    .deliverMobile(po.getDeliverMobile())
                    .deliverName(po.getDeliverName())
                    .deliverRegionId(po.getDeliverRegionId())
                    .senderAddress(po.getSenderAddress())
                    .senderMobile(po.getSenderMobile())
                    .senderName(po.getSenderName())
                    .senderRegionId(po.getSenderRegionId())
                    .status(po.getStatus())
                    .creatorId(po.getCreatorId())
                    .creatorName(po.getCreatorName())
                    .gmtCreate(po.getGmtCreate())
                    .gmtModified(po.getGmtModified())
                    .modifierId(po.getModifierId())
                    .modifierName(po.getModifierName())
                    .build();
            setBo(bo);
            return bo;
        } else {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, "运单对象物流单号不存在");
        }
    }

    public Express findExpressById(Long id) throws RuntimeException {
        if (null == id){
            return null;
        }
        Optional<ExpressPo> ret = expressPoMapper.findById(id);
        if (ret.isPresent()) {
            ExpressPo po = ret.get();
            Express bo = Express.builder()
                    .id(po.getId())
                    .billCode(po.getBillCode())
                    .shopLogisticsId(po.getShopLogisticsId())
                    .shopId(po.getShopId())
                    .deliverAddress(po.getDeliverAddress())
                    .deliverMobile(po.getDeliverMobile())
                    .deliverName(po.getDeliverName())
                    .deliverRegionId(po.getDeliverRegionId())
                    .senderAddress(po.getSenderAddress())
                    .senderMobile(po.getSenderMobile())
                    .senderName(po.getSenderName())
                    .senderRegionId(po.getSenderRegionId())
                    .status(po.getStatus())
                    .creatorId(po.getCreatorId())
                    .creatorName(po.getCreatorName())
                    .gmtCreate(po.getGmtCreate())
                    .gmtModified(po.getGmtModified())
                    .modifierId(po.getModifierId())
                    .modifierName(po.getModifierName())
                    .build();
            setBo(bo);
            return bo;
        } else {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, "运单对象物流单号不存在");
        }
    }
    public Express insert(Express bo, UserDto user)throws RuntimeException{
        setBo(bo);
        /*
         status不能为空，但0和1是签收后的状态，先用-1占位
         */
        ExpressPo po = ExpressPo.builder()
                .shopLogisticsId(bo.getShopLogisticsId())
                .senderName(bo.getSenderName())
                .senderMobile(bo.getSenderMobile())
                .senderAddress(bo.getSenderAddress())
                .senderRegionId(bo.getSenderRegionId())
                .deliverName(bo.getDeliverName())
                .deliverMobile(bo.getDeliverMobile())
                .deliverAddress(bo.getDeliverAddress())
                .deliverRegionId(bo.getDeliverRegionId())
                .status(-1L)
                .build();
        putUserFields(po, "creator", user);
        putGmtFields(po, "create");
        ExpressPo newPo = expressPoMapper.save(po);
        po.setId(newPo.getId());
        bo.setId(newPo.getId());
        LogisticsCompanyInf inf = this.findLogisticsCompanyDao(bo.getShopLogistics().getLogistics());
        String billCode = inf.insert(bo);
        po.setBillCode(billCode);
        bo.setBillCode(billCode);
        expressPoMapper.save(po);
        return bo;
    }
    public String save(Express express, UserDto user) throws RuntimeException {
        Optional<ExpressPo> ret = expressPoMapper.findById(express.getId());
        if(!ret.isPresent()){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "运单", express.getId()));
        }
        ExpressPo po = ret.get();
        if(!po.getShopId().equals(express.getShopId())){
            throw new BusinessException(ReturnNo.STATENOTALLOW,
                    String.format(ReturnNo.STATENOTALLOW.getMessage(), "商家", express.getShopId(), "无权"));
        }
        po.setStatus(express.getStatus());
        if(null!=user){
            putGmtFields(po,"create");
            putUserFields(po,"creator",user);
        }

        ExpressPo newPo = this.expressPoMapper.save(po);
        logger.debug("newPo:{}",newPo);
        return String.format(KEY,express.getId());
    }

    public void cancel(Express bo){
        setBo(bo);
        Optional<ExpressPo> ret = expressPoMapper.findById(bo.getId());
        if(!ret.isPresent()){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "运单", bo.getId()));
        }
        ExpressPo po = ret.get();
        if(!po.getShopId().equals(bo.getShopId())){
            throw new BusinessException(ReturnNo.STATENOTALLOW,
                    String.format(ReturnNo.STATENOTALLOW.getMessage(), "商家", bo.getShopId(), "无权"));
        }
        bo.setShopLogisticsId(po.getShopLogisticsId());
        LogisticsCompanyInf inf = this.findLogisticsCompanyDao(bo.getShopLogistics().getLogistics());
        inf.cancel(bo);

    }
}
