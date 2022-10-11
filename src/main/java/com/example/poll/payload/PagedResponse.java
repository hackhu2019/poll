package com.example.poll.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author huhao
 * @created 2022/10/11
 * Description PagedResponse
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

}
