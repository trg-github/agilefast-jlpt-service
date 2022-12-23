package io.agilefastgateway.config;

import io.agilefastgateway.filter.MyFilter;
import io.agilefastgateway.filter.MyOldFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;

/**
 * Filter配置
 *
 * @author
 * @email
 * @date 2017-04-21 21:56
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean xssFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new MyFilter());
        registration.addUrlPatterns("/*");
        registration.setName("myFilter");
        return registration;
    }
}
