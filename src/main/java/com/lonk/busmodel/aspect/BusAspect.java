package com.lonk.busmodel.aspect;

import com.lonk.busmodel.bo.RuleKeyBO;
import com.lonk.busmodel.config.IUserIdConfigSupplier;
import com.lonk.busmodel.executors.Executor;
import com.lonk.busmodel.executors.Executors;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * 切面
 *
 * @author lonk
 */
@Aspect
public class BusAspect {
    @Resource
    private Executors executors;

    @Resource
    private IUserIdConfigSupplier userIdConfigSupplier;

    /**
     * 切点
     */
    @Pointcut("within(@org.springframework.stereotype.Controller *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void pointcut() {
    }

    /**
     * 织入控制逻辑
     */
    @Around("pointcut()")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(attrs)) {
            return pjp.proceed();
        }

        String requestApiUrl = attrs.getRequest().getRequestURI();
        String userId = userIdConfigSupplier.getUserId(pjp);

        RuleKeyBO ruleKeyBO = executors.matchRuleKeyBO(requestApiUrl, userId);
        Executor executor = executors.getExecutor(ruleKeyBO);
        // 无对应控制器
        if (Objects.isNull(executor)) {
            return pjp.proceed();
        }
        return executor.handle(pjp, userId, ruleKeyBO.getRuleBO());
    }

}
