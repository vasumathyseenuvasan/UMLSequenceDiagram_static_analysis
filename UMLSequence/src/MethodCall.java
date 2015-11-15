import java.util.ArrayList;


public class MethodCall {
	
	private String methodName;
	private String methodCallingClass;
	private boolean isClass;
	private String classCalled ="";
	private String currentClass;
	private String currentMethodName="";
	private String parameters="";
	private String callingMethodReturnValue="";
	private String returnValue="";
	public boolean isReturnMethodCall=false;
	
	public String getReturnValue() {
		return returnValue;
	}
	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}
	public String getCallingMethodReturnValue() {
		return callingMethodReturnValue;
	}
	public void setCallingMethodReturnValue(String callingMethodReturnValue) {
		this.callingMethodReturnValue = callingMethodReturnValue;
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	private ArrayList<String> parameter = new ArrayList<String>();
	
	public ArrayList<String> getParameter() {
		return parameter;
	}
	public void setParameter(ArrayList<String> parameter) {
		this.parameter = parameter;
	}
	public String getClassCalled() {
		return classCalled;
	}
	public String getCurrentClass() {
		return currentClass;
	}
	public void setCurrentClass(String currentClass) {
		this.currentClass = currentClass;
	}
	public String getCurrentMethodName() {
		return currentMethodName;
	}
	public void setCurrentMethodName(String currentMethodName) {
		this.currentMethodName = currentMethodName;
	}
	public void setClassCalled(String classCalled) {
		this.classCalled = classCalled;
	}
	public boolean isClass() {
		return isClass;
	}
	public void setClass(boolean isClass) {
		this.isClass = isClass;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getMethodCallingClass() {
		return methodCallingClass;
	}
	public void setMethodCallingClass(String methodCallingClass) {
		this.methodCallingClass = methodCallingClass;
	}
	
	

}
