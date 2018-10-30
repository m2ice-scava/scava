package org.eclipse.scava.crossflow.tests.csvsourcesinkcase;

import java.util.LinkedList;
import java.util.Collection;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import org.apache.activemq.broker.BrokerService;
import org.eclipse.scava.crossflow.runtime.Workflow;
import org.eclipse.scava.crossflow.runtime.Cache;
import org.eclipse.scava.crossflow.runtime.Mode;
import org.eclipse.scava.crossflow.runtime.utils.TaskStatus;



public class BaseCase extends Workflow {
	
	public static void main(String[] args) throws Exception {
		BaseCase app = new BaseCase();
		new JCommander(app, args);
		app.run();
	}
	
	
	// streams
	protected Additions additions;
	protected AdditionResults additionResults;
	protected EclipseResultPublisher eclipseResultPublisher;
	protected EclipseTaskStatusPublisher eclipseTaskStatusPublisher;
	
	private boolean createBroker = true;
	
	// tasks
	protected NumberPairCsvSource numberPairCsvSource;
	protected Adder adder;
	protected PrinterCsvSink printerCsvSink;
	
	// excluded tasks from workers
	protected Collection<String> tasksToExclude = new LinkedList<String>();
	
	public void excludeTasks(Collection<String> tasks){
		tasksToExclude = tasks;
	}
	
	public BaseCase() {
		this.name = "BaseCase";
	}
	
	public void createBroker(boolean createBroker) {
		this.createBroker = createBroker;
	}
	
	public void run() throws Exception {
	
		if (isMaster()) {
			cache = new Cache(this);
			if (createBroker) {
				BrokerService broker = new BrokerService();
				broker.setUseJmx(true);
				broker.addConnector(getBroker());
				broker.start();
			}
		}

		eclipseResultPublisher = new EclipseResultPublisher(this);
		eclipseTaskStatusPublisher = new EclipseTaskStatusPublisher(this);
		
//TODO test of task status until it is integrated to ui
//		eclipseTaskStatusPublisher.addConsumer(new EclipseTaskStatusPublisherConsumer() {
//			@Override
//			public void consumeEclipseTaskStatusPublisher(TaskStatus status) {
//				System.err.println(status.getCaller()+" : "+status.getStatus()+" : "+status.getReason());
//			}
//		});
//
		
		additions = new Additions(this);
		additionResults = new AdditionResults(this);
		
		
	
				
		numberPairCsvSource = new NumberPairCsvSource();
		numberPairCsvSource.setWorkflow(this);
		
		numberPairCsvSource.setAdditions(additions);
		
				
		
		if (!getMode().equals(Mode.MASTER_BARE) && !tasksToExclude.contains("Adder")) {
	
				
		adder = new Adder();
		adder.setWorkflow(this);
		
			additions.addConsumer(adder, Adder.class.getName());			
	
		adder.setAdditionResults(additionResults);
		}
		else if(isMaster()){
			additions.addConsumer(adder, Adder.class.getName());			
		}
		
				
		
	
		if (isMaster()) {
				
		printerCsvSink = new PrinterCsvSink();
		printerCsvSink.setWorkflow(this);
		}
		
			additionResults.addConsumer(printerCsvSink, PrinterCsvSink.class.getName());			
		if(adder!=null)		
			adder.setEclipseResultPublisher(eclipseResultPublisher);
	
		
				
		
		if (isMaster()){
			numberPairCsvSource.produce();
		}
	}
	
	public Additions getAdditions() {
		return additions;
	}
	public AdditionResults getAdditionResults() {
		return additionResults;
	}
	
	public NumberPairCsvSource getNumberPairCsvSource() {
		return numberPairCsvSource;
	}
	public Adder getAdder() {
		return adder;
	}
	public PrinterCsvSink getPrinterCsvSink() {
		return printerCsvSink;
	}
	
	public void setTaskInProgess(Object caller) {
		eclipseTaskStatusPublisher.send(new TaskStatus(TaskStatuses.INPROGRESS, caller.getClass().getName(), ""));
	}

	public void setTaskWaiting(Object caller) {
		eclipseTaskStatusPublisher.send(new TaskStatus(TaskStatuses.WAITING, caller.getClass().getName(), ""));
	}

	public void setTaskBlocked(Object caller, String reason) {
		eclipseTaskStatusPublisher.send(new TaskStatus(TaskStatuses.BLOCKED, caller.getClass().getName(), reason));
	}

	public void setTaskUnblocked(Object caller) {
		eclipseTaskStatusPublisher.send(new TaskStatus(TaskStatuses.INPROGRESS, caller.getClass().getName(), ""));
	}

}