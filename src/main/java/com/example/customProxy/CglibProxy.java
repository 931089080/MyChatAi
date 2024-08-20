package com.example.customProxy;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author: owo
 * @version: 1.0
 * @description:
 * @date: 2024-08-01  11:21
 */
public class CglibProxy extends Target{

    private MethodInterceptor methodInterceptor;

    public CglibProxy(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }

    private static Method save0;
    private static Method save1;
    private static Method save2;
    private static MethodProxy saveSuper0;
    private static MethodProxy saveSuper1;
    private static MethodProxy saveSuper2;
    static {
        try {
            save0 = Target.class.getMethod("save");
            save1 = Target.class.getMethod("save", int.class);
            save2 = Target.class.getMethod("save", long.class);
            saveSuper0 = MethodProxy.create(Target.class, CglibProxy.class, "()V", "save", "saveSuper");
            saveSuper1 = MethodProxy.create(Target.class, CglibProxy.class, "(I)V", "save", "saveSuper");
            saveSuper2 = MethodProxy.create(Target.class, CglibProxy.class, "(J)V", "save", "saveSuper");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveSuper(){
        super.save();
    }

    public void saveSuper(int i){
        super.save(i);
    }

    public void saveSuper(long j){
        super.save(j);
    }

    @Override
    public void save() {
        try {
            methodInterceptor.intercept(this, save0, new Object[]{}, saveSuper0);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(int i) {
        try {
            methodInterceptor.intercept(this, save1, new Object[]{i}, saveSuper1);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(long j) {
        try {
            methodInterceptor.intercept(this, save2, new Object[]{j}, saveSuper2);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
