package testing;

import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.*;
import java.util.concurrent.*;

public class CustomerGenerator extends Thread
{
    public int customer_generation_rate;
    public int i;
    public int total_customer_count;

    //0 - slow
    //1 - fast

    public Random random_generator;

    public static BlockingQueue<String> item_queue = new ArrayBlockingQueue<String>(200);
    public static CopyOnWriteArrayList till_set = new CopyOnWriteArrayList();
    public static CopyOnWriteArrayList express_till_set = new CopyOnWriteArrayList();

	
	public Stats s;

    public CustomerGenerator(int customer_generation_rate, CopyOnWriteArrayList till_set, CopyOnWriteArrayList express_till_set, Stats s)
    {
        this.i = 0;
        this.till_set = till_set;
        this.express_till_set = express_till_set;
        this.customer_generation_rate = customer_generation_rate;
		this.total_customer_count = 0;
        this.s = s;
        random_generator = new Random();

    }

    public void run()
    {
        while(true)
        {
            switch(customer_generation_rate)
            {

                case 0 : try 
                     {
                        Thread.sleep(1000);
                     } 
                     catch (InterruptedException e) 
                     {
                         // TODO Auto-generated catch block
                         e.printStackTrace();
                     }
                     break;

                case 1 : try
                     {
                         Thread.sleep(500);
                     }
                     catch(InterruptedException e)
                     {
                         // TODO Auto-generated catch block
                         e.printStackTrace();
                     }
                     break;

                default : customer_generation_rate = 0;
                      break;
            }

            Customer customer = new Customer(till_set, express_till_set, s);
            
			total_customer_count++;
			i++;
			
			s.setTotal_customer_count(total_customer_count);

			//Update Customer Count
            Platform.runLater(new UIRunnable(s) {
                @Override
                public void run()
                {
                    Animation.total_customers_value.setText(""+s.getTotal_customer_count());
                }
            });
        }
    }
}