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

        this.parseData();
    }

    private void parseData() {
        Document document = null;
        try {
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(this.data));
            document = this.documentBuilder.parse(is);
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Detected false XML data.");
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

                        if (nodeVal.equals("")) {
                            continue;
                        }

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

                            case "STP":
                                this.dataArray[i].setStp(Float.parseFloat(nodeVal));
                                break;

                            case "SLP":
                                this.dataArray[i].setSlp(Float.parseFloat(nodeVal));
                                break;

                            case "VISIB":
                                this.dataArray[i].setVisib(Float.parseFloat(nodeVal));
                                break;

                            case "WDSP":
                                this.dataArray[i].setWdsp(Float.parseFloat(nodeVal));
                                break;

                            case "PRCP":
                                this.dataArray[i].setPrcp(Float.parseFloat(nodeVal));
                                break;

                            case "SNDP":
                                this.dataArray[i].setSndp(Float.parseFloat(nodeVal));
                                break;

                            case "FRSHTT":
                                this.dataArray[i].parseFrshtt(nodeVal);
                                break;

                            case "CLDC":
                                this.dataArray[i].setCldc(Float.parseFloat(nodeVal));
                                break;

                            case "WNDDIR":
                                this.dataArray[i].setWinddir(Integer.parseInt(nodeVal));
                                break;
                        }
                    }
                }
            }
        }
    }

    public Measurement[] getDataArray() {
        return dataArray;
    }
}
