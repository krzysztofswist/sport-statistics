package com.kswist.statistics.db.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "SS_PERSON", uniqueConstraints = @UniqueConstraint(columnNames = {
		"LOGIN" }) )
@NamedNativeQueries({
	@NamedNativeQuery(name = "User.findForConverter", query = "select * from ss_person u where u.first_Name || ' ' || u.last_Name=:value", resultClass = User.class)
})
public class User implements Serializable {

	private static final long serialVersionUID = -6894466923770538717L;

	@Id
	@Column(name = "USER_ID", insertable = false)
	private int id;

	@Column(name = "LOGIN", nullable = false)
	private String login;

	@Column(name = "FIRST_NAME", nullable = true)
	private String firstName;

	@Column(name = "LAST_NAME", nullable = true)
	private String lastName;

	@Column(name = "EMAIL", nullable = true)
	private String email;

	@Column(name = "PASSWORD_HASH", nullable = true)
	private String passwordHash;

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", login=" + login + ", firstName="
				+ firstName + ", lastName=" + lastName + ", email=" + email
				+ ", passwordHash=" + passwordHash + "]";
	}

}
