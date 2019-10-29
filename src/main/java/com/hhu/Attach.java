package com.hhu;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;
import java.util.Scanner;

public class Attach {

	public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("目标pid: ");

		String pid = scanner.nextLine();
		String agentPath = "F:/Project/java/HotDeployment/target/HotDeployment-1.0-SNAPSHOT.jar";
		String classPath = "F:/Project/java/HotDeployment/target/classes";
		VirtualMachine vm = VirtualMachine.attach(pid);
		vm.loadAgent(agentPath, classPath);
		vm.detach();
	}


}
