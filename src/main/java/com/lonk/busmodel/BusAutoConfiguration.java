package com.lonk.busmodel;

import com.lonk.busmodel.aspect.BusAspect;
import com.lonk.busmodel.config.IRuleConfigSupplier;
import com.lonk.busmodel.config.IUserIdConfigSupplier;
import com.lonk.busmodel.config.impl.DefaultRuleConfigSupplier;
import com.lonk.busmodel.config.impl.DefaultUserIdConfigSupplier;
import com.lonk.busmodel.config.impl.properties.RuleProperties;
import com.lonk.busmodel.executors.Executors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;

/**
 * 自动配置类
 *
 * @author lonk
 */
@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties
@Order()
public class BusAutoConfiguration {
    /**
     * @return 切面
     */
    @Bean
    public BusAspect busAspect() {
        return new BusAspect();
    }

    /**
     * @return 决策器
     */
    @Bean
    public Executors executors() {
        return new Executors();
    }

    /**
     * @return 默认用户标识提供器
     */
    @Bean
    @ConditionalOnMissingBean(IUserIdConfigSupplier.class)
    public DefaultUserIdConfigSupplier defaultUserIdConfigSupplier() {
        return new DefaultUserIdConfigSupplier();
    }

    /**
     * @return 默认规则提供器
     */
    @Bean
    @ConditionalOnMissingBean(IRuleConfigSupplier.class)
    public DefaultRuleConfigSupplier defaultRuleConfigSupplier() {
        return new DefaultRuleConfigSupplier();
    }

    /**
     * @return 默认规则配置
     */
    @Bean
    @ConfigurationProperties(prefix = "bus")
    public RuleProperties ruleProperties() {
        return new RuleProperties();
    }

}
