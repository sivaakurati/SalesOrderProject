package com.dev.backend.beans;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name = "SALES_ORDER")
public class SalesOrder {
	
	@Id
	@Column(name = "ORDER_NUMBER", nullable = false)
	private String orderNumber;
	
	@OneToOne()
	private Customer customer;
	
	@Column(name = "TOTAL_PRICE", nullable = false)
	private Double totalPrice;
	
	@OneToMany(fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinTable(name = "SALESORDER_WITH_PRODUCTS")
	private List<OrderLines> orderLinesList = new ArrayList<OrderLines>();
	
	public SalesOrder() {
		
	}

	public SalesOrder(String orderNumber, Customer customer, Double totalPrice,
			List<OrderLines> orderLinesList) {
		super();
		this.orderNumber = orderNumber;
		this.customer = customer;
		this.totalPrice = totalPrice;
		this.orderLinesList = orderLinesList;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public Customer getCustomer() {
		return customer;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public List<OrderLines> getOrderLinesList() {
		return orderLinesList;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public void setOrderLinesList(List<OrderLines> orderLinesList) {
		this.orderLinesList = orderLinesList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((orderNumber == null) ? 0 : orderNumber.hashCode());
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
		SalesOrder other = (SalesOrder) obj;
		if (orderNumber == null) {
			if (other.orderNumber != null)
				return false;
		} else if (!orderNumber.equals(other.orderNumber))
			return false;
		return true;
	}

	
	

}
