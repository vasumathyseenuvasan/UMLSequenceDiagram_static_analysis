import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.Iterator;
import javax.xml.bind.annotation.XmlElementDecl.GLOBAL;

import net.sourceforge.plantuml.SourceStringReader;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;

public class UMLSeqeuence {

	private static String outputFile;
	private static File[] filesList;
	private static File inputUMLGenerator;
	private static Map sourcesDetailsMap = new HashMap();
	private static Parser objParser;
	public static String sequence;
	public static File getInputUMLGenerator() {
		return inputUMLGenerator;
	}

	public static void setInputUMLGenerator(File inputUMLGenerator) {
		UMLSeqeuence.inputUMLGenerator = inputUMLGenerator;
	}

	public static Map getUMLParserMap() {
		return sourcesDetailsMap;
	}

	public static String getOutputFile() {
		return outputFile;
	}

	public static void setOutputFile(String outputFilePath, String outputFile) {
		//UMLParser.outputFile = "C:/Users/vasumathy/Documents/vasu/"+ outputFile;
		String s = File.separator;
		UMLSeqeuence.outputFile = outputFilePath+s+outputFile;
	}

	public static void main(String args[]) throws FileNotFoundException,
			ParseException {
	//	if (args != null && args.length == 2) {
			try {
				boolean dirExists = true;
				
				//args = new String[2];
				//args[0]="C:\\Users\\vasumathy\\Documents\\Vasu\\UMLParserFiles";
				//args[1]="Sample.png";
				
				UMLSeqeuence umlParser = new UMLSeqeuence();
				
				// Read files one by one from the given directory
				if(args[0]!=null && args[0].length() > 0)
				{
					/*if(args[0].contains("\\"))
					{
					args[0].replace("\\", "/");
					UMLSeqeuence.setOutputFile(args[0],args[1]);
					}
					else*/
						UMLSeqeuence.setOutputFile(args[0],args[1]);
				}
				else
				{
					dirExists = false;
				}
				
				File directory = new File(args[0]);
				if(!directory.exists() || !dirExists
						|| !directory.isDirectory()) { 
					System.out.println("Directory not found or path is invalid");
					return;
				}
				
				if (directory.listFiles() != null)
				{
					filesList = directory.listFiles();
					umlParser.navigateFiles(filesList);
				}
				umlParser.generateSequenceDiagram();
				
			} catch (FileNotFoundException f) {
				System.out.println(f);
			} catch (ParseException p) {
				System.out.println(p);
			} catch (Exception e) {
				System.out.println("please enter file path and output file name");
				System.out.println(e);
			}
	//	} 
//			else {
//			System.out.println("Please Enter valid directory path and output file name");
//		}
	}

	public void navigateFiles(File[] filesList) throws FileNotFoundException,
			ParseException {
		if (filesList.length == 0) {
			System.out.println("No files in the directory");
			return;
		}
		for (int i = 0; i < filesList.length; i++) {
			
			String fileName = filesList[i].getPath();
			 if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0
				 && (fileName.substring(fileName.lastIndexOf(".")+1).equalsIgnoreCase("java")))
				 {
						FileInputStream fis = new FileInputStream(filesList[i].getPath());
						objParser = new Parser(fis);
						objParser.parse();
				 }
		}
		storeMethodCallDetails();
		//removeRedundantDependency();
		//System.out.println("full details:"+sourcesDetailsMap);
		printDetails();
		UmlGeneratorInput umlGenerator = new UmlGeneratorInput();
		String classWithMain = findClassWithMain();
		
