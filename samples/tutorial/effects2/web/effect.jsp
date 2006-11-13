<f:view xmlns:f="http://java.sun.com/jsf/core"
        xmlns:h="http://java.sun.com/jsf/html"
        xmlns:ice="http://www.icesoft.com/icefaces/component">
    <html>
    <head>
    </head>
    <body>
    
<ice:form>
<ice:outputText value="See the Effects Turorial in the Advanced topics section of the Developers guide."/>
  <ice:commandButton value="Invoke" action="#{effectBean.invokeEffect}"/>
  <ice:outputText value="Effect Test" onmouseovereffect="#{effectBean.textEffect}"/>
</ice:form>
    </body>
    </html>
</f:view>