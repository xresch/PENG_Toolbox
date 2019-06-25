package com.pengtoolbox.cfw.db.usermanagement;

import java.awt.Image;
import java.sql.Date;

public class User {
	
	private int id;
	private String username;
	private String email;
	private String firstname;
	private String lastname;
	private Image avatarImage;
	private Date creationDate;
	private boolean isDeletable;
	private boolean isBlocked;
	private boolean isLDAP;
	
	
	public int getId() {
		return id;
	}
	public User id(int id) {
		this.id = id;
		return this;
	}
	public String getUsername() {
		return username;
	}
	public User username(String username) {
		this.username = username;
		return this;
	}
	public String getEmail() {
		return email;
	}
	public User email(String email) {
		this.email = email;
		return this;
	}
	public String getFirstname() {
		return firstname;
	}
	public User firstname(String firstname) {
		this.firstname = firstname;
		return this;
	}
	public String getLastname() {
		return lastname;
	}
	public User lastname(String lastname) {
		this.lastname = lastname;
		return this;
	}
	public Image getAvatarImage() {
		return avatarImage;
	}
	public User avatarImage(Image avatarImage) {
		this.avatarImage = avatarImage;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	
	public User creationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	
	public boolean isDeletable() {
		return isDeletable;
	}
	
	public User deletable(boolean isDeletable) {
		this.isDeletable = isDeletable;
		return this;
	}
	
	public boolean isBlocked() {
		return isBlocked;
	}
	
	public void setBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}
	
	public boolean isLDAP() {
		return isLDAP;
	}
	
	public void setLDAP(boolean isLDAP) {
		this.isLDAP = isLDAP;
	}
	
	
}
