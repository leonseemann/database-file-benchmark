package de.rbbk.databasefilebenchmark.annotation.measureTime;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class MeasureTimeAspect {
    @Pointcut("@annotation(MeasureTime)")
    public void measureTimeAnnotation() {}

    @Around("measureTimeAnnotation()")
    public Object measureMethodExecutionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        log.info("Time needed to upload files: " + executionTime + "ms");
        return result;
    }
}
