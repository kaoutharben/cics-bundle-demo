# CICS Bundle Demo

Demonstrates an identical servlet developed 2 ways: one the traditional Explorer way, 
one the Maven way.  This is designed to highlight the differences between the approaches, and emphasise the
advantages of the Maven approach.

The intended way to use this repository is via a pair of demos, that go with an accompanying presentation.  The demos
both walk the audience through the anatomy of the projects involved, and deploy the java application to a Liberty server
running in CICS.

The first demo must be conducted in CICS Explorer 5.6, the second demo is easiest to conduct in Eclipse, but it's possible
to demonstrate it in any development environment that supports Maven.  It's been successfully demoed in Eclipse, IntelliJ,
Visual Studio Code and Eclipse Che.

# Set-up
Clone this repository!  

### CICS Explorer Set-up
+ [Download CICS Explorer 5.6](http://zeus-rt.rtp.raleigh.ibm.com/ibmdl/export/pub/software/htp/zos/tools/zebra-dev/direct/)
+ Create a workspace in CICS Explorer and import the 2 projects under the `explorer` folder in the source tree using `File > Import > Existing projects into workspace`
+ Create an FTP connection in CICS Explorer to winmvs28.hursley.ibm.com
+ Create a CMCI connection in CICS Explorer to https://winmvs28.hursley.ibm.com:28951 (ssl = true)
+ Test that both of your connections work

### Eclipse Set-up
+ [Download Eclipse 4.8+ for Enterprise Java Developers](https://www.eclipse.org/downloads/packages/)
+ Create a workspace in Eclipse and import the project under the `maven` folder in the source tree using `File > Import > Existing Maven projects`

### CICS Set-up
+ Make sure that you have access to the systems
+ Make sure that the following bundle definitions are in the CSD.  If they're not there, ask, they should be!
    ```
    NAME: EXPLORER
    CSDGROUP: DEMO2
    BUNDDIR: /u/expauto/bundles/demo-explorer-bundle_0.0.1/
    ```
    ```
    NAME: MAVEN
    CSDGROUP: DEMO2
    BUNDDIR: /u/expauto/bundles/demo-maven-war_0.0.1/
    ```
+ Make sure the `EXPLORER` bundle isn't installed in `IYCWEMW2` before you start, as it'll fail to install in your demo if it is!

# Demo 1: CICS Explorer

1. Explain that you’re performing this from the Java developer’s point of view
1. Open the .warbundle file and show where the JVM server is specified
1. Right click on the bundle -> `Export Bundle Project to z/OS UNIX File System`
1. Complete the bundle deploy wizard, highlighting the following intellectual and logistical complexities

    Developer needs to know a whole bunch of inappropriate stuff, like
    
      + The fact that the bundle needs to go on zFS
      + _Where_ the bundle has to go on zFS
      + Developer needs a separate FTP connection in CICS Explorer in order to be able to export the bundle to zFS
      + Developer needs write permission to zFS in order to be able to publish the bundle files
      
    This whole aspect is a leaky abstraction that’s complicated to perform in the UI, and increases the complexity of the set from the POV of the system programmer

1. Click the finish button and export the bundle to zFS
	  
    Highlight that this can be a slow process, particularly when you’re exporting a bundle with a lot of parts, because Explorer transfers the files one at a time.
    
    Explain that this has only written the bundle to the remote file system, we still need to tell CICS to install the bundle

1. Go find the `EXPLORER` bundle definition in the CSD and install it into the target region (`IYCWEMW2`)

    Highlight how this requires developers to have CSD INSTALL permission
    
    Highlight how this is an asynchronous process.  Developers have to find the Bundles view, and refresh until their bundle is in the right state
    
    Highlight how this whole process requires developers to learn how to use CICS Explorer, which they wouldn’t otherwise need

1. [Show the servlet running in CICS in a web browser](http://cicsex56.hursley.ibm.com:28953/demo-explorer-war/)

1. Not done yet!  Show how to redeploy the bundle.  This feels laborious, but is important scene-setting for when we demonstrate the Maven plugin later.
    
    + Make a change to the servlet
    + Find the Bundles view
    + Disable the bundle
    + Refresh until disabled
    + Discard the bundle
    + Refresh until discard
    + Open the wizard to redeploy the bundle
    + Repeat the deploy process.  You can highlight here that you're doing the exact same thing again
    + Show the updated servlet running in CICS in a web browser

# Demo 2: Maven

1. Show how we’ve got the exact same servlet but we’re going to build and deploy it using Maven

1. Show how we only have 1 project

1. Show where in pom.xml the JCICS dependency is specified

1. Show where in pom.xml the JVM server is specified

1. Run the Maven build using the provided launch config

    Enter your user ID and password.  Say how typically you’d have this stored in the environment so you wouldn’t have to supply it every time.  I can elaborate on how you’d set that up if you didn’t want to have this bit of complexity in the demo

    Show the servlet gets published very quickly (99% of the time it takes about 6 seconds to publish.  On some occasions it takes up to 30 seconds when we hit the server config update heartbeat badly I think? I need to investigate which bit takes all the time)

1. [Show the published servlet](http://cicsex56.hursley.ibm.com:28953/demo-maven-war-0.0.1-SNAPSHOT/)

1. Make a change to the servlet

    Show at this point how the IDE’s able to do content assist, and in editor compilation etc based on reading the Maven configuration

1. Republish

    Highlight how we didn’t have to discard the bundle we’d previously installed, the API does that for us now

1. Show the published servlet

1. Make a change to the servlet

1. Republish

1. Show the published servlet

1. Republish

1. etc
