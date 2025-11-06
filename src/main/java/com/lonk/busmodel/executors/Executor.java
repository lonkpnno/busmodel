package com.lonk.busmodel.executors;

import com.lonk.busmodel.bo.RuleBO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.Objects;
import java.util.Optional;
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
     * 最大并发线程数
     */
    private volatile int concurrency;

    /**
     * 通行证
     */
    private final Semaphore semaphore;

    /**
     * @param concurrency 最大并发线程数
     */
    public Executor(int concurrency) {
        this.concurrency = Math.max(concurrency, 0);
        this.semaphore = new Semaphore(concurrency, true);
    }

    /**
     * 在并发度控制下执行目标函数
     *
     * @param pjp    切点
     * @param userId 用户标识
     * @param ruleBO 控制规则
     * @return 目标函数执行结果
     * @throws Throwable 目标函数所抛异常
     */
    public Object handle(ProceedingJoinPoint pjp, String userId, RuleBO ruleBO) throws Throwable {
        // 没有控制规则
        if (Objects.isNull(ruleBO)) {
            return pjp.proceed();
        }

        try {
            if (!adjustPermitsSmartly(ruleBO)
                    || !semaphore.tryAcquire(ruleBO.getTimeout(), TimeUnit.MILLISECONDS)) {
                log.warn("请求拦截，userId={}, ruleBO={}", userId, ruleBO);
                return Optional.ofNullable(ruleBO.getFailMsg()).orElse("System is busy.");
            }
        } catch (InterruptedException ex) {
            log.warn("请求中断，userId={}, ruleBO={}", userId, ruleBO, ex);
            return "System error.";
        }

        try {
            return pjp.proceed();
        } finally {
            semaphore.release();
        }
    }

    private boolean adjustPermitsSmartly(RuleBO ruleBO) throws InterruptedException {
        int curConcurrency = ruleBO.getConcurrency();
        if (curConcurrency == this.concurrency) {
            return true;
        }

        synchronized (semaphore) {
            int countDiff = curConcurrency - this.concurrency;
            if (countDiff == 0) {
                return true;
            }
            if (countDiff > 0) {
                semaphore.release(countDiff);
                this.concurrency = curConcurrency;
                return true;
            }

            boolean tryAcquire = semaphore.tryAcquire(Math.min(-countDiff, this.concurrency), ruleBO.getTimeout(), TimeUnit.MILLISECONDS);
            if (!tryAcquire) {
                return false;
            }

            this.concurrency = curConcurrency;
            return true;
        }
    }

}
