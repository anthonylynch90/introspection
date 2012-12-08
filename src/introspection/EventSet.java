package introspection;

import java.lang.reflect.Method;

public class EventSet {
	private Method addMethod;
	private Method removeMethod;
	private String name;
	
	public EventSet(Method method, String name) {
		if(method.getName().startsWith("add")) {
			addMethod = method;
		}
		else {
			removeMethod = method;
		}
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public Method getAddMethod() {
		return addMethod;
	}
	
	public Method getRemoveMethod() {
		return removeMethod;
	}

	public void setRemoveMethod(Method method) {
		removeMethod = method;	
	}

	public void setAddMethod(Method method) {
		addMethod = method;
	}
	
	public Method[] getListenerMethods() {
		Class<?>[] param = addMethod.getParameterTypes();
		return param[0].getMethods();
	}

}
