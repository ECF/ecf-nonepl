/**
 * WordDefinition.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.aonaware.services.webservices;

public class WordDefinition  implements java.io.Serializable {
    private java.lang.String word;

    private com.aonaware.services.webservices.Definition[] definitions;

    public WordDefinition() {
    }

    public WordDefinition(
           java.lang.String word,
           com.aonaware.services.webservices.Definition[] definitions) {
           this.word = word;
           this.definitions = definitions;
    }


    /**
     * Gets the word value for this WordDefinition.
     * 
     * @return word
     */
    public java.lang.String getWord() {
        return word;
    }


    /**
     * Sets the word value for this WordDefinition.
     * 
     * @param word
     */
    public void setWord(java.lang.String word) {
        this.word = word;
    }


    /**
     * Gets the definitions value for this WordDefinition.
     * 
     * @return definitions
     */
    public com.aonaware.services.webservices.Definition[] getDefinitions() {
        return definitions;
    }


    /**
     * Sets the definitions value for this WordDefinition.
     * 
     * @param definitions
     */
    public void setDefinitions(com.aonaware.services.webservices.Definition[] definitions) {
        this.definitions = definitions;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WordDefinition)) return false;
        WordDefinition other = (WordDefinition) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.word==null && other.getWord()==null) || 
             (this.word!=null &&
              this.word.equals(other.getWord()))) &&
            ((this.definitions==null && other.getDefinitions()==null) || 
             (this.definitions!=null &&
              java.util.Arrays.equals(this.definitions, other.getDefinitions())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getWord() != null) {
            _hashCode += getWord().hashCode();
        }
        if (getDefinitions() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDefinitions());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDefinitions(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(WordDefinition.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.aonaware.com/webservices/", "WordDefinition"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("word");
        elemField.setXmlName(new javax.xml.namespace.QName("http://services.aonaware.com/webservices/", "Word"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("definitions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://services.aonaware.com/webservices/", "Definitions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://services.aonaware.com/webservices/", "Definition"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://services.aonaware.com/webservices/", "Definition"));
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
