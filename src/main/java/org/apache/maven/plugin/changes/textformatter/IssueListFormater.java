package org.apache.maven.plugin.changes.textformatter;

import java.util.List;

import org.apache.maven.plugin.issues.Issue;

public interface IssueListFormater {
	
	public String formatIssueList(List<Issue> issueList);

}
