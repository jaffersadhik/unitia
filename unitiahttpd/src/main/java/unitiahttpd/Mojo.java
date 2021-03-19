package unitiahttpd;

import java.util.List;

public class Mojo {

	private String password;
	private String username;
	private List<SMS> smslist;
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public List<SMS> getSmslist() {
		return smslist;
	}
	public void setSmslist(List<SMS> smslist) {
		this.smslist = smslist;
	}
	
	
}
