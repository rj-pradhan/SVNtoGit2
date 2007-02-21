<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output
		doctype-public="-//Sun Microsystems, Inc.//DTD JavaServer Faces Config 1.1//EN"
		doctype-system="dtd/web-facesconfig-overlay_1_1.dtd" />
		
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>