package com.icesoft.metadata.generators;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.faces.render.RenderKitFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.rave.jsfmeta.beans.AttributeBean;
import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.DescriptionBean;
import com.sun.rave.jsfmeta.beans.DisplayNameBean;
import com.sun.rave.jsfmeta.beans.FacesConfigBean;
import com.sun.rave.jsfmeta.beans.FacetBean;
import com.sun.rave.jsfmeta.beans.IconBean;
import com.sun.rave.jsfmeta.beans.NamedValueBean;
import com.sun.rave.jsfmeta.beans.PropertyBean;
import com.sun.rave.jsfmeta.beans.RenderKitBean;
import com.sun.rave.jsfmeta.beans.RendererBean;

public abstract class AbstractGenerator {


	private static final ResourceBundle bundle;

	protected static Map defaults;

	protected static Set keywords;

	protected static Map unwrappers;

	protected static Map wrappers;

	private String defaultRenderKitId;

	private String excludes[];

	private String includes[];

	private FacesConfigBean config;

	private File dest;

	private boolean verbose;

	private JavaSourceWriter writer;

	protected static Log log = LogFactory.getLog(AbstractGenerator.class);
	
	static {
		
		ClassLoader classLoader = Thread.currentThread()
		.getContextClassLoader();
		bundle = ResourceBundle.getBundle("com.sun.rave.jsfmeta.Bundle", Locale
				.getDefault(),classLoader);
		defaults = new HashMap();
		defaults.put("boolean", "false");
		defaults.put("byte", "Byte.MIN_VALUE");
		defaults.put("char", "Character.MIN_VALUE");
		defaults.put("double", "Double.MIN_VALUE");
		defaults.put("float", "Float.MIN_VALUE");
		defaults.put("int", "Integer.MIN_VALUE");
		defaults.put("long", "Long.MIN_VALUE");
		defaults.put("short", "Short.MIN_VALUE");
		keywords = new HashSet();
		keywords.add("abstract");
		keywords.add("boolean");
		keywords.add("break");
		keywords.add("byte");
		keywords.add("case");
		keywords.add("cast");
		keywords.add("catch");
		keywords.add("char");
		keywords.add("class");
		keywords.add("const");
		keywords.add("continue");
		keywords.add("default");
		keywords.add("do");
		keywords.add("double");
		keywords.add("else");
		keywords.add("extends");
		keywords.add("final");
		keywords.add("finally");
		keywords.add("float");
		keywords.add("for");
		keywords.add("future");
		keywords.add("generic");
		keywords.add("goto");
		keywords.add("if");
		keywords.add("implements");
		keywords.add("import");
		keywords.add("inner");
		keywords.add("instanceof");
		keywords.add("int");
		keywords.add("interface");
		keywords.add("long");
		keywords.add("native");
		keywords.add("new");
		keywords.add("null");
		keywords.add("operator");
		keywords.add("outer");
		keywords.add("package");
		keywords.add("private");
		keywords.add("protected");
		keywords.add("public");
		keywords.add("rest");
		keywords.add("return");
		keywords.add("short");
		keywords.add("static");
		keywords.add("super");
		keywords.add("switch");
		keywords.add("synchronized");
		keywords.add("this");
		keywords.add("throw");
		keywords.add("throws");
		keywords.add("transient");
		keywords.add("try");
		keywords.add("var");
		keywords.add("void");
		keywords.add("volatile");
		keywords.add("while");
		unwrappers = new HashMap();
		unwrappers.put("boolean", "booleanValue");
		unwrappers.put("byte", "byteValue");
		unwrappers.put("char", "charValue");
		unwrappers.put("double", "doubleValue");
		unwrappers.put("float", "floatValue");
		unwrappers.put("int", "intValue");
		unwrappers.put("long", "longValue");
		unwrappers.put("short", "shortValue");
		wrappers = new HashMap();
		wrappers.put("boolean", "Boolean");
		wrappers.put("byte", "Byte");
		wrappers.put("char", "Character");
		wrappers.put("double", "Double");
		wrappers.put("float", "Float");
		wrappers.put("int", "Integer");
		wrappers.put("long", "Long");
		wrappers.put("short", "Short");
	}
	
	
	public AbstractGenerator() {

		defaultRenderKitId = RenderKitFactory.HTML_BASIC_RENDER_KIT;
		excludes = new String[0];
		includes = new String[0];
		config = null;
		dest = new File(".");
		verbose = false;
		writer = new JavaSourceWriter();
	}

	
	public String getDefaultRenderKitId() {
		return defaultRenderKitId;
	}

