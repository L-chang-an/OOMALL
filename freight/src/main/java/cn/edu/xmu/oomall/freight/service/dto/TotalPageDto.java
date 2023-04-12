package cn.edu.xmu.oomall.freight.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TotalPageDto<T> {
    /**
     * 对象列表
     */
    private List<T> list;
    /**
     * 第几页
     */
    private int page;
    /**
     * 每页数目
     */
    private int pageSize;
    /**
     * 总数
     */
    private int total;
    /**
     * 总页数
     */
    private int pages;
}
