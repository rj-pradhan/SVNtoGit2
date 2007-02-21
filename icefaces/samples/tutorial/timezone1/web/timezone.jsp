<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
    <f:view>
    <html>
        <head><title>ICEfaces: TimeZone Sample Application</title></head>
        <body>
            <h3>ICEfaces: TimeZone Sample Application</h3>  
            <h:form>
                <h:panelGrid columns="2">
                <h:outputText style="font-weight:600" value="Server Time Zone"/>
                <h:outputText style="font-weight:600" value="Time Zone Selected from Map"/>
                <h:outputText value="#{timeZoneBean.serverTimeZoneName}"/>
                <h:outputText value="#{timeZoneBean.selectedTimeZoneName}"/>
                <h:outputText style="font-weight:800" value="#{timeZoneBean.serverTime}"/>
                <h:outputText style="font-weight:800" value="#{timeZoneBean.selectedTime}"/>
                </h:panelGrid>
                <h:panelGrid columns="6" cellspacing="0" cellpadding="0">
                    <h:commandButton id="GMTminus10" image="images/hawaii.jpg" actionListener="#{timeZoneBean.listen}"/>
                    <h:commandButton id="GMTminus9" image="images/alaska.jpg" actionListener="#{timeZoneBean.listen}"/>
                    <h:commandButton id="GMTminus8" image="images/pacific.jpg" actionListener="#{timeZoneBean.listen}"/>
                    <h:commandButton id="GMTminus7" image="images/mountain.jpg" actionListener="#{timeZoneBean.listen}"/>
                    <h:commandButton id="GMTminus6" image="images/central.jpg" actionListener="#{timeZoneBean.listen}"/>
                    <h:commandButton id="GMTminus5" image="images/eastern.jpg" actionListener="#{timeZoneBean.listen}"/>
                </h:panelGrid>
            </h:form>        
        </body>
    </html>
    </f:view>
