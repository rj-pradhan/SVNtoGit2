package com.icesoft.metadata.generators;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.PropertyBean;
import com.sun.rave.jsfmeta.beans.RendererBean;
import java.util.logging.Level;

public class TagLibraryGenerator extends AbstractGenerator {
    
    
    private boolean base;
    
    private String constantMethodBindingPackage;
    
    private String tagClassPackage;
    
    public TagLibraryGenerator() {
        
        base = false;
        constantMethodBindingPackage = null;
        tagClassPackage = null;
    }
    
    public boolean getBase() {
        return base;
    }
    
    public void setBase(boolean base) {
        this.base = base;
    }
    
    public String getConstantMethodBindingPackage() {
        return constantMethodBindingPackage;
    }
    
    public void setConstantMethodBindingPackage(
            String constantMethodBindingPackage) {
        this.constantMethodBindingPackage = constantMethodBindingPackage;
    }
    
    public String getTagClassPackage() {
        return tagClassPackage;
    }
    
    public void setTagClassPackage(String tagClassPackage) {
        this.tagClassPackage = tagClassPackage;
    }
    
    public void generate() throws IOException {
        ComponentBean cbs[] = getConfig().getComponents();
        for (int i = 0; i < cbs.length; i++)
            if (generated(cbs[i].getComponentClass()))
                generate(cbs[i]);
        
    }
    
    private boolean actionListener(String name) {
        if (name.equals("actionListener"))
            return true;
        return name.endsWith("ActionListener");
    }
    
    private void attribute(ComponentBean cb, PropertyBean pb)
    throws IOException {
        String name = pb.getPropertyName();
        String var = mangle(name);
        JavaSourceWriter writer = getWriter();
        writer.emitExpression("// " + name, true);
        writer.emitExpression("private String " + var + " = null;", true);
        writer.startMethod("set" + capitalize(name), "void",
                new String[] { "String" }, new String[] { var });
        writer.emitExpression("this." + var + " = " + var + ";", true);
        writer.endMethod();
        writer.emitNewline();
    }
    
    private void attributes(ComponentBean cb, RendererBean rb)
    throws IOException {
        attributes(cb, rb, ((Set) (new HashSet())));
    }
    
    private void attributes(ComponentBean cb, RendererBean rb, Set set)
    throws IOException {
        PropertyBean pbs[] = cb.getProperties();
        if (pbs == null)
            pbs = new PropertyBean[0];
        for (int i = 0; i < pbs.length; i++) {
            if ("id".equals(pbs[i].getPropertyName())
            || "rendered".equals(pbs[i].getPropertyName())
            || set.contains(pbs[i].getPropertyName()))
                continue;
            set.add(pbs[i].getPropertyName());
            PropertyBean pb = merge(pbs[i], rb.getAttribute(pbs[i]
                    .getPropertyName()));
            if (pb.isTagAttribute())
                attribute(cb, pb);
        }
        
        String baseComponentType = cb.getBaseComponentType();
        if (baseComponentType != null) {
            ComponentBean bcb = getConfig().getComponent(baseComponentType);
            attributes(bcb, rb, set);
        }
    }
    
    private void footer(ComponentBean cb) throws IOException {
        JavaSourceWriter writer = getWriter();
        writer.endClass();
    }
    
    private String stripHtmlName(String s, String word) {
        
        String tmp = "";
        if (!s.startsWith(word)) {
            tmp = s;
        } else {
            tmp = s.substring(word.length());
        }
        return tmp;
    }
    
