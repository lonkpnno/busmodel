package com.lonk.busmodel.config;

import com.lonk.busmodel.bo.RuleBO;

import java.util.List;

/**
 * 控制规则提供器接口
 *
 * @author lonk
 */
public interface IRuleConfigSupplier {
    /**
     * @return 用户维度规则
     */
    List<RuleBO> getUserRuleList();

    /**
     * @return 全局维度规则
     */
    List<RuleBO> getGlobalRuleList();

}
