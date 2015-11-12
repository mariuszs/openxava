
OpenXava Mavenized (beta) *outdated*
================================

Using
-----

### Sample pom.xml for Portlet Web Archive (WAR)

    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>

        <artifactId>openxava-portlet</artifactId>
        <packaging>war</packaging>

        <dependencies>
            <dependency>
                <groupId>org.openxava</groupId>
                <version>${openxava.version}</version>
                <artifactId>openxava-portlet</artifactId>
                <type>war</type>
                <scope>runtime</scope>
            </dependency>	
        </dependencies>
    </project>

### Sample pom.xml for Web Archive (WAR)

    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>

        <artifactId>openxava-portlet</artifactId>
        <packaging>war</packaging>

        <dependencies>
            <dependency>
                <groupId>org.openxava</groupId>
                <version>${openxava.version}</version>
                <artifactId>openxava-war</artifactId>
                <type>war</type>
                <scope>runtime</scope>
            </dependency>	
        </dependencies>
    </project>

### Project structure

    pom.xml
    src/
       main/
          java/
             ...
          resources/
             META-INF/
                persistance.xml
             application.xml
             hibernate.cfg.xml
             editors.xml
             controllers.xml
             ...
          webapp/
             WEB-INF/
                web.xml
                ...
             

