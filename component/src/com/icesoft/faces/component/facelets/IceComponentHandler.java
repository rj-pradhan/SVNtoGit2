package com.icesoft.faces.component.facelets;

import com.sun.facelets.tag.jsf.ComponentHandler;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.MethodRule;
import com.icesoft.faces.component.dragdrop.DragEvent;
import com.icesoft.faces.component.dragdrop.DropEvent;
import com.icesoft.faces.component.ext.RowSelectorEvent;
import com.icesoft.faces.component.panelpositioned.PanelPositionedEvent;
import com.icesoft.faces.component.paneltabset.TabChangeEvent;
import com.icesoft.faces.component.outputchart.OutputChart;

import java.util.EventObject;

/**
 * @author Mark Collette
 * @since 1.6
 */
public class IceComponentHandler extends ComponentHandler {
    public IceComponentHandler(ComponentConfig componentConfig) {
        super(componentConfig);
    }
    
    protected MetaRuleset createMetaRuleset(Class type) {
        MetaRuleset m = super.createMetaRuleset(type);
        if( tag.getNamespace() != null &&
            tag.getNamespace().equals("http://www.icesoft.com/icefaces/component") )
        {
            if( tag.getLocalName().equals("inputFile") ) {
                m.addRule( new MethodRule("progressListener", null, new Class[] {EventObject.class}) );
            }
            else if( tag.getLocalName().equals("outputChart") ) {
                m.addRule( new MethodRule("renderOnSubmit", Boolean.TYPE, new Class[] {OutputChart.class}) );
            }
            else if( tag.getLocalName().equals("panelGroup") ) {
                m.addRule( new MethodRule("dragListener", null, new Class[] {DragEvent.class}) );
                m.addRule( new MethodRule("dropListener", null, new Class[] {DropEvent.class}) );
            }
            else if( tag.getLocalName().equals("panelPositioned") ) {
                m.addRule( new MethodRule("listener", null, new Class[] {PanelPositionedEvent.class}) );
            }
            else if( tag.getLocalName().equals("panelTabSet") ) {
                m.addRule( new MethodRule("tabChangeListener", null, new Class[] {TabChangeEvent.class}) );
            }
            else if( tag.getLocalName().equals("rowSelector") ) {
                m.addRule( new MethodRule("selectionListener", null, new Class[] {RowSelectorEvent.class}) );
                m.addRule( new MethodRule("selectionAction", null, new Class[0]) );
            }
        }
        return m;
    }
}
