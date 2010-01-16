/**
 * DictServiceSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.aonaware.services.webservices;

public interface DictServiceSoap extends java.rmi.Remote {

    /**
     * Show remote server information
     */
    public java.lang.String serverInfo() throws java.rmi.RemoteException;

    /**
     * Returns a list of available dictionaries
     */
    public com.aonaware.services.webservices.Dictionary[] dictionaryList() throws java.rmi.RemoteException;

    /**
     * Returns a list of advanced dictionaries (e.g. translating dictionaries)
     */
    public com.aonaware.services.webservices.Dictionary[] dictionaryListExtended() throws java.rmi.RemoteException;

    /**
     * Show information about the specified dictionary
     */
    public java.lang.String dictionaryInfo(java.lang.String dictId) throws java.rmi.RemoteException;

    /**
     * Define given word, returning definitions from all dictionaries
     */
    public com.aonaware.services.webservices.WordDefinition define(java.lang.String word) throws java.rmi.RemoteException;

    /**
     * Define given word, returning definitions from specified dictionary
     */
    public com.aonaware.services.webservices.WordDefinition defineInDict(java.lang.String dictId, java.lang.String word) throws java.rmi.RemoteException;

    /**
     * Return list of all available strategies on the server
     */
    public com.aonaware.services.webservices.Strategy[] strategyList() throws java.rmi.RemoteException;

    /**
     * Look for matching words in all dictionaries using the given
     * strategy
     */
    public com.aonaware.services.webservices.DictionaryWord[] match(java.lang.String word, java.lang.String strategy) throws java.rmi.RemoteException;

    /**
     * Look for matching words in the specified dictionary using the
     * given strategy
     */
    public com.aonaware.services.webservices.DictionaryWord[] matchInDict(java.lang.String dictId, java.lang.String word, java.lang.String strategy) throws java.rmi.RemoteException;
}
