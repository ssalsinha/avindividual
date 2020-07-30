package projetoAPDC.util;

public class UserData {
	public String name;
	public String password;
	public String confirmation;
	public String address;
	public String phoneNR;
	
	public UserData() {}
	
	//TODO - test email
	public boolean validRegistration() {
		return password.equals(confirmation);
	}
}
