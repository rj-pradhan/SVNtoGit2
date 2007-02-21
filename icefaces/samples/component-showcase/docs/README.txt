              COMPONENT SHOWCASE GETTING STARTED GUIDE
                            Rev. 1.6
                         (www.icefaces.org)
--------------------------------------------------------------------------------

TOPICS

1. What is the Component Showcase?
2. Required JAR Files
3. Building Component Showcase
4. Known Issues

--------------------------------------------------------------------------------
1. What is the Component Showcase?

    The component showcase shows how current ICEfaces custom and standard
components can be used to build a rich web application.  This application
is an excellent reference when using a component for the first time.
The basic shell of the component showcase can also be used as a template for 
developing rich applications.

--------------------------------------------------------------------------------
2. Required JAR Files

    All JAR files used by component showcase are found in the ICEfaces bundle
lib folder and are imported by the Ant build script.

--------------------------------------------------------------------------------
3. Building Component Showcase

    The Component showcase can be built with Ant using two possible targets:

      >ant build.war
      - Builds a Sun RI version of component showcase.

      >ant build.facelet.war
      - Builds a Sun RI and Facelet version of component showcase.

    Both Ant targets build a ./dist/component-showcase.war as well as copying
the classes, lib and web.xml to the ./web/web-inf/ directory.  The war file can
be deployed to any Servlet container.  Developers can also run the application
from the web folder to minimize deployment time.

    The Ant target clean can be used to removed all files and directories
created with build targets.

--------------------------------------------------------------------------------
4. Known Issues

Currently no known issues.