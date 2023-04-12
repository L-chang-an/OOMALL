package cn.edu.xmu.oomall.order.dao.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.ToString;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum StatusNo {
    /***************************************************
     *    待付款，状态码100
     **************************************************/
    NEW_ORDER(101,"新订单"),
    WAIT_BALANCE(102,"待支付尾款"),

    /***************************************************
     *    待收货，状态码200
     **************************************************/
    HAS_PAID(201,"已支付"),
    WAIT_GROUP(202,"待成团"),
    WAIT_SHIPMENTS(203,"待发货"),
    HAS_SHIPMENTS(204,"已发货"),

    /***************************************************
     *    已完成，状态码300
     **************************************************/
    COMPLETED(300,"已完成"),

    /***************************************************
     *    已取消，状态码400
     **************************************************/
    WAIT_REFUND(401,"待退款"),
    CANCELLED(402,"已取消");

    public static final Integer ALL=1;
    public static final Integer INTERNAL=100;

    public static final Integer NEW=100;

    public static final Integer SHIPMENT=200;

    public static final Integer SUCCESS=300;

    public static final Integer FAIL=400;

    private int statusNo;
    private String statusMsg;

    StatusNo(int code,String msg){
        this.statusNo=code;
        this.statusMsg=msg;
    }

    @JsonIgnore
    @ToString.Exclude
    public static final Map<Integer, StatusNo> StatusNames = new HashMap() {
        {
            for (Object enum1 : values()) {
                put(((StatusNo) enum1).statusNo, enum1);
            }
        }
    };

    /**
     * 允许的状态迁移
     */
    @JsonIgnore
    @ToString.Exclude
    private static final Map<Integer, Set<Integer>> toStatus = new HashMap<>(){
        {
            put(NEW_ORDER.getStatusNo(), new HashSet<>(){
                {
                    add(WAIT_BALANCE.getStatusNo());
                    add(WAIT_GROUP.getStatusNo());
                    add(HAS_PAID.getStatusNo());
                }
            });
            put(WAIT_BALANCE.getStatusNo(), new HashSet<>(){
                {
                    add(HAS_PAID.getStatusNo());
                    add(WAIT_REFUND.getStatusNo());
                    add(CANCELLED.getStatusNo());
                }
            });
            put(HAS_PAID.getStatusNo(), new HashSet<>(){
                {
                    add(WAIT_SHIPMENTS.getStatusNo());
                    add(WAIT_REFUND.getStatusNo());
                    add(COMPLETED.getStatusNo());
                }
            });
            put(WAIT_GROUP.getStatusNo(), new HashSet<>(){
                {
                    add(HAS_PAID.getStatusNo());
                    add(WAIT_REFUND.getStatusNo());
                }
            });
            put(WAIT_SHIPMENTS.getStatusNo(), new HashSet<>(){
                {
                    add(HAS_SHIPMENTS.getStatusNo());
                    add(WAIT_REFUND.getStatusNo());
                }
            });
            put(HAS_SHIPMENTS.getStatusNo(), new HashSet<>(){
                {
                    add(COMPLETED.getStatusNo());
                }
            });
            put(WAIT_REFUND.getStatusNo(), new HashSet<>(){
                {
                    add(CANCELLED.getStatusNo());
                }
            });
        }
    };

    /**
     * 是否允许状态迁移
     * @author Ming Qiu
     * <p>
     * date: 2022-11-13 0:25
     * @param source
     * @param target
     * @return
     */
    public static boolean allowStatus(Integer source,Integer target){
        boolean ret = false;

        if (null != source && null != target){
            Set<Integer> allowStatusSet = toStatus.get(source);
            if (null != allowStatusSet) {
                ret = allowStatusSet.contains(target);
            }
        }
        return ret;
    }

    public int getStatusNo() {
        return statusNo;
    }

    public String getMessage(){return statusMsg;}
}
