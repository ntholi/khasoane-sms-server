package khasoane.datasource;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;


public class DAO<E>{

	protected static Logger logger = LogManager.getLogger(DAO.class);
	private Class<E> type;
	private int pageSize = 40;

	public DAO(Class<E> type){
		this.type = type;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public Class<E> getType() {
		return type;
	}

	public void setType(Class<E> type) {
		this.type = type;
	}

	/**
	 * A few things to note about the save operation first, if the object being 
	 * saved is an  instance of {@link AuditableModel}, updatedBy (the current user) 
	 * and branch (the current branch) will be assigned to the object. And secondly 
	 * the method will upload the object to the server
	 * 
	 * @param obj
	 * @param saveAndSync
	 */
	public void save(E obj) {
		save(obj, true);
	}
	
	private void save(E obj, boolean saveAndSync) {
		Transaction tx = null;
		Session session = HibernateHelper.getSession();
		try {
			tx = session.beginTransaction();
			session.saveOrUpdate(obj);
			tx.commit();
		}
		catch(HibernateException ex){
			try {
				if(tx != null){
					tx.rollback();
				}
			} catch (Exception e) {
				logger.error("Unable to rollback transaction: "+ e);
			}
			ex.printStackTrace();
			logger.error(ex);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		finally{
			session.close();
		}
	}

	public E load(Serializable id) {
		Session session = HibernateHelper.getSession();
		E obj = null;
		try{
			obj = session.load(type, id);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		finally{
			session.close();
		}

		return obj;
	}

	public E get(Serializable id) {
		Session session = HibernateHelper.getSession();
		E obj = null;
		try{
			obj = session.get(type, id);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		finally{
			session.close();
		}

		return obj;
	}
	
	public List<E> all() {
		Session session = HibernateHelper.getSession();
		List<E> list = new ArrayList<>();
		try {
			StringBuilder hql = new StringBuilder("from ").append(type.getName());
			Query<E> query = session.createQuery(hql.toString(), type);
			list = query.list();
		}
		catch(Exception ex){
			ex.printStackTrace();
			logger.error(ex);
		}
		finally{
			session.close();
		}
		return list;
	}

	public Long count() {
		Session session = HibernateHelper.getSession();
		Long size = null;
		try {
			Query<Long> query = session.createQuery("select count(*) from "+type.getName(), 
					Long.class);
			size = query.getSingleResult();
		}
		catch(Exception ex){
			ex.printStackTrace();
			logger.error(ex);
		}
		finally{
			session.close();
		}
		return size;
	}

	public static Long count(Class<?> type) {
		Session session = HibernateHelper.getSession();
		Long size = null;
		try {
			Query<Long> query = session.createQuery("select count(*) from "+type.getName(), 
					Long.class);
			size = query.getSingleResult();
		}
		catch(Exception ex){
			ex.printStackTrace();
			logger.error(ex);
		}
		finally{
			session.close();
		}
		return size;
	}
	
	public Integer countTotalPages() {
		Session session = HibernateHelper.getSession();
		Integer pages = null;
		try {
			Query<Long> query = session.createQuery("select count(*) from "+type.getName(), 
					Long.class);
			double size = query.getSingleResult();
			if(size > pageSize) {
				pages = (int) Math.ceil((size / pageSize));
			} 
			else {
				pages = 1;
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			logger.error(ex);
		}
		finally{
			session.close();
		}
		return pages;
	}
	
	public void delete(Object obj) {
		Session session = HibernateHelper.getSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			session.delete(obj);
			tx.commit();
		}
		catch(HibernateException ex){
			try {
				if(tx != null){
					tx.rollback();
				}
			} catch (Exception e) {
				logger.error("Unable to rollback transaction: "+ e);
			}
			ex.printStackTrace();
			logger.error(ex);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		finally{
			session.close();
		}
	}
}
