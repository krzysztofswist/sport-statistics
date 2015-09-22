package com.kswist.statistics.db.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import com.kswist.statistics.db.entities.User;

@Named
@Stateless
public class UserDAO {

	private static final Logger logger = Logger.getLogger(UserDAO.class);

	@Inject
	private EntityManager em;

	public User getByLogin(String login) {
		logger.debug("Searching user with login: " + login);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> user = cq.from(User.class);

		cq.select(user);
		cq.where(cb.equal(user.get("login"), login));
		try {
			return em.createQuery(cq).getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	public List<User> getAll() {
		logger.debug("Searching all users");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> user = cq.from(User.class);

		cq.select(user);
		return em.createQuery(cq).getResultList();
	}

	public User findForConverter(String value) {
		Query query = em.createNamedQuery("User.findForConverter");
		query.setParameter("value", value);
		return (User)query.getSingleResult();

	}

}
