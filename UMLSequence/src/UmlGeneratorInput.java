import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.plantuml.SourceStringReader;


public class UmlGeneratorInput {

	String UMLInputText="@startuml\nskinparam classAttributeIconSize 0\n";
	String UMLInputTextBallSocket= new String("@startuml\n");
	Map sourcesDetailsMap;
	
	public void generateUMLTextInput(){
		try{
		sourcesDetailsMap = UMLSeqeuence.getUMLParserMap();
		if (sourcesDetailsMap != null) {
			for (Object sourceDetailskey : sourcesDetailsMap.keySet()) {
				//List<Dependency> dependencies = new ArrayList<Dependency>();
				//Dependency objDependency;	
				String attibutesDetails="";
				String methodDetails="";
				ClassTypeDetails classDetails = new ClassTypeDetails();
				if (sourcesDetailsMap.containsKey(sourceDetailskey)) {
					Map classDetailsMap = (HashMap)sourcesDetailsMap.get(sourceDetailskey);
					if (classDetailsMap.size() == 0) {
						return;
					} else {
						
							// Store class or interface desourceDetailskey.toString()ndencies
							if(classDetailsMap.containsKey(Global.CLASS_TYPE)){ 
								Object classSpecificDetail = classDetailsMap.get(Global.CLASS_TYPE);
								if(classSpecificDetail != null && classSpecificDetail instanceof ClassTypeDetails)
								{
									classDetails =((ClassTypeDetails)classSpecificDetail); 
								 	if(classDetails.getInterfaces() != null){
										for(String interfaceName:classDetails.getInterfaces()){
											if(sourcesDetailsMap.containsKey(interfaceName))
											UMLInputText+= classDetails.getClassName() +" ..|> "+interfaceName+"\n";
										}
									}
								 	if(classDetails.getInheritances() != null){
										for(String inheritanceName:classDetails.getInheritances()){
											if(sourcesDetailsMap.containsKey(inheritanceName))
											UMLInputText+= classDetails.getClassName() +" --|> "+inheritanceName+"\n";
										}
									}
								}
							}
							
							// Set dependencies from attributes
							if(classDetailsMap.containsKey(Global.ATTRIBUTES)){ 
								Object attributesDetailsList = classDetailsMap.get(Global.ATTRIBUTES);
								if(attributesDetailsList != null && attributesDetailsList instanceof ArrayList
										&& ((ArrayList)attributesDetailsList).size()>0){
									List<Attributes> attributeList = new ArrayList<Attributes>();
									attributeList = (ArrayList)attributesDetailsList;  
									for(Attributes attribute:attributeList){
										if(attribute.getModifier()==1){
											attibutesDetails+="+ ";
										}
										if(attribute.getModifier()==2){
											attibutesDetails+="- ";
										}
										if(attribute.getName()!= null){
											attibutesDetails+=attribute.getName()+" : ";
										}
										if(attribute.getDataType()!= null){
											attibutesDetails+=attribute.getAttributeTypeDisplay()+"\n";
										}
									}
								}
							}
							
							if(classDetailsMap.containsKey(Global.METHODS)){
								Object methodsDetailsList = classDetailsMap.get(Global.METHODS);
								if(methodsDetailsList != null && methodsDetailsList instanceof ArrayList
										&& ((ArrayList)methodsDetailsList).size()>0){
									List<Methods> methodsList = new ArrayList<Methods>();
									methodsList = (ArrayList)methodsDetailsList;
									//attibutesMethodsDetails+="class "+sourceDetailskey.toString()+" {\n";
									for(Methods method:methodsList){
										if(!method.isDependencySet){
											
										if (method.isMainMethod)
											methodDetails+="<u>";
											
										if (method.getModifier() ==1){
											methodDetails+="+";
										}
										if(method.getName()!= null){
											methodDetails+=method.getName();
										}
										
										if(method.getParameters()!=null && method.getParameters().size()>0){
											methodDetails+="(";
											for(MethodParameter parameter:method.getParameters()){
												
												if(parameter.getParamType()!=null)
												{
													methodDetails+=parameter.getParamName()+":";
												}
												if(parameter.getParamType()!=null)
												{
													methodDetails+=parameter.getParamTypeDisplay();
												}
												methodDetails+=",";
											}
											methodDetails = methodDetails.substring(0, methodDetails.length()-1); 
											methodDetails+=")";
										}
										else{
											methodDetails+="()";
										}
										if(method.getReturnType()!=null && !method.getReturnType().isEmpty()){
											methodDetails+=":"+method.getReturnTypeDisplay();
										}
										methodDetails+="\n";
										}
									}
								}
							}
							
							if(classDetails.getType().equals(Global.INTERFACE))
								UMLInputText+= classDetails.getType() +" " + sourceDetailskey.toString() + "<<Interface>>"+"{\n";
							else
								UMLInputText+= classDetails.getType() +" " + sourceDetailskey.toString() + "{\n";
							
							if(attibutesDetails.length()>0 || methodDetails.length()>0)
							{
							UMLInputText+=attibutesDetails+methodDetails;
							}
							
							UMLInputText+="}\n";

					}
					//classDetailsMap.put(Global.DEPENDECY, dependencies);
				}
			}
			UMLInputText+="@enduml";
		}
		//System.out.println(UMLInputText);
		displayUMLDiagram();
		}catch(Exception e){
			UMLInputText+="@enduml";
			displayUMLDiagram();
		}
	}
	
	
	public void displayUMLDiagram()
	{

		try {
			final OutputStream os = new FileOutputStream(new File(
					UMLSeqeuence.getOutputFile()));
			SourceStringReader reader = new SourceStringReader(UMLInputText);
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