		//umlGenerator.generateUMLTextInput();
	}
	
	public void storeMethodCallDetails(){

		if (sourcesDetailsMap != null) {
			for (Object sourceDetailskey : sourcesDetailsMap.keySet()) {
				//System.out.println("Class Name:" + sourceDetailskey.toString());
				String currentClassName= sourceDetailskey.toString();
				
				List<Attributes> attributeList = new ArrayList<Attributes>();
				List<Methods> methodList = new ArrayList<Methods>();
				
				if (sourcesDetailsMap.get(sourceDetailskey) instanceof HashMap) {
					// if there are no details in class return to next class in
					// the sourceDetailsMap
					Map classDetailsMap = (HashMap) sourcesDetailsMap.get(sourceDetailskey);
					if (classDetailsMap.size() == 0) {
						return;
					} else {
						// store attributes details
						if (classDetailsMap.containsKey(Global.ATTRIBUTES)) {
							Object attributesDetailsList = classDetailsMap.get(Global.ATTRIBUTES);
							if (attributesDetailsList != null && attributesDetailsList instanceof ArrayList && ((ArrayList) attributesDetailsList).size() > 0) {
								attributeList = (ArrayList) attributesDetailsList;
							}
						}
						
						
						if (classDetailsMap.containsKey(Global.METHODS)) {
							Object methodsDetailsList = classDetailsMap.get(Global.METHODS);
							if (methodsDetailsList != null
									&& methodsDetailsList instanceof ArrayList
									&& ((ArrayList) methodsDetailsList).size() > 0) {
								methodList = (ArrayList) methodsDetailsList;
								
								for (final java.util.Iterator<Methods> iterator = methodList.iterator(); iterator.hasNext();) {
									Methods method = iterator.next();									

									if(method.getBodyStatements() != null ){
										// Method calls dependencies
										for(MethodCall methodCallObj : method.getBodyStatements().getMethodCalls())
										{
											methodCallObj.setCurrentClass(currentClassName);
											methodCallObj.setCurrentMethodName(method.getName());
											
											if(methodCallObj.getMethodCallingClass()!=null){
												String[] recClasses= methodCallObj.getMethodCallingClass().split(",");
												ArrayList<String> arrLisMethodCallClasses = new ArrayList<String>(Arrays.asList(recClasses)); 
												Collections.reverse(arrLisMethodCallClasses);
												
												if(arrLisMethodCallClasses.size()==1){
													String objName = arrLisMethodCallClasses.get(0);
													
													//check if you can get the object class from parameter.
												if(method.getParameters()!=null && method.getParameters().size()>0){
														for(MethodParameter param:method.getParameters()){
															
															//check if object is initialized inside method
															if(method.getBodyStatements().getVarDeclarations()!=null
																	&& method.getBodyStatements().getVarDeclarations().size()>0){
																for(VariableDecl variableDecl:method.getBodyStatements().getVarDeclarations()){
																	if(variableDecl.getVariableName()!=null && !variableDecl.getVariableName().isEmpty()){
																		if(variableDecl.getVariableName().equals(objName) 
																				&& sourcesDetailsMap.containsKey(variableDecl.getVariableClassType())){
																			methodCallObj.setClassCalled(variableDecl.getVariableClassType());
																		}
																	}
																}
															}
															else if(param.getParamName().equals(objName) && param.getParamType()!=""
																	&& sourcesDetailsMap.containsKey(param.getParamType())){
															methodCallObj.setClassCalled(param.getParamType());
														}
													}
												}//check if you can get the object class from variables declared inside method.
												if(method.getBodyStatements().getVarDeclarations()!=null
														&& method.getBodyStatements().getVarDeclarations().size()>0){
													for(VariableDecl variableDecl:method.getBodyStatements().getVarDeclarations()){
														if(variableDecl.getVariableName()!=null && !variableDecl.getVariableName().isEmpty()){
															if(variableDecl.getVariableName().equals(objName) 
																	&& sourcesDetailsMap.containsKey(variableDecl.getVariableClassType())){
																methodCallObj.setClassCalled(variableDecl.getVariableClassType());
															}
														}
													}
												}
												// check if it is a direct method call to the class (similar to static call)
												if(methodCallObj.isClass() && methodCallObj.getMethodCallingClass()!=null && !methodCallObj.getMethodCallingClass().isEmpty()
														&& sourcesDetailsMap.containsKey(methodCallObj.getMethodCallingClass())){
													methodCallObj.setClassCalled(methodCallObj.getMethodCallingClass());
												}
												//check if you can get the object class from attributes declared in class.
												if(attributeList!=null && attributeList.size()>0){
													for(Attributes attrObj:attributeList){
															if(!attrObj.getName().isEmpty() && attrObj.getName().equals(objName)
																	&& sourcesDetailsMap.containsKey(attrObj.getDataType())){
																methodCallObj.setClassCalled(attrObj.getDataType());
																if(constructorHasObjInit(currentClassName,objName)!=null
																		&& !constructorHasObjInit(currentClassName, objName).isEmpty()){
																	methodCallObj.setClassCalled(constructorHasObjInit(currentClassName, objName));
																}
															}
														}
													}
												}
												
											}
											// If  method is from currentClass 
											else{
												methodCallObj.setClassCalled(currentClassName);
											}
											

											if(checkIfMethodHasReturnCallValue(methodCallObj.getClassCalled(),methodCallObj.getMethodName())!=null
													&& !(checkIfMethodHasReturnCallValue(methodCallObj.getClassCalled(),methodCallObj.getMethodName())).isEmpty()){
												methodCallObj.setReturnValue(checkIfMethodHasReturnCallValue(methodCallObj.getClassCalled(),methodCallObj.getMethodName()));
											}
										}
									}
								}

							}
						}
					}
				}
			}
		}
	}
	
	public void printDetails(){
		if(sourcesDetailsMap !=null){
			for(Object sourceDetailsKey:sourcesDetailsMap.keySet()){
				if(sourcesDetailsMap.get(sourceDetailsKey) instanceof HashMap){
					Map classDetailsMap = (HashMap) sourcesDetailsMap.get(sourceDetailsKey);
					//System.out.println("current class name "+sourceDetailsKey.toString());
					
					if (classDetailsMap.containsKey(Global.METHODS)) {
						Object methodsDetailsList = classDetailsMap.get(Global.METHODS);
						if (methodsDetailsList != null
								&& methodsDetailsList instanceof ArrayList
								&& ((ArrayList) methodsDetailsList).size() > 0) {
							List<Methods> methodList = new ArrayList<Methods>();
							methodList = (ArrayList) methodsDetailsList;
							
							for(Methods method:methodList){
								if(method.getBodyStatements()!=null){
									if(method.getBodyStatements().getMethodCalls()!=null
											&& method.getBodyStatements().getMethodCalls().size()>0){
										for(final java.util.Iterator<MethodCall> iterator = method.getBodyStatements().getMethodCalls().iterator(); iterator.hasNext();){
											MethodCall methodCall = iterator.next();
											if(methodCall.getClassCalled()!=null && ((!methodCall.getClassCalled().isEmpty()
													&& !sourcesDetailsMap.containsKey(methodCall.getClassCalled())) 
													||(methodCall.getClassCalled().isEmpty()))){
												iterator.remove();
											}
										} 
									}
								}
							}
						}
					}
					}
				}
			}
		}
	
	public String findClassWithMain(){
		String classWithMain="";
		if(sourcesDetailsMap !=null){
			for(Object sourceDetailsKey:sourcesDetailsMap.keySet()){
				if(sourcesDetailsMap.get(sourceDetailsKey) instanceof HashMap){
					Map classDetailsMap = (HashMap) sourcesDetailsMap.get(sourceDetailsKey);
					
					if (classDetailsMap.containsKey(Global.METHODS)) {
						Object methodsDetailsList = classDetailsMap.get(Global.METHODS);
						if (methodsDetailsList != null
								&& methodsDetailsList instanceof ArrayList
								&& ((ArrayList) methodsDetailsList).size() > 0) {
							List<Methods> methodList = new ArrayList<Methods>();
							methodList = (ArrayList) methodsDetailsList;
							
							for(Methods method:methodList){
								if(method.getName()!=null && method.getName()!="" && !method.getName().isEmpty()
										&& method.getName().equals("main")){
									return sourceDetailsKey.toString(); 
								}
							}
						}
					}
					}
				}
			}
		return "";
	}
	
	public List<MethodCall> methodCalls(String currentClassName, String currentMethodName){
		List<MethodCall> methodCallList = new ArrayList<MethodCall>();
		if(sourcesDetailsMap !=null){
			//for(Object sourceDetailsKey:sourcesDetailsMap.keySet()){
				if(sourcesDetailsMap.containsKey(currentClassName) 
						&& sourcesDetailsMap.get(currentClassName) instanceof HashMap){
					Map classDetailsMap = (HashMap) sourcesDetailsMap.get(currentClassName);
					
					if (classDetailsMap.containsKey(Global.METHODS)) {
						Object methodsDetailsList = classDetailsMap.get(Global.METHODS);
						if (methodsDetailsList != null
								&& methodsDetailsList instanceof ArrayList
								&& ((ArrayList) methodsDetailsList).size() > 0) {
							List<Methods> methodList = new ArrayList<Methods>();
							methodList = (ArrayList) methodsDetailsList;
							
							for(Methods method:methodList){
								if(method.getName()!=null && method.getName()!="" && !method.getName().isEmpty()
										&& method.getName().equals(currentMethodName)
										&& method.getBodyStatements()!=null && method.getBodyStatements().getMethodCalls()!=null
										&& method.getBodyStatements().getMethodCalls().size()>0){
									return method.getBodyStatements().getMethodCalls(); 
								}
							}
						}
					}
					}
				//}
			}
		return methodCallList;
	}
	
