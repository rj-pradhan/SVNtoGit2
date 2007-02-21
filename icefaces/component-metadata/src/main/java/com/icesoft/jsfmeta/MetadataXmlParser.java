package com.icesoft.jsfmeta;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.digester.Digester;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.rave.jsfmeta.beans.FacesConfigBean;
import com.sun.rave.jsfmeta.rules.FacesConfigRuleSet;



public class MetadataXmlParser {

	static final String FACES_CONFIG_1_0_ID = "-//Sun Microsystems, Inc.//DTD JavaServer Faces Config 1.0//EN";

	static final String FACES_CONFIG_1_0_DTD = "com/sun/faces/web-facesconfig_1_0.dtd";

	static final String FACES_CONFIG_1_1_ID = "-//Sun Microsystems, Inc.//DTD JavaServer Faces Config 1.1//EN";

	static final String FACES_CONFIG_1_1_DTD = "com/sun/faces/web-facesconfig_1_1.dtd";

	static final String FACES_OVERLAY_1_0_ID = "-//Sun Microsystems, Inc.//DTD JavaServer Faces Overlay 1.0//EN";

	static final String FACES_OVERLAY_1_0_DTD = "com/sun/rave/jsfmeta/web-facesconfig-overlay_1_0.dtd";

	static final String FACES_OVERLAY_1_1_ID = "-//Sun Microsystems, Inc.//DTD JavaServer Faces Overlay 1.1//EN";

	static final String FACES_OVERLAY_1_1_DTD = "com/sun/rave/jsfmeta/web-facesconfig-overlay_1_1.dtd";

	static final String FACES_OVERLAY_1_2_ID = "-//Sun Microsystems, Inc.//DTD JavaServer Faces Overlay 1.2//EN";

	static final String FACES_OVERLAY_1_2_DTD = "com/sun/rave/jsfmeta/web-facesconfig-overlay_1_2.dtd";

	private boolean configured;

	private Digester digester;

	private boolean design;

	private boolean generate;

	private boolean runtime;

	public MetadataXmlParser() {
		configured = false;
		digester = new Digester();
		design = true;
		generate = true;
		runtime = false;
		digester.setNamespaceAware(false);
		digester.setUseContextClassLoader(true);
		digester.setValidating(false);
		URL url = null;
		url = getClass().getClassLoader().getResource(
				"com/sun/faces/resources/web-facesconfig_1_0.dtd");
		if (url != null)
			register(
					"-//Sun Microsystems, Inc.//DTD JavaServer Faces Config 1.0//EN",
					url);
		url = getClass().getClassLoader().getResource(
				"com/sun/faces/resources/web-facesconfig_1_1.dtd");
		if (url != null)
			register(
					"-//Sun Microsystems, Inc.//DTD JavaServer Faces Config 1.1//EN",
					url);
		url = getClass().getClassLoader().getResource(
				"com/sun/rave/jsfmeta/resources/web-facesconfig-overlay_1_0.dtd");
		if (url != null)
			register(
					"-//Sun Microsystems, Inc.//DTD JavaServer Faces Overlay 1.0//EN",
					url);
		url = getClass().getClassLoader().getResource(
				"com/sun/rave/jsfmeta/resources/web-facesconfig-overlay_1_1.dtd");
		if (url != null)
			register(
					"-//Sun Microsystems, Inc.//DTD JavaServer Faces Overlay 1.1//EN",
					url);
		url = getClass().getClassLoader().getResource(
				"com/sun/rave/jsfmeta/resources/web-facesconfig-overlay_1_2.dtd");
		if (url != null)
			register(
					"-//Sun Microsystems, Inc.//DTD JavaServer Faces Overlay 1.2//EN",
					url);
	}

	public boolean isDesign() {
		return design;
	}

	public void setDesign(boolean design) {
		this.design = design;
	}

	public boolean isGenerate() {
		return generate;
	}

	public void setGenerate(boolean generate) {
		this.generate = generate;
	}

	public boolean isRuntime() {
		return runtime;
	}

	public void setRuntime(boolean runtime) {
		this.runtime = runtime;
	}

	public FacesConfigBean parse(URL url) throws IOException, SAXException {
		return parse(url, new FacesConfigBean());
	}
	
	public FacesConfigBean parse(File url) throws IOException, SAXException {		
		return parse(url.toURL(), new FacesConfigBean());
	}

	public FacesConfigBean parse(URL url, FacesConfigBean metadata)
			throws IOException, SAXException {
		configure();
		digester.clear();
		digester.push(metadata);
		InputSource source = null;
		InputStream stream = null;
		FacesConfigBean fcb = null;
		try {
			stream = new BufferedInputStream(url.openStream());
			source = new InputSource(url.toString());
			source.setByteStream(stream);
			fcb = (FacesConfigBean) digester.parse(source);
		} finally {
			if (stream != null)
				try {
					stream.close();
				} catch (IOException e) {
				}
			stream = null;
		}
		return fcb;
	}

	public void register(String publicId, URL entityURL) {
		digester.register(publicId, entityURL.toString());
	}

	private void configure() {
		if (!configured) {
			digester.addRuleSet(new FacesConfigRuleSet(design, generate,
					runtime));
			configured = true;
		}
	}

}
