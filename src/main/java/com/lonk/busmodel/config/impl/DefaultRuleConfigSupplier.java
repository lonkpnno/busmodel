package com.lonk.busmodel.config.impl;

import com.lonk.busmodel.bo.RuleBO;
import com.lonk.busmodel.config.IRuleConfigSupplier;
import com.lonk.busmodel.config.impl.properties.RuleProperties;
import jakarta.annotation.Resource;

import java.util.List;

/**
 * 默认控制规则提供器
 *
 * @author lonk
 */
public class DefaultRuleConfigSupplier implements IRuleConfigSupplier {
    @Resource
    private RuleProperties ruleProperties;

    @Override
    public List<RuleBO> getUserRuleList() {
        return ruleProperties.getUserRuleList();
    }

    @Override
    public List<RuleBO> getGlobalRuleList() {
        return ruleProperties.getGlobalRuleList();
    }

}
