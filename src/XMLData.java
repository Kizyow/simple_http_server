import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * Classe XMLData permettant de récuperer des données d'un fichier XML
 * et de les mettre dans cette classe
 */
public class XMLData {

    /**
     * Attributs présents dans le fichier .xml
     */
    private final int port;
    private final String root;
    private final boolean index;
    private final String accept;
    private final String reject;

    /**
     * Constructeur
     *
     * @param port   Le port du serveur Web
     * @param root   Le repértoire par défaut
     * @param index  Si le repértoire a un fichier index.html
     * @param accept L'adresse IP autorisée
     * @param reject L'adresse IP refusée
     */
    public XMLData(int port, String root, boolean index, String accept, String reject) {
        this.port = port;
        this.root = root;
        this.index = index;
        this.accept = accept;
        this.reject = reject;
    }

    /**
     * Récupérer le port du serveur Web
     *
     * @return port
     */
    public int getPort() {
        return port;
    }

    /**
     * Récuperer le repértoire par défaut
     *
     * @return URL du repértoire
     */
    public String getRoot() {
        return root;
    }

    /**
     * Savoir si le serveur Web a un index par défaut
     *
     * @return true si le serveur en a un, faux sinon
     */
    public boolean hasIndex() {
        return index;
    }

    /**
     * Connaître l'adresse IP autorisé sur le serveur
     *
     * @return L'adresse IP
     */
    public String getAccept() {
        return accept;
    }

    /**
     * Connaître l'adresse IP refusée sur le serveur
     *
     * @return L'adresse IP
     */
    public String getReject() {
        return reject;
    }

    /**
     * Récupérer les données à partir d'un fichier .xml
     *
     * @param filePath L'URL du fichier contenant les données
     * @return XMLData
     */
    public static XMLData of(String filePath) {

        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File file = new File(filePath);
            Document doc = db.parse(file);

            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("webconf");

            for (int temp = 0; temp < nodeList.getLength(); temp++) {

                Node node = nodeList.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;

                    int port = Integer.parseInt(element.getElementsByTagName("port").item(0).getTextContent());
                    String root = element.getElementsByTagName("root").item(0).getTextContent();
                    boolean index = Boolean.parseBoolean(element.getElementsByTagName("index").item(0).getTextContent());
                    String accept = element.getElementsByTagName("accept").item(0).getTextContent();
                    String reject = element.getElementsByTagName("reject").item(0).getTextContent();

                    return new XMLData(port, root, index, accept, reject);

                }

            }

        } catch (NullPointerException | ParserConfigurationException | IOException | SAXException e) {
            System.err.println("Erreur de la lecture du fichier, valeurs par défaut utilisés à la place");
            System.err.println("Soit le fichier est mal écrit, soit il est corrompu ou pas d'accès de lecture");
        }

        // si la lecture s'est mal passée, on renvoie un XMLDate avec des données par défaut
        return XMLData.empty();

    }

    /**
     * Permet d'avoir les données par défaut
     * @return XMLData
     */
    public static XMLData empty() {
        return new XMLData(80, "", false, "0.0.0.0/0", "255.255.255.255/32");
    }

}
