# #38 — github-text-list renders a closed milestone with no issues

Issue: [MRISS-Projects/maven-changes-plugin#38](https://github.com/MRISS-Projects/maven-changes-plugin/issues/38)
Branch: `issue-38-render-empty-milestones`, cut from `DEVELOP` and merged back into it
Milestone: `2.12.10-SNAPSHOT`
Builds on: #37 (PR #40). It uses `GitHubIssueComparator` and `GitHubMojo.createDownloader()`, so
`DEVELOP` is merged into this branch after #40 lands, before any code is written.

## Problem

`github-text-list` builds its `### Version <milestone>` sections from the issues it downloads. A
milestone with no issue in the list has no section, so a skipped version reads as a missing one.
DSH's `0.3.1` is the case in point: closed, deliberately empty, and absent from its README between
`0.3.2` and `0.3.0`.

## Design

1. **A new `includeEmptyMilestones` parameter** on `GitHubTextListMojo`, with property
   `changes.includeEmptyMilestones`, default `false` (AC004). With it off, nothing below runs, and
   the output is byte-for-byte what it is today. parent-poms turns it on in its `readme-generation`
   profile, as part of MRISS-Projects/parent-poms#86.
2. **`GitHubDownloader.getClosedMilestoneTitles()`** lists the titles of the repository's closed
   milestones, through egit's `MilestoneService.getMilestones( owner, repo, "closed" )`. It asks
   for closed milestones only, so open milestones are never considered (AC003). The service is
   created in `protected MilestoneService createMilestoneService()`, a seam for tests, like #37's
   `createDownloader()`.
3. **A placeholder issue per empty milestone.** `EmptyMilestoneIssue`, a new subclass of `Issue` in
   `org.apache.maven.plugins.issues`, carries the milestone title as its fix version and the status
   `closed`. A closed milestone gets one when no downloaded issue has that title as its fix version.
   - **Where it is added.** `GitHubMojo.executeReport()` calls `addEmptyMilestones( downloader,
     issueList )` straight after the download. `GitHubMojo`'s version is a no-op, so `github-report`
     is untouched. `GitHubTextListMojo` overrides it when `includeEmptyMilestones` is set.
   - **Filters apply as usual.** It runs before the `onlyCurrentVersion` filter, which then keeps or
     drops a placeholder as it does any issue of that version.
   - **Ordering needs no change.** It runs before the sort, and the placeholder has a version, so
     `GitHubIssueComparator` puts its section in version order like any other (AC002).
   - **Why a subclass and not a flag.** No `Issue` field can mark a row as "not an issue" without
     changing `Issue` for every report in the plugin. A subclass keeps the change inside the text
     list.
4. **Both formatters render the placeholder as a single row** with as many cells as the header has
   columns (AC001). Summary is `No issues`, and every other cell is `-`. That includes Id, which is
   otherwise a link. This matches the issue's example:
   `| - | - | No issues | - | - | - |`. The Markdown formatter already opens a new
   `### Version` section when the version changes, so the placeholder gets its heading and header row
   with no further change. The APT formatter prints the same row in its fixed five columns.

## Acceptance-criteria mapping

| AC | Covered by |
|---|---|
| AC001: a closed empty milestone renders one "no issues" row, keeping the column count | Design 3 and 4; tests F1, F2, M1 |
| AC002: same version order as the others | Design 3, sorted by `GitHubIssueComparator`; test M1 |
| AC003: open milestones unaffected | Design 2, closed milestones only; test D1 |
| AC004: configurable, off by default | Design 1; test M2 |

## Out of scope

- `github-report`, the site's report. The issue names `github-text-list` only, and
  `IssuesReportGenerator` has no notion of a placeholder row.
- The parent-poms change that enables the parameter. It belongs to MRISS-Projects/parent-poms#86.

## Plan

- [x] Merge `DEVELOP` into this branch once #40 is merged.
- [x] **D1 (red).** `GitHubDownloaderTestCase`: with a mocked `MilestoneService`,
      `getClosedMilestoneTitles()` returns the milestones' titles, and it asks only for state
      `closed`.
- [x] **F1 (red).** `MarkdownIssueListFormaterTest`, with the version separator on, formats an issue
      in `0.3.2`, an `EmptyMilestoneIssue` for `0.3.1`, and an issue in `0.3.0`. The `0.3.1`
      section is present, has one row `| - | - | No issues | ... |`, and that row has as many cells
      as the header.
- [x] **F2 (red).** `AptIssueListFormaterTest`: an `EmptyMilestoneIssue` renders
      `| - | No issues | - | - | - |`.
- [x] **M1 (red).** `GitHubMojoTestCase`: a text-list mojo with `includeEmptyMilestones` set gets
      closed milestones `0.3.2`, `0.3.1` and `0.3.0` from a mocked downloader, with issues in `0.3.2`
      and `0.3.0` only. `generateReport()` receives one placeholder, for `0.3.1`, between them.
- [x] **M2 (red).** The same mojo with the parameter off receives the downloaded issues only, and the
      downloader is never asked for milestones.
- [x] Run `mvn -B clean install`. The new tests fail because `EmptyMilestoneIssue`,
      `getClosedMilestoneTitles()` and `addEmptyMilestones()` do not exist yet. Check that those
      are the only compile errors.
- [x] Implement design 1–4.
- [x] Run `mvn -B clean install` until it is green, with instruction coverage not below #37's
      38.82%.
- [ ] Open a PR into `DEVELOP` that references `#38`. Once it merges, dispatch `deploy.yml` from
      `DEVELOP`. `2.12.10` is then complete, and the release follows the README's process.
