package com.icesoft.icefaces.samples.showcase.layoutPanels.accordionPanel;

import javax.faces.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;

public class AccordionBean {

    private List data = new ArrayList();
    private boolean open = true;
    private boolean fire = false;


    public AccordionBean() {
        boolean b = true;
        for(int i = 0; i < 5;i++){
            data.add(new AccordionItem("Panel #" + i, b));
            b = !b;
        }
    }


    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }


    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String fireNow(){
        fire = true;
        return null;
    }

    public String dontFire(){
        fire=false;
        return null;
    }


    public boolean isFire() {
        return fire;
    }

    public void setFire(boolean fire) {
        this.fire = fire;
    }
}
