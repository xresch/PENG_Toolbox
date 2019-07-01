package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.utils.CFWEncryption;

public class User {
	
	private int id = -999;
	private String username;
	private String email;
	private String firstname;
	private String lastname;
	private String passwordHash;
	private String passwordSalt;
	private String status;
	private Blob avatarImage;
	
	private Timestamp dateCreated = new Timestamp(new Date().getTime());
	
	private boolean isDeletable = true;
	private boolean isRenamable = true;

	private static Logger logger = CFWLog.getLogger(User.class.getName());
	
	//Username and password is managed in another source, like LDAP or CSV
	private boolean isForeign;
	
	public User(String username) {
		this.username = username;
	}
	
	public User(ResultSet result) throws SQLException {
		
		int col = 1;
		this.id(result.getInt(col++))
		.username(result.getString(col++))
		.email(result.getString(col++))
		.firstname(result.getString(col++))
		.lastname(result.getString(col++))
		.passwordHash(result.getString(col++))
		.passwordSalt(result.getString(col++))
		.avatarImage(result.getBlob(col++))
		.dateCreated(result.getTimestamp(col++))
		.status(result.getString(col++))
		.isDeletable(result.getBoolean(col++))
		.isRenamable(result.getBoolean(col++))
		.isForeign(result.getBoolean(col++));
		
	}
	
	public int id() {
		return id;
	}
	
	public User id(int id) {
		this.id = id;
		return this;
	}
	
	public String username() {
		return username;
	}
	
	public User username(String username) {
		this.username = username;
		return this;
	}
	
	public String email() {
		return email;
	}
	
	public User email(String email) {
		this.email = email;
		return this;
	}
	
	public String firstname() {
		return firstname;
	}
	
	public User firstname(String firstname) {
		this.firstname = firstname;
		return this;
	}
	
	public String lastname() {
		return lastname;
	}
	
	public User lastname(String lastname) {
		this.lastname = lastname;
		return this;
	}
	
	
	public User setInitialPassword(String password, String repeatedPassword) {
		
		if(!password.equals(repeatedPassword)) {
			new CFWLog(logger)
			.method("setInitialPassword")
			.severe("The two provided passwords are not equal.");
		}
		
		this.passwordSalt(CFW.Encryption.createPasswordSalt(31));
		this.passwordHash(CFW.Encryption.createPasswordHash(password, this.passwordSalt()) );
		
		return this;
	}
	
	public String passwordHash() {
		return passwordHash;
	}

	public User passwordHash(String passwordHash) {
		this.passwordHash = passwordHash;
		return this;
	}

	public String passwordSalt() {
		return passwordSalt;
	}

	public User passwordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
		return this;
	}

	public Blob avatarImage() {
		return avatarImage;
	}
	
	public User avatarImage(Blob avatarImage) {
		this.avatarImage = avatarImage;
		return this;
	}
	
	
	public Timestamp dateCreated() {
		return dateCreated;
	}
	
	public User dateCreated(Timestamp creationDate) {
		this.dateCreated = creationDate;
		return this;
	}
	
	public boolean isDeletable() {
		return isDeletable;
	}
	
	public User isDeletable(boolean isDeletable) {
		this.isDeletable = isDeletable;
		return this;
	}
	
	public String status() {
		return status;
	}
		
	public User status(String status) {
		this.status = status;
		return this;
	}
	
	public boolean isRenamable() {
		return isRenamable;
	}
	public User isRenamable(boolean isRenamable) {
		this.isRenamable = isRenamable;
		return this;
	}
	
	public boolean isForeign() {
		return isForeign;
	}
	
	public User isForeign(boolean isForeign) {
		this.isForeign = isForeign;
		return this;
	}
	
	public String getKeyValueString() {
		
		StringBuilder builder = new StringBuilder();
		builder.append("\nid: "+id);
		builder.append("\nusername: "+username);
		builder.append("\nemail: "+email);
		builder.append("\nfirstname: "+firstname);
		builder.append("\nlastname: "+lastname);
		builder.append("\npasswordHash: "+passwordHash);
		builder.append("\npasswordSalt: "+passwordSalt);
		builder.append("\navatarImage: "+avatarImage);
		builder.append("\ndateCreated: "+dateCreated);
		builder.append("\nisDeletable: "+isDeletable);
		builder.append("\nisRenamable: "+isRenamable);
		builder.append("\nstatus: "+status);
		
		return builder.toString();
	}
	
	
}
