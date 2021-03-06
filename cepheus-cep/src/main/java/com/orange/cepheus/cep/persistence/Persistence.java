/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.cep.persistence;


import com.orange.cepheus.cep.exception.PersistenceException;
import com.orange.cepheus.cep.model.Configuration;

/**
 * Created by pborscia on 30/06/2015.
 */
public interface Persistence {

    /**
     * check configuration file
     * @return true if configuration file exists else false
     */
    Boolean checkConfigurationDirectory();

    /**
     * Load persited configuration to the CEP
     * @throws PersistenceException when the configuration could not be loaded successfully
     * @return configuration
     */
    Configuration loadConfiguration() throws PersistenceException;

    /**
     * Save configuration
     * @param configuration cep configuration
     * @throws PersistenceException when the configuration could not be saved successfully
     */
    void saveConfiguration(Configuration configuration) throws PersistenceException;
}
