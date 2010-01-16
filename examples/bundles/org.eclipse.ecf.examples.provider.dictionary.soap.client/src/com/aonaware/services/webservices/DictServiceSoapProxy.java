package com.aonaware.services.webservices;

public class DictServiceSoapProxy implements com.aonaware.services.webservices.DictServiceSoap {
  private String _endpoint = null;
  private com.aonaware.services.webservices.DictServiceSoap dictServiceSoap = null;
  
  public DictServiceSoapProxy() {
    _initDictServiceSoapProxy();
  }
  
  public DictServiceSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initDictServiceSoapProxy();
  }
  
  private void _initDictServiceSoapProxy() {
    try {
      dictServiceSoap = (new com.aonaware.services.webservices.DictServiceLocator()).getDictServiceSoap();
      if (dictServiceSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)dictServiceSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)dictServiceSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (dictServiceSoap != null)
      ((javax.xml.rpc.Stub)dictServiceSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.aonaware.services.webservices.DictServiceSoap getDictServiceSoap() {
    if (dictServiceSoap == null)
      _initDictServiceSoapProxy();
    return dictServiceSoap;
  }
  
  public java.lang.String serverInfo() throws java.rmi.RemoteException{
    if (dictServiceSoap == null)
      _initDictServiceSoapProxy();
    return dictServiceSoap.serverInfo();
  }
  
  public com.aonaware.services.webservices.Dictionary[] dictionaryList() throws java.rmi.RemoteException{
    if (dictServiceSoap == null)
      _initDictServiceSoapProxy();
    return dictServiceSoap.dictionaryList();
  }
  
  public com.aonaware.services.webservices.Dictionary[] dictionaryListExtended() throws java.rmi.RemoteException{
    if (dictServiceSoap == null)
      _initDictServiceSoapProxy();
    return dictServiceSoap.dictionaryListExtended();
  }
  
  public java.lang.String dictionaryInfo(java.lang.String dictId) throws java.rmi.RemoteException{
    if (dictServiceSoap == null)
      _initDictServiceSoapProxy();
    return dictServiceSoap.dictionaryInfo(dictId);
  }
  
  public com.aonaware.services.webservices.WordDefinition define(java.lang.String word) throws java.rmi.RemoteException{
    if (dictServiceSoap == null)
      _initDictServiceSoapProxy();
    return dictServiceSoap.define(word);
  }
  
  public com.aonaware.services.webservices.WordDefinition defineInDict(java.lang.String dictId, java.lang.String word) throws java.rmi.RemoteException{
    if (dictServiceSoap == null)
      _initDictServiceSoapProxy();
    return dictServiceSoap.defineInDict(dictId, word);
  }
  
  public com.aonaware.services.webservices.Strategy[] strategyList() throws java.rmi.RemoteException{
    if (dictServiceSoap == null)
      _initDictServiceSoapProxy();
    return dictServiceSoap.strategyList();
  }
  
  public com.aonaware.services.webservices.DictionaryWord[] match(java.lang.String word, java.lang.String strategy) throws java.rmi.RemoteException{
    if (dictServiceSoap == null)
      _initDictServiceSoapProxy();
    return dictServiceSoap.match(word, strategy);
  }
  
  public com.aonaware.services.webservices.DictionaryWord[] matchInDict(java.lang.String dictId, java.lang.String word, java.lang.String strategy) throws java.rmi.RemoteException{
    if (dictServiceSoap == null)
      _initDictServiceSoapProxy();
    return dictServiceSoap.matchInDict(dictId, word, strategy);
  }
  
  
}