package testing;

public abstract class UIRunnable implements Runnable
{
	private Till till;
	private Customer customer;
	private Stats stats;

	public UIRunnable(Till till)
	{
		this.till = till;
	}
	
	public UIRunnable(Customer customer)
	{
		this.customer = customer;
	}

	public UIRunnable(Till till, Customer customer, Stats stats)
	{
		this.till = till;
		this.customer = customer;
		this.stats = stats;
	}

	public UIRunnable(Stats stats)
	{
		this.stats = stats;
	}

	public Till getTill() {
		return till;
	}

	public Customer getCustomer() {
		return customer;
	}
}
