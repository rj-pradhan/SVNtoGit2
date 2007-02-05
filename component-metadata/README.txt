

directory of metadata (src/main/resources):
-- conf
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

<property>
        <property-name>actionListener</property-name>
        <property-extension>
                <category>ADVANCED</category>
                <editor-class>
                        &methodBindingEditor;
                </editor-class>
                <is-hidden>true</is-hidden>
                <tag-attribute>false</tag-attribute> //hide property from tag attribute, no tag class and tld generated.
        </property-extension>
</property>

also 
<property>
        <description>
                The dropListener specifies a method on a backing bean that will
                accept DnDEvents. This value must be a method binding
                expression. This method will be called when a droppable is
                dropped or hovered on this panel unless masked.
        </description>
        <property-name>dropListener</property-name>
        <property-class>javax.faces.el.MethodBinding</property-class>
                <property-extension>
                <category>DRAGANDDROP</category>
                <editor-class>
                        &methodBindingEditor;
                </editor-class>
                <is-bindable>true</is-bindable>//indicating whether
    or not value binding expressions may be used to specify the value of
    the surrounding attribute or property
        </property-extension>
</property>

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


#5 run generator target in build.xml. The following class will be generated: tag, baseline component, beaninfo and new tld file

#6 IDE design time related 

extends com/icesoft/faces/component/style/OutputStyleBeanInfoBase.java
in designtime directory:
com/icesoft/faces/component/style/OutputStyleBeanInfo.java

#7 run time related

implement OutputStyle and design time related class ...


Running code generator:

build.xml file related target


Override specific method output etc ...

Modify metadata generator:
metadata/src  ("jsfmeta data generators")


Generated Sources Directory:

component-metadata/target/geneated-sources

Component Baseline
component-metadata/target/geneated-sources/component/main/java

Tag Classes under
component-metadata/target/geneated-sources/taglib/main/java

Tld under
component-metadata/target/geneated-sources/tld/icefaces_component.tld

beaninfo
component-metadata/target/generated-sources/beaninfo/main/java

testbeaninfo
component-metadata/target/generated-sources/testbeaninfo/main/java

(testbeaninfo is generated without dependencies on IDE related classes)

