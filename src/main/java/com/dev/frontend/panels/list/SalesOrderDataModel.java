package com.dev.frontend.panels.list;

import java.util.List;

import com.dev.backend.beans.SalesOrder;
import com.dev.frontend.services.Services;


public class SalesOrderDataModel extends ListDataModel
{
	private static final long serialVersionUID = 7526529951747614655L;

	public SalesOrderDataModel() 
	{
		super(new String[]{"Order Number","Customer","Total Price"}, 0);
	}

	@Override
	public int getObjectType() {
		return Services.TYPE_SALESORDER;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String[][] convertRecordsListToTableModel(List<Object> list) 
	{
		//TODO by the candidate
		/*
		 * This method use list returned by Services.listCurrentRecords and should convert it to array of rows
		 * each row is another array of columns of the row
		 */
		String[][] sampleData = new String[list.size()][];
		int i = 0;
		for(SalesOrder p : (List<SalesOrder>)(Object) list) {
			String[] s = new String[] { p.getOrderNumber(), p.getCustomer().getName(),
					String.valueOf(p.getTotalPrice())};
			sampleData[i] = s;
			i++;
		}
		
//		String[][] sampleData = new String [][]{{"22423","(01)Customer 1","122.5"},{"22424","(02)Customer 2","3242.5"}};
		return sampleData;
	}
}
