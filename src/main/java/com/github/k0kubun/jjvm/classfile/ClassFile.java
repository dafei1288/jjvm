package com.github.k0kubun.jjvm.classfile;

import java.util.ArrayList;
import java.util.List;

public class ClassFile {
    private final int minorVersion;
    private final int majorVersion;
    private final ConstantInfo[] constantPool;
    private final List<AccessFlag> accessFlags;
    private final ConstantInfo.Class thisClass;
    private final ConstantInfo.Class superClass;
    private final int[] interfaces;
    private final FieldInfo[] fields;
    private final MethodInfo[] methods;
    private final AttributeInfo[] attributes;

    private final static int MAGIC = 0xCAFEBABE;

    ClassFile(int magic, int minorVersion, int majorVersion, ConstantInfo[] constantPool, int accessFlags, int thisClass,
              int superClass, int[] interfaces, FieldInfo[] fields, MethodInfo[] methods, AttributeInfo[] attributes) {
        if (magic != MAGIC) {
            throw new RuntimeException(String.format("unexpected magic: 0x%X", magic));
        }
        this.minorVersion = minorVersion;
        this.majorVersion = majorVersion;
        this.constantPool = constantPool;
        this.accessFlags = AccessFlag.fromInt(accessFlags);
        this.thisClass = (ConstantInfo.Class)constantPool[thisClass - 1];
        this.superClass = (superClass == 0 ? null : (ConstantInfo.Class)constantPool[superClass - 1]);
        this.interfaces = interfaces;
        this.fields = fields;
        this.methods = methods;
        this.attributes = attributes;
    }

    int getMinorVersion() {
        return minorVersion;
    }

    int getMajorVersion() {
        return majorVersion;
    }

    public String getThisClassName() {
        return thisClass.getName();
    }

    public ConstantInfo.Class getSuperClass() {
        return superClass;
    }

    public String getSuperClassName() {
        if (superClass == null)
            return null;
        return superClass.getName();
    }

    public List<AccessFlag> getAccessFlags() {
        return accessFlags;
    }

    public MethodInfo[] getMethods() {
        return methods;
    }

    public FieldInfo[] getFields() {
        return fields;
    }

    public ConstantInfo[] getConstantPool() {
        return constantPool;
    }

    AttributeInfo[] getAttributes() {
        return attributes;
    }

    public enum AccessFlag {
        ACC_PUBLIC(0x0001),
        ACC_FINAL(0x0010),
        ACC_SUPER(0x0020),
        ACC_INTERFACE(0x0200),
        ACC_ABSTRACT(0x0400),
        ACC_SYNTHETIC(0x1000),
        ACC_ANNOTATION(0x2000),
        ACC_ENUM(0x4000);

        private final int value;

        AccessFlag(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        static List<AccessFlag> fromInt(int accessFlags) {
            List<AccessFlag> list = new ArrayList<>();
            for (AccessFlag flag : AccessFlag.values()) {
                if ((flag.getValue() & accessFlags) != 0) {
                    list.add(flag);
                }
            }
            return list;
        }
    }
}
