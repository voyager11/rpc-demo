package org.example.common;

import java.io.Serializable;

/**
 * the invoke information
 */
public class ClassInfo implements Serializable {

    private static final long serivalVersionUID = 1L;

    private String className;
    private String methodName;
    private Class<?>[] types; //argument type
    private Object[] objects; //arguments

    public static long getSerivalVersionUID() {
        return serivalVersionUID;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getTypes() {
        return types;
    }

    public void setTypes(Class<?>[] types) {
        this.types = types;
    }

    public Object[] getObjects() {
        return objects;
    }

    public void setObjects(Object[] objects) {
        this.objects = objects;
    }
}