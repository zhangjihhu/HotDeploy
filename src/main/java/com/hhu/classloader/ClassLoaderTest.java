package com.hhu.classloader;


import java.io.InputStream;

public class ClassLoaderTest {

	public static void main(String[] args) {
		ClassLoaderTest classLoaderTest = new ClassLoaderTest();

		InputStream inputStream1 = classLoaderTest.getInputStreamFromClassloader();

		InputStream inputStream2 = classLoaderTest.getInputStreamFromClass();

	}

	/**
	 * 使用ClassLoader.getResourceAsStream(name)获取指定name的InputStream
	 * getResource()查找路径为 classPath, 所以name不能以'/'开头
	 *
	 * getResource() This method will first search the parent class loader for the resource;
	 * if the parent is null the path of the class loader built-in to the virtual machine is searched.
	 * That failing, this method will invoke findResource(String) to find the resource.
	 */
	private InputStream getInputStreamFromClassloader() {
		ClassLoader loader = ClassLoaderTest.class.getClassLoader();
		return loader.getResourceAsStream("com/hhu/Test.class");
		// return loader.getResourceAsStream("com/hhu/classloader/A.class");
	}

	/**
	 * 使用class.getResourceAsStream(name)获取指定name的InputStream
	 * 如果name以'/'开头，那么getResource(String)会在classPath下查找相应的资源
	 * 否则，在当前类所在的包下查找相应的资源
	 *
	 * getResource() Finds a resource with a given name.  The rules for searching resources
	 * associated with a given class are implemented by the defining ClassLoader of the class.
	 * This method delegates to this object's class loader.
	 * If this object was loaded by the bootstrap class loader, the method delegates to
	 * ClassLoader.getSystemResourceAsStream().
	 */
	private InputStream getInputStreamFromClass() {
		return ClassLoaderTest.class.getResourceAsStream("/com/hhu/Test.class");
		// return ClassLoaderTest.class.getResourceAsStream("A.class");
	}

}
