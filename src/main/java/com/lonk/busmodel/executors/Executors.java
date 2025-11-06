package com.lonk.busmodel.executors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lonk.busmodel.bo.RuleBO;
import com.lonk.busmodel.bo.RuleKeyBO;
import com.lonk.busmodel.config.IRuleConfigSupplier;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 控制器决策器
 *
 * @author lonk
 */
@Slf4j
public class Executors {
    /**
     * 维度+apiUrl拼接符
     */
    private final static String API_KEY_JOIN = ",";

    private final Map<String, Executor> EXECUTOR_MAP = Maps.newConcurrentMap();

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Resource
    private IRuleConfigSupplier ruleConfigSupplier;

    /**
     * 根据请求api路径获取一个执行器
     *
     * @param ruleKeyBO 控制规则
     * @return 执行器
     */
    public Executor getExecutor(RuleKeyBO ruleKeyBO) {
        if (Objects.isNull(ruleKeyBO)) {
            return null;
        }
        String controlApiUrlKey = ruleKeyBO.getControlApiUrlKey();
        RuleBO ruleBO = ruleKeyBO.getRuleBO();
        if (StringUtils.isEmpty(controlApiUrlKey)
                || Objects.isNull(ruleBO)
                || StringUtils.isEmpty(ruleBO.getApiUrl())) {
            return null;
        }

        Executor executor = EXECUTOR_MAP.get(controlApiUrlKey);
        if (Objects.nonNull(executor)) {
            return executor;
        }

        synchronized (EXECUTOR_MAP) {
            executor = EXECUTOR_MAP.get(controlApiUrlKey);
            if (Objects.nonNull(executor)) {
                return executor;
            }
            executor = new Executor(ruleBO.getConcurrency());
            EXECUTOR_MAP.put(controlApiUrlKey, executor);
            return executor;
        }
    }

    /**
     * 获取控制规则
     *
     * @param requestApiUrl 请求的api路径
     * @param userId        用户id
     * @return 控制规则
     */
    public RuleKeyBO matchRuleKeyBO(String requestApiUrl, String userId) {
        RuleBO ruleBO;
        if (StringUtils.isNotEmpty(userId)
                && Objects.nonNull(ruleBO = getRuleBO(requestApiUrl
                , Optional.ofNullable(ruleConfigSupplier.getUserRuleList()).orElse(Lists.newArrayList())))) {
            return RuleKeyBO.builder()
                    .controlApiUrlKey(userId + API_KEY_JOIN + requestApiUrl)
                    .ruleBO(ruleBO)
                    .build();
        }

        ruleBO = getRuleBO(requestApiUrl, Optional.ofNullable(ruleConfigSupplier.getGlobalRuleList()).orElse(Lists.newArrayList()));
        if (Objects.isNull(ruleBO)) {
            return null;
        }
        return RuleKeyBO.builder()
                .controlApiUrlKey(API_KEY_JOIN + requestApiUrl)
                .ruleBO(ruleBO)
                .build();
    }

    private RuleBO getRuleBO(String requestApiUrl, List<RuleBO> configRuleList) {
        for (RuleBO ruleBO : Optional.ofNullable(configRuleList).orElse(Lists.newArrayList())) {
            if (antPathMatcher.match(ruleBO.getApiUrl(), requestApiUrl)) {
                return ruleBO;
            }
        }

        return null;
    }

}
