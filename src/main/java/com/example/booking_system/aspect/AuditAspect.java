package com.example.booking_system.aspect;

import com.example.booking_system.service.AuditLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

    private final AuditLogService auditLogService;

    public AuditAspect(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    private Integer getUserId(Object[] args) {
        return args[0] instanceof Integer ? (Integer) args[0] : 0;
    }

    @AfterReturning("execution(* com.example.booking_system.service.ProviderService.register(..))")
    public void auditProviderRegister(JoinPoint jp) {
        Object[] args = jp.getArgs();
        auditLogService.log(getUserId(args), "PROVIDER_REGISTER", "Provider", null, null, null);
    }

    @AfterReturning("execution(* com.example.booking_system.service.ServiceService.create(..))")
    public void auditServiceCreate(JoinPoint jp) {
        Object[] args = jp.getArgs();
        int operator = args[3] instanceof Integer ? (Integer) args[3] : 0;
        auditLogService.log(operator, "SERVICE_CREATE", "Service", null, null, null);
    }

    @AfterReturning("execution(* com.example.booking_system.service.ServiceService.update(..))")
    public void auditServiceUpdate(JoinPoint jp) {
        Object[] args = jp.getArgs();
        int operator = args[1] instanceof Integer ? (Integer) args[1] : 0;
        auditLogService.log(operator, "SERVICE_UPDATE", "Service", null, null, null);
    }

    @AfterReturning("execution(* com.example.booking_system.service.ScheduleSlotService.generateSlots(..))")
    public void auditSlotGenerate(JoinPoint jp) {
        Object[] args = jp.getArgs();
        int operator = args[1] instanceof Integer ? (Integer) args[1] : 0;
        auditLogService.log(operator, "SLOT_GENERATE", "ScheduleSlot", null, null, null);
    }

    @AfterReturning("execution(* com.example.booking_system.service.OrderService.createOrder(..))")
    public void auditOrderCreate(JoinPoint jp) {
        Object[] args = jp.getArgs();
        auditLogService.log(getUserId(args), "ORDER_CREATE", "Order", null, null, null);
    }

    @AfterReturning("execution(* com.example.booking_system.service.OrderService.cancelOrder(..))")
    public void auditOrderCancel(JoinPoint jp) {
        Object[] args = jp.getArgs();
        auditLogService.log(getUserId(args), "ORDER_CANCEL", "Order", null, null, null);
    }
}
