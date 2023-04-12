package cn.edu.xmu.oomall.freight.aop.checkor.warehouses;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.model.UserToken;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.oomall.freight.mapper.WarehousePoMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @auther mingqiu
 * @date 2020/6/26 下午2:16
 *      modifiedBy Ming Qiu 2020/11/3 22:59
 *
 */
@Aspect
@Component
@Order(1)
public class WarehouseAspect {
    WarehousePoMapper warehousePoMapper;

    @Autowired
    public WarehouseAspect(WarehousePoMapper warehousePoMapper) {
        this.warehousePoMapper = warehousePoMapper;
    }

    private final Logger logger = LoggerFactory.getLogger(cn.edu.xmu.javaee.core.aop.AuditAspect.class);


    @Before("cn.edu.xmu.oomall.freight.aop.checkor.warehouses.CommonPointCuts.warehouseExistAnnotation()")
    public void beforeWarehouseExist(JoinPoint joinPoint) throws Throwable{
        /*
         * 1. 检查仓库是否存在
         * 2. 检查仓库是否属于该商户  ，不过二者似乎可以直接通过一条exist完成
         * 返回false，则代表商户无权操作该仓库（不管是因为仓库不存在，还是仓库不属于该商户）
         * 如果后期qm的测试有细分，再添加一条判断即可
         */
        Object[] objects = joinPoint.getArgs();
        Long shopId = (Long)objects[0];
        Long warehouseId = (Long)objects[1];
        logger.debug("beforeWarehouseExist: warehouseId = {}, shopId = {}", warehouseId, shopId);
        if (!warehousePoMapper.existsByIdAndShopId(warehouseId, shopId))
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺仓库", warehouseId));
    }


}
