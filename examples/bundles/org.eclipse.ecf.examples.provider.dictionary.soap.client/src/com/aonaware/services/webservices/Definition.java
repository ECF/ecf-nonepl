/**
 * Definition.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.aonaware.services.webservices;

public class Definition  implements java.io.Serializable {
    private java.lang.String word;

    private com.aonaware.services.webservices.Dictionary dictionary;

    private java.lang.String wordDefinition;

    public Definition() {
    }

    public Definition(
           java.lang.String word,
           com.aonaware.services.webservices.Dictionary dictionary,
           java.lang.String wordDefinition) {
           this.word = word;
           this.dictionary = dictionary;
           this.wordDefinition = wordDefinition;
    }


    /**
     * Gets the word value for this Definition.
     * 
     * @return word
     */
    public java.lang.String getWord() {
        return word;
    }


    /**
     * Sets the word value for this Definition.
     * 
     * @param word
     */
    public void setWord(java.lang.String word) {
        this.word = word;
    }


    /**
     * Gets the dictionary value for this Definition.
     * 
     * @return dictionary
     */
    public com.aonaware.services.webservices.Dictionary getDictionary() {
        return dictionary;
    }


    /**
     * Sets the dictionary value for this Definition.
     * 
     * @param dictionary
     */
    public void setDictionary(com.aonaware.services.webservices.Dictionary dictionary) {
        this.dictionary = dictionary;
    }


    /**
     * Gets the wordDefinition value for this Definition.
     * 
     * @return wordDefinition
     */
    public java.lang.String getWordDefinition() {
        return wordDefinition;
    }


    /**
     * Sets the wordDefinition value for this Definition.
     * 
     * @param wordDefinition
     */
    public void setWordDefinition(java.lang.String wordDefinition) {
        this.wordDefinition = wordDefinition;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Definition)) return false;
        Definition other = (Definition) obj;
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
            ((this.dictionary==null && other.getDictionary()==null) || 
             (this.dictionary!=null &&
              this.dictionary.equals(other.getDictionary()))) &&
            ((this.wordDefinition==null && other.getWordDefinition()==null) || 
             (this.wordDefinition!=null &&
              this.wordDefinition.equals(other.getWordDefinition())));
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
        if (getDictionary() != null) {
            _hashCode += getDictionary().hashCode();
        }
        if (getWordDefinition() != null) {
            _hashCode += getWordDefinition().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Definition.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.aonaware.com/webservices/", "Definition"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("word");
        elemField.setXmlName(new javax.xml.namespace.QName("http://services.aonaware.com/webservices/", "Word"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dictionary");
        elemField.setXmlName(new javax.xml.namespace.QName("http://services.aonaware.com/webservices/", "Dictionary"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://services.aonaware.com/webservices/", "Dictionary"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("wordDefinition");
        elemField.setXmlName(new javax.xml.namespace.QName("http://services.aonaware.com/webservices/", "WordDefinition"));
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