	public void setDefaultRenderKitId(String defaultRenderKitId) {
		this.defaultRenderKitId = defaultRenderKitId;
	}

	public String[] getExcludes() {
		return excludes;
	}

	public void setExcludes(String excludes[]) {
		this.excludes = excludes;
	}

	public String[] getIncludes() {
		return includes;
	}

	public void setIncludes(String includes[]) {
		this.includes = includes;
	}

	public FacesConfigBean getConfig() {
		return config;
	}

	public void setConfig(FacesConfigBean config) {
		this.config = config;
	}

	public File getDest() {
		return dest;
	}

	public void setDest(File dest) {
		this.dest = dest;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public JavaSourceWriter getWriter() {
		return writer;
	}

	public void setWriter(JavaSourceWriter writer) {
		this.writer = writer;
	}

	protected ComponentBean baseComponent(ComponentBean cb) {
		String baseComponentType = cb.getBaseComponentType();
		if (baseComponentType == null) {
			return null;
		}
		ComponentBean bcb = getConfig().getComponent(baseComponentType);
		if (bcb == null) {
			
			log.debug(" invalid base component");
			
			throw new IllegalArgumentException(" invalid base component");
		} else {
			return bcb;
		}
	}

	protected String capitalize(String name) {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	public String componentFamily(ComponentBean cb) {
		ComponentBean bcb = null;
		String componentFamily = cb.getComponentFamily();
		if (componentFamily == null) {
			bcb = baseComponent(cb);
			do {
				if ((componentFamily != null) || (bcb == null)) {
					break;
				}
				componentFamily = bcb.getComponentFamily();
				if (componentFamily == null) {
					bcb = baseComponent(bcb);
				}
			} while (true);
		}
		return componentFamily;
	}

	protected FacetBean[] facets(ComponentBean cb, RendererBean rb) {
		List components = new ArrayList();
		components.add(cb);
		ComponentBean current = cb;
		do {
			ComponentBean bcb = baseComponent(current);
			if (bcb == null) {
				break;
			}
			components.add(bcb);
			current = bcb;
		} while (true);
		List facets = new ArrayList();
		FacetBean f[] = null;
		for (int i = components.size() - 1; i >= 0; i--) {
			current = (ComponentBean) components.get(i);
			f = current.getFacets();
			for (int j = 0; j < f.length; j++) {
				FacetBean facet = lookupFacet(facets, f[j].getFacetName());
				if (facet == null) {
					facets.add(f[j].clone());
				} else {
					mergeFacet(facet, f[j]);
				}
			}

		}

		f = rb.getFacets();
		for (int j = 0; j < f.length; j++) {
			FacetBean facet = lookupFacet(facets, f[j].getFacetName());
			if (facet == null) {
				facets.add(f[j].clone());
			} else {
				mergeFacet(facet, f[j]);
			}
		}

		return (FacetBean[]) facets.toArray(new FacetBean[facets.size()]);
	}

	protected boolean generated(String fqcn) {
		// System.out.println("&&&&generated ="+ fqcn);
		for (int i = 0; i < excludes.length; i++) {
			if (fqcn.equals(excludes[i])) {
				return false;
			}
		}

		for (int i = 0; i < includes.length; i++) {
			if (fqcn.equals(includes[i])) {
				return true;
			}
		}

		return includes.length == 0;
	}

	protected String mangle(String name) {
		if (keywords.contains(name)) {
			return "_" + name;
		} else {
			return name;
		}
	}

	protected PropertyBean merge(PropertyBean pb, AttributeBean ab) {
		if (ab == null) {
			return pb;
		}
		PropertyBean newPb = new PropertyBean();
		pb.copy(newPb);
		if (!ab.isBindable()) {
			newPb.setBindable(false);
		}
		if (ab.isExpert()) {
			newPb.setExpert(true);
		}
		if (ab.isHidden()) {
			newPb.setHidden(true);
		}
		if (ab.isRequired()) {
			newPb.setRequired(true);
		}
		if (ab.isSuppressed()) {
			newPb.setSuppressed(true);
		}
		if (!ab.isTagAttribute()) {
			newPb.setTagAttribute(false);
		}
		return newPb;
	}


	protected File outputFile(String fqcn) {
		return new File(getDest(), fqcn.replace('.', File.separatorChar)
				+ ".java");
	}

	protected boolean primitive(String type) {
		return defaults.containsKey(type);
	}

	protected RendererBean renderer(ComponentBean cb) {
		String rendererType = rendererType(cb);
		if (rendererType == null) {
			return null;
		}
		String componentFamily = componentFamily(cb);
		if (componentFamily == null) {
			
			log.debug("NoComponentFamily");
			throw new IllegalArgumentException("NoComponentFamily");
		}

		RenderKitBean renderKitBean = getConfig().getRenderKit(
				getDefaultRenderKitId());
		// System.out.println("renderKit="+ renderKitBean.getRenderKitId());

		RendererBean rb = renderKitBean.getRenderer(componentFamily,
				rendererType);

		if (rb == null) {
			System.out.println("### RenderBean componentFamily="
					+ componentFamily + " rendererType=" + rendererType);
		}
		// System.out.println("renderbean
		// className="+rb.getClass().getName()+"### render bean instance Name"+
		// rb.getInstanceName());
		if (rb == null) {
			
			log.debug("InvalidRendererType");			
			throw new IllegalArgumentException("InvalidRendererType");
		} else {
			return rb;
		}
	}

	public String rendererType(ComponentBean cb) {
		ComponentBean bcb = null;
		String rendererType = cb.getRendererType();
		if (rendererType == null) {
			bcb = baseComponent(cb);
			do {
				if ((rendererType != null) || (bcb == null)) {
					break;
				}
				rendererType = bcb.getRendererType();
				if (rendererType == null) {
					bcb = baseComponent(bcb);
				}
			} while (true);
		}
		return rendererType;
	}

	protected String simpleClassName(String fqcn) {
		int last = fqcn.lastIndexOf('.');
		if (last >= 0) {
			return fqcn.substring(last + 1);
		} else {
			return fqcn;
		}
	}

	private FacetBean lookupFacet(List list, String name) {
		for (Iterator facets = list.iterator(); facets.hasNext();) {
			FacetBean facet = (FacetBean) facets.next();
			if (name.equals(facet.getFacetName())) {
				return facet;
			}
		}

		return null;
	}

	private void mergeFacet(FacetBean orig, FacetBean upd) {
		DescriptionBean dbs[] = upd.getDescriptions();
		for (int i = 0; i < dbs.length; i++) {
			orig.addDescription(new DescriptionBean(dbs[i].getDescription(),
					dbs[i].getLang()));
		}

		DisplayNameBean dns[] = upd.getDisplayNames();
		for (int i = 0; i < dns.length; i++) {
			orig.addDisplayName(new DisplayNameBean(dns[i].getDisplayName(),
					dns[i].getLang()));
		}

		IconBean ibs[] = upd.getIcons();
		for (int i = 0; i < ibs.length; i++) {
			orig.addIcon(new IconBean(ibs[i].getLang(), ibs[i].getLargeIcon(),
					ibs[i].getSmallIcon()));
		}

		Map map = upd.getNamedValues();
		NamedValueBean nvb;
		for (Iterator keys = map.keySet().iterator(); keys.hasNext(); orig
				.addNamedValue(new NamedValueBean(nvb.getName(), nvb
						.getExpression(), nvb.getValue()))) {
			String key = (String) keys.next();
			nvb = (NamedValueBean) map.get(key);
		}

	}

}
