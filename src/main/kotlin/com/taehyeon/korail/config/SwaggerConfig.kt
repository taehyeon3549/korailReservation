package com.taehyeon.korail.config

import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType
import org.springframework.boot.actuate.endpoint.ExposableEndpoint
import org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver
import org.springframework.boot.actuate.endpoint.web.EndpointMapping
import org.springframework.boot.actuate.endpoint.web.EndpointMediaTypes
import org.springframework.boot.actuate.endpoint.web.WebEndpointsSupplier
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.util.StringUtils
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket


@Configuration
class SwaggerConfig {
    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .useDefaultResponseMessages(false)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.taehyeon.korail.domain.reservation"))
//            .paths(PathSelectors.ant("/v1/**"))
            .build()
            .apiInfo(this.apiInfo())
    }

    private fun apiInfo() : ApiInfo {
        return ApiInfoBuilder()
            .title("코레일 기차 예메 시스템 API")
            .version("V1.0")
            .description("Ticketing for Korail Train")
            .contact(
                Contact("taehyeonkim", "", "taehyeonkim@kakao.com")
            )
            .license("Apache License Version 2.0")
            .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
            .build()
    }

    /*
    Setting for Swagger Error,
    Cannot invoke "org.springframework.web.servlet.mvc.condition.PatternsRequestCondition.getPatterns()" because "this.condition" is null
    */
    @Bean
    fun webEndpointServletHandlerMapping(
        webEndpointsSupplier: WebEndpointsSupplier,
        servletEndpointsSupplier: ServletEndpointsSupplier,
        controllerEndpointsSupplier: ControllerEndpointsSupplier,
        endpointMediaTypes: EndpointMediaTypes?,
        corsProperties: CorsEndpointProperties,
        webEndpointProperties: WebEndpointProperties,
        environment: Environment
    ): WebMvcEndpointHandlerMapping? {
        val allEndpoints: MutableList<ExposableEndpoint<*>?> = ArrayList()
        val webEndpoints = webEndpointsSupplier.endpoints
        allEndpoints.addAll(webEndpoints)
        allEndpoints.addAll(servletEndpointsSupplier.endpoints)
        allEndpoints.addAll(controllerEndpointsSupplier.endpoints)

        val basePath = webEndpointProperties.basePath
        val endpointMapping = EndpointMapping(basePath)
        val shouldRegisterLinksMapping = shouldRegisterLinksMapping(webEndpointProperties, environment, basePath)

        return WebMvcEndpointHandlerMapping(
            endpointMapping,
            webEndpoints,
            endpointMediaTypes,
            corsProperties.toCorsConfiguration(),
            EndpointLinksResolver(allEndpoints, basePath),
            shouldRegisterLinksMapping,
            null
        )
    }


    private fun shouldRegisterLinksMapping(
        webEndpointProperties: WebEndpointProperties,
        environment: Environment,
        basePath: String
    ): Boolean {
        return webEndpointProperties.discovery.isEnabled
            && (StringUtils.hasText(basePath) || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
    }
}