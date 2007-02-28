package com.icesoft.faces.utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * This class has been designed, so the custom components can get 
 * facesMessages either from the icefaces' ResourceBundle or an 
 * application's resourceBundle. The location of ice's messages.properties
 * is under com.icesoft.faces.resources package. 
 */

public class MessageUtils {
    private static String DETAIL_SUFFIX = "_detail";
    private static int SUMMARY = 0;
    private static int DETAIL = 1;
    private static String ICE_MESSAGES_BUNDLE = "com.icesoft.faces.resources.messages";
    public static FacesMessage getMessage(FacesContext context, 
            String messageId) {
        return getMessage(context, messageId, null);
    }
    
    public static FacesMessage getMessage(FacesContext facesContext, 
            String messageId, Object params[]) {
        String messageInfo[] = new String[2];
        
        Locale locale = facesContext.getViewRoot().getLocale();
        String bundleName = facesContext.getApplication().getMessageBundle();
        //see if the message has been overridden by the application
        if (bundleName != null) {
            loadMessageInfo(bundleName, locale, messageId, messageInfo);
        }
        //if not overridden then check in Icefaces message bundle.
        if (messageInfo[SUMMARY] == null && messageInfo[DETAIL]== null) {
            loadMessageInfo(ICE_MESSAGES_BUNDLE, locale, messageId, messageInfo);
        }
        if (params != null) {
            MessageFormat format;
            for (int i= 0; i <messageInfo.length; i++) {
                if (messageInfo[i] != null) {
                    format = new MessageFormat(messageInfo[i], locale);
                    messageInfo[i] = format.format(params);
                }
            }
        }
        return new FacesMessage(messageInfo[SUMMARY], messageInfo[DETAIL]);
    }
    
    private static void loadMessageInfo(String bundleName, 
                                Locale locale,
                                String messageId,  
                                String[] messageInfo) {
        ResourceBundle bundle = ResourceBundle.
                    getBundle(bundleName, locale, getClassLoader(bundleName));
        try {
            messageInfo[SUMMARY] = bundle.getString(messageId);
            messageInfo[DETAIL] = bundle.getString(messageId + DETAIL_SUFFIX);
        } catch (MissingResourceException e) {         
        }
    }
    
   
    private static ClassLoader getClassLoader(Object fallback) {
        ClassLoader classLoader = Thread.currentThread()
                                    .getContextClassLoader();
        if (classLoader == null) {
            classLoader = fallback.getClass().getClassLoader();
        }
        return classLoader;
    }
}
