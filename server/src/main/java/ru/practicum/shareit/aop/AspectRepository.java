package ru.practicum.shareit.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Optional;

@Aspect
@Component
@Slf4j
public class AspectRepository {
    @Pointcut("execution(* org.springframework.data.jpa.repository.JpaRepository+.findById(..))")
    public void findByIdMethod() {
    }

    @AfterReturning(
            pointcut = "findByIdMethod()",
            returning = "result"
    )
    public void afterFindById(JoinPoint joinPoint, Optional<?> result) {
        if (result == null || result.isEmpty()) {
            Object[] args = joinPoint.getArgs();
            Integer id = (Integer) args[0];
            String repositoryName = joinPoint.getTarget().getClass().getName();
            String entityName = repositoryName.replace("Repository", "");
            throw new NotFoundException(entityName + "with id=" + id + " not found");
        }
    }
}
