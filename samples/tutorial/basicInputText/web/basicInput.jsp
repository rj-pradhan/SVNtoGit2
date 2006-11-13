<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<html>

<head>
   <title>D2D Tutorial - BasicInputText</title>
   <link rel="stylesheet" type="text/css" href="icesoft_styles1.css">
</head>
<body>
<f:view>

    <h:form>
    
        <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr align="left" >
        <td>
        <h2>D2D Tutorial - Basic InputText Renderer</h2>
        </td>
        </tr>
        
        <tr align="left" >
            <td><ol>
            <li>Enter some text in the input textbox below.</li>
            <li>Use the 'Enter key' to submit the text.</li>
            <li>The text will be extracted, capitalized and displayed to the right of the input text box.</li>
            <li>The input textbox is cleared.</li>
            </ol></td>
        </tr>
        <tr align="left" >
            <td>
            <h:inputText
            name="basic"
            type="text"
            id="basic"
            required="false"
            value="#{tutorial.input}"
            />
            <h:outputText value="#{tutorial.output}"/>
            </td>
        </tr>   
        
        </table>
    </h:form>                                                
    
</f:view>

</body>
</html>
    