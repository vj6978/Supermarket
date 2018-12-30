package testing;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Till extends Thread
{
    public int item;
    private int tillId;
    public static int idCounter;
    public int till_customer_count;

    public String name;

    public boolean express_till;
    public boolean open;

    public BlockingQueue<Integer> item_queue = new ArrayBlockingQueue<Integer>(5);
    public BlockingQueue<Customer> customer_queue = new ArrayBlockingQueue<Customer>(10);

	public Random random;
    public Customer c;
    public Stats s;

    private StackPane stackPane;
    private FlowPane tillFlowPane;
    public Rectangle rectangle;
    public Label label;

    public Till(Stats s, Boolean express_till) throws InterruptedException
    {
        random = new Random();
        stackPane = new StackPane();

        this.express_till = express_till;   //Boolean : Checks if the present till is an express till or not.
        this.open = true;
        this.s = s;

        this.till_customer_count = 0;
        this.tillId = ++idCounter;

        if(express_till)
        {
            name = "Till " + this.getTillId()+" EXPRESS";
            label = new Label(name);
            s.addToUtilization(name, 0);
        }
        else
        {
            name = "Till " + this.getTillId();
            label = new Label(name);
            s.addToUtilization(name, 0);
        }
        label.setTextFill(Color.WHITE);

        rectangle = new Rectangle(80,50);
        rectangle.setFill(Color.GREY);

        stackPane.getChildren().addAll(rectangle,label);

        tillFlowPane = new FlowPane(Orientation.HORIZONTAL, 10, 5);

        Platform.runLater(new UIRunnable(this)
        {
            @Override
            public void run()
            {
                Animation.GROUP_ROOT.add(stackPane,0, getTillId());

            }
        });
    }

    public int getTillId()
    {
        return tillId;
    }
	public FlowPane getTillFlowPane()
    {
		return tillFlowPane;
	}

    public void run()
    {                   
        while(true)
        {

            try
            {
                this.till_customer_count++;
                s.updateTillUtilization(this.name);

                c = customer_queue.take();

                synchronized(c.lock)
                {
                    c.lock.notify();
                }

                do
                    {
                        this.item = this.item_queue.take();
                        Thread.sleep(500+random.nextInt(100));

                    } while(this.item != -1);


                //I'm Done! Leaving the queue now!
                Platform.runLater(new UIRunnable(this, c, s){
                    @Override
                    public void run() {

                        int position = 0;

                        Animation.GROUP_ROOT.getChildren().remove(this.getCustomer().stackPane);
                        this.getTill().getTillFlowPane().getChildren().remove(c.stackPane);

                        for(Map.Entry entry : s.till_utilization_matric.entrySet())
                        {
                            for(Label l : Animation.till_utilization_labels)
                            {
                                if(l.getText().equals(entry.getKey())) {
                                    position = Animation.till_utilization_labels.indexOf(l);
                                    break;
                                }

                            }
                            Animation.till_utilization_values.get(position).setText(""+entry.getValue()+"/"+s.getTotal_customer_count());
                        }


                    }
                });
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}