# Using JCICS from Maven central

1. Add JCICS dependency to `demo-war/pom.xml`.  See: [1]
    
    ```xml
    <dependency>
      <groupId>com.ibm.cics</groupId>
      <artifactId>com.ibm.cics.server</artifactId>
      <version>1.700.0-5.5-PH10453</version>
      <scope>provided</scope>
    </dependency>
    ``` 

1. Add some code that uses JCICS to `demo-war/src/main/demo/SimpleServlet.java`. See: [1]

    ```java
    Task task = Task.getTask();

    try {
        String userid = task.getUSERID();
        response.setContentType("text/html");
        response.getWriter().print("\nI am " + userid);
    } catch (InvalidRequestException e) {
        throw new RuntimeException(e);
    }
    ```

1. Add imports for `Task` and `InvalidRequestException`.

    In Eclipse: Right Click -> Source -> Organize Imports

    ```java
    import com.ibm.cics.server.InvalidRequestException;
    import com.ibm.cics.server.Task;
    ```

1. Run mvn clean install on `demo-war` and observe that it compiles cleanly

# Create a CICS bundle that packages your war for deployment into CICS

1. Create a new module in the `cics-bundle-demo` project called `demo-bundle`.  Set the of type `pom`

1. Edit `demo-bundle/pom.xml` to tell Maven that we're building a CICS bundle.
    Replace `<packaging>pom</packaging>` with this snippet.

    ```xml
    <packaging>cics-bundle</packaging> 

    <build>
      <plugins>
	      
	<!-- [1] Add your dependency to the war project here -->
      
        <plugin>
          <groupId>com.ibm.cics</groupId>
          <artifactId>cics-bundle-maven-plugin</artifactId>
          <version>0.0.2-SNAPSHOT</version>
          <extensions>true</extensions>
	  <!-- [2] Set the default JVM server here -->
        </plugin>
    
      </plugins>
    </build>
    ```

1. Add a dependency on your war project, to include it in the CICS bundle.  Replace [1].

    ```xml
    <dependencies>
      <dependency>
        <groupId>${project.groupId}</groupId>
        <artifactId>demo-war</artifactId>
        <version>${project.version}</version>
        <type>war</type>
      </dependency>
    </dependencies>
    ```

1. Tell the plugin which JVM server to set as the target for any Java part by default. Replace [2].

    ```xml
    <configuration>
      <defaultjvmserver>EYUCMCIJ</defaultjvmserver>
      <!-- [3] Add your deployment configuration here! -->
    </configuration>
    ```

1. Run mvn clean install on your reactor and look in `demo-war/target` for your built CICS bundle
    In Eclipse:
    * Right click on `cics-bundle-demo` -> Run as -> Maven clean
    * Right click on `cics-bundle-demo` -> Run as -> Maven install
    
# Have your system programmer create your bundle definition in CSD

Your system programmer has the freedom to choose whichever CSD group and BUNDDEF name they like, but the bundle directory must be contained in your system's bundle deploy root in order to be deployable by the endpoint.

The bundle directory of your bundle definition should be set as follows: <bundle_deploy_root>/<bundle_id>_<bundle_version>.  So for the demo, ensure the bundle deploy root is set to `/u/expauto/demo-bundle_1.0.0`.

# Deploying your bundle into CICS

1. Add the bundle deploy goal to `demo-bundle/pom.xml` at location [3].  We'd recommend that you use Maven's password encryption, or supply your credentials using environment variables or properties, as shown here.

    ```xml
    <executions>
      <execution>
        <goals>
          <goal>deploy</goal>
        </goals>
        <phase>verify</phase>
	<configuration>
          <url>https://winmvs28.hursley.ibm.com:28951</url>
          <username>${deploy-user}</username>
          <password>${deploy-password}</password>
	  <!-- [4] Add deploy target location here -->
        </configuration>
      </execution>
    </executions>
    ```

1. Set your deploy target in `demo-bundle/pom.xml` at location [4].  You have to identify which `BUNDDEF` you're going to use from the CSD, and which region you want to deploy to.
    
    ```xml
    <bunddef>DEMO</bunddef>
    <csdgroup>DEMO</csdgroup>
    <cicsplex>CICSEX56</cicsplex>
    <region>IYCWEMW2</region>
    ```

1. run build on `cics-bundle-demo` reactor (with temp security config!)
    In Eclipse: Launch the 

    ```
    mvn \
      -gs global-settings.xml \
      -us user-settings.xml \
      -Djavax.net.ssl.trustStore=cacerts \
      -Djavax.net.ssl.trustStoreType=jks \
      -Djavax.net.ssl.trustStorePassword=changeit \
      -Ddeploy-user=USERID \
      -Ddeploy-password=Passw0rd \
      clean install
    ```

1. Visit [the servlet](http://cicsex56.hursley.ibm.com:28953/demo-war-0.0.1-SNAPSHOT) to see what you published
