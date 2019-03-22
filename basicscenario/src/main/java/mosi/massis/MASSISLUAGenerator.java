package mosi.massis;
/*
Copyright (C) 2014 Jorge Gomez Sanz (initial versions with Rafael Martinez)
This file is part of INGENIAS IDE, a support tool for the INGENIAS
methodology, availabe at http://grasia.fdi.ucm.es/ingenias or
http://ingenias.sourceforge.net
INGENIAS IDE is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.
INGENIAS IDE is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with INGENIAS IDE; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */


import ingenias.generator.browser.*;
import ingenias.generator.datatemplate.*;
import ingenias.generator.interpreter.Codegen;
import ingenias.ingenme.plugin.Ingened2Ingenme;
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




/**
 *  This class generates HTML documentation from a INGENIAS specification
 *
 *@author     Jorge Gomez
 *@created    29 de marzo de 2003
 */
public class MASSISLUAGenerator
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

	public MASSISLUAGenerator(String file) throws Exception {
		super(file);
		this.addTemplate("templates/lua.xml");

	}

	/**
	 *  Initialises HTML generation from an existing browser
	 *  and files containing templates to fullfill.
	 *
	 *@param  diagramTemplate  Description of Parameter
	 *@param  indexTemplate    Description of Parameter
	 *@exception  Exception    Description of Exception
	 */
	public MASSISLUAGenerator(Browser browser) throws Exception {
		super(browser);
		this.addTemplate("templates/lua.xml");
	}

	@Override
	public String getVersion() {
		return "@modmassisluagen.ver@";
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
		return "LUA Spec generator";
	}

	public String getDescription() {
		return "It generates a LUA specification for the simulation";
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
			GraphEntity[] populations = generateEntitiesOfType("MASSISPopulation", browser);
			for (GraphEntity ge:populations) {
				Repeat rep=new Repeat("popclass");
				rep.add(new Var("popclasspath", ge.getAttributeByName("PATHProperty").getSimpleValue()));
				rep.add(new Var("popclasstype", "FollowingPathAgent"));
				rep.add(new Var("popclassminspeed", "1"));
				rep.add(new Var("popclassmaxspeed", "3"));
				if (ge.getAttributeByName("InitialLocation").getSimpleValue()!=null && ge.getAttributeByName("InitialLocation").getSimpleValue()!="")
					rep.add(new Var("InitialLocation", ge.getAttributeByName("InitialLocation").getSimpleValue()));
				else
					rep.add(new Var("InitialLocation","MainGate"));

				rep.add(new Var("popclassname", ge.getID()));
				rep.add(new Var("amount", ge.getAttributeByName("AmountProperty").getSimpleValue()));
				seq.addRepeat(rep);
			}

			populations = generateEntitiesOfType("MASSISRunAway", browser);
			for (GraphEntity ge:populations) {
				Repeat rep=new Repeat("popclass");
				rep.add(new Var("popclasspath", ge.getAttributeByName("PATHProperty").getSimpleValue()));
				rep.add(new Var("popclasstype", "RunAway"));
				rep.add(new Var("popclassminspeed", "1"));
				rep.add(new Var("popclassmaxspeed", "3"));
				if (ge.getAttributeByName("InitialLocation").getSimpleValue()!=null && ge.getAttributeByName("InitialLocation").getSimpleValue()!="")
					rep.add(new Var("InitialLocation", ge.getAttributeByName("InitialLocation").getSimpleValue()));
				else
					rep.add(new Var("InitialLocation","MainGate"));
				rep.add(new Var("popclassname", ge.getID()));
				rep.add(new Var("amount", ge.getAttributeByName("AmountProperty").getSimpleValue()));
				seq.addRepeat(rep);
			}

			populations = generateEntitiesOfType("MASSISPopWithSpeed", browser);
			for (GraphEntity ge:populations) {
				Repeat rep=new Repeat("popclass");
				rep.add(new Var("popclasspath", ge.getAttributeByName("PATHProperty").getSimpleValue()));
				rep.add(new Var("popclasstype", "FollowingPathAgent"));
				rep.add(new Var("popclassminspeed",  ge.getAttributeByName("PopulationSpeed").getSimpleValue()));
				rep.add(new Var("popclassmaxspeed", ge.getAttributeByName("PopulationSpeed").getSimpleValue()));
				if (ge.getAttributeByName("InitialLocation").getSimpleValue()!=null && ge.getAttributeByName("InitialLocation").getSimpleValue()!="")
					rep.add(new Var("InitialLocation", ge.getAttributeByName("InitialLocation").getSimpleValue()));
				else
					rep.add(new Var("InitialLocation","MainGate"));

				rep.add(new Var("popclassname", ge.getID()));
				rep.add(new Var("amount", ge.getAttributeByName("AmountProperty").getSimpleValue()));
				seq.addRepeat(rep);
			}
		} catch (NotInitialised e) {			
			e.printStackTrace();
		} catch (NotFound e) {
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
		System.out.println("MASSIS LUA Generator  (C) 2012 Jorge Gomez");
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
				MASSISLUAGenerator luagen = new MASSISLUAGenerator(args[0]);
				Properties props = luagen.getBrowser().getState().prop;				
				luagen.putProperty(new ProjectProperty("massisluagen","massisluagen:fname","fname","./sample","massisluagen"));  

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

