MOSIAGIL-ML
------------
The projects contains a Metamodel definition to build a MOSIAGIL-ML Editor. This editor allows a developer to define scenarios to be simulated in UBIKSIM or MASSIS.

Requirements
------------
ANT 1.6.5 (or higher) and JDK 1.6.0 (or higher) 

MAVEN 3.0 or superior

An environment variables

First run
-----------------
In order to launch the meta editor, write the following in the root directory:

mosiagilml$ mvn install


GENERATING AND USING MOSIAGILML EDITOR
--------------------------------------

Once the metamodel is changed and saved, to generate a new editor write:

sociaalml$ mvn package

A selfcontained editor will be created in the mosiagilmled/target/ directory. You can run the editor to check the meta model with

mosiagilml$ java -jar target/mosiagilmled-1.0-SNAPSHOT-selfcontained.jar

Using the MOSI-AGIL ML Editor
-----------------------------

Go to the *basicscenario* folder. Run the editor
	ant edit
Create a specification using MASSISPopulation or UbikSimPopulation entities. If creating MASSISPopulation, determine the PATH the population will follow. 

When you save, the specification will be stored into "src/main/spec/specification.xml"

GENERATING SIMULATOR SPECIFIC CONFIGURATIONS
--------------------------------------------
To generate the MASSIS configuration run the following within the *basicscenario* folder

	mvn clean compile exec:java -Dexec.mainClass="mosi.massis.MASSISLUAGenerator" -Dexec.args="src/main/spec/specification.xml"

This will produce a *sample.lua* file that can be feeded into the MASSIS terminal

DOCUMENTING
-----------

To generate the documentation of the metamodel and the example, run
mvn clean site site:deploy

The documentation is stored in the root folder of the distribution under target/finalsite/index.html


Licenses
--------
This software is distributed under the terms of the GPLv3 license. A copy is available in the home folder of this distribution.
