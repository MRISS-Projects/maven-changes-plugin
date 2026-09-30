# #36 — github-text-list keeps issues that have no label

Issue: [MRISS-Projects/maven-changes-plugin#36](https://github.com/MRISS-Projects/maven-changes-plugin/issues/36)
Branch: `issue-36-keep-unlabelled-issues`, cut from `DEVELOP` and merged back into it
Milestone: `2.12.10-SNAPSHOT`

## Problem

`GitHubDownloader.createIssue()` sets an issue's type from its first label and leaves it `null`
when the issue has no labels. Then each formatter fails in its own way:

- `MarkdownIssueListFormater.formatIssueList()` skips the issue:
  `if ( issue == null || issue.getType() == null ) continue;`.
- `AptIssueListFormater.formatIssueList()` keeps the issue but prints the literal `null` in the
  Type column.

## Design

**The fix goes in the two text formatters, not in the downloader.** `Issue.getType() == null`
correctly means "this issue has no type". The site's `github-report` renders that case already,
and it listed all 8 issues of parent-poms `3.9.0` in the observation. Putting a placeholder into
the downloader's model would change every renderer to fix two.

1. `MarkdownIssueListFormater`: drop the `issue.getType() == null` half of the skip. The
   `issue == null` guard stays. In `generateDetail`, the `COLUMN_TYPE` case renders a `null` type
   as `NOT_AVAILABLE` (`"n/a"`), the class's existing placeholder for Created and Updated.
2. `AptIssueListFormater`: the Type cell renders a `null` type as `n/a`. APT has no placeholder
   constant yet, so it gets its own `NOT_AVAILABLE`, spelled the same way.
3. `GitHubDownloader` does not change: first label, else `null`.

**The fallback order is first label, then `n/a`.** AC002 prefers GitHub's native issue type
(`issueType`) over the label, and says the order is negotiable. This design leaves it out:

- The client is `org.eclipse.egit.github.core` 2.1.5. Its Gson model has no issue-type field, so
  the value is dropped on deserialisation. Reading it would need a second, hand-rolled REST call
  per issue, or a custom deserialiser on a library that has been unmaintained for years.
- None of the consuming repos (DSH, parent-poms, this fork) set issue types. Their `bug`,
  `enhancement` and `task` labels are the types.

If issue types come into use later, that is a new issue, opened then.

## Acceptance-criteria mapping

| AC | Covered by |
|---|---|
| AC001: an unlabelled issue appears in both formatters | Design 1 and 2; tests T1 and T3 |
| AC002: the Type cell is meaningful, never `null` | `n/a`; tests T2 and T3 |
| AC003: a formatter test per class, red before the fix | T1–T3, run red before any production change |
| AC004: released as `2.12.10` | Not on this branch. The README's process, once `#37` and `#38` are merged on `DEVELOP` |

## Out of scope

- The Assignee column prints `null` for an unassigned issue. The issue already names this. The APT
  formatter's Priority and Resolution cells do the same, because GitHub sets neither. Both belong in
  the follow-up issue that the out-of-scope note points to.
- `AptIssueListFormater` throws an NPE on `issue.getVersion()` for an issue with no version when
  `versionSeparator` is on. It is not triggered by a missing label.

## Plan

- [x] **T1 (red).** `MarkdownIssueListFormaterTest.testIssueWithoutTypeIsListed`: two issues, one
      typed `bug` and one with type `null`. Both rows appear, the untyped one identified by its
      `[id](url)` link. Fails today: the row is missing.
- [x] **T2 (red).** `MarkdownIssueListFormaterTest.testIssueWithoutTypeShowsNotAvailable`: the
      untyped issue's row contains `| n/a |` in the Type column and no `null` cell.
- [x] **T3 (red).** `AptIssueListFormaterTest.testIssueWithoutTypeShowsNotAvailable`: an untyped
      issue, with priority and resolution set so that only Type could print `null`. The row shows
      `n/a` and does not contain `null`. Fails today: it prints `null`.
- [x] **T4.** `GitHubDownloaderTestCase`: pin the downloader's contract. No labels leave the type
      `null`, and two labels give the first as the type. This test is green before the fix and
      guards design point 3.
- [x] Run `mvn -B clean install` and confirm that T1–T3 fail for the stated reasons.
- [x] Fix `MarkdownIssueListFormater` (design 1) and `AptIssueListFormater` (design 2).
- [x] Run `mvn -B clean install`, which is what `build.yml` runs, until it is green. Check that the
      JaCoCo coverage has not dropped.
- [ ] Open a PR into `DEVELOP` that references `#36`. Once it merges, dispatch `deploy.yml` (manual
      only) from `DEVELOP` to publish `2.12.10-SNAPSHOT`, as the README's process asks ("Test `build`
      and `deploy` workflows"). parent-poms `#86` can then validate against it before the release.
