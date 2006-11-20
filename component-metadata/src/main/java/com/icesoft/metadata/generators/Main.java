package com.icesoft.metadata.generators;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.icesoft.jsfmeta.MetadataXmlParser;
import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.ConverterBean;
import com.sun.rave.jsfmeta.beans.FacesConfigBean;
import com.sun.rave.jsfmeta.beans.FacetBean;
import com.sun.rave.jsfmeta.beans.RenderKitBean;
import com.sun.rave.jsfmeta.beans.RendererBean;
import com.sun.rave.jsfmeta.beans.ValidatorBean;

public final class Main {
	
	private static Log log = LogFactory.getLog(Main.class); 
	
	private String baseBI;

	private String categoryDescriptors;

	private FacesConfigBean config;

	private String constantMethodBindingPackage;

	private String defaultMarkupSection;

	private String defaultRenderKitId;

	private String defaultTaglibPrefix;

	private String defaultTaglibURI;

	private String descriptor;

	private File dest;

	private List excludes;

	private String implBD;

	private String implPD;

	private List includes;

	private List listeners;

	protected boolean noBundles;

	private boolean override;

	private MetadataXmlParser parser;

	private String tagClassPackage;

	private List validators;

	private boolean verbose;

	private boolean warning;

	public Main() {
		
		baseBI = "java.beans.SimpleBeanInfo";
		//TODO:
		categoryDescriptors = "com.icesoft.faces.ide.creator2.util.CategoryDescriptors";
		config = new FacesConfigBean();
		constantMethodBindingPackage = null;
		defaultMarkupSection = null;
		defaultRenderKitId = "HTML_BASIC";
		defaultTaglibPrefix = null;
		defaultTaglibURI = null;
		descriptor = "taglib.tld";
		dest = new File(".");
		excludes = new ArrayList();
		implBD = "java.beans.BeanDescriptor";
		implPD = "java.beans.PropertyDescriptor";
		includes = new ArrayList();
		listeners = new ArrayList();
		noBundles = false;
		override = false;
		parser = new MetadataXmlParser();
		tagClassPackage = null;
		validators = new ArrayList();
		verbose = false;
		warning = false;
	}

	public static void main(String args[]) throws Exception {
		
		Main main = new Main();
		main.execute(args);
	}

	private void component(boolean base) throws Exception {
		ComponentGenerator generator = new ComponentGenerator();
		generator.setBase(base);
		generator.setConfig(config);
		generator.setDest(dest);
		generator.setExcludes((String[]) excludes.toArray(new String[excludes
				.size()]));
		generator.setIncludes((String[]) includes.toArray(new String[includes
				.size()]));
		generator.setDefaultRenderKitId(defaultRenderKitId);
		generator.setOverride(override);
		generator.setVerbose(verbose);
		generator.generate();
	}

	private void componentBeanInfo(boolean base) throws Exception {
		ComponentBeanInfoGenerator generator = new ComponentBeanInfoGenerator();
		generator.setBase(base);
		generator.setBaseBI(baseBI);
		generator.setCategoryDescriptors(categoryDescriptors);
		generator.setConfig(config);
		generator.setDefaultMarkupSection(defaultMarkupSection);
		generator.setDefaultRenderKitId(defaultRenderKitId);
		generator.setDefaultTaglibPrefix(defaultTaglibPrefix);
		generator.setDefaultTaglibURI(defaultTaglibURI);
		generator.setDest(dest);
		generator.setExcludes((String[]) excludes.toArray(new String[excludes
				.size()]));
		generator.setImplBD(implBD);
		generator.setImplPD(implPD);
		generator.setIncludes((String[]) includes.toArray(new String[includes
				.size()]));
		generator.setUseComponentResourceBundles(!noBundles);
		generator.setVerbose(verbose);
		generator.generate();
	}

	private void descriptor() throws Exception {
		DescriptorGenerator generator = new DescriptorGenerator();
		generator.setConfig(config);
		generator.setDefaultRenderKitId(defaultRenderKitId);
		generator.setDescriptor(descriptor);
		generator.setDest(dest);
		generator.setExcludes((String[]) excludes.toArray(new String[excludes
				.size()]));
		generator.setIncludes((String[]) includes.toArray(new String[includes
				.size()]));
		generator.setListeners((String[]) listeners
				.toArray(new String[listeners.size()]));
		generator.setPrefix(defaultTaglibPrefix);
		generator.setTagClassPackage(tagClassPackage);
		generator.setURI(defaultTaglibURI);
		generator.setValidators((String[]) validators
				.toArray(new String[validators.size()]));
		generator.setVerbose(verbose);
		generator.generate();
	}

	private void dump() throws Exception {
		ComponentBean cbs[] = config.getComponents();
		for (int i = 0; i < cbs.length; i++) {
			ComponentBean cb = cbs[i];
			System.out.println("Component(componentType="
					+ cb.getComponentType() + ",componentFamily="
					+ cb.getComponentFamily() + ",rendererType="
					+ cb.getRendererType() + ",baseComponentType="
					+ cb.getBaseComponentType() + ")");
			FacetBean fbs[] = cbs[i].getFacets();
			for (int j = 0; j < fbs.length; j++)
				System.out.println("  Facet(facetName=" + fbs[j].getFacetName()
						+ ",displayName=" + fbs[j].getDisplayName("") + ")");

		}

		RenderKitBean rkbs[] = config.getRenderKits();
		for (int i = 0; i < rkbs.length; i++) {
			RenderKitBean rkb = rkbs[i];
			RendererBean rbs[] = rkb.getRenderers();
			for (int j = 0; j < rbs.length; j++) {
				RendererBean rb = rbs[j];
				System.out.println("Renderer(renderKitId="
						+ rkb.getRenderKitId() + ",componentFamily="
						+ rb.getComponentFamily() + ",rendererType="
						+ rb.getRendererType() + ")");
				FacetBean fbs[] = rbs[j].getFacets();
				for (int k = 0; k < fbs.length; k++)
					System.out.println("  Facet(facetName="
							+ fbs[k].getFacetName() + ",displayName="
							+ fbs[k].getDisplayName("") + ")");

			}

		}

	}

