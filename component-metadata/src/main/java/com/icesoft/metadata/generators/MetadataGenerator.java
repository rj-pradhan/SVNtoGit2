package com.icesoft.metadata.generators;

import com.icesoft.jsfmeta.util.DebugUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.icesoft.jsfmeta.MetadataXmlParser;
import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.ConverterBean;
import com.sun.rave.jsfmeta.beans.FacesConfigBean;
import com.sun.rave.jsfmeta.beans.FacetBean;
import com.sun.rave.jsfmeta.beans.RenderKitBean;
import com.sun.rave.jsfmeta.beans.RendererBean;
import com.sun.rave.jsfmeta.beans.ValidatorBean;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.SAXException;

public final class MetadataGenerator {
    
    private static Logger logger = Logger.getLogger("com.icesoft.metadata.generators.MetadataGenerator");
    
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
    
    private boolean noBundles;
    
    private boolean override;
    
    private MetadataXmlParser parser;
    
    private String tagClassPackage;
    
    private List validators;
    
    private boolean verbose;
    
    private boolean warning;
    
    private static final String WORKING_FOLDER;
    
    public MetadataGenerator() {
        
        baseBI = "java.beans.SimpleBeanInfo";
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
    
    static{
        String result = ".";
        try {
            ClassLoader classLoader = Thread.currentThread()
            .getContextClassLoader();
            URL localUrl = classLoader.getResource(".");
            if(localUrl != null){
                result = localUrl.getPath();
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        WORKING_FOLDER = result;
    }
    
    public static void main(String args[]) throws Exception {
        
        MetadataGenerator main = new MetadataGenerator();
        main.execute(args);
    }
    
    private void parseXML(String[] urlList){
        for(int i=0; i< urlList.length; i++){
            String url = "file:"+WORKING_FOLDER+urlList[i];
            try {
                parser.parse(new URL(url), config);
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                System.exit(1);
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(1);
            } catch (SAXException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }
    
    //TODO: move to catalog
    private void init(){
        
        String standard_html_renderkit = "jar_xml_dtd/com/sun/faces/standard-html-renderkit.xml";
        String standard_html_renderkit_overlay = "jar_xml_dtd/com/sun/rave/jsfmeta/standard-html-renderkit-overlay.xml";
        String standard_html_renderkit_fixup = "jar_xml_dtd/META-INF/standard-html-renderkit-fixups.xml";
        
        String[] baseUrlList = new String[]{standard_html_renderkit, standard_html_renderkit_overlay, standard_html_renderkit_fixup};
        parseXML(baseUrlList);
        exclude();
                
        String component_faces_config = "../../../component/conf/META-INF/faces-config.xml";
        String extended_faces_config = "conf/extended-faces-config.xml";
        String[] urlList = new String[]{component_faces_config, extended_faces_config};
        parseXML(urlList);
        
    }
    
    
    private void execute(String args[]) throws Exception {
        
        init();
        //TODO: metadata 
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
            if (arg.equals("--cpTestBeanInfoBase")) {
                componentTestBeanInfo(true);
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
                //TODO:
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
    
    
    //TODO: BaseComponentGenerator
    private void component(boolean base) throws Exception {
    }
    
    //TODO: IDEComponentBeanInfoGenerator
    private void componentBeanInfo(boolean base) throws Exception {
    }
    
    private void componentTestBeanInfo(boolean base) throws Exception {
        ComponentTestBeanInfoGenerator generator = new ComponentTestBeanInfoGenerator();
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
        for (int i = 0; i < rb.length; i++){
            excludes.add(rb[i].getRendererClass());
        }
        
        ValidatorBean vb[] = config.getValidators();
        for (int i = 0; i < vb.length; i++){
            excludes.add(vb[i].getValidatorClass());
        }
    }
    
    //TODO:
    private void usage() {
        String info = "TODO";
        logger.info(info);
    }
    
}
