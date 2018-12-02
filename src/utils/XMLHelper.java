package utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class XMLHelper {
    private String          data;
    private DocumentBuilder documentBuilder;
    private int             count;
    private Measurement[]   dataArray;

    public XMLHelper() {
        this.count                  = 0;
        DocumentBuilderFactory dbf  = DocumentBuilderFactory.newInstance();

        try {
            this.documentBuilder = dbf.newDocumentBuilder();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setData(String data) {
        this.data       = data;
        this.count      = 0;
        this.dataArray  = null;
        System.out.println(data);

        this.parseData();
    }

    private void parseData() {
        Document document = null;
        try {
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(this.data));
            document = this.documentBuilder.parse(is);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (document != null) {
            NodeList nodeList   = document.getElementsByTagName("MEASUREMENT");
            this.count          = nodeList.getLength();
            this.dataArray      = new Measurement[this.count];

            for (int i=0;i<this.count;i++) {
                Node node = nodeList.item(i); // Contains measurement data.
                this.dataArray[i] = new Measurement();

                if (node.hasChildNodes()) {
                    NodeList children = node.getChildNodes();

                    for (int c=0;c<children.getLength();c++) {
                        Node child      = children.item(c);
                        String nodeName = child.getNodeName();
                        String nodeVal  = child.getTextContent();

                        switch (nodeName) {
                            case "STN":
                                this.dataArray[i].setStation(Integer.parseInt(nodeVal));
                                break;

                            case "DATE":
                                this.dataArray[i].setDate(nodeVal);
                                break;

                            case "TIME":
                                this.dataArray[i].setTime(nodeVal);
                                break;

                            case "TEMP":
                                this.dataArray[i].setTemp(Float.parseFloat(nodeVal));
                                break;

                            case "DEWP":
                                this.dataArray[i].setDewp(Float.parseFloat(nodeVal));
                                break;

                                // @TODO finish me.
                        }
                    }
                }
            }
        }
    }
}
