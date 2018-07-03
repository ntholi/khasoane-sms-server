package khasoane.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import khasoane.datasource.HibernateHelper;


@WebListener
public class HibernateLoader implements ServletContextListener {


    public void contextInitialized(ServletContextEvent sce)  { 
    	System.out.println("SMSServer: Loading Hibernate...");
    	HibernateHelper.getSession();
    }
    
    public void contextDestroyed(ServletContextEvent sce)  { 
    	HibernateHelper.destroy();
    }
	
}
