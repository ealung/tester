package org.channel.tester.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author channel<zclsoft@163.com>
 * @since 2018/05/14 20:10.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(TesterConfig.class)
public @interface EnableTester {
}
