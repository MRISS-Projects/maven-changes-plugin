package org.apache.maven.plugin.changes.textformatter;

import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.issues.Issue;

public class AptIssueListFormater implements IssueListFormater {
	
	private final static String COLUMN_SEPARATOR = "|";
	
	private final static String APT_LINE_PREFIX = "";
	
	private final static String APT_COLUMN_SPACE = " ";
	
	private final static String LINE   = APT_LINE_PREFIX + "*------------+----------+-------+-----------+--------------+\n";
	private final static String HEADER = APT_LINE_PREFIX + "|| Ticket Id || Summary || Type || Priority || Resolution ||\n";

	private boolean versionSeparator;

	public AptIssueListFormater(boolean versionSeparator) {
		this.versionSeparator = versionSeparator;
	}
	
	@Override
	public String formatIssueList(List<Issue> issueList) {
		String result = "";
		if (!versionSeparator) result = LINE + HEADER + LINE;		
		String version = "";
		for (Iterator<Issue> iterator = issueList.iterator(); iterator.hasNext();) {
			Issue issue = (Issue) iterator.next();
			if (!issue.getVersion().equals(version) && versionSeparator) {
				version = issue.getVersion();
				result += ("\n Version " + issue.getVersion() + "\n\n");
				result += (LINE + HEADER + LINE);
			}
			result += (APT_LINE_PREFIX + COLUMN_SEPARATOR);
			result += (APT_COLUMN_SPACE + issue.getId() + APT_COLUMN_SPACE + COLUMN_SEPARATOR);
			result += (APT_COLUMN_SPACE + issue.getSummary() + APT_COLUMN_SPACE + COLUMN_SEPARATOR);
			result += (APT_COLUMN_SPACE + issue.getType() + APT_COLUMN_SPACE + COLUMN_SEPARATOR);
			result += (APT_COLUMN_SPACE + issue.getPriority() + APT_COLUMN_SPACE + COLUMN_SEPARATOR);
			result += (APT_COLUMN_SPACE + issue.getResolution() + APT_COLUMN_SPACE + COLUMN_SEPARATOR + "\n");
			result += LINE;
		}
		return result;
	}

}
