package testing;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.*;
import java.util.concurrent.*;

public class Customer extends Thread
{
	Random random_generator = new Random();
	
	public int minimumQueueLength;
	public int minimumLength;
	public int item_count;
	public int item;
	public int CustomerId;
    public static int idCounter = 0;

    public Iterator iterator;
    public boolean placed_in_queue;
	public boolean express;
	public double start_time, end_time, duration;

	public List<Integer> queue_length_list;
	public CopyOnWriteArrayList till_set;
	public CopyOnWriteArrayList express_till_set;
	public Till till, till_to_join;

	public final Object lock;

	public Stats s;

	public StackPane stackPane;
	public Rectangle rectangle;
	public Label label;

	public Customer(CopyOnWriteArrayList till_set, CopyOnWriteArrayList express_till_set, Stats s)
	{
		  queue_length_list = new ArrayList<Integer>(Animation.till_count+Animation.express_till_count);
		  lock = new Object();

		  this.CustomerId = ++idCounter;

		  this.till_set = till_set;
		  this.express_till_set = express_till_set;
		  this.placed_in_queue = false;
		  this.express = false;
		  this.s = s;

		  this.item_count = random_generator.nextInt(100);

		  stackPane = new StackPane();

		  label = new Label("Customer: "+ CustomerId+ "\nItem Count: "+item_count);
		  label.setTextFill(Color.WHITE);

		  rectangle = new Rectangle(80,50);
		  rectangle.setFill(Color.CHOCOLATE);

		  stackPane.getChildren().addAll(rectangle,label);
		  
		  this.start();
	}

	public void run()
	{
		  try
		  {
			  place_in_queue();
		  }
		  catch (InterruptedException e1)
		  {
			  // TODO Auto-generated catch block
			  e1.printStackTrace();
		  }

		  if(placed_in_queue)
		  {
			  this.start_time = System.currentTimeMillis();

			  synchronized(this.lock)
			  {
				  try
				  {
					  this.lock.wait();
				  }
				  catch (InterruptedException e)
				  {
					  e.printStackTrace();
				  }

			  }

			  this.end_time = System.currentTimeMillis();
			  this.duration = this.end_time - this.start_time;
			  s.updateAverageCustomerWaitTime(this.duration);

			  //Update Wait Time
			  Platform.runLater(new UIRunnable(s) {
				  @Override
				  public void run() {
					  Animation.average_wait_time_value.setText(""+s.getAverageCustomerWaitTime()/1000);
				  }
			  });

			  for(int x = 0; x<item_count; x++)
			  {
				  item = random_generator.nextInt(10);
				  try
				  {
					this.till_to_join.item_queue.put(this.item);
				  }
				  catch (InterruptedException e)
				  {
						  e.printStackTrace();
				  }
			  }

			  try
			  {
				  this.till_to_join.item_queue.put(-1);
			  } catch (InterruptedException e)
			  {
				  e.printStackTrace();
			  }

			  Platform.runLater(new UIRunnable(this)
			  {
				  @Override
				  public void run()
				  {
					  Animation.GROUP_ROOT.getChildren().remove(this.getCustomer().stackPane);
					  this.getCustomer().till_to_join.getTillFlowPane().getChildren().remove(this.getCustomer().stackPane);
				  }
			  });

		  }
		  else
		  {
			  s.setCustomers_lost();

			  //Update Customers Lost
			  Platform.runLater(new UIRunnable(s) {
				  @Override
				  public void run() {
					  Animation.customers_lost_value.setText(""+s.getCustomers_lost());
				  }
			  });
		  }
	}


	public void place_in_queue() throws InterruptedException
	{
		  placed_in_queue = false;

		  if(this.item_count<50)
		  {
			  findMyQueue(express_till_set);
			  this.express = true;
		  }
		  else
		  {
			  findMyQueue(till_set);
			  this.express = false;
		  }

		  if(express)
		  {
			  if(minimumLength < 8)
			  {
				  placeInQueue(express_till_set);
			  }
			  else
			  {
			  	  findMyQueue(till_set);
				  placeInQueue(till_set);
			  }
		  }
		  else
		  {
			  if(minimumLength < 6)
			  {
				  placeInQueue(till_set);
			  }
			  else
			  {
				  placed_in_queue = false;
			  }
		  }
	}
    
    public void findMyQueue(CopyOnWriteArrayList<Till> find_till_set)
    {
    	 iterator = find_till_set.iterator();
    	 queue_length_list.clear();

         while(iterator.hasNext())
         {
             till = (Till)iterator.next();
             queue_length_list.add(till.customer_queue.size());
         }
         minimumQueueLength = queue_length_list.indexOf(Collections.min(queue_length_list));
         minimumLength = queue_length_list.get(minimumQueueLength);
    }
    
    public void placeInQueue(CopyOnWriteArrayList<Till> place_till_set)
    {
    	 try 
		 {
             till_to_join = (Till)place_till_set.get(minimumQueueLength);

             till_to_join.customer_queue.put(this);

	         //entering animation
	         Platform.runLater(new UIRunnable(this){
	                @Override
	                public void run() {
	                    StackPane customerIcon = this.getCustomer().stackPane;
	                    till_to_join.getTillFlowPane().getChildren().add(customerIcon);
	                }

	            });
	         
	         
	         placed_in_queue = true;
         } 
         catch (InterruptedException e) 
         {
             // TODO Auto-generated catch block
             e.printStackTrace();
         }
    }
}