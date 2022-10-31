package personas;

public class Persona {
	
	@SuppressWarnings("unused")
	private String email;
	private String pw;
	private String actions = ""; 
	
	public Persona(String email, String pw) {
		this(email, pw, "login,check,");
	}
	
	public Persona(String email, String pw, String actions) {
		this.actions = "login,check," + actions;
		this.pw = pw;
		this.email = email;
	}
	
	public boolean login(String pw) {
		return this.pw.equals(pw);
	}
	
	public boolean letActions(String action) {
		String []all_actions = this.actions.split(",");
		for( String let_action : all_actions) {
			if(action.equals(let_action)) {
				return true;
			}
		}
		return false;
	}
}
