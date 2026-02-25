package javaSocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;


public class Client {
	// socket venant du server permettant d'etablir la connection entre le client et le server
	private Socket socket;
	// permet de lire les donnees venant du client
	private BufferedReader bufferedReader;
	// permet d'ecrire aux autres clients 
	private BufferedWriter bufferedWriter;
	private String username;
	
	
	public Client(Socket socket, String username) {
		try {
			this.socket = socket;
			this.bufferedReader = new BufferedReader(new  InputStreamReader(socket.getInputStream()));
			this.bufferedWriter = new BufferedWriter(new  OutputStreamWriter(socket.getOutputStream()));
			this.username = username;			
		} 
		catch (IOException e) {
			closeSession(socket, bufferedReader, bufferedWriter);
		}
	}
	
	public void sendMessage() {
		try {
			bufferedWriter.write(username);
			bufferedWriter.newLine();
			bufferedWriter.flush();
			
			Scanner scanner = new Scanner(System.in);
			
			while (socket.isConnected()) {
				String message = scanner.nextLine();
				bufferedWriter.write(username + ": " + message);
				bufferedWriter.newLine();
				bufferedWriter.flush();
			}
			scanner.close();
		}
		catch (IOException e) {
			closeSession(socket, bufferedReader, bufferedWriter);
		}
	}
	
	public void listenForMessage() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String chatMessage;
				
				while(socket.isConnected()) {
					try {
						chatMessage = bufferedReader.readLine();
						System.out.println(chatMessage);
					}
					catch(IOException e) {
						closeSession(socket, bufferedReader, bufferedWriter);
					}
				}
			}
		}).start();
	}

	public void closeSession(Socket socket, BufferedReader bufferedReader,BufferedWriter bufferedWriter) {
		
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
	
	public static void main(String[]  args) throws IOException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Entrez votre pseudo acceder au chat");
		
		String username = scanner.nextLine();
		Socket socket = new Socket("localhost", 2026);
		Client client = new Client(socket, username);
		
		client.listenForMessage();
		client.sendMessage();
		scanner.close();
	}
}
