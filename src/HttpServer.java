import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe permettant de lancer un serveur HTTP
 */
public class HttpServer {

    public static void main(String[] args) {

        XMLData xmlData = XMLData.empty();

        if (args.length > 0) {
            xmlData = XMLData.of(args[0]);
        }

        int port = xmlData.getPort();

        try {

            // Lancement du serveur Web
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("[WebServer] Listening on port " + port);

            // Le serveur tourne à l'infini
            while (true) {

                // En attente d'une connexion
                Socket socket = serverSocket.accept();
                System.out.println("[WebServer] Fetching connection (" + socket.getRemoteSocketAddress() + ")");

                // Lecture de la requete
                InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                BufferedReader br = new BufferedReader(isr);

                String request = br.readLine();
                String url = request.split(" ")[1];

                System.out.println("[WebServer] Request: " + request);

                try {

                    // On répond à la requete
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                    printWriter.println("HTTP/1.1 200 OK \n");
                    printWriter.flush();

                    boolean accept = AddressChecker.match(socket.getInetAddress(), xmlData.getAccept());
                    boolean deny = AddressChecker.match(socket.getInetAddress(), xmlData.getReject());

                    // on vérifie l'adresse IP
                    if (!accept || deny) {
                        printWriter.println("<html><body><h1>Connection refused</h1><p>Your IP address is rejected</p></body></html>");
                        printWriter.flush();

                    // on vérifie si c'est la racine
                    } else if (url.equalsIgnoreCase("/")) {

                        // s'il y a un index, le mettre par défaut
                        if (xmlData.hasIndex()) {

                            url = "/index.html";

                            FileInputStream fis = new FileInputStream(xmlData.getRoot() + url);
                            byte[] file = fis.readAllBytes();

                            socket.getOutputStream().write(file);
                            fis.close();

                        // sinon, on génère nous-même l'index de la racine
                        } else {

                            String htmlListFiles = "";
                            File directory = new File(xmlData.getRoot());
                            File[] listFiles = directory.listFiles();

                            if (listFiles != null) {
                                htmlListFiles += "<ul>";
                                for (File file : listFiles) {
                                    htmlListFiles += "<li><a href=" + file.getName() + ">" + file.getName() + "</li>";
                                }
                            }

                            printWriter.println("<html><body><h1>Index of " + url + "</h1>" + htmlListFiles + "</body></html>");
                            printWriter.flush();

                        }

                    // si c'est une requete vers un fichier en particulier
                    } else {

                        File file = new File(xmlData.getRoot() + url);

                        // on vérifie si c'est un dossier
                        if (file.isDirectory()) {

                            // faire l'index du dossier
                            String htmlListFiles = "";
                            File[] listFiles = file.listFiles();

                            if (listFiles != null) {
                                htmlListFiles += "<ul>";
                                for (File f : listFiles) {
                                    htmlListFiles += "<li><a href=" + file.getName() + "/" + f.getName() + ">" + f.getName() + "</li>";
                                }
                            }

                            printWriter.println("<html><body><h1>Index of " + url + "</h1>" + htmlListFiles + "</body></html>");
                            printWriter.flush();

                        // sinon on envoie le fichier au client
                        } else {

                            FileInputStream fis = new FileInputStream(file);
                            byte[] fileArray = fis.readAllBytes();

                            socket.getOutputStream().write(fileArray);
                            fis.close();

                        }

                    }

                    // on répond et on ferme la connexion
                    System.out.println("[WebServer] Replied on the request");
                    socket.close();

                } catch (FileNotFoundException e) {
                    System.out.println("[WebServer] Couldn't reply because the file requested wasn't found");
                    socket.close();
                }

                System.out.println("---------------------------------------------------------------------------------");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
