

directory of metadata (src/main/resources):
-- conf
-- -- attributes (override default ones, should avoid if possible)
-- -- component (extended standard component)
-- -- custom (custom component)
-- -- ice_cust_properties (custom component properties)
-- -- ice_properties (extended component properties)
-- -- properties (Sun's RI properties, normally comes from baseline component)


How to add in a new custom component:

#1 add metadata in conf/custom

style-component.xml

<?xml version="1.0" encoding="UTF-8"?>
<component>
	<description>
		<![CDATA[ Links one or more theme CSS files into the page ]]>
	</description>
	<display-name>Output Style</display-name>
	<component-type>com.icesoft.faces.OutputStyleComp</component-type>

	&ice-cust-style-props; //entity reference in sun-faces-config.xml
	<component-extension>
	    <base-component-type>com.sun.faces.Component</base-component-type> //baseline component extended from using generated component 
		<component-family>com.icesoft.faces.OutputStyle</component-family> // same from faces-config.xml
		<renderer-type>com.icesoft.faces.style.OutputStyleRenderer</renderer-type>  // same from faces-config.xml
	</component-extension>
</component>

style-renderer.xml

<?xml version="1.0" encoding="UTF-8"?>
<renderer>
         <description>
             <![CDATA[ Description in here will be used to generate TLD file ]]>
         </description>
	<component-family>com.icesoft.faces.OutputStyle</component-family>
	<renderer-type>com.icesoft.faces.style.OutputStyleRenderer</renderer-type>	
	<renderer-extension>
		<instance-name>outputStyle</instance-name>
		<is-container>false</is-container> //has children
		<renders-children>false</renders-children> //render children
		<tag-name>outputStyle</tag-name>
		<taglib-prefix>@taglib-prefix@</taglib-prefix>
		<taglib-uri>@taglib-uri@</taglib-uri>
	</renderer-extension>
</renderer>


#2 add properties in  conf/ice_cust_properties

cust-style-props.xml

#3 create entity in sun-faces-config.xml

  <!ENTITY ice-cust-style-props SYSTEM "ice_cust_properties/cust-style-props.xml">
  <!ENTITY style-component SYSTEM "custom/style-component.xml">
  <!ENTITY style-renderer SYSTEM "custom/style-renderer.xml">

#4 add component and render in faces-config.xml

      <renderer>
            <component-family>com.icesoft.faces.OutputStyle</component-family>
            <renderer-type>com.icesoft.faces.style.OutputStyleRenderer</renderer-type>
            <renderer-class>com.icesoft.faces.component.style.OutputStyleRenderer</renderer-class>
      </renderer>

    <component>
      <component-type>com.icesoft.faces.OutputStyleComp</component-type>
      <component-class>com.icesoft.faces.component.style.OutputStyle</component-class>
    </component>


#5 complie and generated tag, baseline component and new tld file

#6 extends com/icesoft/faces/component/style/OutputStyleBeanInfoBase.java
in designtime directory:
com/icesoft/faces/component/style/OutputStyleBeanInfo.java

#7 implement OutputStyle and design time related class ...


Running code generation:

build.xml file related target


Override specific method output etc ...

Modify metadata generator:
metadata/src  ("jsfmeta data generators")


Generated Sources Directory:

component-metadata\target\geneated-sources

Component Baseline
component-metadata\target\geneated-sources\component\main\java

Tag Classes under
component-metadata\target\geneated-sources\taglib\main\java

Tld under
component-metadata\target\geneated-sources\tld\icefaces_component.tld

