/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.model.cep;

/**
 * A broker is a NGSI Context Manager that can handle updates of ContentElements.
 *
 * If the mustRegister property is FALSE, the broker will be notified with /updateContext requests on each event.
 * If the mustRegister property is TRUE, the broker will receive an initial /registerContext with the CEP as providing application
 * (the broker will then be able to call /queryContext)
 */
public class Broker {

    /**
     * Url to the broker
     */
    private String url;

    /**
     * Fiware specific service name (optional)
     */
    private String serviceName;

    /**
     * Fiware specific service path (optional)
     */
    private String servicePath;

    /**
     * Whether or not to mustRegister to the broker. Default: false.
     */
    private boolean mustRegister;

    public Broker() {
    }

    public Broker(String url, boolean mustRegister) {
        this.mustRegister = mustRegister;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServicePath() {
        return servicePath;
    }

    public void setServicePath(String servicePath) {
        this.servicePath = servicePath;
    }

    public boolean getMustRegister() {
        return mustRegister;
    }

    public void setMustRegister(boolean mustRegister) {
        this.mustRegister = mustRegister;
    }
}
