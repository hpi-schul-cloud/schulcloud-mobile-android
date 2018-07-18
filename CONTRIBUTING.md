# Contributing

Please feel free to help us with the ongoing development of this app. See open [issues] to get some inspiration and open a Pull Request as soon as you are ready to go.

Some issues are tagged with [🐶beginner friendly](https://ticketsystem.schul-cloud.org/browse/AN-1?jql=project%20%3D%20AN%20AND%20resolution%20%3D%20Unresolved%20AND%20Difficulty%20%3D%20%22%F0%9F%90%B6beginner%20friendly%22%20ORDER%20BY%20priority%20DESC%2C%20updated%20DESC) to get you started!

- [Contributing](#contributing)
  - [1. Clone the project](#1-clone-the-project)
  - [2. How to name your branch](#2-how-to-name-your-branch)
  - [3. Getting started](#3-getting-started)
  - [4. Creating a pull request](#4-creating-a-pull-request)


## 1. Clone the project

Clone the git repository:

```git
git clone --recursive https://github.com/schul-cloud/schulcloud-mobile-android
```


## 2. How to name your branch

You first need to start a new branch. As a base you can either **choose the `dev` branch**, or another issue-branch that was recently developed but not yet merged. You should normally choose the former, except there is a super cool new feature which you want to improve. However, don't base your branch on master, as it contains the latest stable release and might be outdated.

To name your branch, take the number of the Jira-issue and append the issue name in lowercase, replacing spaces with dashes.

**Example:** Issue [AN-2](https://ticketsystem.schul-cloud.org/browse/AN-2), "Add styleguide for coding" becomes branch _2-add-styleguide-for-coding_


## 3. Getting started

We have documented the underlaying architecture of our app in the [architecture] file. It also contains code snippets for common features, such as embedding a `SwipeRefreshLayout` with only three lines of code or showing a progress dialog with just one line.

When commiting, please use [conventional commits]. This makes it much easier to get an overview of what you did.


## 4. Creating a pull request

Before you create a pull request, please enter a short description of what changed in our [changelog] (*Unreleased* section).

For writing the PR message and details on what to include, please use our [Pull Request template][pr-template].


[android studio]: https://developer.android.com/studio/
[conventional commits]: https://conventionalcommits.org/
[architecture]: ./architecture.md
[changelog]: ./CHANGELOG.md
[issues]: https://ticketsystem.schul-cloud.org/projects/AN/issues
[pr-template]: ./.github/PULL_REQUEST_TEMPLATE.md
