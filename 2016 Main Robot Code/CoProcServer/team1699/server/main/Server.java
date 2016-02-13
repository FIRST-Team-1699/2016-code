package team1699.server.main;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	ServerSocket sSocket;
	Socket socket;
	PrintWriter output;
	
	public Server(int port, String path){
		try {
			sSocket = new ServerSocket(port);
			
			socket = sSocket.accept();
			
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			FileOutputStream fout = new FileOutputStream(path);
			int i;
			while((i = dis.read()) > -1){
				fout.write(i);
			}
			
			fout.flush();
			fout.close();
			dis.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
