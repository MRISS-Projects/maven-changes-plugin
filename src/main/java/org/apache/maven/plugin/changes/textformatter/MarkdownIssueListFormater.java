package org.apache.maven.plugin.changes.textformatter;

import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.issues.Issue;

public class MarkdownIssueListFormater implements IssueListFormater {
	
	private final static String COLUMN_SEPARATOR = "|";
	
	private final static String MD_LINE_PREFIX = "";
	
	private final static String MD_COLUMN_SPACE = " ";
	
	private final static String HEADER = MD_LINE_PREFIX + "| Ticket Id | Summary | Type | Priority | Resolution |\n"
			                                            + "| --------- | ------- | ---- | -------- | ---------- |\n";
	private boolean versionSeparator;

	private String issueManagementUrl;

	private String ticketSuffix;	

	public MarkdownIssueListFormater(boolean versionSeparator, String issueManagementUrl, String ticketSuffix) {
		this.versionSeparator = versionSeparator;
		if (issueManagementUrl.endsWith("/")) {
			this.issueManagementUrl = issueManagementUrl;
		} else {
			this.issueManagementUrl = issueManagementUrl + "/";
		}
		this.ticketSuffix = ticketSuffix;
	}

	@Override
	public String formatIssueList(List<Issue> issueList) {
		String result = "";
		if (!versionSeparator) result = HEADER;		
		String version = "";
		for (Iterator<Issue> iterator = issueList.iterator(); iterator.hasNext();) {
			Issue issue = (Issue) iterator.next();
			if (!issue.getVersion().equals(version) && versionSeparator) {
				if (!version.isEmpty()) result += "\n";
				version = issue.getVersion();
				result += ("### Version " + issue.getVersion() + "\n\n");
				result += (HEADER);
			}
			result += (MD_LINE_PREFIX + COLUMN_SEPARATOR);
			result += (MD_COLUMN_SPACE + "["+ issue.getId() +"](" + this.issueManagementUrl + (this.ticketSuffix.isEmpty() ? "" : this.ticketSuffix + "/") + issue.getId() + ")" + MD_COLUMN_SPACE + COLUMN_SEPARATOR);
			result += (MD_COLUMN_SPACE + issue.getSummary() + MD_COLUMN_SPACE + COLUMN_SEPARATOR);
			result += (MD_COLUMN_SPACE + issue.getType() + MD_COLUMN_SPACE + COLUMN_SEPARATOR);
			if (issue.getPriority() != null)
				result += (MD_COLUMN_SPACE + issue.getPriority() + MD_COLUMN_SPACE + COLUMN_SEPARATOR);
			else
				result += (MD_COLUMN_SPACE + "N/A" + MD_COLUMN_SPACE + COLUMN_SEPARATOR);
			if (issue.getResolution() != null)
				result += (MD_COLUMN_SPACE + issue.getResolution() + MD_COLUMN_SPACE + COLUMN_SEPARATOR);
			else
				result += (MD_COLUMN_SPACE + "N/A" + MD_COLUMN_SPACE + COLUMN_SEPARATOR);
			result += "\n";
		}
		return result;
	}

}
