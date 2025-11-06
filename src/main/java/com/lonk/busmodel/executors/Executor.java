package com.lonk.busmodel.executors;

import com.lonk.busmodel.bo.RuleBO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 控制器
 *
 * @author lonk
 */
@Slf4j
public class Executor {
    /**
     * 拦截规则BO
     */
    private final RuleBO ruleBO;

    /**
     * 通行证
     */
    private final Semaphore semaphore;

    /**
     * @param ruleBO 控制规则
     */
    public Executor(RuleBO ruleBO) {
        this.ruleBO = ruleBO;
        this.semaphore = new Semaphore(ruleBO.getConcurrency());
    }

    /**
     * 在并发度控制下执行目标函数
     *
     * @param pjp           切点
     * @param requestApiUrl 请求api路径
     * @param userId        用户标识
     * @return 目标函数执行结果
     * @throws Throwable 目标函数所抛异常
     */
    public Object handle(ProceedingJoinPoint pjp, String requestApiUrl, String userId) throws Throwable {
        try {
            // 超时
            if (!semaphore.tryAcquire(ruleBO.getTimeout(), TimeUnit.MILLISECONDS)) {
                log.warn("请求拦截，userId={}, requestApiUrl={}", userId, requestApiUrl);
                return "系统繁忙";
            }
        } catch (InterruptedException interruptedException) {
            log.warn("请求中断，userId={}, requestApiUrl={}", userId, requestApiUrl);
            return "请求中断";
        }

        try {
            return pjp.proceed();
        } finally {
            semaphore.release();
        }
    }

}
