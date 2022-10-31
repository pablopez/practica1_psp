package personas;

public class Admin extends Persona{

	public Admin(String email, String pw) {
		super(email, pw, "add,remove");
	}
}