    private void generate(ComponentBean cb) throws IOException {
        if (cb.getComponentClass().startsWith("javax.faces.component."))
            return;
        if (cb.isSuppressed())
            return;
        RendererBean rb = renderer(cb);
        if (rb == null)
            rb = new RendererBean();
        if (rb.getTagName() == null)
            return;
        String tagClass = null;
        if (tagClassPackage == null) {
            tagClass = cb.getComponentClass() + (getBase() ? "TagBase" : "Tag");
        } else {
            String componentClass = cb.getComponentClass();
            if (componentClass.indexOf(".ext.") > 0) {
                // System.out.println("tag class name="+
                // simpleClassName(cb.getComponentClass()));
                tagClass = tagClassPackage
                        + "."
                        + stripHtmlName(
                        simpleClassName(cb.getComponentClass()), "Html")
                        + (getBase() ? "TagBase" : "Tag");
                
            } else {
                
                tagClass = cb.getComponentClass()
                + (getBase() ? "TagBase" : "Tag");
            }
        }
        File outputFile = outputFile(tagClass);
        outputFile.mkdirs();
        outputFile.delete();
        getWriter().setOutputWriter(
                new BufferedWriter(new FileWriter(outputFile)));
        logger.log(Level.FINER, "Generate " + outputFile.getAbsoluteFile());
        license(cb);
        header(cb);
        required(cb);
        release(cb, rb);
        properties(cb, rb);
        attributes(cb, rb);
        signatures(cb);
        footer(cb);
        getWriter().flush();
        getWriter().close();
    }
    
    private void header(ComponentBean cb) throws IOException {
        ComponentBean bcb = null;
        String baseComponentType = cb.getBaseComponentType();
        if (baseComponentType != null) {
            bcb = getConfig().getComponent(baseComponentType);
            if (bcb == null) {
                    
                logger.log(Level.SEVERE, "invalid baseComponentType");                                
                throw new IllegalArgumentException("invalid baseComponentType");
            }
        }
        JavaSourceWriter writer = getWriter();
        
        // fix html prefix
        String simple = stripHtmlName(simpleClassName(cb.getComponentClass()),
                "Html")
                + "Tag";
        if (getBase())
            simple = simple + "Base";
        if (tagClassPackage == null) {
            String componentClass = cb.getComponentClass();
            int last = componentClass.lastIndexOf('.');
            if (last >= 0)
                writer.emitPackage(componentClass.substring(0, last));
        } else {
            String componentClass = cb.getComponentClass();
            if (componentClass.indexOf(".ext.") > 0) {
                writer.emitPackage(tagClassPackage);
            } else {
                int last = componentClass.lastIndexOf('.');
                if (last >= 0)
                    writer.emitPackage(componentClass.substring(0, last));
            }
        }
        writer.emitNewline();
        writer.emitImport("java.io.IOException");
        writer.emitImport("javax.servlet.jsp.JspException");
        writer.emitImport("javax.faces.component.UIComponent");
        writer.emitImport("javax.faces.context.FacesContext");
        writer.emitImport("javax.faces.convert.Converter");
        writer.emitImport("javax.faces.el.MethodBinding");
        writer.emitImport("javax.faces.el.ValueBinding");
        writer.emitImport("javax.faces.event.ActionEvent");
        writer.emitImport("javax.faces.event.ValueChangeEvent");
        writer.emitImport("javax.faces.webapp.UIComponentTag");
        writer.emitImport("com.icesoft.faces.component.dragdrop.DragEvent");
        writer.emitImport("com.icesoft.faces.component.dragdrop.DropEvent");
        
        if (constantMethodBindingPackage != null)
            writer.emitImport(constantMethodBindingPackage
                    + ".MethodBindingString");
        writer.emitNewline();
        writer.startJavaDoc();
        writer.emitJavaDoc("<p>Auto-generated component tag class.");
        writer.emitJavaDoc("Do <strong>NOT</strong> modify; all changes");
        writer.emitJavaDoc("<strong>will</strong> be lost!</p>");
        writer.endJavaDoc();
        writer.emitNewline();
        writer.startClass(simple, "UIComponentTag", null);
        writer.emitNewline();
    }
    
    private void license(ComponentBean componentbean) throws IOException {
        
        JavaSourceWriter writer = getWriter();
        writer.emitLicense();
        
    }
    
