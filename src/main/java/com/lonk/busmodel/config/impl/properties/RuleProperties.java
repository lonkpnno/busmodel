package com.lonk.busmodel.config.impl.properties;

import com.google.common.collect.Lists;
import com.lonk.busmodel.bo.RuleBO;
import lombok.Data;

import java.util.List;

/**
 * 规则配置
 *
 * @author lonk
 */
@Data
public class RuleProperties {
    /**
     * 用户维度规则
     */
    private List<RuleBO> userRuleList = Lists.newArrayList();

    /**
     * 全局维度规则
     */
    private List<RuleBO> globalRuleList = Lists.newArrayList();

}
