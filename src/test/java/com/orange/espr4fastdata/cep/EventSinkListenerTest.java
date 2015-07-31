/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.cep;

import com.espertech.esper.client.*;
import com.espertech.esper.client.EventType;
import com.orange.espr4fastdata.Application;
import com.orange.espr4fastdata.model.Attribute;
import com.orange.espr4fastdata.model.Broker;
import com.orange.espr4fastdata.model.EventTypeOut;
import com.orange.espr4fastdata.model.Configuration;
import com.orange.ngsi.client.NgsiClient;
import com.orange.ngsi.model.ContextAttribute;
import com.orange.ngsi.model.ContextElement;
import com.orange.ngsi.model.UpdateAction;
import com.orange.ngsi.model.UpdateContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.*;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests for the EventSinkListener
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class EventSinkListenerTest {

    @Mock
    public NgsiClient ngsiClient;

    @Mock
    public EPStatement statement;

    @Mock
    public EPServiceProvider provider;

    @Autowired
    @InjectMocks
    public EventSinkListener eventSinkListener;

    private Broker broker = new Broker("http://orion", false);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Configuration setup
        Configuration configuration = new Configuration();
        EventTypeOut eventTypeOut = new EventTypeOut("OUT1", "TempSensorAvg", false);
        eventTypeOut.addBroker(broker);
        eventTypeOut.addAttribute(new Attribute("avgTemp", "double"));
        configuration.setEventTypeOuts(Collections.singletonList(eventTypeOut));

        eventSinkListener.setConfiguration(configuration);
    }


    /**
     * Check that an updateContext is fired when a new event bean arrives
     */
    @Test
    public void postMessageOnEventUpdate() {

        when(statement.getText()).thenReturn("statement");

        // Trigger event update
        List<ContextAttribute> attributes = new LinkedList<>();
        attributes.add(new ContextAttribute("id", "string", "OUT1234"));
        attributes.add(new ContextAttribute("avgTemp", "double", 10.25));
        EventBean[]beans = {buildEventBean("TempSensorAvg", attributes)};
        eventSinkListener.update(beans, null, statement, provider);

        // Capture updateContext when postUpdateContextRequest is called on updateContextRequest,
        ArgumentCaptor<UpdateContext> updateContextArg = ArgumentCaptor.forClass(UpdateContext.class);
        verify(ngsiClient).updateContext(eq(broker.getUrl()), updateContextArg.capture(), any(), any());

        // Check updateContext is valid
        UpdateContext updateContext = updateContextArg.getValue();
        assertEquals(UpdateAction.UPDATE, updateContext.getUpdateAction());
        assertEquals(1, updateContext.getContextElements().size());

        ContextElement contextElement = updateContext.getContextElements().get(0);
        assertEquals("OUT1234", contextElement.getEntityId().getId());
        assertEquals("TempSensorAvg", contextElement.getEntityId().getType());
        assertFalse(contextElement.getEntityId().getIsPattern());
        assertEquals(1, contextElement.getContextAttributeList().size());

        ContextAttribute attr = contextElement.getContextAttributeList().get(0);
        assertEquals("avgTemp", attr.getName());
        assertEquals("double", attr.getType());
        assertEquals(10.25, attr.getValue());
    }

    /**
     * Check that when no id is set in configuration, the one used in the configuration is used
     */
    @Test
    public void fallbackOnConfigurationId() {

        when(statement.getText()).thenReturn("statement");

        // Trigger event update
        List<ContextAttribute> attributes = new LinkedList<>();
        attributes.add(new ContextAttribute("id", "string", null)); // null => when statement does not indicate id
        attributes.add(new ContextAttribute("avgTemp", "double", 10.25));
        EventBean[]beans = {buildEventBean("TempSensorAvg", attributes)};
        eventSinkListener.update(beans, null, statement, provider);

        // Capture updateContext when postUpdateContextRequest is called on updateContextRequest,
        ArgumentCaptor<UpdateContext> updateContextArg = ArgumentCaptor.forClass(UpdateContext.class);
        verify(ngsiClient).updateContext(eq(broker.getUrl()), updateContextArg.capture(), any(), any());

        // Check id correspond to the one set in configuration
        ContextElement contextElement = updateContextArg.getValue().getContextElements().get(0);
        assertEquals("OUT1", contextElement.getEntityId().getId());
    }

    /**
     * Check that when no update is triggered when some removed event beans fire the update
     */
    @Test
    public void noUpdateOnRemovedEventBeans() {

        when(statement.getText()).thenReturn("statement");

        // Trigger remove event update
        List<ContextAttribute> attributes = new LinkedList<>();
        attributes.add(new ContextAttribute("id", "string", null)); // null => when statement does not indicate id
        attributes.add(new ContextAttribute("avgTemp", "double", 10.25));
        EventBean[]beans = {buildEventBean("TempSensorAvg", attributes)};
        eventSinkListener.update(null, beans, statement, provider);

        verify(ngsiClient, never()).updateContext(any(), any(), any(), any());
    }

    /**
     * Check that when no update is triggered when no attributes are updated by the event
     */
    @Test
    public void noUpdateWhenNoAttributes() {

        when(statement.getText()).thenReturn("statement");

        // Trigger remove event update
        List<ContextAttribute> attributes = new LinkedList<>();
        attributes.add(new ContextAttribute("id", "string", "OUT1"));
        EventBean[]beans = {buildEventBean("TempSensorAvg", attributes)};
        eventSinkListener.update(beans, null, statement, provider);

        verify(ngsiClient, never()).updateContext(any(), any(), any(), any());
    }

    /**
     * Helper to generate a Esper Event Bean for a given type and attributes
     */
    private EventBean buildEventBean(String typeName, List<ContextAttribute> attributes) {
        return new EventBean() {
            @Override public EventType getEventType() {
                EventType eventType = new EventType() {
                    @Override public Class getPropertyType(String s) {
                        return null;
                    }

                    @Override public boolean isProperty(String s) {
                        return false;
                    }

                    @Override public EventPropertyGetter getGetter(String s) {
                        return null;
                    }

                    @Override public FragmentEventType getFragmentType(String s) {
                        return null;
                    }

                    @Override public Class getUnderlyingType() {
                        return null;
                    }

                    @Override public String[] getPropertyNames() {
                        return new String[0];
                    }

                    @Override public EventPropertyDescriptor[] getPropertyDescriptors() {
                        return new EventPropertyDescriptor[0];
                    }

                    @Override public EventPropertyDescriptor getPropertyDescriptor(String s) {
                        return null;
                    }

                    @Override public EventType[] getSuperTypes() {
                        return new EventType[0];
                    }

                    @Override public Iterator<EventType> getDeepSuperTypes() {
                        return null;
                    }

                    @Override public String getName() {
                        return typeName;
                    }

                    @Override public EventPropertyGetterMapped getGetterMapped(String s) {
                        return null;
                    }

                    @Override public EventPropertyGetterIndexed getGetterIndexed(String s) {
                        return null;
                    }

                    @Override public int getEventTypeId() {
                        return 0;
                    }

                    @Override public String getStartTimestampPropertyName() {
                        return null;
                    }

                    @Override public String getEndTimestampPropertyName() {
                        return null;
                    }
                };
                return eventType;
            }

            @Override public Object get(String s) throws PropertyAccessException {
                for (ContextAttribute attribute : attributes) {
                    if (s.equals(attribute.getName())) {
                        return attribute.getValue();
                    }
                }
                return null;
            }

            @Override public Object getUnderlying() {
                return null;
            }

            @Override public Object getFragment(String s) throws PropertyAccessException {
                return null;
            }
        };
    }
}
