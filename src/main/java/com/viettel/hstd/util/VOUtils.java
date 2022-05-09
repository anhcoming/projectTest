package com.viettel.hstd.util;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.HashMap;

public class VOUtils {

    public static HashMap parseXML(String data) throws Exception {
        HashMap<String, String> params = new HashMap<>();
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(data));
        Document doc = db.parse(is);
        doc.getDocumentElement().normalize();

        if (doc.hasChildNodes()) {
            params.putAll(getValue(doc.getChildNodes()));
        }
        return params;
    }

    private static HashMap getValue(NodeList nodeList) throws Exception {
        HashMap<String, String> params = new HashMap<>();
        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            // make sure it's element node
            params.put(tempNode.getNodeName(), tempNode.getTextContent());
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                // get node name and value
                if (tempNode.hasAttributes()) {
                    // get attributes names and values
                    NamedNodeMap nodeMap = tempNode.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node node = nodeMap.item(i);
                        params.put(node.getNodeName(), node.getNodeValue());
                    }
                }
                if (tempNode.hasChildNodes()) {
                    // loop again if has child nodes
                    params.putAll(getValue(tempNode.getChildNodes()));
                }
            }
        }
        return params;
    }

}
