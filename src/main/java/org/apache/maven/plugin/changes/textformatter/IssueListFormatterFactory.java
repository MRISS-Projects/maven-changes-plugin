package org.apache.maven.plugin.changes.textformatter;

public class IssueListFormatterFactory {

	private static IssueListFormatterFactory INSTANCE;

	private IssueListFormatterFactory() {

	}

	public static IssueListFormatterFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new IssueListFormatterFactory();
		}
		return INSTANCE;
	}

	public IssueListFormater getIssueListFormatter(String formatterId, boolean versionSeparator,
			String issueManagementUrl, String ticketSuffix) {
		if (formatterId.equals("markdown")) {
			return new MarkdownIssueListFormater(versionSeparator, issueManagementUrl, ticketSuffix);
		} else if (formatterId.equals("apt")) {
			return new AptIssueListFormater(versionSeparator);
		} else {
			throw new IllegalArgumentException("Illegal issue formatter id: " + formatterId);
		}
	}

}
