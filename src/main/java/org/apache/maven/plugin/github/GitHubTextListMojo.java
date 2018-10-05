package org.apache.maven.plugin.github;

import java.util.List;
import java.util.Locale;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.changes.textformatter.IssueListFormater;
import org.apache.maven.plugin.changes.textformatter.IssueListFormatterFactory;
import org.apache.maven.plugin.issues.Issue;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.MavenReportException;

/**
 * Mojo to format a list of issues from github in a text format. It will
 * generate a apt or markdown text formatting style which can be set to a
 * property. That property can then be inserted in another markdown or apt text
 * file.
 * 
 * @author riss
 * @since 2.12.3
 *
 */
@Mojo(name = "github-text-list")
public class GitHubTextListMojo extends GitHubMojo {

	/***
	 * The text formatter to be used to format issues. Accepted values are
	 * <code>apt</code> or <code>markdown</code>.
	 */
	@Parameter(property = "changes.text.list.formater", defaultValue = "markdown")
	private String textListFormater;

	/***
	 * The name of the property to be set with the formatted result of issues,
	 * following the format defined at <code>textListFormater</code>.
	 */
	@Parameter(property = "changes.issue.list.propert.name", defaultValue = "issues.text.list")
	private String issueListPropertyName;

	@Override
	protected void generateReport(Locale locale, List<Integer> columnIds, List<Issue> issueList) {
		IssueListFormater issueFormatter = IssueListFormatterFactory.getInstance()
				.getIssueListFormatter(textListFormater, true, project.getIssueManagement().getUrl(), "");
		project.getProperties().put(issueListPropertyName, issueFormatter.formatIssueList(issueList));
	}

	public MavenProject getMavenProject() {
		return project;
	}

	@Override
	public void execute() throws MojoExecutionException {
        if ( !canGenerateReport() )
        {
            return;
        }
        Locale locale = Locale.getDefault();
        try {
			executeReport(locale);
		} catch (MavenReportException e) {
            throw new MojoExecutionException( "An error has occurred in " + getName( Locale.ENGLISH )
            	+ " report generation.", e );
		}
	}

}
