package com.lonk.busmodel.config;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 用户标识提供器接口
 *
 * @author lonk
 */
public interface IUserIdConfigSupplier {
    /**
     * 获取用户标识，可以是用户id、用户名，或用户角色、类别等
     *
     * @param pjp 待执行目标函数切点
     * @return 用户id，用户标识
     */
    String getUserId(ProceedingJoinPoint pjp);

}
