/**
 * DictServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.aonaware.services.webservices;

public class DictServiceLocator extends org.apache.axis.client.Service implements com.aonaware.services.webservices.DictService {

/**
 * Word Dictionary Web Service
 */

    public DictServiceLocator() {
    }


    public DictServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public DictServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for DictServiceSoap12
    private java.lang.String DictServiceSoap12_address = "http://services.aonaware.com/DictService/DictService.asmx";

    public java.lang.String getDictServiceSoap12Address() {
        return DictServiceSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String DictServiceSoap12WSDDServiceName = "DictServiceSoap12";

    public java.lang.String getDictServiceSoap12WSDDServiceName() {
        return DictServiceSoap12WSDDServiceName;
    }

    public void setDictServiceSoap12WSDDServiceName(java.lang.String name) {
        DictServiceSoap12WSDDServiceName = name;
    }

    public com.aonaware.services.webservices.DictServiceSoap getDictServiceSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(DictServiceSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getDictServiceSoap12(endpoint);
    }

    public com.aonaware.services.webservices.DictServiceSoap getDictServiceSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.aonaware.services.webservices.DictServiceSoap12Stub _stub = new com.aonaware.services.webservices.DictServiceSoap12Stub(portAddress, this);
            _stub.setPortName(getDictServiceSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setDictServiceSoap12EndpointAddress(java.lang.String address) {
        DictServiceSoap12_address = address;
    }


    // Use to get a proxy class for DictServiceSoap
    private java.lang.String DictServiceSoap_address = "http://services.aonaware.com/DictService/DictService.asmx";

    public java.lang.String getDictServiceSoapAddress() {
        return DictServiceSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String DictServiceSoapWSDDServiceName = "DictServiceSoap";

    public java.lang.String getDictServiceSoapWSDDServiceName() {
        return DictServiceSoapWSDDServiceName;
    }

    public void setDictServiceSoapWSDDServiceName(java.lang.String name) {
        DictServiceSoapWSDDServiceName = name;
    }

    public com.aonaware.services.webservices.DictServiceSoap getDictServiceSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(DictServiceSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getDictServiceSoap(endpoint);
    }

    public com.aonaware.services.webservices.DictServiceSoap getDictServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.aonaware.services.webservices.DictServiceSoapStub _stub = new com.aonaware.services.webservices.DictServiceSoapStub(portAddress, this);
            _stub.setPortName(getDictServiceSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setDictServiceSoapEndpointAddress(java.lang.String address) {
        DictServiceSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     * This service has multiple ports for a given interface;
     * the proxy implementation returned may be indeterminate.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.aonaware.services.webservices.DictServiceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                com.aonaware.services.webservices.DictServiceSoap12Stub _stub = new com.aonaware.services.webservices.DictServiceSoap12Stub(new java.net.URL(DictServiceSoap12_address), this);
                _stub.setPortName(getDictServiceSoap12WSDDServiceName());
                return _stub;
            }
            if (com.aonaware.services.webservices.DictServiceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                com.aonaware.services.webservices.DictServiceSoapStub _stub = new com.aonaware.services.webservices.DictServiceSoapStub(new java.net.URL(DictServiceSoap_address), this);
                _stub.setPortName(getDictServiceSoapWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("DictServiceSoap12".equals(inputPortName)) {
            return getDictServiceSoap12();
        }
        else if ("DictServiceSoap".equals(inputPortName)) {
            return getDictServiceSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://services.aonaware.com/webservices/", "DictService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://services.aonaware.com/webservices/", "DictServiceSoap12"));
            ports.add(new javax.xml.namespace.QName("http://services.aonaware.com/webservices/", "DictServiceSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("DictServiceSoap12".equals(portName)) {
            setDictServiceSoap12EndpointAddress(address);
        }
        else 
if ("DictServiceSoap".equals(portName)) {
            setDictServiceSoapEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
