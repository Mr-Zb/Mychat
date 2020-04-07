package com.fun.framework.configure;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.fun.framework.utils.NumberUtils;
import com.fun.framework.web.support.interceptor.SecureHandlerInterceptorAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Configuration
public class WebMvcConfigure extends WebMvcConfigurerAdapter {
    @Value("${uploadWindows}")
    private String uploadWindows;
    @Value("${uploadLinux}")
    private String uploadLinux;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")){
            registry.addResourceHandler("/img/**").addResourceLocations("file:"+uploadWindows);
        }else {
            //将请求/img/**中资源的请求重新指定为uploadLinux的位置
            registry.addResourceHandler("/img/**").addResourceLocations("file:"+uploadLinux);
        }
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.clear();
        // 转换为utf-8编码，防止乱码
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        stringHttpMessageConverter.setWriteAcceptCharset(false);
        stringHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON));
        converters.add(stringHttpMessageConverter);

        // 允许字节下载
        ByteArrayHttpMessageConverter byteArrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
        converters.add(byteArrayHttpMessageConverter);

        // 使用fastjson输出json
        ValueFilter filter = new ValueFilter() {
            @Override
            public Object process(Object object, String name, Object value) {
                if (value instanceof BigDecimal || value instanceof Double || value instanceof Float) {
                    return new BigDecimal(Math.round(NumberUtils.toDouble(value.toString()) * 10000d) / 10000d);
                }
                return value;
            }
        };
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter4 = new FastJsonHttpMessageConverter();
        fastJsonHttpMessageConverter4.getFastJsonConfig().setSerializerFeatures(SerializerFeature.BrowserCompatible,
                SerializerFeature.DisableCircularReferenceDetect);//解决fastjson内存对象重复/循环引用json错误
        converters.add(fastJsonHttpMessageConverter4);
        super.configureMessageConverters(converters);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
        registry.addInterceptor(secureHandlerInterceptorAdapter()).addPathPatterns("/api/**");
        super.addInterceptors(registry);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").exposedHeaders("Authorization");
    }
    /**
     * 国际化配置
     *
     * @return
     */
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver slr = new CookieLocaleResolver();
        // 设置默认区域,
        slr.setDefaultLocale(Locale.CHINA);
        slr.setCookieMaxAge(3600);// 设置cookie有效期.
        return slr;
    }

    /**
     * 国际化
     *
     * @return
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        // 设置请求地址的参数,默认为：locale
        lci.setParamName(LocaleChangeInterceptor.DEFAULT_PARAM_NAME);
        return lci;
    }

    /**
     * 国际化
     *
     * @return
     */
    @Bean
    public SecureHandlerInterceptorAdapter secureHandlerInterceptorAdapter() {
        SecureHandlerInterceptorAdapter interceptor = new SecureHandlerInterceptorAdapter();
        return interceptor;
    }

    /**
     * 跨域访问能力
     *
     * @return
     */
    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