    private void properties(ComponentBean cb, RendererBean rb)
    throws IOException {
        JavaSourceWriter writer = getWriter();
        writer.startJavaDoc();
        writer
                .emitJavaDoc("<p>Transfer tag attributes to component properties.</p>");
        writer.endJavaDoc();
        writer.startMethod("setProperties", "void",
                new String[] { "UIComponent" }, new String[] { "_component" },
                "protected");
        writer.emitExpression("try{", true);
        writer.indent();
        writer.emitExpression("super.setProperties(_component);", true);                
        properties(cb, rb, ((Set) (new HashSet())));
        writer.outdent();
        writer.emitExpression("}catch(Exception e1){e1.printStackTrace();throw new RuntimeException(e1);}", true);
        writer.endMethod();
        writer.emitNewline();
    }
    
    private void properties(ComponentBean cb, RendererBean rb, Set set)
    throws IOException {
        JavaSourceWriter writer = getWriter();
        PropertyBean pbs[] = cb.getProperties();
        if (pbs == null)
            pbs = new PropertyBean[0];
        for (int i = 0; i < pbs.length; i++) {
            if ("id".equals(pbs[i].getPropertyName())
            || "rendered".equals(pbs[i].getPropertyName())
            || set.contains(pbs[i].getPropertyName()))
                continue;
            set.add(pbs[i].getPropertyName());
            PropertyBean pb = merge(pbs[i], rb.getAttribute(pbs[i]
                    .getPropertyName()));
            if (pb.isSuppressed() || !pb.isTagAttribute())
                continue;
            String name = pb.getPropertyName();
            String type = pb.getPropertyClass();
            String var = mangle(name);
            writer.emitExpression("if (" + var + " != null) {", true);
            writer.indent();
            if (!pb.isBindable()) {
                if (primitive(type)) {
                    if ("char".equals(type)
                    || "java.lang.Character".equals(type))
                        writer.emitExpression(
                                "_component.getAttributes().put(\"" + name
                                + "\", new Character(" + var
                                + ".charAt(0)));", true);
                    else
                        writer.emitExpression(
                                "_component.getAttributes().put(\"" + name
                                + "\", " + wrappers.get(type)
                                + ".valueOf(" + var + "));", true);
                } else {
                    if (name.equalsIgnoreCase("var")
                    && cb.getComponentClass().indexOf(".icesoft.") > 0) {
                        
                        String className = simpleClassName(cb
                                .getComponentClass());
                        
                        String[] hasVarClassArray = new String[] {
                            "HtmlDataTable", "PanelTabSet", "PanelSeries" };
                        String[] castClassArray = new String[] {
                            "javax.faces.component.UIData",
                            "com.icesoft.faces.component.panelseries.UISeries",
                            "com.icesoft.faces.component.panelseries.UISeries" };
                        // System.out.println("var class name="+ className);
                        for (int v = 0; v < hasVarClassArray.length; v++) {
                            
                            if (className.equalsIgnoreCase(hasVarClassArray[v])) {
                                
                                String tmp = castClassArray[v] + " "
                                        + hasVarClassArray[v].toLowerCase()
                                        + " = " + "(" + castClassArray[v]
                                        + ")_component;";
                                
                                writer.emitExpression("try{", true);
                                writer.indent();
                                writer.emitExpression(tmp, true);
                                writer.emitExpression(hasVarClassArray[v]
                                        .toLowerCase()
                                        + ".setVar(" + var + ");", true);
                                writer.outdent();
                                writer.emitExpression(
                                        "}catch (ClassCastException cce) {",
                                        true);
                                writer.indent();
                                writer
                                        .emitExpression(
                                        "throw new IllegalStateException(_component.toString() + \" not expected type.  Expected: "
                                        + castClassArray[v]
                                        + ".  Perhaps you're missing a tag?\");",
                                        true);
                                writer.outdent();
                                writer.emitExpression("}", true);
                            }
                        }
                        
                    } else {
                        writer.emitExpression(
                                "_component.getAttributes().put(\"" + name
                                + "\", " + var + ");", true);
                    }
                }
            } else if ("com.icesoft.faces.context.effects.Effect".equals(type)
            && !"effect".equalsIgnoreCase(name)) {
                writer.emitExpression(
                        "com.icesoft.faces.component.ext.taglib.Util.addLocalEffect("
                        + var + ", \"" + name + "\", _component);",
                        true);
            } else if ("com.icesoft.faces.context.effects.Effect".equals(type)
            && "effect".equalsIgnoreCase(name)) {
                writer.emitExpression(
                        "com.icesoft.faces.component.ext.taglib.Util.addEffect("
                        + var + ", _component);", true);
            } else if ("javax.faces.convert.Converter".equals(type)) {
                writer.emitExpression("if (isValueReference(" + var + ")) {",
                        true);
                writer.indent();
                writer.emitExpression(
                        "ValueBinding _vb = getFacesContext().getApplication().createValueBinding("
                        + var + ");", true);
                writer.emitExpression("_component.setValueBinding(\"" + name
                        + "\", _vb);", true);
                writer.outdent();
                writer.emitExpression("} else {", true);
                writer.indent();
                writer
                        .emitExpression(
                        "Converter _converter = FacesContext.getCurrentInstance().",
                        true);
                writer.emitExpression("    getApplication().createConverter("
                        + var + ");", true);
                writer
                        .emitExpression(
                        "_component.getAttributes().put(\"converter\", _converter);",
                        true);
                writer.outdent();
                writer.emitExpression("}", true);
            } else if ("javax.faces.el.MethodBinding".equals(type)) {
                if ("action".equals(name) || "linkAction".equals(name)) {
                    writer.emitExpression("if (isValueReference(" + var
                            + ")) {", true);
                    writer.indent();
                    writer.emitExpression(
                            "MethodBinding _mb = getFacesContext().getApplication().createMethodBinding("
                            + var + ", actionArgs);", true);
                    writer.emitExpression("_component.getAttributes().put(\""
                            + name + "\", _mb);", true);
                    writer.outdent();
                    writer.emitExpression("} else {", true);
                    writer.indent();
                    writer
                            .emitExpression(
                            "MethodBinding _mb = new com.icesoft.faces.component.ext.taglib.MethodBindingString("
                            + var + ");", true);
                    writer.emitExpression("_component.getAttributes().put(\""
                            + name + "\", _mb);", true);
                    writer.outdent();
                    writer.emitExpression("}", true);
                } else if (actionListener(name)) {
                    
                    writer.emitExpression("if (isValueReference(" + var
                            + ")) {", true);
                    writer.indent();
                    writer.emitExpression(
                            "MethodBinding _mb = getFacesContext().getApplication().createMethodBinding("
                            + var + ", actionListenerArgs);", true);
                    writer.emitExpression("_component.getAttributes().put(\""
                            + name + "\", _mb);", true);
                    writer.outdent();
                    writer.emitExpression("} else {", true);
                    writer.indent();
                    writer.emitExpression("throw new IllegalArgumentException("
                            + var + ");", true);
                    writer.outdent();
                    writer.emitExpression("}", true);
                } else if (validator(name)) {
                    writer.emitExpression("if (isValueReference(" + var
                            + ")) {", true);
                    writer.indent();
                    writer.emitExpression(
                            "MethodBinding _mb = getFacesContext().getApplication().createMethodBinding("
                            + var + ", validatorArgs);", true);
                    writer.emitExpression("_component.getAttributes().put(\""
                            + name + "\", _mb);", true);
                    writer.outdent();
                    writer.emitExpression("} else {", true);
                    writer.indent();
                    writer.emitExpression("throw new IllegalArgumentException("
                            + var + ");", true);
                    writer.outdent();
                    writer.emitExpression("}", true);
                } else if ("valueChangeListener".equals(name)) {
                    writer.emitExpression("if (isValueReference(" + var
                            + ")) {", true);
                    writer.indent();
                    writer
                            .emitExpression(
                            "MethodBinding _mb = getFacesContext().getApplication().createMethodBinding("
                            + var
                            + ", valueChangeListenerArgs);",
                            true);
                    writer.emitExpression("_component.getAttributes().put(\""
                            + name + "\", _mb);", true);
                    writer.outdent();
                    writer.emitExpression("} else {", true);
                    writer.indent();
                    writer.emitExpression("throw new IllegalArgumentException("
                            + var + ");", true);
                    writer.outdent();
                    writer.emitExpression("}", true);
                } else if ("progressListener".equals(name)) {
                    writer.emitExpression("if (isValueReference(" + var
                            + ")) {", true);
                    writer.indent();
                    writer
                            .emitExpression(
                            "Class progressListenerArgs[] = { java.util.EventObject.class };",
                            true);
                    writer.emitExpression(
                            "MethodBinding _mb = getFacesContext().getApplication().createMethodBinding("
                            + var + ", progressListenerArgs);", true);
                    writer.emitExpression("_component.getAttributes().put(\""
                            + name + "\", _mb);", true);
                    writer.outdent();
                    writer.emitExpression("} else {", true);
                    writer.indent();
                    writer.emitExpression("throw new IllegalArgumentException("
                            + var + ");", true);
                    writer.outdent();
                    writer.emitExpression("}", true);
                } else if("listener".equalsIgnoreCase(name)){
//	                Class[] ca = ca = new Class[]{PanelPositionedEvent.class};
//	                MethodBinding mb = getFacesContext().getApplication()
//	                        .createMethodBinding(listener, ca);
//	                series.setListener(mb);
                    writer.emitExpression("if (isValueReference(" + var
                            + ")) {", true);
                    writer.indent();
                    writer
                            .emitExpression(
                            "Class[] panelPositionedArgs = new Class[]{PanelPositionedEvent.class};",
                            true);
                    writer.emitExpression(
                            "MethodBinding _mb = getFacesContext().getApplication().createMethodBinding("
                            + var + ", panelPositionedArgs);", true);
                    writer.emitExpression("_component.getAttributes().put(\""
                            + name + "\", _mb);", true);
                    writer.outdent();
                    writer.emitExpression("} else {", true);
                    writer.indent();
                    writer.emitExpression("throw new IllegalArgumentException("
                            + var + ");", true);
                    writer.outdent();
                    writer.emitExpression("}", true);
                    
                    
                } else if("dragListener".equalsIgnoreCase(name)){
                    System.out.println("Generating Method Binding for drag Listener");
//	                Class[] ca = ca = new Class[]{PanelPositionedEvent.class};
//	                MethodBinding mb = getFacesContext().getApplication()
//	                        .createMethodBinding(listener, ca);
//	                series.setListener(mb);
                    writer.emitExpression("if (isValueReference(" + var
                            + ")) {", true);
                    writer.indent();
                    writer
                            .emitExpression(
                            "Class[] dragListenerArgs = new Class[]{DragEvent.class};",
                            true);
                    writer.emitExpression(
                            "MethodBinding _mb = getFacesContext().getApplication().createMethodBinding("
                            + var + ", dragListenerArgs);", true);
                    writer.emitExpression("_component.getAttributes().put(\""
                            + name + "\", _mb);", true);
                    writer.outdent();
                    writer.emitExpression("} else {", true);
                    writer.indent();
                    writer.emitExpression("throw new IllegalArgumentException("
                            + var + ");", true);
                    writer.outdent();
                    writer.emitExpression("}", true);
                    
                }
                
                else if("dropListener".equalsIgnoreCase(name)){
//	                Class[] ca = ca = new Class[]{PanelPositionedEvent.class};
//	                MethodBinding mb = getFacesContext().getApplication()
//	                        .createMethodBinding(listener, ca);
//	                series.setListener(mb);
                    writer.emitExpression("if (isValueReference(" + var
                            + ")) {", true);
                    writer.indent();
                    writer
                            .emitExpression(
                            "Class[] dropListenerArgs= new Class[]{DropEvent.class};",
                            true);
                    writer.emitExpression(
                            "MethodBinding _mb = getFacesContext().getApplication().createMethodBinding("
                            + var + ", dropListenerArgs );", true);
                    writer.emitExpression("_component.getAttributes().put(\""
                            + name + "\", _mb);", true);
                    writer.outdent();
                    writer.emitExpression("} else {", true);
                    writer.indent();
                    writer.emitExpression("throw new IllegalArgumentException("
                            + var + ");", true);
                    writer.outdent();
                    writer.emitExpression("}", true);
                    
                }
                
                else {
                    
                    throw new IllegalArgumentException(name);
                }
            } else if (primitive(type)) {
                writer.emitExpression("if (isValueReference(" + var + ")) {",
                        true);
                writer.indent();
                writer.emitExpression(
                        "ValueBinding _vb = getFacesContext().getApplication().createValueBinding("
                        + var + ");", true);
                writer.emitExpression("_component.setValueBinding(\"" + name
                        + "\", _vb);", true);
                writer.outdent();
                writer.emitExpression("} else {", true);
                writer.indent();
                if ("char".equals(type) || "java.lang.Character".equals(type))
                    writer.emitExpression("_component.getAttributes().put(\""
                            + name + "\", new Character(" + var
                            + ".charAt(0)));", true);
                else
                    writer.emitExpression("_component.getAttributes().put(\""
                            + name + "\", " + wrappers.get(type) + ".valueOf("
                            + var + "));", true);
                writer.outdent();
                writer.emitExpression("}", true);
            } else {
                writer.emitExpression("if (isValueReference(" + var + ")) {",
                        true);
                writer.indent();
                writer.emitExpression(
                        "ValueBinding _vb = getFacesContext().getApplication().createValueBinding("
                        + var + ");", true);
                writer.emitExpression("_component.setValueBinding(\"" + name
                        + "\", _vb);", true);
                writer.outdent();
                writer.emitExpression("} else {", true);
                writer.indent();
                
                if (name.equalsIgnoreCase("listValue")
                && rb.getRendererType().equalsIgnoreCase(
                        "com.icesoft.faces.SelectInputText")) {
                    writer
                            .emitExpression(
                            "throw new IllegalArgumentException(\"Not a valid value bind expression\");",
                            true);
                } else {
                    writer.emitExpression("_component.getAttributes().put(\""
                            + name + "\", " + var + ");", true);
                }
                writer.outdent();
                writer.emitExpression("}", true);
            }
            writer.outdent();
            writer.emitExpression("}", true);
        }
        
        String baseComponentType = cb.getBaseComponentType();
        if (baseComponentType != null) {
            ComponentBean bcb = getConfig().getComponent(baseComponentType);
            properties(bcb, rb, set);
        }
    }
    
