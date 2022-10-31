package library_server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import library.Library;
import personas.UserManager;

public class LibraryServer {
	
	public static final int PORT = 2018;

	public static void main(String[] args) {
		System.out.println("SERVIDOR: biblioteca inicializada");
		Library lb = new Library(); //inicializamos la librería
		UserManager user_manager = new UserManager();
		
		try (ServerSocket server = new ServerSocket()){			
			InetSocketAddress adress = new InetSocketAddress(PORT);
			server.bind(adress);

			System.out.println("SERVIDOR: Esperando peticion por el puerto " + PORT);
			
			while (true) {
				//creamos el socket y se lo pasamos al objeto tipo librarythread para que ya se encarge ella de gestionarlo
				Socket socketToCliente = server.accept();
				System.out.println("SERVIDOR: petición recibida");
				//creamos un método estático para poder pasar el método por referencia y así asignar la actualización de la libreria
				// es la manera más práctica que he encontrado para evitar recurrir a serializar y guardar en un fichero o usar una base de datos
				LibraryThread.launchThread(socketToCliente, lb, user_manager);
			}			
		} catch (IOException e) {
			System.err.println("SERVIDOR: Error de entrada/salida");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("SERVIDOR: Error");
			e.printStackTrace();
		}
	}

}
