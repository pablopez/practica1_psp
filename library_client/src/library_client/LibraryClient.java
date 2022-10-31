package library_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class LibraryClient {
	
	public static final int PORT = 2018;
	public static final String IP_SERVER = "localhost";
	private final Scanner sc = new Scanner(System.in);
	private InetSocketAddress server_adress;
	private boolean isAdmin;
	private boolean wantGoOut;
	private String credentials = "";

	public LibraryClient() {
		this.setServAddrs();
		this.setIsAdmin(false);
		this.setWantGoOut(false);
	}
	
	public void setIsAdmin(boolean is_admin) {
		this.isAdmin = is_admin;
	}
	
	public boolean getIsAdmin() {
		return this.isAdmin;
	}

	public boolean isLogged() {
		return !this.credentials.equals("");
	}
	
	private void setServAddrs() {
		this.server_adress = new InetSocketAddress(IP_SERVER, PORT);
	}
	
	public boolean isWantGoOut() {
		return wantGoOut;
	}

	public void setWantGoOut(boolean wantGoOut) {
		this.wantGoOut = wantGoOut;
	}
	
	private String execute(String action, String body) {
		// se crean las peticiones
		// el formato es acción#usuario#contraseña#cuerpo dela petición
		// el cuerpo de la petición tendrá (como se ve en el código) el formato "atributo:valor"
		String response = "";
		String request = action+"#"+this.credentials;
		if(body != "") {
			request += "#"+body; 
		}
		System.out.println("CLIENTE: enviando " + request);		
		try (Socket socket_to_server = new Socket()) {
			System.out.println("CLIENTE: Esperando a que el servidor acepte la conexión");
			
			socket_to_server.connect(this.server_adress);
			
			System.out.println("CLIENTE: Conectado a " + IP_SERVER + " por el puerto " + PORT);

			PrintStream output = new PrintStream(socket_to_server.getOutputStream());

			output.println(request);
			
			InputStreamReader input = new InputStreamReader(socket_to_server.getInputStream());
					
			BufferedReader bf = new BufferedReader(input);
			
			System.out.println("CLIENTE: Esperando al resultado del servidor...");
			response = bf.readLine();
			
		} catch (UnknownHostException e) {
			System.err.println("CLIENTE: No encuentro el servidor en la direcci�n" + IP_SERVER);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("CLIENTE: Error de entrada/salida");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("CLIENTE: Error -> " + e);
			e.printStackTrace();
		}			
		System.out.println("CLIENTE: "+response);
		return response;
	}
	
	private String getSimpleReq(String get_by) {
		String value = "";
		if(get_by.equals("isbn")) {
			System.out.println("Introduzca el isbn:");
			value = "isbn";
		}else if(get_by.equals("title")) {
			System.out.println("Introduzca el titulo:");
			value = "title";
		}else if(get_by.equals("author")) {
			value = "author";
			System.out.println("Introduzca el nombre del autor:");
		}
		if(value != "") {
			value += ":"+sc.nextLine();
		}
		return value;
	}
	
	private void checkBook(String check_by) {
		this.execute("check", this.getSimpleReq(check_by));
	}
	
	private void removeBook(String get_by) {
		this.execute("remove", this.getSimpleReq(get_by));		
	}
	
	private void buyBook(String get_by) {
		this.execute("buy", this.getSimpleReq(get_by));
	}
	
	private void addBook() {
		String body_req = "";
		System.out.println("Introduzca el isbn:");
		body_req += "isbn:"+sc.nextLine();
		System.out.println("Introduzca el titulo:");
		body_req += ";title:"+sc.nextLine();
		System.out.println("Introduzca el autor:");
		body_req += ";author:"+sc.nextLine();
		System.out.println("Introduzca el precio:");
		body_req += ";price:"+Double.parseDouble(sc.nextLine());
		System.out.println("Introduzca el numero de copias:");
		body_req += ";copies:"+Integer.parseInt(sc.nextLine());
		this.execute("add", body_req);
	}
	
	public void login() {
		// solicitamos usuario y contraseña
		System.out.println("Introduzca usuario");
		String username = sc.nextLine();
		System.out.println("Introduzca contraseña");
		String pwd = sc.nextLine();
		this.credentials = username+"#"+pwd;
		// construimos el cuerpo de la petición con los datos requeridos		
		String response = this.execute("login", "");		
		// si la respuesta es 'admin' sabemos que está ejecutándolo un admin
		
		// si la respuesta es distinta de "" entonces guardamos las credenciales para el resto de peticiones
		
		if(response.equals("admin")) {
			this.setIsAdmin(true);
			System.out.println("CLIENTE: el tipo de usuario es: "+response);
		}else if(response.equals("user")) {
			this.setIsAdmin(false);
			System.out.println("CLIENTE: el tipo de usuario es: "+response);
		}else {
			this.credentials = "";
			System.out.println("CLIENTE: No existe ningun usuario con estas credenciales");
		}
	}
	
	public void exit() {
		//si el usuario quiere salir ponemos en flag wantGoOut a true 
		System.out.println("Si desea salir escriba salir, de lo conrario pulse cualquier otra tecla");
		String op = sc.nextLine();
		if(op.equals("salir")) {
			System.out.println("CLIENTE: ha salido, ¡adios!");
			this.setWantGoOut(true);
		}else {
			this.setWantGoOut(false);
		}		
	}
	
	public void launchMenu() {
		// lanza un menú que tiene acciones genéricas y luego disgrega si se es admin o usuario normal
		String actions = "Seleccione una opción introduciéndo el número marcado. Si desea salir introduzca cualquier otro número o pulse intro: \n\n"+ 	
				"1. Consultar libro por isbn \n"+
				"2. Consultar libro por título \n"+
				"3. Consultar libro por autor \n";
		if(this.getIsAdmin()) {
			actions += 	"4. Añadir libro \n"+
						"5. Descatalogar libro \n";
		}else {
			actions += 	"4. Comprar libro \n";
		}
		this.setWantGoOut(false);
		System.out.println(actions);
		String op = sc.nextLine();
		
		if(op.equals("1")) {
			this.checkBook("isbn");
		}else if(op.equals("2")) {
			this.checkBook("title");
		}else if(op.equals("3")) {
			this.checkBook("author");
		}else if(op.equals("4")) {
			if(this.getIsAdmin()) {
				this.addBook();
			}else {
				this.buyBook("isbn");
			}
		}else if(op.equals("5")) {
			if(this.getIsAdmin()){
				this.removeBook("isbn");
			}
		}else{
			this.setWantGoOut(true);
		}
		
	}
	
	public static void main(String[] args) {
		System.out.println("        APLICACI�N CLIENTE         ");
		System.out.println("-----------------------------------");
		LibraryClient lc = new LibraryClient();
		while(!lc.isLogged() && !lc.wantGoOut) {			
			lc.login();
			if(!lc.isLogged()) {
				lc.exit();
			}			
		}
		while(!lc.wantGoOut) {
			lc.launchMenu();
		}
		
		System.out.println("CLIENTE: Fin del programa");

	}



	

}
