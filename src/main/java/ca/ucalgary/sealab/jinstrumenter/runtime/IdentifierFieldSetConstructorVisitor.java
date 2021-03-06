package ca.ucalgary.sealab.jinstrumenter.runtime;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Visits the methods of classes and adds the code to set the `___objectid` field value in the constructor.
 */
public class IdentifierFieldSetConstructorVisitor extends MethodVisitor implements Opcodes {

	public static final String FIELD_OBJECTID = "___objectid";
	
	private boolean isConstructorInstrumented = false;

	private String superClassName;

	private String className;

	public IdentifierFieldSetConstructorVisitor(MethodVisitor methodVisitor, String className, 
	                                            String superClassName) {
		super(ASM7, methodVisitor);
		this.className = className;
		this.superClassName = superClassName;
	}

	@Override
	public void visitMethodInsn(int opCode, String owner, String name, String desc, boolean isInterface) {
		super.visitMethodInsn(opCode, owner, name, desc, isInterface);
		if (!isConstructorInstrumented && (opCode == INVOKESPECIAL) &&  name.equals("<init>") && owner.equals(superClassName)) {
			isConstructorInstrumented = true;
			super.visitVarInsn(ALOAD, 0);
			super.visitMethodInsn(INVOKESTATIC, "ca/ucalgary/sealab/jinstrumenter/runtime/CallSequenceSetRecorder", "getNextId", "()I", false);
			super.visitFieldInsn(PUTFIELD, className, FIELD_OBJECTID, "I");
		}
	}
}
