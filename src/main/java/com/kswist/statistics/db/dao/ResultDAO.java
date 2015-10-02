package com.kswist.statistics.db.dao;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import com.kswist.statistics.db.entities.Result;
import com.kswist.statistics.db.entities.User;

@Stateless
public class ResultDAO {
	private static final Logger logger = Logger.getLogger(ResultDAO.class);
	@Inject
	private EntityManager em;

	public void save(Result result) {
		logger.debug("Saving: " + result);
		em.persist(result);
	}

	public List<Result> getAllDesc() {
		logger.debug("Searching all results");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Result> cq = cb.createQuery(Result.class);
		Root<Result> result = cq.from(Result.class);

		cq.select(result);
		cq.orderBy(cb.desc(result.<Date> get("date")));
		return em.createQuery(cq).getResultList();
	}

	public List<Result> getAllAsc() {
		logger.debug("Searching all results");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Result> cq = cb.createQuery(Result.class);
		Root<Result> result = cq.from(Result.class);

		cq.select(result);
		cq.orderBy(cb.asc(result.<Date> get("date")));
		return em.createQuery(cq).getResultList();
	}

	public List<Result> getUserResults(User user) {
		logger.debug("Searching all results");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Result> cq = cb.createQuery(Result.class);
		Root<Result> result = cq.from(Result.class);

		cq.select(result);
		cq.where(cb.or(cb.equal(result.<User> get("user"), user),
				cb.equal(result.<User> get("opponent"), user)));
		cq.orderBy(cb.desc(result.<Date> get("date")));
		return em.createQuery(cq).getResultList();
	}

	public void update(Result result) {
		em.merge(result);
	}

	public void delete(Result result) {
		em.remove(em.contains(result) ? result : em.merge(result));
	}

}
