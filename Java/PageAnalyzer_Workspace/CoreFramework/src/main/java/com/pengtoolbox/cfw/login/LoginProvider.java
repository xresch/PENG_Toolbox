package com.pengtoolbox.cfw.login;

public interface LoginProvider {

	public boolean checkCredentials(String username, String password);
}
