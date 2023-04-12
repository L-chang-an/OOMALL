package cn.edu.xmu.oomall.freight.aop.checkor.warehouses;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class CommonPointCuts {
    @Pointcut("@annotation(cn.edu.xmu.oomall.freight.aop.checkor.warehouses.WarehouseExist)")
    public void warehouseExistAnnotation() {
    }
}
