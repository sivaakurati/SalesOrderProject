package com.dev.frontend.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.dev.backend.beans.Customer;
import com.dev.backend.beans.OrderLines;
import com.dev.backend.beans.Product;
import com.dev.backend.beans.SalesOrder;
import com.dev.backend.utils.HibernateUtil;
import com.dev.backend.utils.ValidationException;
import com.dev.frontend.panels.ComboBoxItem;

public class Services {
	public static final int TYPE_PRODUCT = 1;
	public static final int TYPE_CUSTOMER = 2;
	public static final int TYPE_SALESORDER = 3;

	public static SessionFactory sessionFactory = HibernateUtil
			.getSessionFactory();
	static Logger logger = Logger.getLogger(Services.class);

	public static Object save(Object object, int objectType) {
		// TODO by the candidate
		/*
		 * This method is called eventually after you click save on any edit
		 * screen object parameter is the return object from calling method
		 * guiToObject on edit screen and the type is identifier of the object
		 * type and may be TYPE_PRODUCT , TYPE_CUSTOMER or TYPE_SALESORDER
		 */
		
		if (!validateEmptyvalues(object, objectType)) {
			throw new ValidationException("Please fill all the fields before saving");
		}
		
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();

		try {
			if (objectType == TYPE_SALESORDER) {
				SalesOrder s = (SalesOrder) object;
				
				
				if (!handleCustCreditAndProdQuant(session, s)) {
					logger.info("Validation failed. Not saving the salesorder");
					session.getTransaction().rollback();
					throw new ValidationException("You dont have enough credit or Quantity of product is not available");
				}

				for (OrderLines ol : s.getOrderLinesList()) {
					session.saveOrUpdate(OrderLines.class.getName(), ol);
				}
				session.saveOrUpdate(s);
			} else
				session.saveOrUpdate(object);
			session.flush();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			logger.error("Error While Saving record", e);
			session.getTransaction().rollback();
			throw e;
		}
		return object;
	}

	private static boolean validateEmptyvalues(Object object, int objectType) {
		switch(objectType) {
		case TYPE_CUSTOMER:
			Customer c = (Customer) object;
			if(c.getCode().isEmpty() || c.getName().isEmpty() || c.getAddress().isEmpty() || c.getPhone1().isEmpty()) {
				return false;
			}
			break;
		case TYPE_PRODUCT:
			Product p = (Product) object;
			if(p.getCode().isEmpty() || p.getDescription().isEmpty()) {
				return false;
			}
			break;
		case TYPE_SALESORDER:
			SalesOrder s = (SalesOrder) object;
			return (!s.getOrderNumber().isEmpty());
		default:
			break;
		}
		return true;
	}

	private static boolean handleCustCreditAndProdQuant(Session session,
			SalesOrder s) {

		Customer c = (Customer) session.get(Customer.class, s.getCustomer().getCode());
		if (s.getTotalPrice() > (c.getCreditLimit() - c.getCurrentCredit())) {
			return false;
		} else {
			Double curCredit = c.getCreditLimit() - s.getTotalPrice()
					- c.getCurrentCredit();
			c.setCurrentCredit(curCredit);
			session.saveOrUpdate(c);
		}
		for (OrderLines ol : s.getOrderLinesList()) {
			Product p = (Product) session.get(Product.class, ol.getProduct().getCode());
			int remQuantity = p.getQuantity() - ol.getQuantity();
			if (remQuantity < 0) {
				return false;
			} else {
				p.setQuantity(remQuantity);
				session.saveOrUpdate(p);
			}
		}
		return true;
	}

	public static Object readRecordByCode(String code, int objectType) {
		// TODO by the candidate
		/*
		 * This method is called when you select record in list view of any
		 * entity and also called after you save a record to re-bind the record
		 * again the code parameter is the first column of the row you have
		 * selected and the type is identifier of the object type and may be
		 * TYPE_PRODUCT , TYPE_CUSTOMER or TYPE_SALESORDER
		 */

		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();

		Object o = null;
		try {
			switch (objectType) {
			case TYPE_CUSTOMER:
				o = session.get(Customer.class, code);
				break;
			case TYPE_PRODUCT:
				o = session.get(Product.class, code);
				break;
			case TYPE_SALESORDER:
				o = session.get(SalesOrder.class, code);
				break;
			default:
				break;
			}
			session.getTransaction().commit();
		} catch (HibernateException e) {
			logger.error("Error While Retrieving", e);
			session.getTransaction().rollback();
		}
		return o;
	}

