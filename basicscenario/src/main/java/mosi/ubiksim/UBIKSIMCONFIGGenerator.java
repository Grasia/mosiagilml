package mosi.ubiksim;

import ingenias.generator.browser.*;
import ingenias.generator.datatemplate.*;
import ingenias.generator.interpreter.Codegen;
import ingenias.ingenme.plugin.Ingened2Ingenme;
import mosi.massis.MASSISLUAGenerator;
import ingenias.codeproc.HTMLDocumentGenerator;
import ingenias.editor.*;
import ingenias.editor.entities.Entity;
import ingenias.exception.NotFound;
import ingenias.exception.NotInitialised;
import ingenias.exception.NullEntity;

import java.util.*;
import java.awt.Frame;
import java.awt.geom.Rectangle2D;
import java.io.*;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;

public class UBIKSIMCONFIGGenerator 
extends ingenias.editor.extension.BasicCodeGeneratorImp {
	/**
	 *  Initialises HTML generation from a file containing INGENIAS specification
	 *  and files containing templates to fullfill
	 *
	 *@param  file             Path to file containing INGENIAS specification
	 *@param  diagramTemplate  Description of Parameter
	 *@param  indexTemplate    Description of Parameter
	 *@param  output           Output path for the specification
	 *@exception  Exception    Error accessing any file or malformed XML exception
	 */

	public UBIKSIMCONFIGGenerator(String file) throws Exception {
		super(file);
		this.addTemplate("templates/config.xml");

	}

	/**
	 *  Initialises HTML generation from an existing browser
	 *  and files containing templates to fullfill.
	 *
	 *@param  diagramTemplate  Description of Parameter
	 *@param  indexTemplate    Description of Parameter
	 *@exception  Exception    Description of Exception
	 */
	public UBIKSIMCONFIGGenerator(Browser browser) throws Exception {
		super(browser);
		this.addTemplate("templates/config.xml");
	}

	@Override
	public String getVersion() {
		return "@modubiksimconfiggen.ver@";
	}

	public boolean verify() {
		return true;
	}

	public Vector<ProjectProperty> defaultProperties() {
		Vector<ProjectProperty> result=new Vector<ProjectProperty>();
		Properties p = new Properties();

		/*result.add(
				new ingenias.editor.ProjectProperty(this.getName(),"htmldoc", "HTML document folder",
						"html",
						"The document folder that will contain HTML version of this specification"));*/
		return result;
	}

	public String getName() {
		return "Config.props Spec generator";
	}

	public String getDescription() {
		return "It generates a config specification for the simulation";
	}

	/**
	 *  Generates HTML code
	 *
	 *@exception  Exception  XML exception
	 */
	public Sequences generate() {
		Sequences seq = new Sequences();
		GraphEntity[] ges = browser.getAllEntities();
		seq.addVar(new Var("fname",getProperty("massisluagen:fname").value));		

		
		try {
			GraphEntity[] populations = generateEntitiesOfType("UbikSimPopulation", browser);
			for (GraphEntity ge:populations) {				
				seq.addVar(new Var("AmountProperty", ge.getAttributeByName("AmountProperty").getSimpleValue()));
				seq.addVar(new Var("PopulationDistribution", ge.getAttributeByName("PopulationDistribution").getSimpleValue()));
				seq.addVar(new Var("PopulationBehavior", ge.getAttributeByName("PopulationBehavior").getSimpleValue()));
			}
			GraphEntity[] fireemergency = generateEntitiesOfType("FireEmergency", browser);
			for (GraphEntity ge:fireemergency) {				
				seq.addVar(new Var("EventLocationY", ge.getAttributeByName("EventLocationY").getSimpleValue()));
				seq.addVar(new Var("EventLocationX", ge.getAttributeByName("EventLocationX").getSimpleValue()));
				seq.addVar(new Var("SizeFire", ge.getAttributeByName("SizeFire").getSimpleValue()));
				seq.addVar(new Var("SpeedFire", ge.getAttributeByName("SpeedFire").getSimpleValue()));
				seq.addVar(new Var("EmergencyCreationDelay", ge.getAttributeByName("EmergencyCreationDelay").getSimpleValue()));
			}
		} catch (NotInitialised e) {			
			e.printStackTrace();
		}catch (NotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return seq;
	}
	
	/**
	 * It obtains all entities in the specification whose type represented as string
	 * is the same as the string passed as parameter
	 * 
	 * @param type The type the application is looking for
	 * @return
	 * @throws NotInitialised 
	 */
	public static  GraphEntity[] generateEntitiesOfType(String type, Browser browser) throws NotInitialised {
		Graph[] gs = browser.getGraphs();
		Sequences p = new Sequences();
		GraphEntity[] ges = browser.getAllEntities();
		HashSet actors = new HashSet();
		for (int k = 0; k < ges.length; k++) {			
			if (ges[k].getType().equals(type)) {
				actors.add(ges[k]);
			}
		}
		return toGEArray(actors.toArray());
	}
	
	/**
	 * It casts an array of objets to an array of GraphEntity
	 *  
	 * @param o the array of objects
	 * @return
	 */
	public static GraphEntity[] toGEArray(Object[] o) {
		GraphEntity[] result = new GraphEntity[o.length];
		System.arraycopy(o, 0, result, 0, o.length);
		return result;
	}

	/**
	 *  Generates an index page for the documentation
	 *
	 *@exception  Exception  Description of Exception
	 */

	private String[] toArray(String path) {
		StringTokenizer st = new StringTokenizer(path);
		int tokens = st.countTokens();
		String[] result = new String[tokens];
		for (int k = 0; k < tokens; k++) {
			result[k] = st.nextToken();
		}
		return result;
	}

	private String toSafeName(String name){
		name=name.replace('/','_');
		name=name.replace('\\','_');
		name=name.replace(' ','_');
		return name;

	}

	/**
	 *  Generates HTMLdoc from a INGENIAS specification file (1st param), a diagram
	 *  template (2nd param), and an indexTemplate (3rd param)
	 *
	 *@param  args           Description of Parameter
	 *@exception  Exception  Description of Exception
	 */
	public static void main(String args[]) throws Exception {
		System.out.println("UBIKSIM CONFIG Generator  (C) 2018 Eduardo Varas");
		System.out.println("This program comes with ABSOLUTELY NO WARRANTY; for details check www.gnu.org/copyleft/gpl.html.");
		System.out.println("This is free software, and you are welcome to redistribute it under certain conditions;; for details check www.gnu.org/copyleft/gpl.html.");

		if (args.length==0){
			System.err.println("The first argument (mandatory) has to be the specification file");
		} else {

			if (args.length>=0){ 
				ingenias.editor.Log.initInstance(new PrintWriter(System.out));
				ModelJGraph.disableAllListeners(); // this disable layout listeners that slow down code generation
				// it is a bug of the platform which will be addressed in the future

				ingenias.editor.Log.initInstance(new PrintWriter(System.out));
				UBIKSIMCONFIGGenerator luagen = new UBIKSIMCONFIGGenerator(args[0]);
				Properties props = luagen.getBrowser().getState().prop;				
				luagen.putProperty(new ProjectProperty("massisluagen","massisluagen:fname","fname","./config","massisluagen"));  
				
				luagen.run();
				if (ingenias.editor.Log.getInstance().areThereErrors() ){
					for (Frame f:Frame.getFrames()){
						f.dispose();

					}
					throw new RuntimeException("There are the following code generation errors: "+Log.getInstance().getErrors());		
				}
			} else {
				System.err.println("The first argument (mandatory) has to be the specification file and the second  " +
						"the outputfolder");
			}

		}
		for (Frame f:Frame.getFrames()){ 
			f.dispose();
		}	
	}
}