    private void release(ComponentBean cb, RendererBean rb) throws IOException {
        JavaSourceWriter writer = getWriter();
        writer.startJavaDoc();
        writer
                .emitJavaDoc("<p>Release any allocated tag handler attributes.</p>");
        writer.endJavaDoc();
        writer.startMethod("release", "void", null, null);
        writer.emitExpression("super.release();", true);
        release(cb, rb, ((Set) (new HashSet())));
        writer.endMethod();
        writer.emitNewline();
    }
    
    private void release(ComponentBean cb, RendererBean rb, Set set)
    throws IOException {
        JavaSourceWriter writer = getWriter();
        PropertyBean pbs[] = cb.getProperties();
        if (pbs == null)
            pbs = new PropertyBean[0];
        for (int i = 0; i < pbs.length; i++) {
            if ("id".equals(pbs[i].getPropertyName())
            || "rendered".equals(pbs[i].getPropertyName())
            || set.contains(pbs[i].getPropertyName()))
                continue;
            set.add(pbs[i].getPropertyName());
            PropertyBean pb = merge(pbs[i], rb.getAttribute(pbs[i]
                    .getPropertyName()));
            if (pb.isTagAttribute())
                writer.emitExpression(
                        mangle(pb.getPropertyName()) + " = null;", true);
        }
        
        String baseComponentType = cb.getBaseComponentType();
        if (baseComponentType != null) {
            ComponentBean bcb = getConfig().getComponent(baseComponentType);
            release(bcb, rb, set);
        }
    }
    
