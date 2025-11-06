package com.lonk.busmodel.bo;

import lombok.Builder;
import lombok.Data;

/**
 * 控制规则+keyBO
 *
 * @author lonk
 */
@Data
@Builder
public class RuleKeyBO {
    /**
     * 控制key
     */
    private String controlApiUrlKey;

    /**
     * 控制规则
     */
    private RuleBO ruleBO;

}
