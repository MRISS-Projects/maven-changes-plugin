# #37 — github-report orders issues by version, then by update date

Issue: [MRISS-Projects/maven-changes-plugin#37](https://github.com/MRISS-Projects/maven-changes-plugin/issues/37)
Branch: `issue-37-order-github-report`, cut from `DEVELOP` and merged back into it
Milestone: `2.12.10-SNAPSHOT`

## Problem

`github-report` lists issues in the order the GitHub API returns them. `GitHubMojo.executeReport()`
hands the downloaded list straight to `generateReport()`, and no step sorts it. `github-text-list`
does sort, with `GitHubTextListMojo.IssueComparator`, in its own `generateReport()` override. So the
two goals render the same data in different orders.

That comparator is also wrong on its own terms. `getVersion()` deletes the dots and parses an `int`,
so two versions go wrong:

- `3.10.0` becomes 3100 and `3.9.10` becomes 3910, so 3.10.0 sorts below 3.9.10.
- A non-numeric milestone title, such as `Backlog`, throws `NumberFormatException`.

On top of that, an issue with a `null` update date throws an NPE in the tie-break.

## Design

1. **`GitHubIssueComparator`, a new top-level class in `org.apache.maven.plugins.github`.** It
   replaces the inner `GitHubTextListMojo.IssueComparator`, which is deleted (AC002).
   - **Version key:** `Issue.getVersion()`, else the first fix version. This is the existing
     lookup, unchanged. A blank or missing version means no version.
   - **Version order:** the `-SNAPSHOT` suffix is stripped first, as today. Open milestones are
     titled `X.Y.Z-SNAPSHOT` and are renamed `X.Y.Z` when released, so both spellings group as one
     version. The rest is compared with `org.apache.maven.artifact.versioning.ComparableVersion`,
     descending. `maven-artifact` is already a dependency (AC003).
   - **Missing version:** sorts after every issue that has one (AC001).
   - **Tie-break:** the update date, descending. A `null` update date sorts last within its version,
     where today it throws.
   - Issues that compare equal keep the API's order, because `Collections.sort` is stable.
2. **Sorting moves into `GitHubMojo.executeReport()`**, after the `onlyCurrentVersion` filter and
   before `generateReport()`. `GitHubTextListMojo` extends `GitHubMojo`, so both goals get one sort
   in one place. The `Collections.sort` call in `GitHubTextListMojo.generateReport()` is removed.
3. **A seam for the test.** `executeReport()` builds its `GitHubDownloader` inline, and the
   existing mojo tests call real GitHub. The construction moves into
   `protected GitHubDownloader createDownloader()`. A test subclass then returns a Mockito mock with
   a fixed, unsorted list, and captures the list `generateReport()` receives. Mockito is already in
   use in `GitHubDownloaderTestCase`.
4. **No `sortOrder` parameter.** The issue lists it as optional, and no consumer needs another
   order. It can come later as its own issue.

## Acceptance-criteria mapping

| AC | Covered by |
|---|---|
| AC001: `github-report` orders by version, then update date, both descending; no version last | Design 1 and 2; tests C1–C4 and M1 |
| AC002: one comparator serves both goals | Design 1 and 2; test M2 |
| AC003: versions compare as versions; no `NumberFormatException` | `ComparableVersion`; tests C1 and C4 |
| AC004: tests for `3.10.0` vs `3.9.x`, same version by date, missing version, non-numeric title | C1, C2, C3, C4 |
| AC005: released in `2.12.10` with #36 | Not on this branch. The README's process, after `#38` |

## Plan

- [x] **C1 (red).** `GitHubIssueComparatorTest`: `3.10.0` sorts before `3.9.10` and `3.9.2`.
      `3.9.0-SNAPSHOT` and `3.9.0` compare equal on version.
- [x] **C2 (red).** For the same version, the later update date comes first.
- [x] **C3 (red).** An issue with no version sorts after every versioned issue. So does an issue
      whose only fix version is blank.
- [x] **C4 (red).** A non-numeric title (`Backlog`) sorts without throwing, and ahead of the
      no-version issues.
- [x] **C5 (red).** A `null` update date sorts last within its version, and does not throw.
- [x] **M1 (red).** `GitHubMojoTestCase`: a `GitHubMojo` subclass with a mocked downloader returns an
      unsorted list, and the list `generateReport()` receives is in the order above.
- [x] **M2 (red).** The same test for a `GitHubTextListMojo` subclass: it receives the same order
      from the shared sort.
- [x] Run `mvn -B clean install`. C1–C5 fail because `GitHubIssueComparator` does not compile yet.
      That is the red for a class that does not exist, so check it is the only compile error. M1
      fails because nothing sorts.
- [x] Add `GitHubIssueComparator`, delete the inner comparator, sort in `executeReport()`, and
      extract `createDownloader()`.
- [x] Run `mvn -B clean install` until it is green, with instruction coverage no lower than 38%.
- [x] Tick #36's last plan step in `specs/36-keep-unlabelled-issues.md`, which the #36 PR merged
      unticked.
- [ ] Open a PR into `DEVELOP` that references `#37`. Once it merges, dispatch `deploy.yml` from
      `DEVELOP` to refresh `2.12.10-SNAPSHOT`.
