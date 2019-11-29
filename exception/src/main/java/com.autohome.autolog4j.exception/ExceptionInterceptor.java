package com.autohome.autolog4j.exception;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.autohome.autolog4j.exception.annotation.ExceptionWrapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * Created by kcq on 2017/3/13.
 */
public class ExceptionInterceptor {
    private boolean isOpen;

    private Map<String, Class<? extends DependencyException>> exceptionRoutes;

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public Map<String, Class<? extends DependencyException>> getExceptionRoutes() {
        return exceptionRoutes;
    }

    public void setExceptionRoutes(Map<String, Class<? extends DependencyException>> exceptionRoutes) {
        this.exceptionRoutes = exceptionRoutes;
    }

    public void afterThrowing(JoinPoint jp, Throwable error) {
        if (this.isOpen) {
            if (error instanceof DependencyException) {
                return;
            }
            Object[] args = jp.getArgs();
            DependencyException ex = buildExceptionByTargetAnnotations(jp, error, args);
            if (ex == null && this.exceptionRoutes != null && this.exceptionRoutes.size() > 0) {
                ex = buildExceptionByRoutes(jp, error, args);
            }
            if (ex != null) {
                throw ex;
            }
        }
    }

    private DependencyException buildExceptionByTargetAnnotations(JoinPoint jp, Throwable error, Object[] args) {
        Signature signature = jp.getSignature();
        if (!(signature instanceof MethodSignature)) {
            return null;
        }
        DependencyException ex = null;
        Annotation annotation = findAnnotationByMethod(((MethodSignature) signature).getMethod());
        if (annotation == null) {
            annotation = findAnnotationByClass(((MethodSignature) signature).getMethod().getDeclaringClass());
        }
        if (annotation != null) {
            ex = buildExceptionByOneAnnotation(annotation, error, args);
        }
        return ex;
    }

    private DependencyException buildExceptionByOneAnnotation(Annotation annotation, Throwable error, Object[] args) {
        if (!(annotation instanceof ExceptionWrapper)) {
            return null;
        }
        Class errorType = ((ExceptionWrapper) annotation).toType();
        if (!DependencyException.class.isAssignableFrom(errorType)) {
            return null;
        }
        DependencyException ex = build(errorType, error, args);
        return ex;
    }

    DependencyException build(Class<? extends DependencyException> errorType, Throwable cause, Object[] args) {
        Constructor c1 = null;
        try {
            c1 = errorType.getDeclaredConstructor(Throwable.class);
            DependencyException ex = (DependencyException) c1.newInstance(cause);
            ex.setArgs(args);
            return ex;
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            return null;
        }
    }

    private DependencyException buildExceptionByRoutes(JoinPoint jp, Throwable cause, Object[] args) {
        DependencyException ex;
        Signature signature = jp.getSignature();
        if (!(signature instanceof MethodSignature)) {
            return null;
        }
        Method method = ((MethodSignature) signature).getMethod();
        String shortClassName = method.getDeclaringClass().getSimpleName();
        String methodFullName = String.format("%s:%s:%s",
                method.getDeclaringClass().getPackage().getName(),
                shortClassName,
                method.getName());

        for (Map.Entry<String, Class<? extends DependencyException>> entry : exceptionRoutes.entrySet()) {
            if (methodFullName.startsWith(entry.getKey())) {
                ex = build(entry.getValue(), cause, args);
                if (ex != null) {
                    return ex;
                }
            }
        }
        return null;
    }

    private Annotation findAnnotationByMethod(Method method) {
        Annotation[] methodAnnotations = method.getDeclaredAnnotations();
        for (Annotation annotation : methodAnnotations) {
            if (annotation instanceof ExceptionWrapper) {
                return annotation;
            }
        }
        return null;
    }

    private Annotation findAnnotationByClass(Class clazz) {
        Annotation[] annotations = clazz.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof ExceptionWrapper) {
                return annotation;
            }
        }
        return null;
    }
}
