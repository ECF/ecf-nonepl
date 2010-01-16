/**
 * DictionaryWord.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.aonaware.services.webservices;

public class DictionaryWord  implements java.io.Serializable {
    private java.lang.String dictionaryId;

    private java.lang.String word;

    public DictionaryWord() {
    }

    public DictionaryWord(
           java.lang.String dictionaryId,
           java.lang.String word) {
           this.dictionaryId = dictionaryId;
           this.word = word;
    }


    /**
     * Gets the dictionaryId value for this DictionaryWord.
     * 
     * @return dictionaryId
     */
    public java.lang.String getDictionaryId() {
        return dictionaryId;
    }


    /**
     * Sets the dictionaryId value for this DictionaryWord.
     * 
     * @param dictionaryId
     */
    public void setDictionaryId(java.lang.String dictionaryId) {
        this.dictionaryId = dictionaryId;
    }


    /**
     * Gets the word value for this DictionaryWord.
     * 
     * @return word
     */
    public java.lang.String getWord() {
        return word;
    }


    /**
     * Sets the word value for this DictionaryWord.
     * 
     * @param word
     */
    public void setWord(java.lang.String word) {
        this.word = word;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DictionaryWord)) return false;
        DictionaryWord other = (DictionaryWord) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.dictionaryId==null && other.getDictionaryId()==null) || 
             (this.dictionaryId!=null &&
              this.dictionaryId.equals(other.getDictionaryId()))) &&
            ((this.word==null && other.getWord()==null) || 
             (this.word!=null &&
              this.word.equals(other.getWord())));
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
        if (getDictionaryId() != null) {
            _hashCode += getDictionaryId().hashCode();
        }
        if (getWord() != null) {
            _hashCode += getWord().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DictionaryWord.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.aonaware.com/webservices/", "DictionaryWord"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dictionaryId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://services.aonaware.com/webservices/", "DictionaryId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("word");
        elemField.setXmlName(new javax.xml.namespace.QName("http://services.aonaware.com/webservices/", "Word"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
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
