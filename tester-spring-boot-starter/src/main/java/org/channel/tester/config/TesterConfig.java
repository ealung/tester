package org.channel.tester.config;

import org.channel.tester.vo.TesterUser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author channel<zclsoft@163.com>
 * @since 2018/05/14 20:06.
 */
@Configuration
@ComponentScan(basePackages = "org.channel.tester")
public class TesterConfig {
    private int maxLength=20;
    @Resource
    private TesterUserConfig testerUserConfig;
    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        TesterUser testerUser = testerUserConfig.loginUser();
        if(testerUser.getUserName().length()>maxLength||testerUser.getUserPwd().length()>maxLength){
            throw new IllegalArgumentException("用户密码长度不能超过"+maxLength+"位:"+testerUser);
        }
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean( new WebTesterServlet(), "/tester/*");
        servletRegistrationBean.addInitParameter(WebTesterServlet.PARAM_NAME_USERNAME,testerUser.getUserName());
        servletRegistrationBean.addInitParameter(WebTesterServlet.PARAM_NAME_PASSWORD,testerUser.getUserPwd());
        return servletRegistrationBean;
    }
    @Bean
    @ConditionalOnMissingBean(TesterUserConfig.class)
    public TesterUserConfig loginUser() {
        return new TesterUserConfig() {
            @Override
            TesterUser loginUser() {
                TesterUser testerUser = new TesterUser();
                testerUser.setUserName("tester");
                testerUser.setUserPwd("kadatester");
                return testerUser;
            }
        };
    }
}
