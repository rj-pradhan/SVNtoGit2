package com.icesoft.icefaces.samples.showcase.layoutPanels.accordionPanel;

import javax.faces.event.ActionEvent;

public class AccordionItem {

    private boolean open;
    private String label;


    public AccordionItem(String value, boolean open) {
        this.label = value;
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getLabel() {
        return label + " " + (open?"Open":"Closed");
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void actionListener(ActionEvent e){
        System.err.println("Row [" + label + "] is [" + (open?"Open":"Closed") + "]");
    }
}

