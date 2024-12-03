package cn.iocoder.yudao.module.demo.framework.web.config;

import cn.iocoder.yudao.framework.swagger.config.YudaoSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class DemoWebConfiguration {

    @Bean
    public GroupedOpenApi demoGroupedOpenApi() {
        return YudaoSwaggerAutoConfiguration.buildGroupedOpenApi("demo");
    }
}