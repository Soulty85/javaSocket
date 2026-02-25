package javaSocket;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
	private ServerSocket serverSocket;
	
    
	public Server(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}
	
	public void startServer() {
		try {
			// boucle infini pour que le sever puisse accepter plusieurs clients
			while(!serverSocket.isClosed()) {
				Socket socket = serverSocket.accept();
				System.out.println("Nouveau client connecte");
				
				// creation d'un objet pour le nouveau client connecte
				ClientManager clientManager = new ClientManager(socket);
				// creation d'un thread pour le client 
				Thread thread = new Thread(clientManager);
				
				thread.start();
			}
		} catch (IOException e) {
			
		}
	}
	
	public void closeServerSocket() {
		try {
			if (serverSocket != null) {
				serverSocket.close();
				System.out.println("Fermeture du serveur");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(2026);
		Server server = new Server(serverSocket);
		server.startServer();
	}
}
