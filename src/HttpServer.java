import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    public static void main(String[] args) {

        int port = 80;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

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
                if(url.equalsIgnoreCase("/")){
                    url = "/index.html";
                }

                try {
                    FileInputStream fis = new FileInputStream(url.substring(1));
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

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
