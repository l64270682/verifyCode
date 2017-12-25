package com.example.config;

/*
*
 * SwaggerConfig
 * <p>
 * SpringBoot默认已经将classpath:/META-INF/resources/和classpath:/META-INF/resources/webjars/映射
 * 所以该方法不需要重写，如果在SpringMVC中，可能需要重写定义（我没有尝试）
 * 重写该方法需要 extends WebMvcConfigurerAdapter
 * <p>
 * ")
 * //                .addResourceLocations("classpath:/META-INF/resources/webjars/");
 * //    }
*/


import com.google.common.base.Predicate;
import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author by cdx
 * Created on 2017/12/14.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        Predicate<RequestHandler> predicate = new Predicate<RequestHandler>() {
            @Override
            public boolean apply(RequestHandler input) {
                Class<?> declaringClass = input.declaringClass();
                if (declaringClass == BasicErrorController.class) {
                    return false;
                }// 排除

                if (declaringClass.isAnnotationPresent(RestController.class)) {
                    return true;
                } // 被注解的类

                if (input.isAnnotatedWith(ResponseBody.class)) {
                    return true;
                } // 被注解的方法

                return false;
            }
        };
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .select()
                .apis(predicate)
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //大标题
                .title("包含媒体、咨询、搜索引擎关键字、广告等类型接口的服务")
                        //版本
                .version("1.0")
                .build();
    }
}