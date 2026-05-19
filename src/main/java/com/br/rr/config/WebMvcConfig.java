package com.br.rr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	private final PaywallInterceptor paywallInterceptor;

	public WebMvcConfig(PaywallInterceptor paywallInterceptor) {
		this.paywallInterceptor = paywallInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(paywallInterceptor);
	}

}
