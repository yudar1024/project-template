// file: src/main/java/com/gcl/app/common/response/PageResponse.java
package com.gcl.app.common.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应封装
 *
 * @param <T> 列表项类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> items;
    private long total;
    private long page;
    private long size;
    private long totalPages;

    /**
     * 从 MyBatis-Plus IPage 构建
     */
    public static <T> PageResponse<T> of(IPage<T> pageResult) {
        PageResponse<T> response = new PageResponse<>();
        response.setItems(pageResult.getRecords());
        response.setTotal(pageResult.getTotal());
        response.setPage(pageResult.getCurrent());
        response.setSize(pageResult.getSize());
        response.setTotalPages(pageResult.getPages());
        return response;
    }

    /**
     * 从自定义列表和分页信息构建
     */
    public static <T> PageResponse<T> of(List<T> items, long total, long page, long size) {
        PageResponse<T> response = new PageResponse<>();
        response.setItems(items);
        response.setTotal(total);
        response.setPage(page);
        response.setSize(size);
        response.setTotalPages(total == 0 ? 0 : (total + size - 1) / size);
        return response;
    }
}