    private void required(ComponentBean cb) throws IOException {
        String componentType = cb.getComponentType();
        String rendererType = rendererType(cb);
        JavaSourceWriter writer = getWriter();
        writer.startJavaDoc();
        writer.emitJavaDoc("<p>Return the requested component type.</p>");
        writer.endJavaDoc();
        writer.startMethod("getComponentType", "String", null, null);
        writer.emitExpression("return \"" + componentType + "\";", true);
        writer.endMethod();
        writer.emitNewline();
        writer.startJavaDoc();
        writer.emitJavaDoc("<p>Return the requested renderer type.</p>");
        writer.endJavaDoc();
        writer.startMethod("getRendererType", "String", null, null);
        
        // hack to resolve UIColumn no renderer type issue
        if (rendererType.equalsIgnoreCase("null")) {
            writer.emitExpression("return   null;", true);
        } else {
            writer.emitExpression("return \"" + rendererType + "\";", true);
        }
        writer.endMethod();
        writer.emitNewline();
    }
    
    private void signatures(ComponentBean cb) throws IOException {
        JavaSourceWriter writer = getWriter();
        writer.emitExpression(
                "private static Class actionArgs[] = new Class[0];", true);
        writer
                .emitExpression(
                "private static Class actionListenerArgs[] = { ActionEvent.class };",
                true);
        writer
                .emitExpression(
                "private static Class validatorArgs[] = { FacesContext.class, UIComponent.class, Object.class };",
                true);
        writer
                .emitExpression(
                "private static Class valueChangeListenerArgs[] = { ValueChangeEvent.class };",
                true);
        writer.emitNewline();
        appendDoTagMethods();
    }
    