public String constructorHasObjInit(String currentClassName, String objName){
	String varClassType="";
		if(sourcesDetailsMap !=null){
				if(sourcesDetailsMap.containsKey(currentClassName) 
						&& sourcesDetailsMap.get(currentClassName) instanceof HashMap){
					Map classDetailsMap = (HashMap) sourcesDetailsMap.get(currentClassName);
					
					if (classDetailsMap.containsKey(Global.METHODS)) {
						Object methodsDetailsList = classDetailsMap.get(Global.METHODS);
						if (methodsDetailsList != null
								&& methodsDetailsList instanceof ArrayList
								&& ((ArrayList) methodsDetailsList).size() > 0) {
							List<Methods> methodList = new ArrayList<Methods>();
							methodList = (ArrayList) methodsDetailsList;
							
							for(Methods method:methodList){
								if(method.isConstructor
										&& method.getBodyStatements()!=null && method.getBodyStatements().getVarDeclarations()!=null
										&& method.getBodyStatements().getVarDeclarations().size()>0){
									for(VariableDecl varDecl:method.getBodyStatements().getVarDeclarations()){
										if(varDecl.getVariableName()!=null && !varDecl.getVariableName().isEmpty()
												&& varDecl.getVariableName().equals(objName) && varDecl.getVariableClassType()!=null
												&& !varDecl.getVariableClassType().isEmpty()){
											varClassType= varDecl.getVariableClassType();
										}
									}
								}
							}
						}
					}
				}
			}
		return varClassType;
}
	
