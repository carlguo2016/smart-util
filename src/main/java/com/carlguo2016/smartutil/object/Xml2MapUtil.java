package com.carlguo2016.smartutil.object;

import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 * xml转map，map转xml 带属性
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class Xml2MapUtil {
    public static void main(String[] args) throws DocumentException, IOException {
       String payXML =
                "<?xml version=\"1.0\" encoding=\"GBK\" standalone=\"yes\"?>"+
                        "<PackageList xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"+
                        "  <Package>                                                         "+
                        "    <Header>                                                        "+
                        "    <Version>2</Version>                                            "+
                        "      <RequestType>230</RequestType>                                "+
                        "      <SessionId>LX1c76e57d1ce949eebd3b2a025075348a</SessionId>     "+
                        "      <SendTime>2016-11-03 11:43:42</SendTime>                      "+
                        "      <SellerId>GW0000021</SellerId>                                "+
                        "      <Status>100</Status>                                          "+
                        "      <ErrorMessage>承保通过</ErrorMessage>                         "+
                        "    </Header>                                                       "+
                        "    <Response>                                                      "+
                        "		<Order>                                                          "+
                        "			<TBOrderId>161103113125691629113</TBOrderId>                   "+
                        "			<Premium>335038</Premium>                                      "+
                        "			<PayNo></PayNo>                                                "+
                        "			<SubOrderList>                                                 "+
                        "				<SubOrder type=\"biz\">                                      "+
                        "					<TBOrderId>161103113125691629113</TBOrderId>               "+
                        "					<ItemId>36983314279216374</ItemId>                         "+
                        "					<Premium>219538</Premium>                                  "+
                        "					<ProposalNo>TDDC201633019129002450</ProposalNo>            "+
                        "					<PolicyNo>PDDC201633019129002000</PolicyNo>                "+
                        "				</SubOrder>                                                  "+
                        "				<SubOrder type=\"force\">                                    "+
                        "					<TBOrderId>161103113125691629113</TBOrderId>               "+
                        "					<ItemId>36983314279230926</ItemId>                         "+
                        "					<Premium>115500</Premium>                                  "+
                        "					<ProposalNo>TDDC201633019129002450</ProposalNo>            "+
                        "					<PolicyNo>PDDC201633019129002000</PolicyNo>                "+
                        "				 </SubOrder>                                                 "+
                        "			</SubOrderList>                                                "+
                        "		</Order>                                                         "+
                        "	</Response>                                                        "+
                        "  </Package>                                                        "+
                        "</PackageList>                                                      ";

        Map<String,Object> map = xml2mapWithAttr(payXML,false);
        System.out.println(map);
        Map<String,Object> header = (Map<String, Object>) map.get("Package");
        System.out.println(header.get("Response"));
    }

    /**
     * xml转map 不带属性
     * @param xmlStr
     * @param needRootKey 是否需要在返回的map里加根节点键
     * @return
     * @throws DocumentException
     */
    public static Map xml2map(String xmlStr, boolean needRootKey) throws DocumentException {
        Document doc = DocumentHelper.parseText(xmlStr);
        Element root = doc.getRootElement();
        Map<String, Object> map = (Map<String, Object>) xml2map(root);
        if(root.elements().size()==0 && root.attributes().size()==0){
            return map;
        }
        if(needRootKey){
            //在返回的map里加根节点键（如果需要）
            Map<String, Object> rootMap = new HashMap<String, Object>();
            rootMap.put(root.getName(), map);
            return rootMap;
        }
        return map;
    }

    /**
     * xml转map 带属性
     * @param xmlStr
     * @param needRootKey 是否需要在返回的map里加根节点键
     * @return
     * @throws DocumentException
     */
    public static Map xml2mapWithAttr(String xmlStr, boolean needRootKey) throws DocumentException {
        Document doc = DocumentHelper.parseText(xmlStr);
        Element root = doc.getRootElement();
        Map<String, Object> map = (Map<String, Object>) xml2mapWithAttr(root);
        if(root.elements().size()==0 && root.attributes().size()==0){
            return map; //根节点只有一个文本内容
        }
        if(needRootKey){
            //在返回的map里加根节点键（如果需要）
            Map<String, Object> rootMap = new HashMap<String, Object>();
            rootMap.put(root.getName(), map);
            return rootMap;
        }
        return map;
    }

    /**
     * xml转map 不带属性
     * @param e
     * @return
     */
    private static Map xml2map(Element e) {
        Map map = new LinkedHashMap();
        List list = e.elements();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Element iter = (Element) list.get(i);
                List mapList = new ArrayList();

                if (iter.elements().size() > 0) {
                    Map m = xml2map(iter);
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(m);
                        }
                        if (obj instanceof List) {
                            mapList = (List) obj;
                            mapList.add(m);
                        }
                        map.put(iter.getName(), mapList);
                    } else
                        map.put(iter.getName(), m);
                } else {
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(iter.getText());
                        }
                        if (obj instanceof List) {
                            mapList = (List) obj;
                            mapList.add(iter.getText());
                        }
                        map.put(iter.getName(), mapList);
                    } else
                        map.put(iter.getName(), iter.getText());
                }
            }
        } else
            map.put(e.getName(), e.getText());
        return map;
    }

    /**
     * xml转map 带属性
     * @param element
     * @return
     */
    private static Map xml2mapWithAttr(Element element) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();

        List<Element> list = element.elements();
        List<Attribute> listAttr0 = element.attributes(); // 当前节点的所有属性的list
        for (Attribute attr : listAttr0) {
            map.put("@" + attr.getName(), attr.getValue());
        }
        if (list.size() > 0) {

            for (int i = 0; i < list.size(); i++) {
                Element iter = list.get(i);
                List mapList = new ArrayList();

                if (iter.elements().size() > 0) {
                    Map m = xml2mapWithAttr(iter);
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(m);
                        }
                        if (obj instanceof List) {
                            mapList = (List) obj;
                            mapList.add(m);
                        }
                        map.put(iter.getName(), mapList);
                    } else
                        map.put(iter.getName(), m);
                } else {

                    List<Attribute> listAttr = iter.attributes(); // 当前节点的所有属性的list
                    Map<String, Object> attrMap = null;
                    boolean hasAttributes = false;
                    if (listAttr.size() > 0) {
                        hasAttributes = true;
                        attrMap = new LinkedHashMap<String, Object>();
                        for (Attribute attr : listAttr) {
                            attrMap.put("@" + attr.getName(), attr.getValue());
                        }
                    }

                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            // mapList.add(iter.getText());
                            if (hasAttributes) {
                                attrMap.put("#text", iter.getText());
                                mapList.add(attrMap);
                            } else {
                                mapList.add(iter.getText());
                            }
                        }
                        if (obj instanceof List) {
                            mapList = (List) obj;
                            // mapList.add(iter.getText());
                            if (hasAttributes) {
                                attrMap.put("#text", iter.getText());
                                mapList.add(attrMap);
                            } else {
                                mapList.add(iter.getText());
                            }
                        }
                        map.put(iter.getName(), mapList);
                    } else {
                        // map.put(iter.getName(), iter.getText());
                        if (hasAttributes) {
                            attrMap.put("#text", iter.getText());
                            map.put(iter.getName(), attrMap);
                        } else {
                            map.put(iter.getName(), iter.getText());
                        }
                    }
                }
            }
        } else {
            // 根节点的
            if (listAttr0.size() > 0) {
                map.put("#text", element.getText());
            } else {
                map.put(element.getName(), element.getText());
            }
        }
        return map;
    }

    /**
     * map转xml map中没有根节点的键
     * @param map
     * @param rootName
     * @throws DocumentException
     * @throws IOException
     */
    public static Document map2xml(Map<String, Object> map, String rootName) throws DocumentException, IOException  {
        Document doc = DocumentHelper.createDocument();
        Element root = DocumentHelper.createElement(rootName);
        doc.add(root);
        map2xml(map, root);
        //System.out.println(doc.asXML());
        //System.out.println(formatXml(doc));
        return doc;
    }

    /**
     * map转xml map中含有根节点的键
     * @param map
     * @throws DocumentException
     * @throws IOException
     */
    public static Document map2xml(Map<String, Object> map) throws DocumentException, IOException  {
        Iterator<Map.Entry<String, Object>> entries = map.entrySet().iterator();
        if(entries.hasNext()){ //获取第一个键创建根节点
            Map.Entry<String, Object> entry = entries.next();
            Document doc = DocumentHelper.createDocument();
            Element root = DocumentHelper.createElement(entry.getKey());
            doc.add(root);
            map2xml((Map)entry.getValue(), root);
            //System.out.println(doc.asXML());
            //System.out.println(formatXml(doc));
            return doc;
        }
        return null;
    }

    /**
     * map转xml
     * @param map
     * @param body xml元素
     * @return
     */
    private static Element map2xml(Map<String, Object> map, Element body) {
        Iterator<Map.Entry<String, Object>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = entries.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if(key.startsWith("@")){	//属性
                body.addAttribute(key.substring(1, key.length()), value.toString());
            } else if(key.equals("#text")){	//有属性时的文本
                body.setText(value.toString());
            } else {
                if(value instanceof List ){
                    List list = (List)value;
                    Object obj;
                    for(int i=0; i<list.size(); i++){
                        obj = list.get(i);
                        //list里是map或String，不会存在list里直接是list的，
                        if(obj instanceof Map){
                            Element subElement = body.addElement(key);
                            map2xml((Map)list.get(i), subElement);
                        } else {
                            body.addElement(key).setText((String)list.get(i));
                        }
                    }
                } else if(value instanceof Map ){
                    Element subElement = body.addElement(key);
                    map2xml((Map)value, subElement);
                } else {
                    body.addElement(key).setText(value.toString());
                }
            }
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        return body;
    }

    /**
     * 格式化输出xml
     * @param xmlStr
     * @return
     * @throws DocumentException
     * @throws IOException
     */
    public static String formatXml(String xmlStr) throws DocumentException, IOException  {
        Document document = DocumentHelper.parseText(xmlStr);
        return formatXml(document);
    }

    /**
     * 格式化输出xml
     * @param document
     * @return
     * @throws DocumentException
     * @throws IOException
     */
    public static String formatXml(Document document) throws DocumentException, IOException  {
        // 格式化输出格式
        OutputFormat format = OutputFormat.createPrettyPrint();
        //format.setEncoding("UTF-8");
        StringWriter writer = new StringWriter();
        // 格式化输出流
        XMLWriter xmlWriter = new XMLWriter(writer, format);
        // 将document写入到输出流
        xmlWriter.write(document);
        xmlWriter.close();
        return writer.toString();
    }

}