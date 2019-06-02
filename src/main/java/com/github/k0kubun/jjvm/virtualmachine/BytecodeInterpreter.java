package com.github.k0kubun.jjvm.virtualmachine;

import com.github.k0kubun.jjvm.classfile.AttributeInfo;
import com.github.k0kubun.jjvm.classfile.ClassFile;
import com.github.k0kubun.jjvm.classfile.ClassFileParser.DescriptorParser;
import com.github.k0kubun.jjvm.classfile.ConstantInfo;
import com.github.k0kubun.jjvm.classfile.FieldType;
import com.github.k0kubun.jjvm.classfile.Instruction.Opcode;
import com.github.k0kubun.jjvm.classfile.Instruction;
import com.github.k0kubun.jjvm.classfile.MethodInfo;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

// The core of the VirtualMachine.
// https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html
public class BytecodeInterpreter {
    private final VirtualMachine vm;
    private final ClassFile klass;
    private final Value self;
    private int pc; // program counter
    private final Deque<Value> stack;

    public BytecodeInterpreter(VirtualMachine vm, Value self, ClassFile klass) {
        this.vm = vm;
        this.klass = klass;
        this.self = self;
        this.stack = new ArrayDeque<>();
        this.pc = 0;
    }

    public void execute(AttributeInfo.Code code) {
        List<Instruction> instructions = code.getInstructions();
        while (true) {
            Instruction instruction = instructions.get(pc);
            Opcode opcode = instruction.getOpcode();

            switch (opcode) {
                case Getstatic:
                    ConstantInfo.Fieldref value = (ConstantInfo.Fieldref)getConstant(instruction.getIndex()); // TODO: switch on constant type
                    ConstantInfo.NameAndType nameAndType = (ConstantInfo.NameAndType)getConstant(value.getNameAndTypeIndex());
                    FieldType type = DescriptorParser.parseField(((ConstantInfo.Utf8)getConstant(nameAndType.getDescriptorIndex())).getString());
                    stack.push(new Value(type, value));
                    break;
                case Ldc:
                    type = DescriptorParser.parseField("Ljava/lang/String;"); // TODO: switch on constant type
                    stack.push(new Value(type, getConstant(instruction.getOperands()[0])));
                    break;
                case Invokevirtual:
                    /*
                    ConstantInfo.String str = (ConstantInfo.String)stack.pop().getValue();
                    ConstantInfo.Utf8 utf8 = (ConstantInfo.Utf8)getConstant(str.getNameIndex());
                    Value receiver = stack.pop();
                    vm.getClass(receiver.getType());
                    System.out.println(utf8.getString());
                     */
                    ConstantInfo.Methodref methodref = (ConstantInfo.Methodref)getConstant(instruction.getIndex());
                    nameAndType = (ConstantInfo.NameAndType)getConstant(methodref.getNameAndTypeIndex());
                    String methodName = ((ConstantInfo.Utf8)getConstant(nameAndType.getNameIndex())).getString();
                    MethodInfo.Descriptor methodType = DescriptorParser.parseMethod(
                            ((ConstantInfo.Utf8)getConstant(nameAndType.getDescriptorIndex())).getString());

                    Value[] args = new Value[methodType.getParameters().size()];
                    for (int i = 0; i < args.length; i++) {
                        args[args.length - 1 - i] = stack.pop();
                    }
                    Value recv = stack.pop();

                    vm.callMethod(methodName, methodType, recv, args);
                    break;
                case Return:
                    return;
                default:
                    throw new RuntimeException("BytecodeInterpreter#execute does not implement opcode: " + opcode.getName());
            }

            pc++;
        }
    }

    private ConstantInfo getConstant(int index) {
        return klass.getConstantPool()[index - 1];
    }

    public static class Value {
        private final FieldType type;
        private final ConstantInfo value;

        public Value(FieldType type, ConstantInfo value) {
            this.type = type;
            this.value = value;
        }

        public FieldType getType() {
            return type;
        }

        public ConstantInfo getValue() {
            return value;
        }
    }
}
