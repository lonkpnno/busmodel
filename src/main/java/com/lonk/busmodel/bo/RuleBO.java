package com.lonk.busmodel.bo;

import lombok.Builder;
import lombok.Data;

/**
 * 控制规则BO
 *
 * @author lonk
 */
@Data
@Builder
public class RuleBO {
    /**
     * api请求路径
     */
    private String apiUrl;

    /**
     * 最大并发线程数
     */
    private int concurrency;

    /**
     * 超时时间
     */
    private int timeout;

}
