package com.bbedu.test;

import com.bbedu.bbspringmvc.context.BBWebApplicationContext;
import com.bbedu.bbspringmvc.xml.XMLParser;
import org.junit.Test;

public class BBSpringMVCTest {

    @Test
    public void readXML() {
        String basePackage = XMLParser.getBasePackage("bbspringmvc.xml");
        System.out.println("basePackage = " + basePackage);
    }

    public static void main(String[] args) {
        
    }
}
