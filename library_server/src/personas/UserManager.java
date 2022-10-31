package personas;

import java.util.HashMap;
import java.util.Map;

public class UserManager {

	Map<String, Persona> personas;

	public UserManager() {			
		this.personas = new HashMap<String, Persona>();
		this.loadDefUsers();
	}
	
	public Persona login(String email, String pw) {
		Persona user = this.getUser(email);
		if(user != null) {
			if(user.login(pw)) {
				return user;
			}			
		}
		return null;		
	}
	
	private Persona getUser(String email) {
		if(this.personas.containsKey(email)) {
			return this.personas.get(email);
		}else {
			return null;
		}		
	}
	
	private void loadDefUsers() {
		this.personas.put("lib_admin@gmail.com", new Admin("lib_admin@gmail.com", "1234"));
		this.personas.put("lib_user@gmail.com", new User("lib_admin@gmail.com", "1234"));
		this.personas.put("asd", new User("asd", "1234"));
		this.personas.put("a", new Admin("a", "a"));
	}

}
