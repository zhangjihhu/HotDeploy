package com.hhu;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.*;
import java.lang.instrument.*;
import java.util.*;

import static org.objectweb.asm.Opcodes.ASM7;

public class AgentMain {

	private static String projectCompileOutputPath;

	private static Map<String, Long> classFileAttrMap = new HashMap<>();


	public static void agentmain(String agentArgs, Instrumentation instrumentation) throws UnmodifiableClassException, ClassNotFoundException, IOException, InterruptedException {
		System.out.println("agentmain called");
		projectCompileOutputPath = agentArgs;
		Class[] classes = initClassFileAttrMap(instrumentation);
		while(true) {
			for (Class clazz : classes) {
				byte[] classFileBuffer = getClassFileBufferIfModify(clazz.getName());
				if (classFileBuffer != null) {
					reLoad(clazz, classFileBuffer, instrumentation);
				}
			}
			Thread.sleep(3000);
		}

	}

	/**
	 * reLoad class if the @clazz changed
	 */
	private static void reLoad(Class clazz, byte[] classFileBuffer, Instrumentation instrumentation) throws UnmodifiableClassException, ClassNotFoundException {
		ClassReader cr = new ClassReader(classFileBuffer);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		ClassVisitor cv = new ClassVisitor(ASM7, cw) {
			@Override
			public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
				super.visit(version, access, name, signature, superName, interfaces);
			}
		};
		cr.accept(cv, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
		byte[] bytes = cw.toByteArray();
		ClassDefinition classDefinition = new ClassDefinition(clazz, bytes);
		instrumentation.redefineClasses(classDefinition);
	}

	/**
	 * return classFileBuffer if the class has rebuild/recompile else return null
	 */
	private static byte[] getClassFileBufferIfModify(String className) throws IOException {
		String relativeClassPath = "/" + className.replaceAll("\\.", "/") + ".class";
		File file = new File(projectCompileOutputPath + relativeClassPath);
		long lastModify = file.lastModified();
		if (lastModify != classFileAttrMap.get(className)) {
			System.out.println(className + " has modify");
			classFileAttrMap.put(className, lastModify);

			if (file.length() > Integer.MAX_VALUE) {
				System.out.println(className + ".class too large to load.");
				return null;
			}

			byte[] classFileBuffer = new byte[(int) file.length()];
			InputStream in = AgentMain.class.getResourceAsStream(relativeClassPath);
			int len = in.read(classFileBuffer);
			return classFileBuffer;
		}
		return null;
	}

	/**
	 * initialize the classFileAttrMap with it's className and modify time
	 * classFileAttrMap = Map<className, lastModifyTime>()
	 */
	private static Class[] initClassFileAttrMap(Instrumentation instrumentation) {
		Class[] classes = instrumentation.getAllLoadedClasses();
		Set<Class> myProjectClassSet = new HashSet<>();
		for (Class clazz : classes) {
			if (clazz.getName().startsWith("com.hhu")) {
				myProjectClassSet.add(clazz);
				classFileAttrMap.put(clazz.getName(), 0L);
			}
		}
		Class[] myProjectClassArray = new Class[myProjectClassSet.size()];
		return myProjectClassSet.toArray(myProjectClassArray);
	}


}
