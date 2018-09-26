package org.eclipse.scava.platform.bugtrackingsystem.mantis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.eclipse.scava.platform.bugtrackingsystem.mantis.utils.MantisReaderUtils;
import org.eclipse.scava.repository.model.mantis.MantisBugTrackingSystem;
import org.eclipse.scava.platform.Date;
import org.eclipse.scava.platform.delta.bugtrackingsystem.BugTrackingSystemBug;
import org.eclipse.scava.platform.delta.bugtrackingsystem.BugTrackingSystemComment;
import org.eclipse.scava.platform.delta.bugtrackingsystem.BugTrackingSystemDelta;
import org.eclipse.scava.platform.delta.bugtrackingsystem.IBugTrackingSystemManager;
import org.eclipse.scava.repository.model.BugTrackingSystem;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MantisManager implements IBugTrackingSystemManager<MantisBugTrackingSystem> {

	public MantisManager() {

		this.mantisIssueList = new ArrayList<>();

	}

	private final static String PAGE_SIZE = "100";
	private static List<MantisIssue> mantisIssueList;

	@Override
	public boolean appliesTo(BugTrackingSystem bugTrackingSystem) {
		// TODO Auto-generated method stub
		return bugTrackingSystem instanceof MantisBugTrackingSystem;
	}

	@Override
	public BugTrackingSystemDelta getDelta(DB db, MantisBugTrackingSystem bugTrackingSystem, Date date)
			throws Exception {
		
		

		BugTrackingSystemDelta delta = new BugTrackingSystemDelta();
		delta.setBugTrackingSystem(bugTrackingSystem);
		for (MantisIssue issue : mantisIssueList) {
			if (issue.getCreationTime() == issue.getUpdated_at()){
				delta.getNewBugs().add(issue);
				
			}else if (DateUtils.isSameDay(issue.getCreationTime(), date.toJavaDate())) {
				delta.getNewBugs().add(issue);
			}
			
			if(issue.getNotes() != null){
				
				for(BugTrackingSystemComment note : issue.getNotes()){
					if (DateUtils.isSameDay(note.getCreationTime(), date.toJavaDate())){
						
											
						delta.getComments().add(note);

					}
				}
			}
			
			if (issue.getUpdated_at() != null) {
				
				if (DateUtils.isSameDay(issue.getUpdated_at(), date.toJavaDate())) {
					delta.getUpdatedBugs().add(issue);
				}
			}
			


		}
		return delta;
	}

	@Override
	public Date getFirstDate(DB db, MantisBugTrackingSystem bugTrackingSystem)
			throws IOException, NullPointerException {

		OkHttpClient mantisClient = new OkHttpClient();

		HttpUrl.Builder url = HttpUrl.parse(bugTrackingSystem.getHost()).newBuilder().addEncodedPathSegment("api")
				.addEncodedPathSegment("rest").addEncodedPathSegment("issues")
				.addEncodedQueryParameter("project_id", bugTrackingSystem.getProject_id()).addEncodedQueryParameter("pagesize", PAGE_SIZE);

		Request request = new Request.Builder().get().url(url.toString())
				.addHeader("Authorization", bugTrackingSystem.getToken()).build();

		Response response = mantisClient.newCall(request).execute();

		JsonNode jsonNode = new ObjectMapper().readTree(response.body().string()).get("issues");

		response.close();

		if (jsonNode.isArray()) {

			for (JsonNode node : jsonNode) {

				MantisIssue issue = new MantisIssue();

				issue.setBugTrackingSystem(bugTrackingSystem);
				issue.setBugId(node.get("id").toString());
				issue.setCreationTime(MantisReaderUtils.convertStringToDate(node.get("created_at").toString()));
				issue.setUpdated_at(MantisReaderUtils.convertStringToDate(node.get("updated_at").toString()));
				issue.setSummary(MantisReaderUtils.setAttribute(node, "summary"));
				issue.setStatus(MantisReaderUtils.setAttribute(node, "status", "label"));
				issue.setResolution(MantisReaderUtils.setAttribute(node, "resolution", "label"));
				issue.setPriority(MantisReaderUtils.setAttribute(node, "priority", "label"));
				issue.setCreator(MantisReaderUtils.setAttribute(node, "reporter", "name"));
				issue.setResolution(MantisReaderUtils.setAttribute(node, "resolution", "label"));
				issue.setSeverity(MantisReaderUtils.setAttribute(node, "severity", "label"));
				issue.setDescription(MantisReaderUtils.setAttribute(node, "description"));
				issue.setHandler(MantisReaderUtils.setAttribute(node, "handler", "name"));
				issue.setView_state(MantisReaderUtils.setAttribute(node, "view_state", "label"));
				issue.setTags(MantisReaderUtils.setTags(node));
				issue.setPlatform(MantisReaderUtils.setAttribute(node, "platform"));
				issue.setReproducibility(MantisReaderUtils.setAttribute(node, "reproducibility", "label"));
				issue.setOperatingSystem(MantisReaderUtils.setAttribute(node, "os"));
				issue.setOs_build(MantisReaderUtils.setAttribute(node, "os_build"));
				issue.setNotes(MantisReaderUtils.setNotes(issue, node));
				
				mantisIssueList.add(issue);

			}
		}

		Date firstDate = new Date((mantisIssueList.get(mantisIssueList.size() - 1).getCreationTime()));

		return firstDate;

	}

	@Override
	public String getContents(DB db, MantisBugTrackingSystem bugTrackingSystem, BugTrackingSystemBug bug)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContents(DB db, MantisBugTrackingSystem bugTrackingSystem, BugTrackingSystemComment comment)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	public static void main(String[] args) throws Exception {

		MantisBugTrackingSystem mantis = new MantisBugTrackingSystem();

		// Name of the project that you are focusing on
		
		mantis.setProject_id("1");

		// This token is generated in by the user in Mantis
		mantis.setToken("mGzHRDpCrXIWtjuRZPrLJIAxa1QtpnKS"); // EHU PC
		//mantis.setToken("pGN4iQMz0yBiFqG7ywj94TagTbhtuUqd"); // Laptop

		// Host (url) of the server containing the mantis instance.
		mantis.setHost("http://localhost:8888/mantis/");

		MantisManager manager = new MantisManager();

		Date today = new Date();
		Date date = new Date(manager.getFirstDate(null, mantis).toString());
		
		do {
			System.out.println("Date = " + date + " -----------------------------------------------------------");
			manager.getDelta(null, mantis, date);
			System.out.println("\t\tNEW BUGS\t" + manager.getDelta(null, mantis, date).getNewBugs());
			System.out.println("\t\tMOD BUGS\t" + manager.getDelta(null, mantis, date).getUpdatedBugs());
			System.out.println("\t\tCOMMENTS\t" + manager.getDelta(null, mantis, date).getComments());													
			date = new Date(date.addDays(1).toString());

		} while (date.compareTo(today) < 1);
		
	}

}