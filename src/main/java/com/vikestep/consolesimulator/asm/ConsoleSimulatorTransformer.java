package com.vikestep.consolesimulator.asm;

import com.vikestep.consolesimulator.common.util.LogHelper;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import squeek.asmhelper.ASMHelper;
import static org.objectweb.asm.Opcodes.*;

import java.util.Arrays;

public class ConsoleSimulatorTransformer implements IClassTransformer
{
    private static final String[] classesBeingTransformed =
            {
                "net.minecraft.client.gui.GuiOptions"
            };

    @Override
    public byte[] transform(String name, String transformedName, byte[] transformingClass)
    {
        boolean isObfuscated = !name.equals(transformedName);
        int index = Arrays.asList(classesBeingTransformed).indexOf(transformedName);
        return index != -1 ? transform(index, transformingClass, isObfuscated) : transformingClass;
    }

    private static byte[] transform(int index, byte[] transformingClass, boolean isObfuscated)
    {
        try
        {
            ClassNode classNode = ASMHelper.readClassFromBytes(transformingClass);
            switch (index)
            {
                case 0:
                    transformGUIOptions(classNode, isObfuscated);
                    break;
                default:
                    break;
            }
            return ASMHelper.writeClassToBytes(classNode);
        }
        catch (Exception e)
        {
            LogHelper.log("Something went wrong trying to transform " +  classesBeingTransformed[index]);
            e.printStackTrace();
        }
        return transformingClass;
    }

    private static void transformGUIOptions(ClassNode guiOptionsClass, boolean isObfuscated)
    {
        final String INIT_GUI     = isObfuscated ? "b" : "initGui" ;
        final String INIT_GUI_SIG = "()V" ;

        for (MethodNode method : guiOptionsClass.methods)
        {
            if (method.name.equals(INIT_GUI) && method.desc.equals(INIT_GUI_SIG))
            {
                AbstractInsnNode lineStartNode = null;
                AbstractInsnNode lineEndNode = null;
                //Remove this.buttonList.add(new GuiButton(101, this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, I18n.format("options.video", new Object[0])));
                for (AbstractInsnNode instruction : method.instructions.toArray())
                {
                    if (instruction.getType() == AbstractInsnNode.LDC_INSN)
                    {
                        if (((LdcInsnNode) instruction).cst.equals("options.stream"))
                        {
                            lineStartNode = ASMHelper.findNextInstructionWithOpcode(instruction, ALOAD);
                            lineEndNode = ASMHelper.findNextInstructionWithOpcode(lineStartNode, POP);
                        }
                    }
                }
                if (lineStartNode != null && lineEndNode != null)
                {
                    ASMHelper.removeFromInsnListUntil(method.instructions, lineStartNode, lineEndNode.getNext());
                }
            }
        }
    }
}
