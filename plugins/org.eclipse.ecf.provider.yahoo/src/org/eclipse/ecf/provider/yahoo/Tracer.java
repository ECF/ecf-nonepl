package org.eclipse.ecf.provider.yahoo;

public class Tracer {
	
	public static void trace(Object object, String message) {
		if(Activator.getDefault().isDebugging()) {
			System.out.println(object.getClass().getName() + ": " + message);
		}
	}

}
