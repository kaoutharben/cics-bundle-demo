# Using JCICS from Maven central

1. Add JCICS dependency to war project
```xml
<dependency>
  <groupId>com.ibm.cics</groupId>
  <artifactId>com.ibm.cics.server</artifactId>
  <version>1.700.0</version>
  <scope>provided</scope>
</dependency>
``` 

2. Add some code that uses JCICS to SimpleServlet.java

```java
Task task = Task.getTask();

try {
	String userid = task.getUSERID();
	response.setContentType("text/html");
	response.getWriter().print("Hello!  I am " + userid);
} catch (InvalidRequestException e) {
	throw new RuntimeException(e);
}
```

3. Add imports for `Task` and `InvalidRequestException`	
```java
import com.ibm.cics.server.InvalidRequestException;
import com.ibm.cics.server.Task;
```

4. Run mvn clean install on `demo-war` and observe that it compiles cleanly

# Create a CICS bundle that packages your war for deployment into CICS

1. Create a new module in the `cics-bundle-demo` called `demo-bundle` of type `pom`

2. Set the packaging type to `cics-bundle`

```xml
<packaging>cics-bundle</packaging>
```

3. Add the `cics-bundle-maven-plugin` configuration
```xml
<build>
  <plugins>
  
    <plugin>
      <groupId>com.ibm.cics</groupId>
      <artifactId>cics-bundle-maven-plugin</artifactId>
      <version>0.0.2-SNAPSHOT</version>
      <extensions>true</extensions>
    </plugin>
    
  </plugins>
</build>
```

4. Add a dependency on your war project
```xml
<dependencies>
  <dependency>
    <groupId>com.ibm.cics</groupId>
    <artifactId>demo-war</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <type>war</type>
  </dependency>
</dependencies>
```

5. Set the default JVM server
```xml
<configuration>
  <defaultjvmserver>EYUCMCIJ</defaultjvmserver>
</configuration>
```

6. run mvn clean install on your reactor and look in `demo-war/target` for your built CICS bundle

# Deploying your bundle into CICS

1. Add deploy endpoint configuration to your global `settings.xml`
```xml
<servers>
  <server>
    <id>cicsex61-https</id>
    <configuration>
      <url>https://winmvs28.hursley.ibm.com:28951</url>
    </configuration>
  </server>
</servers>
```

2. Add authentication information for deploy endpoint to user `settings.xml`
```xml
<servers>
  <server>
    <id>cicsex61-https</id>
    <username>USER</username>
    <password>Passw0rd</password>
  </server>
</servers>
```

3. Add the bundle deploy goal to `demo-bundle`
```xml
<executions>
  <execution>
    <goals>
      <goal>deploy</goal>
    </goals>
    <phase>verify</phase>
  </execution>
</executions>
```

3. Pick which endpoint you'd like to deploy to
```xml
<serverId>cicsex61-https</serverId>
```

4. Pick which region you'd like to deploy to
```xml
<cicsplex>CICSEX61</cicsplex>
<region>IYCWEMW2</region>
```

5. Pick which definition you're using to deploy
```xml
<bunddef>FOO</bunddef>
<csdgroup>BAR</csdgroup>
```

6. run build on `cics-bundle-demo` reactor (with temp security config!)

```
mvn \
  -gs global-settings.xml \
  -us user-settings.xml \
  -Djavax.net.ssl.trustStore=cacerts \
  -Djavax.net.ssl.trustStoreType=jks
  -Djavax.net.ssl.trustStorePassword=changeit
  clean install
```

8. Visit [the servlet](http://cicsex61.hursley.ibm.com:28953/demo-war-0.0.1-SNAPSHOT) to see what you published