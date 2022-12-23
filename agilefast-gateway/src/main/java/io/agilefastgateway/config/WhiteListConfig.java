package io.agilefastgateway.config;

import io.agilefastgateway.util.AntPathMatcher;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Author Bernie
 * @Date 2020/6/9 13:00
 */
@Configuration
@ConfigurationProperties(prefix = "web")
public class WhiteListConfig {

    private List<String> whitelist;

    protected AntPathMatcher pathMatcher = new AntPathMatcher();

    public List<String> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<String> whitelist) {
        this.whitelist = whitelist;
    }

    public boolean isPermitAllUrl(String url) {
        for (String pattern : whitelist) {
            if (pathMatcher.match(pattern, url)) {
                return true;
            }
        }
        return false;
    }
}
