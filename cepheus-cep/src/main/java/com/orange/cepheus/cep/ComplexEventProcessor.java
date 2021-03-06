/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.cep;

import com.orange.cepheus.cep.exception.ConfigurationException;
import com.orange.cepheus.cep.exception.EventProcessingException;
import com.orange.cepheus.cep.model.Event;
import com.orange.cepheus.cep.model.Configuration;

/**
 * Represents a Complex Event Processor (CEP)
 */
public interface ComplexEventProcessor {

    /**
     * Apply a new configuration to the CEP
     * @param configuration the new configuration to apply
     * @throws ConfigurationException when the configuration could not be applied successfully
     */
    void setConfiguration(Configuration configuration) throws ConfigurationException;

    /**
     * @return the active configuration or null
     */
    Configuration getConfiguration();

    /**
     * Supply an event to the CEP
     * @param event
     * @throws EventProcessingException when the event could not be processed successfully
     */
    void processEvent(Event event) throws EventProcessingException;

}
