package introspection;

import java.lang.reflect.Method;

public class Property {
	private Method getter;
	private Method setter;
	private String propertyName;
	
	public Property(Method method) {
		if(method.getName().startsWith("get")) {
			this.getter=method;
		}
		else {
			this.setter = method;
		}
		propertyName = method.getName().substring(3,4).toLowerCase();
		propertyName += method.getName().substring(4);
	}
	
	public void setSetter(Method setter) {
		this.setter = setter;
	}
	
	public void setGetter(Method getter) {
		this.getter = getter;
	}
	
	public String getPropertyType() {
		String type = "";
		if(getter != null) {
			type = getter.getReturnType().toString();
		}
		else if (setter != null){
			Class<?>[] params = setter.getParameterTypes();
			type = params[0].toString();
		}
		return type;
	}
	
	public String getPropertyName() {
		return propertyName;
	}
	
	public String getAccessType() {
		String accessType ="";
		if(getter != null && setter != null) {
			accessType ="Read/Write";
		}
		else if(getter != null) {
			accessType ="Read only";
		}
		else {
			accessType ="Write only";
		}
		return accessType;
	}
	
	public Method getGetter() {
		return getter;
	}
	
	public Method getSetter() {
		return setter;
	}
}
