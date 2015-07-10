package com.dev.frontend.panels.list;

import java.util.List;

import com.dev.backend.beans.Product;
import com.dev.frontend.services.Services;


public class ProductDataModel extends ListDataModel
{
	private static final long serialVersionUID = 7526529951747614655L;

	public ProductDataModel() 
	{
		super(new String[]{"Code","Description","Price","Quantity"}, 0);
	}

	@Override
	public int getObjectType() {
		return Services.TYPE_PRODUCT;
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
		for(Product p : (List<Product>)(Object) list) {
			String[] s = new String[] { p.getCode(), p.getDescription(),
					String.valueOf(p.getPrice()),
					String.valueOf(p.getQuantity()) };
			sampleData[i] = s;
			i++;
		}
//		String[][] sampleData = new String [][]{{"01","Product 1","12.5","25"},{"02","Product 2","10","8"}};
		return sampleData;
	}
}