	private void exclude() {
		ComponentBean cpb[] = config.getComponents();
		for (int i = 0; i < cpb.length; i++)
			excludes.add(cpb[i].getComponentClass());

		ConverterBean cvb1[] = config.getConvertersByClass();
		for (int i = 0; i < cvb1.length; i++)
			excludes.add(cvb1[i].getConverterClass());

		ConverterBean cvb2[] = config.getConvertersById();
		for (int i = 0; i < cvb2.length; i++)
			excludes.add(cvb2[i].getConverterClass());

		RendererBean rb[] = config.getRenderKit("HTML_BASIC").getRenderers();
		for (int i = 0; i < rb.length; i++)
			excludes.add(rb[i].getRendererClass());

		ValidatorBean vb[] = config.getValidators();
		for (int i = 0; i < vb.length; i++)
			excludes.add(vb[i].getValidatorClass());

	}

	private void execute(String args[]) throws Exception {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.equals("-c")) {
				categoryDescriptors = args[++i];
				continue;
			}
			if (arg.equals("-C")) {
				constantMethodBindingPackage = args[++i];
				continue;
			}
			if (arg.equals("-d")) {
				dest = new File(args[++i]);
				dest.mkdirs();
				continue;
			}
			if (arg.equals("-e")) {
				excludes.add(args[++i]);
				continue;
			}
			if (arg.equals("-f")) {
				String url = args[++i];
				if (warning)
					try {
						parser.parse(new URL(url), config);
					} catch (FileNotFoundException e) {
						System.err.println("WARNING:  Missing resource " + url);
					}
				else
					parser.parse(new URL(url), config);
				continue;
			}
			if (arg.equals("-h")) {
				usage();
				continue;
			}
			if (arg.equals("-i")) {
				includes.add(args[++i]);
				continue;
			}
			if (arg.equals("-L")) {
				listeners.add(args[++i]);
				continue;
			}
			if (arg.equals("-m")) {
				defaultMarkupSection = args[++i];
				continue;
			}
			if (arg.equals("-nobundles")) {
				noBundles = true;
				continue;
			}
			if (arg.equals("-o"))
				continue;
			if (arg.equals("-O")) {
				override = true;
				continue;
			}
			if (arg.equals("-p")) {
				defaultTaglibPrefix = args[++i];
				continue;
			}
			if (arg.equals("-r")) {
				defaultRenderKitId = args[++i];
				continue;
			}
			if (arg.equals("-t")) {
				descriptor = args[++i];
				continue;
			}
			if (arg.equals("-T")) {
				tagClassPackage = args[++i];
				continue;
			}
			if (arg.equals("-u")) {
				defaultTaglibURI = args[++i];
				continue;
			}
			if (arg.equals("-v")) {
				verbose = true;
				continue;
			}
			if (arg.equals("-V")) {
				validators.add(args[++i]);
				continue;
			}
			if (arg.equals("-x")) {
				exclude();
				continue;
			}
			if (arg.equals("-w")) {
				warning = !warning;
				continue;
			}
			if (arg.equals("--baseBI")) {
				baseBI = args[++i];
				continue;
			}
			if (arg.equals("--cpBeanInfo")) {
				componentBeanInfo(false);
				continue;
			}
			if (arg.equals("--cpBeanInfoBase")) {
				componentBeanInfo(true);
				continue;
			}
			if (arg.equals("--cpClass")) {
				component(false);
				continue;
			}
			if (arg.equals("--cpClassBase")) {
				component(true);
				continue;
			}
			if (arg.equals("--dump")) {
				dump();
				continue;
			}
			if (arg.equals("--implBD")) {
				implBD = args[++i];
				continue;
			}
			if (arg.equals("--implPD")) {
				implPD = args[++i];
				continue;
			}
			if (arg.equals("--tlClass")) {
				tagLibrary(false);
				continue;
			}
			if (arg.equals("--tlClassBase")) {
				tagLibrary(true);
				continue;
			}
			if (arg.equals("--tlDescriptor")) {
				descriptor();
			} else {
				usage();
				throw new IllegalArgumentException(arg);
			}
		}

	}

	private void tagLibrary(boolean base) throws Exception {
		TagLibraryGenerator generator = new TagLibraryGenerator();
		generator.setBase(base);
		generator.setConfig(config);
		generator.setConstantMethodBindingPackage(constantMethodBindingPackage);
		generator.setDefaultRenderKitId(defaultRenderKitId);
		generator.setDest(dest);
		generator.setExcludes((String[]) excludes.toArray(new String[excludes
				.size()]));
		generator.setIncludes((String[]) includes.toArray(new String[includes
				.size()]));
		generator.setTagClassPackage(tagClassPackage);
		generator.setVerbose(verbose);
		generator.generate();
	}

	private void usage() {
		//TODO: message bundle
		String info = "TODO";
		log.info(info);
	}

}
