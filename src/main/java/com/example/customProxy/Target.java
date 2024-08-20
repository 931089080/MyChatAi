package com.example.customProxy;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author: owo
 * @version: 1.0
 * @description:
 * @date: 2024-08-01  11:15
 */
public class Target {

    public void save(){
        System.out.println("save0");
    }

    public void save(int i){
        System.out.println("save1 " + i);
    }

    public void save(long j){
        System.out.println("save2 " + j);
    }

    public static void main(String[] args) {
        final Target target = new Target();

        CglibProxy cglibProxy = new CglibProxy(new MethodInterceptor() {
            public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                System.out.println("before...");
//                return method.invoke(target, args);
//                return methodProxy.invoke(target, args);
                return methodProxy.invokeSuper(proxy, args);
            }
        });
        cglibProxy.save();
        cglibProxy.save(1);
    }

}