    private void appendDoTagMethods() throws IOException {
        JavaSourceWriter writer = getWriter();
        writer.emitNewline();
        writer.emitExpression("// ", true);
        writer.emitExpression("// Methods From TagSupport", true);
        writer.emitExpression("// ", true);
        doTagMethod("Start");
        doTagMethod("End");
    }
    
    private void doTagMethod(String name) throws IOException {
        JavaSourceWriter writer = getWriter();
        writer.emitNewline();
        writer.emitExpression("public int do" + name
                + "Tag() throws JspException {", true);
        writer.indent();
        writer.emitExpression("int rc = 0;", true);
        writer.emitExpression("try {", true);
        writer.indent();
        writer.emitExpression("rc = super.do" + name + "Tag();", true);
        writer.outdent();
        writer.emitExpression("} catch (JspException e) {", true);
        writer.indent();
        writer.emitExpression("throw e;", true);
        writer.outdent();
        writer.emitExpression("} catch (Throwable t) {", true);
        writer.indent();
        writer.emitExpression("throw new JspException(t);", true);
        writer.outdent();
        writer.emitExpression("}", true);
        writer.emitExpression("return rc;", true);
        writer.outdent();
        writer.emitExpression("}", true);
        writer.emitNewline();
    }
    
    private boolean validator(String name) {
        if (name.equals("validator"))
            return true;
        return name.endsWith("Validator");
    }
    
}
