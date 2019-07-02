package com.pengtoolbox.cfw.response.bootstrap;

import com.pengtoolbox.cfw._main.SessionData;

public abstract class UserMenuItem extends MenuItem {

	public UserMenuItem(SessionData data) {
		super(data.getUser().username());
		
	}

}
