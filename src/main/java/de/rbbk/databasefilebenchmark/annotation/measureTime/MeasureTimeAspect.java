package de.rbbk.databasefilebenchmark.annotation.measureTime;

import de.rbbk.databasefilebenchmark.api.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class MeasureTimeAspect {
    private final FileService fileService;
    @Pointcut("@annotation(MeasureTime)")
    public void measureTimeAnnotation() {}

    @Around("measureTimeAnnotation()")
    public Object measureMethodExecutionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.nanoTime();
        Object result = proceedingJoinPoint.proceed();
        long executionTime = System.nanoTime() - start;
        log.info("Time needed to upload files: " + executionTime + "nsec");

        Method method = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
        MeasureTime measureTimeAnnotation = method.getAnnotation(MeasureTime.class);
        String fileName = measureTimeAnnotation.fileName();

        this.fileService.writeNewLineToFile(fileName, String.valueOf(executionTime));
        return result;
    }
}