public String checkIfMethodHasReturnCallValue(String className, String methodName){
	String returnValue="";
	if(sourcesDetailsMap !=null){
		if(sourcesDetailsMap.containsKey(className) 
				&& sourcesDetailsMap.get(className) instanceof HashMap){
			Map classDetailsMap = (HashMap) sourcesDetailsMap.get(className);
			
			if (classDetailsMap.containsKey(Global.METHODS)) {
				Object methodsDetailsList = classDetailsMap.get(Global.METHODS);
				if (methodsDetailsList != null
						&& methodsDetailsList instanceof ArrayList
						&& ((ArrayList) methodsDetailsList).size() > 0) {
					List<Methods> methodList = new ArrayList<Methods>();
					methodList = (ArrayList) methodsDetailsList;
					
					for(Methods method:methodList){
						if(method.getName()!=null && !method.getName().isEmpty() && method.getName().equals(methodName)){
							if(method.returnValue!=null && !method.returnValue.isEmpty())
								return method.returnValue;
						}
					}
				}
			}
		}
	}
	
	return returnValue;
}

	public void generateSequenceDiagram(){
		
		String start=findClassWithMain();
		sequence="@startuml\n";
		sequence = sequence +"actor Actor\n";
		sequence = sequence +"Actor ->" + start +":main";
		sequence=sequence+"\nactivate "+start;
		List<MethodCall> calledfunctions=methodCalls(start,"main");
		//System.out.println(start+":"+"main");
		for(MethodCall obj:calledfunctions){
			sequence=sequence+"\n"+start+"->"+obj.getClassCalled()+":"+obj.getMethodName();
			
			//method parameters
			if(obj.getParameters()!=null && obj.getParameters()!="" && !obj.getParameters().isEmpty()){
				sequence=sequence+"(" +obj.getParameters()+")";
			}
			
			sequence=sequence+"\n"+"activate "+obj.getClassCalled();
			dfs(obj.getClassCalled(),obj.getMethodName());
			
			//if there is return stmt in the methodcall show the dotted arrow
			if(obj.getReturnValue()!=null && !obj.getReturnValue().isEmpty())
				sequence=sequence+"\n"+obj.getClassCalled()+"-->"+start+":"+ obj.getReturnValue(); //+":"+"Finished execution of "+obj.getMethodName();
			
			sequence=sequence+"\ndeactivate "+obj.getClassCalled();
		}
		sequence = sequence+"\n"+start+"-->"+"Actor";
		sequence=sequence+"\ndeactivate "+start;
		sequence=sequence+"\n@enduml";
		displayUMLDiagram();
		//System.out.println(sequence);
		}
	void dfs(String classname,String methodname)
	{
		//System.out.println(classname+":"+methodname);
		
		List<MethodCall> calledfunctions=methodCalls(classname,methodname);
		if(calledfunctions.isEmpty())
			return;
		for(MethodCall obj:calledfunctions){
			sequence=sequence+"\n"+classname+"->"+obj.getClassCalled()+":"+obj.getMethodName();
			
			//method parameters
			if(obj.getParameters()!=null && obj.getParameters()!="" && !obj.getParameters().isEmpty()){
				sequence=sequence+"(" +obj.getParameters()+")";
			}
			
			sequence=sequence+"\n"+"activate "+obj.getClassCalled();
			dfs(obj.getClassCalled(),obj.getMethodName());
			
			//if there is return stmt in the methodcall show the dotted arrow
			if(obj.getReturnValue()!=null && !obj.getReturnValue().isEmpty())
				sequence=sequence+"\n"+obj.getClassCalled()+"-->"+classname;//+":"+"Finished execution of "+obj.getMethodName();
			
			sequence=sequence+"\ndeactivate "+obj.getClassCalled();
		}
		
	}
	
	public void displayUMLDiagram()
	{

		try {
			final OutputStream os = new FileOutputStream(new File(
					UMLSeqeuence.getOutputFile()));
			SourceStringReader reader = new SourceStringReader(sequence);
			// Write the first image to "png"
			File file = new File(UMLSeqeuence.getOutputFile());
			//File file = new File("C://Users//vasumathy//Documents//vasu//SampleDiagram.png");
			String desc = reader.generateImage(file);
			//System.out.println("Output filesssss"+UMLParser.getOutputFile());
			// Return a null string if no generation

			
			
		    Desktop dt = Desktop.getDesktop();	
		    dt.open(file);
		    
		    System.out.println("Done.");		
		    }
			catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error in generating the file");
		    }
	}
	
}
