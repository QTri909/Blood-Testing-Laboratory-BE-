package sum25.group03.instrumentservice.audit.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sum25.group03.instrumentservice.audit.service.AuditLogService;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLoggingAspect {
//    private final AuditLogService auditLogService;
//
//    @Around("execution(* sum25.group03.instrumentservice.controller.*.*(..)) && " +
//            "!execution(* sum25.group03.instrumentservice.controller.*.*Error*(..)) && " +
//            "!@annotation(sum25.group03.instrumentservice.audit.annotation.SkipAuditLog)")
//    public Object auditControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
//        String methodName = joinPoint.getSignature().getName();
//        String className = joinPoint.getTarget().getClass().getSimpleName();
//
//
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        String ipAddress = "unknown";
//        String userAgent = "unknown";
//
//        if (attributes != null) {
//            HttpServletRequest request = attributes.getRequest();
//            ipAddress = getClientIpAddress(request);
//            userAgent = request.getHeader("User-Agent");
//        }
//
//
//        String actionType = determineActionType(methodName);
//        String operationName = className + "." + methodName;
//
//        try {
//            Object result = joinPoint.proceed();
//
//
//            if ("READ".equals(actionType)) {
//                auditLogService.logRead(operationName, "API", methodName, ipAddress, userAgent);
//            }
////            else {
////                List<AuditLog.FieldChange> changes = new ArrayList<>();
////                auditLogService.logWrite(operationName, "API", methodName, ipAddress, userAgent, changes);
////            }
//
//            return result;
//        } catch (Exception e) {
//            auditLogService.logWriteFailure(operationName, "API", methodName, ipAddress, userAgent,
//                    e.getClass().getSimpleName(), e.getMessage());
//            throw e;
//        }
//    }
//
//    private String determineActionType(String methodName) {
//        if (methodName.startsWith("get") || methodName.startsWith("find") || methodName.startsWith("search")) {
//            return "READ";
//        }
//        return "WRITE";
//    }
//
//    private String getClientIpAddress(HttpServletRequest request) {
//        String xForwardedFor = request.getHeader("X-Forwarded-For");
//        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
//            return xForwardedFor.split(",")[0].trim();
//        }
//        String xRealIp = request.getHeader("X-Real-IP");
//        if (xRealIp != null && !xRealIp.isEmpty()) {
//            return xRealIp;
//        }
//        return request.getRemoteAddr();
//    }
}
