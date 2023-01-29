package com.taehyeon.korail.config

import com.taehyeon.korail.common.BaseResponse
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.context.annotation.Configuration
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.*
import javax.servlet.http.HttpServletRequest

@Aspect
@Configuration
class AopConfig {
    val log = logger<AopConfig>()

    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    @Throws(Throwable::class)
    fun postLogging(pjp: ProceedingJoinPoint): Any {
        return logging(pjp)
    }

    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    @Throws(Throwable::class)
    fun getLogging(pjp: ProceedingJoinPoint): Any {
        return logging(pjp)
    }

    @Throws(Throwable::class)
    private fun logging(pjp: ProceedingJoinPoint): Any {
        val request: HttpServletRequest =
            (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).getRequest()
        val startAt = System.currentTimeMillis()
        val reqParam = getRequestParam(pjp)
        val isSwaggerRequest: Boolean = request.getRequestURI().startsWith("/v3/api-docs")

        // swagger 관련 서비스 호출은 Logging 제외
        if (!isSwaggerRequest) {
            if (reqParam == "") {
                log.info(
                    "REQUEST : [{}][{}] {}({})",
                    request.getRequestURI(),
                    request.getRemoteAddr(),
                    pjp.getSignature().getDeclaringTypeName(),
                    pjp.getSignature().getName()
                )
            } else {
                log.info(
                    "REQUEST : [{}][{}] {}({}) = {}",
                    request.getRequestURI(),
                    request.getRemoteAddr(),
                    pjp.getSignature().getDeclaringTypeName(),
                    pjp.getSignature().getName(),
                    reqParam
                )
            }
        }
        val result: Any = pjp.proceed()
        val endAt = System.currentTimeMillis()
        if (!isSwaggerRequest) {
            if (result is BaseResponse) {
                val resultVo: BaseResponse = result as BaseResponse

                val resultCode = if (resultVo.code.isEmpty()) "" else resultVo.code
                log.info(
                    "RESPONSE : [{}][{}] {}({}) = {} ({}ms)",
                    request.getRequestURI(),
                    request.getRemoteAddr(),
                    pjp.getSignature().getDeclaringTypeName(),
                    pjp.getSignature().getName(),
                    resultCode,
                    endAt - startAt
                )
            } else {
                log.info(
                    "RESPONSE : [{}][{}] {}({}) = {} ({}ms)",
                    request.getRequestURI(),
                    request.getRemoteAddr(),
                    pjp.getSignature().getDeclaringTypeName(),
                    pjp.getSignature().getName(),
                    result,
                    endAt - startAt
                )
            }
        }
        return result
    }

    @Throws(Throwable::class)
    private fun exceptionLogging(pjp: ProceedingJoinPoint): Any {
        val request: HttpServletRequest =
            (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).getRequest()
        val startAt = System.currentTimeMillis()
        if (pjp.getArgs().get(0).javaClass == MethodArgumentNotValidException::class.java || pjp.getArgs()
                .get(0).javaClass == BindException::class.java
        ) {
            log.info(
                "REQUEST : [{}][{}] {}({}) = {}",
                request.getRequestURI(),
                request.getRemoteAddr(),
                pjp.getSignature().getDeclaringTypeName(),
                pjp.getSignature().getName(),
                Objects.requireNonNull<Any>((pjp.getArgs().get(0) as BindingResult).getTarget()).toString()
            )
        }
        val result: Any = pjp.proceed()
        val endAt = System.currentTimeMillis()
        log.info(
            "RESPONSE : [{}][{}] {}({}) = {} ({}ms)",
            request.getRequestURI(),
            request.getRemoteAddr(),
            pjp.getSignature().getDeclaringTypeName(),
            pjp.getSignature().getName(),
            result,
            endAt - startAt
        )
        return result
    }

    private fun getRequestParam(joinPoint: ProceedingJoinPoint): String {
        var argStr = ""
        if (joinPoint.getArgs().size > 0) {
            argStr = if (joinPoint.getArgs().get(0) == null) {
                "null"
            } else {
                joinPoint.getArgs().get(0).toString()
            }
        }
        return argStr
    }
}