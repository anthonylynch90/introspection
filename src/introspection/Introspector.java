package introspection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class Introspector {
	private Class<?> classObject;
	private Method[] methods;
	private JTextArea methodHeaderTextArea;
	private JTextArea propertyTextArea;
	private HashMap<String, EventSet> eventSets;
	private HashMap<String, Property> properties;
	private JTree eventSetTree;
	DefaultMutableTreeNode root;
	
	public Introspector(Class<?> classObject, JTextArea methodTextArea, JTextArea propertyTextArea, JTree eventSetTree) {
		this.classObject = classObject;
		this.methods = this.classObject.getMethods();
		methodHeaderTextArea = methodTextArea;
		this.propertyTextArea = propertyTextArea;
		eventSets = new HashMap<String, EventSet>();
		properties = new HashMap<String, Property>();
		this.eventSetTree = eventSetTree;
		root = (DefaultMutableTreeNode) eventSetTree.getModel().getRoot();
		clearTextAreas();
		generate();
	}
	
	private void generate() {
		List<Method> methodsList;
		methodsList =  Arrays.asList(methods);
		Collections.sort(methodsList, new Comparator<Method>(){

			@Override
			public int compare(Method o1, Method o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		for(Method method: methodsList) {
			generateMethodHeaders(method);
			generateEventSet(method);
			generateProperty(method);
		}
		displayEventSets();
		displayProperties();
		propertyTextArea.select(0, 0);
	}
	
	private void generateMethodHeaders(Method method) {
		String methodHeader ="";
		 int mod = method.getModifiers();
        methodHeader = Modifier.toString(mod) + " "; 
        methodHeader += (getShortName(method.getReturnType().toString()));
        methodHeader += " " + method.getName();
        methodHeader += "(";
        Class<?>[] parameters = method.getParameterTypes();
        if(parameters.length > 0) {
        	methodHeader += this.getShortName(parameters[0].toString()) + " arg1";
         	int argNum = 2;
         	for (int i = 1; i < parameters.length; i++) {
         		methodHeader += ", ";
         		methodHeader += this.getShortName(parameters[i].toString()) + " arg" + argNum;
         		argNum++;
         	}
        }
        methodHeader += ")";
        Class<?>[] exceptions = method.getExceptionTypes();
        if(exceptions.length > 0) {
        	methodHeader += " throws ";
        	methodHeader += getShortName(exceptions[0].toString());
        	for(int j=1; j < exceptions.length; j++) {
        		methodHeader += ", ";
        		methodHeader += getShortName(exceptions[j].toString());
        	}	 
        }
        this.methodHeaderTextArea.append(methodHeader + "\n");
	}
	
	private void clearTextAreas() {
		methodHeaderTextArea.setText("");
		propertyTextArea.setText("");
	}

	private String getShortName(String longName) {
		String shortName = " ";
		StringTokenizer token = new StringTokenizer(longName, ".");
    	while (token.hasMoreTokens()) {
    		shortName= token.nextToken();
    	}
    	if(shortName.charAt(shortName.length()-1) ==';') {
    		shortName = shortName.substring(0, shortName.length()-1);
    		shortName +="[]";
    	}
    	return shortName;
	}
	
	private void generateProperty(Method method) {
		Pattern pattern = Pattern.compile("^(get|set)(.*)$");
		Matcher matcher = pattern.matcher(method.getName());
		if(matcher.find()) {
			Property newProperty = new Property(method);
			boolean propertyExists = false;
			if(properties.containsKey(newProperty.getPropertyName())) {
				propertyExists =true;
				Property prop = properties.get(newProperty.getPropertyName());//returns the property that already exists
				if(prop.getGetter()==null) {
					prop.setGetter(method);//updates getter or setter
				}
				else {
					prop.setSetter(method);
				}
			}
			if(!propertyExists) {
				properties.put(newProperty.getPropertyName(), newProperty);
			}
		}
	}
	
	private void displayProperties() {
		if(!properties.isEmpty()) {
			TreeMap<String, Property> propertiesMap = new TreeMap<String, Property>();
			propertiesMap.putAll(properties);
			Set<String> keys =properties.keySet();
			for(String propertyName: keys) {
				Property property = properties.get(propertyName);
				this.propertyTextArea.append("Name: " + propertyName + " Type: " + getShortName(property.getPropertyType())
						+" Access: [" + property.getAccessType() + "]" + "\n" );
			}
		}
	}
	
	private void generateEventSet(Method method) {
		Pattern pattern = Pattern.compile("^(add|remove)(.*)(Listener)$");
		Matcher matcher = pattern.matcher(method.getName());
		if(matcher.find() &&method.getParameterTypes().length==1) {//non specific binding
			String name = matcher.group(2);
			boolean eventSetExists = false;
			EventSet eventSet = new EventSet(method, name);
			if(eventSets.containsKey(name)) {
				eventSetExists =true;
				EventSet existingEvent = eventSets.get(eventSet.getName());
				if(existingEvent.getAddMethod() != null) {
					existingEvent.setRemoveMethod(method);
				}
				else {
					existingEvent.setAddMethod(method);
				}
			}
			if(!eventSetExists) {
				eventSets.put(name, eventSet);
			}
		}
	}
		
	
	private void displayEventSets() {
		if(!eventSets.isEmpty()) {
			TreeMap<String, EventSet> eventSetMap = new TreeMap<String, EventSet>();
			eventSetMap.putAll(eventSets);
			Set<String> keys = eventSetMap.keySet();
			root.removeAllChildren();
			DefaultTreeModel eventSetTreeModel = (DefaultTreeModel) eventSetTree.getModel();
			for(String name: keys) {
				EventSet eventSet = eventSets.get(name);
				DefaultMutableTreeNode eventSetNode = new DefaultMutableTreeNode(eventSet.getName());
				createEventSetNode(eventSetNode, eventSet);
				if(root.getChildCount()==0) {
					eventSetTreeModel.insertNodeInto(eventSetNode, root, 0);
				}
				else {
					eventSetTreeModel.insertNodeInto(eventSetNode, root, root.getChildCount());
				}
			}
			eventSetTreeModel.reload(root);
		}
	}
	
	private void createEventSetNode(DefaultMutableTreeNode eventSetRootNode, EventSet eventSet) {
		Method[] methods = eventSet.getListenerMethods();
		for(Method method: methods) {
			DefaultMutableTreeNode methodNode = new DefaultMutableTreeNode(method.getName());
			eventSetRootNode.add(methodNode);
		}
	}
}
