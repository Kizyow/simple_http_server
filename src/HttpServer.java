import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    static class HttpData {

        private int port;
        private String root;
        private boolean index;
        private String accept;
        private String reject;

        public HttpData(int port, String root, boolean index, String accept, String reject) {
            this.port = port;
            this.root = root;
            this.index = index;
            this.accept = accept;
            this.reject = reject;
        }

        public int getPort() {
            return port;
        }

        public String getRoot() {
            return root;
        }

        public boolean hasIndex() {
            return index;
        }

        public String getAccept() {
            return accept;
        }

        public String getReject() {
            return reject;
        }
    }

    public static HttpData readFile(String urlFile) {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File file = new File(urlFile);
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

                    System.out.println("Current Element :" + node.getNodeName());
                    System.out.println("port : " + port);
                    System.out.println("root : " + root);
                    System.out.println("index : " + index);
                    System.out.println("accept : " + accept);
                    System.out.println("reject : " + reject);

                    return new HttpData(port, root, index, accept, reject);


                }

            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        return new HttpData(80, "resources", false, "", "");

    }

    public static void main(String[] args) {

        HttpData httpData = readFile("resources/config.xml");

        int port = httpData.getPort();

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("[SERVEUR] Serveur en ecoute sur le port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("[SERVEUR] Connexion etablie (" + socket.getInetAddress() + ":" + socket.getPort() + ")");

                InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                BufferedReader br = new BufferedReader(isr);

                String request = br.readLine();
                System.out.println(request);

                String url = request.split(" ")[1];

                if (!httpData.hasIndex()) {

                    try {

                        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                        printWriter.println("HTTP/1.1 200 OK \n");
                        printWriter.flush();
                        printWriter.println("<html><body><h1>pas d'index</h1></body></html>");
                        printWriter.flush();
                        System.out.println("[SERVEUR] Renvoi de la requete");
                        socket.close();
                    } catch (FileNotFoundException e) {
                        System.out.println("[SERVEUR] Requete non aboutie");
                        socket.close();
                    }

                } else {

                    if (url.equalsIgnoreCase("/")) {
                        url = "/index.html";
                    }

                    try {
                        FileInputStream fis = new FileInputStream(httpData.getRoot() + url);
                        byte[] file = fis.readAllBytes();

                        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                        printWriter.println("HTTP/1.1 200 OK \n");
                        printWriter.flush();
                        socket.getOutputStream().write(file);
                        System.out.println("[SERVEUR] Renvoi de la requete");
                        socket.close();
                    } catch (FileNotFoundException e) {
                        System.out.println("[SERVEUR] Requete non aboutie");
                        socket.close();
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
