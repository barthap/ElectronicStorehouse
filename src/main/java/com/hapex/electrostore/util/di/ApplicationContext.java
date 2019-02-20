package com.hapex.electrostore.util.di;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.InstantiationException;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * Created by barthap on 2019-02-18.
 */
@Slf4j
public class ApplicationContext {
    private static Map<Class, Class> diMap = new HashMap<>();
    private static Map<Class, Method> dmMap = new HashMap<>();

    private static Map<Class, Object> applicationScope = new HashMap<>();

    private static Stack<Class> beanStack = new Stack<>();

    static {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .addUrls(ClasspathHelper.forPackage(""))
                .addScanners(new MethodAnnotationsScanner(), new TypeAnnotationsScanner())
        );
        collectAnnotatedInterfaces(reflections, Service.class);
        collectAnnotatedMethods(reflections, Bean.class);
    }

    private static void collectAnnotatedInterfaces(Reflections reflections, Class<? extends Annotation> annotationClass) {
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(annotationClass);
        for (Class<?> implementationClass : types) {
            log.debug("Found interface bean: " + implementationClass.getCanonicalName());

            Class<?>[] interfaces = implementationClass.getInterfaces();

            //class has no interfaces, then add itself
            //TODO: check for multiple implementations of the same interface etc
            if(interfaces.length == 0)
                diMap.put(implementationClass, implementationClass);
            else
                for (Class iface : interfaces) {
                    diMap.put(iface, implementationClass);
                }
        }
    }

    private static void collectAnnotatedMethods(Reflections reflections, Class<? extends Annotation> annotationClass) {
        Set<Method> methods = reflections.getMethodsAnnotatedWith(annotationClass);
        for(Method method : methods) {
            Class<?> returnType = method.getReturnType();
            log.debug("Found method bean: " + returnType.getSimpleName());

            dmMap.put(returnType, method);
        }
    }

    public Object getBean(Class interfaceClass) {

        if (applicationScope.containsKey(interfaceClass)) {
            return applicationScope.get(interfaceClass);
        }

        if(diMap.containsKey(interfaceClass)) {
            Class implementationClass = diMap.get(interfaceClass);

            synchronized (applicationScope) {
                Object service = null;
                try {
                    log.debug("Instantiating bean: " + implementationClass.getSimpleName());
                    service = injectBeans(implementationClass);
                } catch (InstantiationException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                    return null;
                }
                applicationScope.put(interfaceClass, service);
                return service;
            }

        }

        else if(dmMap.containsKey(interfaceClass)) {
            Method method = dmMap.get(interfaceClass);

            try {
                Object implementationObject;
                if(Modifier.isStatic(method.getModifiers()))
                    implementationObject = method.invoke(null);
                else
                    implementationObject = method.invoke(getBean(method.getDeclaringClass()));

                applicationScope.put(interfaceClass, implementationObject);
                return implementationObject;

            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error(e.getLocalizedMessage());
                e.printStackTrace();
                return null;
            }
        }
        else {
            String msg = "Couldn't find bean: " + interfaceClass.getSimpleName();
            log.error(msg);
            throw new RuntimeException(msg);
        }

    }

    public Object injectBeans(Class<?> targetClass) throws InstantiationException {
        if(beanStack.contains(targetClass)) {
            String msg = "Circular dependency found in " + targetClass.getSimpleName();
            log.error(msg);
            throw new RuntimeException(msg);
        }

        try {
            Object targetObj = null;
            beanStack.push(targetClass);

            // look for constructor taking MyService as a parameter
            for (Constructor<?> c : targetClass.getConstructors()) {
                if (c.getParameterCount() > 0) {

                    List<Object> params = new ArrayList<>();
                    for(Class<?> paramType : c.getParameterTypes()) {
                        params.add(getBean(paramType));
                    }

                    log.trace("Creating instance of " + targetClass.getSimpleName() + " with params: " + Arrays.toString(c.getParameterTypes()));
                    targetObj = c.newInstance(params.toArray());
                    break;
                }
            }

            if(targetObj == null) {
                // didn't find appropriate constructor, just use default constructor:
                log.trace("Creating default instance of " + targetClass.getSimpleName());
                targetObj = targetClass.newInstance();
            }

            injectFields(targetClass, targetObj);

            beanStack.pop();

            return targetObj;

        } catch (Exception exc) {
            log.error("Error occured when injecting beans to " + targetClass.getSimpleName());
            throw new RuntimeException(exc);
        }
    }

    private void injectFields(Class<?> targetClass, Object targetObj) throws IllegalAccessException {
        for(Field field : targetClass.getDeclaredFields()) {

            if (field.isAnnotationPresent(Inject.class)) {
                if (!field.isAccessible())
                    field.setAccessible(true);

                if(field.get(targetObj) != null) break;
                log.trace("\tInjecting field " + field.getName());
                log.trace("\tField type: " + field.getType().getSimpleName());
                Object fieldVal = getBean(field.getType());
                field.set(targetObj, fieldVal);
            }
        }
    }


}
