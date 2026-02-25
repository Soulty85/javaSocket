package javaSocket;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;


public class ClientManager implements Runnable{
	public static ArrayList<ClientManager> clientManagers = new ArrayList<>();
	// socket venant du server permettant d'etablir la connection entre le client et le server
	private Socket socket;
	// permet de lire les donnees venant du client
	private BufferedReader bufferedReader;
	// permet d'ecrire aux autres clients 
	private BufferedWriter bufferedWriter;
	private String clientUsername;
	
	
	public ClientManager(Socket socket) {
		try {
			this.socket = socket;
			this.bufferedReader = new BufferedReader(new  InputStreamReader(socket.getInputStream()));
			this.bufferedWriter = new BufferedWriter(new  OutputStreamWriter(socket.getOutputStream()));
			this.clientUsername = bufferedReader.readLine();
			clientManagers.add(this);
			
			messageAllClients("SERVER: " +  clientUsername + " a rejoint la discussion");
		} 
		catch (IOException e) {
			closeSession(socket, bufferedReader, bufferedWriter);
		}
	}
	
	// on impemente sur un Thread different la lecture des messages
	@Override
	public void run() {
		String clientMessage;
		
		// boucle infini pour que le client puisse envoyer plusieurs messages
		while (socket.isConnected()) {
			try {
				clientMessage = bufferedReader.readLine();
				messageAllClients(clientMessage);
			}
			catch (IOException e) {
				closeSession(socket, bufferedReader, bufferedWriter);
				
				break;
			}
		}
	}	
	
	public void messageAllClients(String message) {
		for (ClientManager clientManager: clientManagers) {
			try {
				if (!clientManager.clientUsername.equals(clientUsername)) {
					clientManager.bufferedWriter.write(message);
					clientManager.bufferedWriter.newLine();
					clientManager.bufferedWriter.flush();
				}
			}
			catch(IOException e) {
				closeSession(socket, bufferedReader, bufferedWriter);
			}
		}
	}
	
	public void removeClientManager() {
		clientManagers.remove(this);
		messageAllClients("SERVER: " + clientUsername + " quitte le chat!");
	}
	
	public void closeSession(Socket socket, BufferedReader bufferedReader,BufferedWriter bufferedWriter) {
		removeClientManager();
		
		try {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (bufferedWriter != null) {
				bufferedWriter.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
