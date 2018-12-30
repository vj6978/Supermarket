package testing;

import java.util.Map;
import java.util.HashMap;

public class Stats
{
	public int total_till_count;
	public int total_express_till_count;
	public int total_customer_count;
	public int customers_lost;
	public double average_customer_waiting_time;
	public double totalCustomerWaitTime;

	public Map<String, Integer> till_utilization_matric = new HashMap<String, Integer>();

	public Stats(int till_count, int express_till_count)
	{
		this.total_till_count = till_count;
		this.total_express_till_count = express_till_count;
		this.total_customer_count = 0;
		this.customers_lost = 0;
		this.average_customer_waiting_time = 0.0;
		this.totalCustomerWaitTime = 0;

	}

	public int getTotal_customer_count()
	{
		return total_customer_count;
	}

	public void setTotal_customer_count(int total_customer_count)
	{
		this.total_customer_count = total_customer_count;
	}

	public int getCustomers_lost()
	{
		return this.customers_lost;
	}

	public void setCustomers_lost()
	{
		this.customers_lost++;
	}

	public void updateAverageCustomerWaitTime(double wait_time)
	{
		this.totalCustomerWaitTime += wait_time;
	}
	
	public double getAverageCustomerWaitTime()
	{
		return (this.totalCustomerWaitTime/this.total_customer_count);
	}

	public void addToUtilization(String till_name, int initial)
	{
		till_utilization_matric.put(till_name, initial);
	}

	public void updateTillUtilization(String till_name)
	{
		till_utilization_matric.put(till_name, till_utilization_matric.get(till_name)+1);
	}

	public Map<String, Integer> showTillUtilization()
	{
		System.out.println(till_utilization_matric);
		return till_utilization_matric;
	}
	
}
