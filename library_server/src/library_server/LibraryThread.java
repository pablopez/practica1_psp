package library_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import library.Book;
import library.Library;
import personas.Persona;
import personas.UserManager;

public class LibraryThread implements Runnable {
	private Thread thread;
	private static int queryNum = 0;
	private Socket socketToClient;
	public Library library;
	private UserManager user_manager;
	
	public LibraryThread(Socket socketToClient, Library lb, UserManager user_manager) {
		queryNum++;
		this.thread = new Thread(this, "query_"+queryNum);
		this.socketToClient = socketToClient;
		this.library = lb;
		this.user_manager = user_manager;
		thread.start();
	}
	
	private Persona login(String user, String pw) {
		return this.user_manager.login(user, pw);
	}
	
	private String checkBooks(String type, String value) {
		Book checked = null;
		String response = "";
		if(type.equals("title"))
			checked = this.library.getBookByTitle(value);
		else if(type.equals("isbn"))
			checked = this.library.getBookByISBN(value);
		else if(type.equals("author")) {			
			for(Book book : this.library.getBooksByAuthor(value)) {
				 response += book.toString() + "\n";
			};			 
		}
		if(checked  != null && response.equals("")) {
			return checked.toString();
		}else if(!response.equals("")) {
			return response;
		}else {
			return "libro no encontrado";
		}
	}
	
	private String addBook(String isbn, String title, String author, double price, int copies) {
		this.library.addBook(isbn, title, author, price, copies);
		
		return "libro añadido a la librería";
	}
	
	private String buyBook(String type, String value) {
		Book bought = null;
		if(type.equals("isbn")) {
			bought = this.library.buyBookByISBN(value);
		}else if(type.equals("title")){
			bought = this.library.buyBookByTitle(value);
		}
		if(bought != null) {
			return bought.getTitle()+" fue comprado";
		}else {
			return "libro no disponible";
		}
	}
	
	private String removeBook(String type, String value) {
		boolean removed = false;
		if(type.equals("removebyisbn")) {
			removed = this.library.removeBookByISBN(value);
		}else if(type.equals("removebytitle")){
			removed = this.library.removeBookByTitle(value);
		}
		if(removed) {
			return "El libro fue eliminado";
		}else {
			return "libro no disponible";
		}
	}
	
	private String processAction(String[] request_args) {
		String response = "";
		String action = request_args[0];
		String user = request_args[1];
		String pwd = request_args[2];
		Persona loged = this.login(user, pwd);
		
		if(loged == null) {
			return "";
		}
		
		if(action.equals("login")) {
			//solo los admins pueden añadir libros, como la clase Persona es genérica usamos esta diferencia para ver qué tipo de usuario es la instancia que estamos haciendo
			if(loged.letActions("add")) {
				response = "admin";
			}else {
				response = "user";
			}
		}else {
			String body = request_args[3];
			if(!loged.letActions(action)) {
				response = "acción no permitida para este usuario";
			}else if(action.equals("add")) {
				String[] book_attrs = body.split(";");
				String isbn = "";
				String title = "";
				String auth = "anom";
				int copies = 0;
				double price = 0;
				for(String attr : book_attrs) {
					if(attr.indexOf("isbn") != -1) {
						isbn = attr.replace("isbn:", "");
					}else if(attr.indexOf("title") != -1) {
						title = attr.replace("title:", "");
					}else if(attr.indexOf("author") != -1) {
						auth = attr.replace("author:", "");
					}else if(attr.indexOf("price") != -1) {
						price = Double.parseDouble(attr.replace("price:", ""));
					}else if(attr.indexOf("copies") != -1) {
						copies = Integer.parseInt(attr.replace("copies:", ""));
					}
				}
				if(!isbn.equals("") && !title.equals("")) {
					response = this.addBook(isbn, title, auth, price, copies);
				}else {
					response = "no se ha podido añadir el libro al catalogo";
				}
			}else {
				String[] query = body.split(":");
				if(action.equals("check")) {
					response = this.checkBooks(query[0], query[1]);
				}else if(action.equals("buy")) {
					response = this.buyBook(query[0], query[1]);							
				}else if(action.equals("remove")) {
					response = this.removeBook(query[0], query[1]);							
				}		
			} 
		}		
		return response;
	}
	
	private String syncAddition( String response, java.lang.String[] strings) {
		synchronized (this) {
			System.out.println("SERVIDOR: resolviendo " + strings);
				response = this.processAction(strings);
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return response;
			}
	}
	
	@Override
	public void run() {
		System.out.println("SERVIDOR: Estableciendo comunicacion con " + thread.getName());
		PrintStream output = null;
		InputStreamReader input = null;
		BufferedReader buffer_input = null;
		
		try {
			output = new PrintStream(this.socketToClient.getOutputStream());
		
			input  = new InputStreamReader(this.socketToClient.getInputStream());
			buffer_input = new BufferedReader(input);
			
			boolean is_the_end = false; // este flag se pondrá a true cuando la petición sea "exit" para acabar la ejecución
			String response = "";
			
			do {
				
				String request_body = buffer_input.readLine();
				String [] request_splitted = request_body.split("#");
				System.out.println("SERVIDOR: resolviendo " + request_body);
				if(request_body == "exit") {
					is_the_end = true;
					response = "SERVIDOR BIBLIOTECA: SESIÓN CERRADA";
				}else {
					if(request_splitted[0].equals("add")) {
						// si es añadir libro la acción es sincronizada
						response = this.syncAddition(response, request_splitted);
					}else {
						response = this.processAction(request_splitted);
					}
					
					output.println(response);					
				}
				System.out.println("SERVIDOR: peticion resuelta... ");
				System.out.println(response);
				output.println(response);	
				
			}while(is_the_end);
			
			this.socketToClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void launchThread(Socket socketToClient, Library lb, UserManager user_manager) {
		// crea un hilo nuevo del tipo librarythread y le asigna el socket y la librería inicializada además de un user manager para gestionar los usuarios
		// cuando manipula la librería se la asigna a lb.
		LibraryThread lt = new LibraryThread(socketToClient, lb, user_manager);
		lb = lt.library;
	}

}
