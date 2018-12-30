package testing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Animation extends Application
{
    public static int i;

    public static final int till_count = 5;
    public static final int express_till_count = 2;

    public final int col_count = 20;
    public final int row_count = 16;

    public static Stats statistics;
    public static Till till_object;

    public static CopyOnWriteArrayList<Till> till_set = new CopyOnWriteArrayList<Till>();
    public static CopyOnWriteArrayList<Till> express_till_set = new CopyOnWriteArrayList<Till>();

    public static ExecutorService till_service = Executors.newFixedThreadPool(till_count + express_till_count);

    public static GridPane GROUP_ROOT = new GridPane();

    public static StackPane stats_pane = new StackPane();
    public static StackPane till_utilization_pane = new StackPane();

    public static VBox stats_labels = new VBox();
    public static VBox till_utilization_labels_box = new VBox();

    public static Label till_count_label = new Label("Regular Tills : ");
    public static Label express_till_count_label = new Label("Express Tills : ");
    public static Label total_customers_label = new Label("Total Customers : ");
    public static Label customers_lost_label = new Label("Customers Lost : ");
    public static Label average_wait_time_label = new Label("Average Customer Waiting Time (Seconds) : ");
    public static List<Label> till_utilization_labels = new ArrayList<>(till_count+express_till_count);

    public static VBox stats_values = new VBox();
    public static VBox till_utilization_values_box = new VBox();

    public static Label till_count_value = new Label();
    public static Label express_till_count_value = new Label();
    public static Label total_customers_value = new Label();
    public static Label customers_lost_value = new Label();
    public static Label average_wait_time_value = new Label();
    public static List<Label> till_utilization_values = new ArrayList<>(till_count+express_till_count);

    public static HBox stats = new HBox();
    public static HBox utilization = new HBox();

    public Animation()
    {
        till_count_value.setMinWidth(100);
        express_till_count_value.setMinWidth(100);
        total_customers_value.setMinWidth(100);
        customers_lost_value.setMinWidth(100);
        average_wait_time_value.setMinWidth(100);

        for(i=0; i<till_count+express_till_count; i++)
        {
            Label label = new Label();
            label.setMinWidth(100);
            till_utilization_values.add(label);
        }
    }

    public static void main(String[] args)
    {
        launch(args);
    }

	public void startApplication() throws InterruptedException
	{
    	statistics  = new Stats(till_count, express_till_count);
		
		//Boolean True - Express
		//Boolean False - Regular
		
        for(i=0; i<till_count; i++)
        {
            till_object = new Till(statistics, false);
            till_utilization_labels.add(new Label("Till "+till_object.getTillId()));
            till_set.add(till_object);
        }
        
        for(i=0; i<express_till_count; i++)
        {
        	till_object = new Till(statistics, true);
            till_utilization_labels.add(new Label("Till "+till_object.getTillId()+" EXPRESS"));
        	express_till_set.add(till_object);
        }

        till_count_value.setText(""+till_count);
        express_till_count_value.setText(""+express_till_count);

        CustomerGenerator customer_generator = new CustomerGenerator(0, till_set, express_till_set, statistics);
        customer_generator.start();

        //5 seconds wait for customers to start filling up
        Thread.sleep(5000);

        for(Till t : till_set)
        {
            till_service.submit(t);
        }

        for(Till t : express_till_set)
        {
        	till_service.submit(t);
        }
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception
    {
		// TODO Auto-generated method stub

        startApplication();
		createUI(primaryStage);
	}

	private void createUI(Stage primaryStage)
    {
		// TODO Auto-generated method stub

		GROUP_ROOT.setHgap(80);
        GROUP_ROOT.setVgap(4);
        GROUP_ROOT.setPadding(new Insets(10));

        for (i = 0; i < col_count; i++)
        {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPrefWidth(10);
            GROUP_ROOT.getColumnConstraints().add(colConst);
        }
        for (i = 0; i < row_count; i++)
        {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPrefHeight(50);
            GROUP_ROOT.getRowConstraints().add(rowConst);
        }
        for (i = 0; i < till_count; i++)
        {
            till_object = till_set.get(i);
            GROUP_ROOT.add(till_object.getTillFlowPane(),
                    0, till_object.getTillId(), 20, 1);
        }

        for (i = 0; i < express_till_count; i++)
        {
            till_object = express_till_set.get(i);
            GROUP_ROOT.add(till_object.getTillFlowPane(),
                    0, till_object.getTillId(), 20, 1);
        }

        for(Label label : till_utilization_labels)
        {
            label.setMinWidth(100);
            till_utilization_pane.getChildren().add(label);
        }

        stats_labels.getChildren().addAll(
                till_count_label, express_till_count_label, total_customers_label, customers_lost_label,
                average_wait_time_label);

        stats_values.getChildren().addAll(till_count_value, express_till_count_value, total_customers_value,
                    customers_lost_value, average_wait_time_value);

        stats.getChildren().addAll(stats_labels, stats_values);

        stats_pane.getChildren().add(stats);

        GROUP_ROOT.add(stats_pane, 0,till_count+express_till_count+5, 20, 1);

        till_utilization_labels_box.getChildren().addAll(till_utilization_labels);
        till_utilization_values_box.getChildren().addAll(till_utilization_values);
        utilization.getChildren().addAll(till_utilization_labels_box, till_utilization_values_box);
        till_utilization_pane.getChildren().add(utilization);

        GROUP_ROOT.add(till_utilization_pane, 5, till_count+express_till_count+5, 20, 1);

        Scene scene1 = new Scene(GROUP_ROOT, 1200, 600);
        primaryStage.setTitle("Simulation Window");
        primaryStage.setScene(scene1);

        primaryStage.show();
		
	}

}
