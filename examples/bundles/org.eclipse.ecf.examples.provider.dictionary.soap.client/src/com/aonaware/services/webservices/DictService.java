/**
 * DictService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.aonaware.services.webservices;

public interface DictService extends javax.xml.rpc.Service {

/**
 * Word Dictionary Web Service
 */
    public java.lang.String getDictServiceSoap12Address();

    public com.aonaware.services.webservices.DictServiceSoap getDictServiceSoap12() throws javax.xml.rpc.ServiceException;

    public com.aonaware.services.webservices.DictServiceSoap getDictServiceSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getDictServiceSoapAddress();

    public com.aonaware.services.webservices.DictServiceSoap getDictServiceSoap() throws javax.xml.rpc.ServiceException;

    public com.aonaware.services.webservices.DictServiceSoap getDictServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
