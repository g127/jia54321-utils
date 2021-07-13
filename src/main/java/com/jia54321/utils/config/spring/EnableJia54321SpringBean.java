package com.jia54321.utils.config.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启 常用的 Spring Bean 对象
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SpringJia54321ImportSelector.class)
@Documented
public @interface EnableJia54321SpringBean {

}
