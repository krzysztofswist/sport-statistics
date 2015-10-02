package com.kswist.statistics.security;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;

import com.kswist.statistics.db.dao.UserDAO;
import com.kswist.statistics.db.entities.User;
import com.kswist.statistics.utils.WebMessage;

@Named("authBean")
@SessionScoped
public class AuthorizationBean implements Serializable {
	private static final long serialVersionUID = 1234469943233056167L;
	private static final Logger logger = Logger
			.getLogger(AuthorizationBean.class);
	HttpSession session;

	@Inject
	private transient FacesContext facesContext;

	@Inject
	private WebMessage webMessage;

	@Inject
	private UserDAO userDAO;

	private String user;
	private String password;
	private User dbUser;

	public User getDbUser() {
		return dbUser;
	}

	public void setDbUser(User dbUser) {
		this.dbUser = dbUser;
	}

	@RequestScoped
	public String getUser() {
		user = (String) ((HttpSession) facesContext.getExternalContext()
				.getSession(false)).getAttribute("USER");
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void login()
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		dbUser = userDAO.getByLogin(user);
		session = ((HttpSession) facesContext.getExternalContext()
				.getSession(false));
		if (dbUser != null && password != null
				&& SHA1(password).equals(dbUser.getPasswordHash())) {

			session.setAttribute("USER", user);

			try {
				String url = (String) session.getAttribute("URL");
				if (url.contains("/login.jsf")) {
					url = url.replaceAll("/login.jsf", "/");
				}
				String redirectUrl = url;
				logger.trace("Redirect URL is: " + redirectUrl);
				facesContext.getExternalContext().redirect(redirectUrl);
			} catch (IOException ioe) {
				logger.errorv("Can't redirect to login page", ioe);
			}
		} else {
			webMessage.error(
					"Invalid Login/Password combination or user has no access to application");
		}
	}

	private String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	public String SHA1(String text)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		byte[] sha1hash = md.digest();
		return convertToHex(sha1hash);
	}

}
