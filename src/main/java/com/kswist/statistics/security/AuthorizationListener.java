package com.kswist.statistics.security;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;

public class AuthorizationListener implements PhaseListener {

	private static final Logger logger = Logger
			.getLogger(AuthorizationListener.class);
	private static final long serialVersionUID = 7812141963873550290L;

	@Override
	public void afterPhase(PhaseEvent event) {
		FacesContext facesContext = event.getFacesContext();
		HttpSession session = (HttpSession) facesContext.getExternalContext()
				.getSession(true);

		if (session.getAttribute("USER") == null) {
			HttpServletRequest request = (HttpServletRequest) facesContext
					.getExternalContext().getRequest();

			String path = request.getServletPath();
			logger.debug("Request Path: " + path);

			if (!path.contains("login.jsf")) {
				try {

					String url = "http://" + request.getLocalAddr() + ":"
							+ request.getLocalPort() + request.getRequestURI();
					logger.debug("URL: " + url);
					session.setAttribute("URL", url);
					String app = request.getRequestURI().split("/")[1];
					logger.debug("APP: " + app);
					session.setAttribute("APP", app);
					logger.trace("Redirecting to login page");

					String redirectUrl = "http://" + request.getLocalAddr()
							+ ":" + request.getLocalPort() + "/" + app + "/"
							+ "login.jsf";

					logger.trace("Redirect URL is: " + redirectUrl);
					event.getFacesContext().getExternalContext()
							.redirect(redirectUrl);
				} catch (IOException ioe) {
					logger.errorv("Can't redirect to login page", ioe);
				}
			} else {
				String url = "http://" + request.getLocalAddr() + ":"
						+ request.getLocalPort() + request.getRequestURI();
				if (session.getAttribute("URL") == null)
					session.setAttribute("URL", url);
				String app = request.getRequestURI().split("/")[1];
				logger.debug("APP: " + app);
				session.setAttribute("APP", app);
			}
		} else {
			logger.debug(session.getAttribute("USER") + " AUTHORIZED");
		}

	}

	@Override
	public void beforePhase(PhaseEvent arg0) {

	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

}