	public static boolean deleteRecordByCode(String code, int objectType) {
		// TODO by the candidate
		/*
		 * This method is called when you click delete button on an edit view
		 * the code parameter is the code of (Customer - PRoduct ) or order
		 * number of Sales Order and the type is identifier of the object type
		 * and may be TYPE_PRODUCT , TYPE_CUSTOMER or TYPE_SALESORDER
		 */

		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();

		try {
			switch (objectType) {
			case TYPE_CUSTOMER:
				Customer c = new Customer();
				c.setCode(code);
				session.delete(c);
				break;
			case TYPE_PRODUCT:
				Product p = new Product();
				p.setCode(code);
				session.delete(p);
				break;
			case TYPE_SALESORDER:
				SalesOrder s = (SalesOrder) session.get(SalesOrder.class, code);
				if(s != null) {
					for (OrderLines ol : s.getOrderLinesList()) {
						session.delete(ol);
					}
					session.delete(s);
				}
				break;
			default:
				break;
			}
			session.flush();
			session.getTransaction().commit();
			return true;
		} catch (HibernateException e) {
			logger.error("Error While Deleting", e);
			session.getTransaction().rollback();
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static List<Object> listCurrentRecords(int objectType) {
		// TODO by the candidate
		/*
		 * This method is called when you open any list screen and should return
		 * all records of the specified type
		 */
		Session session = sessionFactory.getCurrentSession();
		List<Object> objList = new ArrayList<Object>();
		try {
			session.beginTransaction();
			switch (objectType) {
			case TYPE_CUSTOMER:
				objList = session.createCriteria(Customer.class).list();
				break;
			case TYPE_PRODUCT:
				objList = session.createCriteria(Product.class).list();
				break;
			case TYPE_SALESORDER:
				Set<SalesOrder> set = new HashSet<SalesOrder>(session.createCriteria(SalesOrder.class).list());
				objList = new ArrayList<Object>(set);
				break;
			default:
				break;
			}
			session.getTransaction().commit();
		} catch (HibernateException e) {
			logger.error("Error while retrieving records", e);
			session.getTransaction().rollback();
		}
		return objList;
	}

	@SuppressWarnings("unchecked")
	public static List<ComboBoxItem> listCurrentRecordRefernces(int objectType) {
		// TODO by the candidate
		/*
		 * This method is called when a Combo Box need to be initialized and
		 * should return list of ComboBoxItem which contains code and
		 * description/name for all records of specified type
		 */
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		List<ComboBoxItem> cbox = new LinkedList<ComboBoxItem>();

		try {
			switch (objectType) {
			case TYPE_CUSTOMER:
				List<Customer> list = session.createCriteria(Customer.class)
						.list();
				for (Customer item : list) {
					cbox.add(new ComboBoxItem(item.getCode(), item.getName()));
				}
				break;
			case TYPE_PRODUCT:
				List<Product> pList = session.createCriteria(Product.class)
						.list();
				for (Product item : pList) {
					cbox.add(new ComboBoxItem(item.getCode(), item
							.getDescription()));
				}
				break;
			case TYPE_SALESORDER:
				List<SalesOrder> sList = session.createCriteria(
						SalesOrder.class).list();
				for (SalesOrder item : sList) {
					cbox.add(new ComboBoxItem(item.getOrderNumber(), item
							.getOrderNumber()));
				}
				break;
			default:
				break;
			}
			session.getTransaction().commit();
		} catch (HibernateException e) {
			logger.error("Error while retrieving records", e);
			session.getTransaction().rollback();
		}
		return cbox;
	}

	public static double getProductPrice(String productCode) {
		// TODO by the candidate
		/*
		 * This method is used to get unit price of product with the code passed
		 * as a parameter
		 */
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		Double price = null;
		try {
			Product p = (Product) session.get(Product.class, productCode);
			price = p.getPrice();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			logger.error("Error while retrieving price", e);
			session.getTransaction().rollback();
		}

		return price;
	}
}
