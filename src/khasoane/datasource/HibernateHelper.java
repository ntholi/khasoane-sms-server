package khasoane.datasource;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import khasoane.Message;
import khasoane.User;
import khasoane.credits.Credits;

public class HibernateHelper {

	public static final String USERNAME = "khasoane_soft";
	public static final String PASSWORD = "Fd_Xd45+67Ma3cP=";
	public static final String DB_NAME = "khasoane_db";

	private static Logger logger = LogManager.getLogger(HibernateHelper.class);
	
	private static SessionFactory sessionFactory;

	private HibernateHelper() {}

	public static Session getSession() {
		if(sessionFactory == null) {
			sessionFactory = getSessionFactory();
		}
		return sessionFactory.openSession();
	}

	public static SessionFactory getSessionFactory() {
        Map<String, String> settings = new HashMap<>();
        settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect");
        settings.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
        settings.put(Environment.URL, "jdbc:mysql://localhost/"+DB_NAME+"?autoReconnect=true&useSSL=false");
        settings.put(Environment.USER, USERNAME);
        settings.put(Environment.PASS, PASSWORD);
        settings.put(Environment.HBM2DDL_AUTO, "update");
        settings.put(Environment.SHOW_SQL, "true");
        
        StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder()
        		.applySettings(settings);
        StandardServiceRegistry registry = registryBuilder.build();

        MetadataSources sources = new MetadataSources(registry);
        sources.addAnnotatedClass(Message.class);
        sources.addAnnotatedClass(User.class);
        sources.addAnnotatedClass(Credits.class);
        
        return sources.getMetadataBuilder().build()
        		.getSessionFactoryBuilder().build();
    }
	
    public static void close(Session session) {
        if (session != null) {
            try {
                session.close();
            } catch (HibernateException e) {
            	e.printStackTrace();
            	logger.error("Couldn't close Session ", e);
            }
        }
    }
    
    public static void destroy() {
        if (sessionFactory != null) {
            try {
            	sessionFactory.close();
            } catch (Exception e) {
            	e.printStackTrace();
            	logger.error("Couldn't close Session ", e);
            }
        }
    }
}
