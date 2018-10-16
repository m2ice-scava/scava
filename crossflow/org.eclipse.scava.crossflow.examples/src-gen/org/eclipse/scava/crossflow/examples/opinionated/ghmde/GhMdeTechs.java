package org.eclipse.scava.crossflow.examples.opinionated.ghmde;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.eclipse.scava.crossflow.runtime.Workflow;
import org.eclipse.scava.crossflow.runtime.Job;

public class GhMdeTechs {
	
	protected Destination destination;
	protected Destination pre;
	protected Destination post;
	protected Session session;
	protected Workflow workflow;
	
	public GhMdeTechs(Workflow workflow) throws Exception {
		this.workflow = workflow;
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(workflow.getBroker());
		Connection connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		destination = session.createQueue("GhRepos");
		pre = session.createQueue("FileExtensionPre");
		post = session.createQueue("FileExtensionPost");
		
		if (workflow.isMaster()) {
			MessageConsumer preConsumer = session.createConsumer(pre);
			preConsumer.setMessageListener(new MessageListener() {

				@Override
				public void onMessage(Message message) {
					try {
						
						Job job = (Job) ((ObjectMessage) message).getObject();
						if (workflow.getCache().hasCachedOutputs(job)) {
							for (Job output : workflow.getCache().getCachedOutputs(job)) {
								if (output.getDestination().equals("GhRepos")) {
									((GhMdeTechExample) workflow).getGhMdeTechs().send((GhMdeTech) output);
								}
							}
						}
						else {
							MessageProducer producer = session.createProducer(destination);
							producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
							producer.send(message);
						}
						
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				
			});
			
			MessageConsumer destinationConsumer = session.createConsumer(destination);
			destinationConsumer.setMessageListener(new MessageListener() {

				@Override
				public void onMessage(Message message) {
					try {
						ObjectMessage objectMessage = (ObjectMessage) message;
						Job job = (Job) objectMessage.getObject();
						if (!job.isCached()) {
							workflow.getCache().cache(job);
						}
						MessageProducer producer = session.createProducer(post);
						producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
						producer.send(message);
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				
			});
		}
	}
	
	public void send(GhMdeTech ghMdeTech) {
		try {
			MessageProducer producer = session.createProducer(pre);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			ObjectMessage message = session.createObjectMessage();
			ghMdeTech.setDestination("GhRepos");
			message.setObject(ghMdeTech);
			producer.send(message);
			
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void addConsumer(GhMdeTechsConsumer consumer) throws Exception {
		MessageConsumer messageConsumer = session.createConsumer(post);
		messageConsumer.setMessageListener(new MessageListener() {
	
			@Override
			public void onMessage(Message message) {
				ObjectMessage objectMessage = (ObjectMessage) message;
				try {
					Job job = (Job) objectMessage.getObject();
					consumer.consumeWords((GhMdeTech) job);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}	
		});
	}
	
}