package com.lonk.busmodel.executors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lonk.busmodel.bo.RuleBO;
import com.lonk.busmodel.config.IRuleConfigSupplier;
import com.lonk.busmodel.config.IUserIdConfigSupplier;
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
    private final static String USERID_API_JOIN = ";";

    private final static String GLOBAL_API_JOIN = ",";

    private final Map<String, Executor> EXECUTOR_MAP = Maps.newConcurrentMap();

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Resource
    private IUserIdConfigSupplier userIdConfigSupplier;

    @Resource
    private IRuleConfigSupplier ruleConfigSupplier;

    /**
     * 根据请求api路径获取一个执行器
     *
     * @param requestApiUrl 请求qpi路径
     * @return 执行器
     */
    public Executor getExecutor(String requestApiUrl) {
        // 先匹配全局的，全局没有则再匹配用户维度的
        Executor executor = doGetExecutor(requestApiUrl
                , GLOBAL_API_JOIN + requestApiUrl
                , Optional.ofNullable(ruleConfigSupplier.getGlobalRuleList()).orElse(Lists.newArrayList()));
        if (Objects.nonNull(executor)) {
            return executor;
        }

        String userId = userIdConfigSupplier.getUserId();
        List<RuleBO> userRuleList = Optional.ofNullable(ruleConfigSupplier.getUserRuleList()).orElse(Lists.newArrayList());
        // 用户维度下，不提供用户标识不拦截
        if (StringUtils.isEmpty(userId)) {
            if (userRuleList.stream().anyMatch(it -> antPathMatcher.match(it.getApiUrl(), requestApiUrl))) {
                log.trace("用户维度拦截，但用户信息为空，requestApiUrl={}", requestApiUrl);
            }
            return null;
        }

        return doGetExecutor(requestApiUrl, userId + USERID_API_JOIN + requestApiUrl, userRuleList);
    }

    private Executor doGetExecutor(String requestApiUrl, String apiUrlKey, List<RuleBO> configRuleList) {
        Executor executor = EXECUTOR_MAP.get(apiUrlKey);
        if (Objects.nonNull(executor)) {
            return executor;
        }

        for (RuleBO ruleBO : configRuleList) {
            if (Objects.isNull(ruleBO)
                    || !antPathMatcher.match(ruleBO.getApiUrl(), requestApiUrl)) {
                continue;
            }
            // 匹配到控制路径
            synchronized (EXECUTOR_MAP) {
                executor = EXECUTOR_MAP.get(apiUrlKey);
                if (Objects.nonNull(executor)) {
                    return executor;
                }
                executor = new Executor(ruleBO);
                EXECUTOR_MAP.put(apiUrlKey, executor);
                return executor;
            }
        }
        return null;
    }

}
