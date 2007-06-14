package com.icesoft.faces.component.accordion;

import com.icesoft.faces.context.BridgeFacesContext;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Map;

public class PanelAccordionState implements Serializable {


    public static PanelAccordionState getState(FacesContext context, UIComponent component) {

        String id = component.getClientId(context);
        String viewNumber = ((BridgeFacesContext) context).getViewNumber();

        String key = id + viewNumber;
        Map map = context.getExternalContext().getSessionMap();
        PanelAccordionState state = (PanelAccordionState) map.get(key);
        if (state == null) {
            state = new PanelAccordionState();
            map.put(key, state);
        }
        return state;
    }


    private boolean changedViaDecode;
    private boolean lastExpandedValue;
    private boolean firstTime = true;


    public boolean isChangedViaDecode() {
        return changedViaDecode;
    }

    public void setChangedViaDecode(boolean changedViaDecode) {
        this.changedViaDecode = changedViaDecode;
    }

    public boolean isLastExpandedValue() {
        return lastExpandedValue;
    }

    public void setLastExpandedValue(boolean lastExpandedValue) {
        this.lastExpandedValue = lastExpandedValue;
    }

    public boolean isFirstTime() {
        return firstTime;
    }

    public void setFirstTime(boolean firstTime) {
        this.firstTime = firstTime;
    }
}
