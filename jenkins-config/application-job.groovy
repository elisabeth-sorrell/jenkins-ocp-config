import hudson.util.PersistedList
import jenkins.model.*
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.ItemGroupMixIn;
import hudson.model.Items;
import jenkins.branch.*
import jenkins.plugins.git.*
import org.jenkinsci.plugins.workflow.multibranch.*
import com.cloudbees.hudson.plugins.folder.*
import org.jenkinsci.plugins.github_branch_source.*
import org.jenkinsci.plugins.github_branch_source.ForkPullRequestDiscoveryTrait.TrustPermission
import jenkins.scm.api.mixin.ChangeRequestCheckoutStrategy;
import jenkins.scm.api.SCMRevision
import jenkins.scm.api.SCMHead
import jenkins.scm.impl.trait.*

def env = System.getenv()

String jobName = (env['APP_JOB_NAME']) ? env['APP_JOB_NAME'] : 'app'
String jobScript = (env['APP_JOB_SCRIPT_PATH']) ? env['APP_JOB_SCRIPT_PATH'] : 'Jenkinsfile'
String gitRepo = (env['GITHUB_API_ENDPOINT']) ? env['GITHUB_API_ENDPOINT'] : 'https://github.com/default-app-endpoint/v2'
//String gitRepoName = (env['APP_REPO_NAME']) ? env['APP_REPO_NAME'] : 'app-repo'
String credentialsId = (env['GITHUB_CREDENTIAL_ID']) ? env['GITHUB_CREDENTIAL_ID'] : 'default-github-auth'
String githubRepoOwner = (env['GITHUB_REPO_OWNER']) ? env['GITHUB_REPO_OWNER'] : 'default-owner'
String scmFilterRegex = (env['APP_SCM_FILTER_REGEX']) ? env['APP_SCM_FILTER_REGEX'] : '(master|development|PR-.*)'

Jenkins jenkins = Jenkins.instance // saves some typing

// Multibranch creation
WorkflowMultiBranchProject mbp = jenkins.createProject(WorkflowMultiBranchProject.class, jobName)

// Configure the script this MBP uses
mbp.getProjectFactory().setScriptPath(jobScript)

// Add git repo
String id = null
String remote = gitRepo
String includes = "*"
String excludes = ""
boolean ignoreOnPushNotifications = false
GitHubSCMSource gitSCMSource = new GitHubSCMSource(githubRepoOwner, remote)
BranchSource branchSource = new BranchSource(gitSCMSource)

// Add all the traits for how we want to pull from the repo.
gitSCMSource.traits.add(new BranchDiscoveryTrait(true, false))
gitSCMSource.traits.add(new OriginPullRequestDiscoveryTrait([ChangeRequestCheckoutStrategy.MERGE].toSet()))
gitSCMSource.traits.add(new ForkPullRequestDiscoveryTrait([ChangeRequestCheckoutStrategy.MERGE].toSet(), new TrustPermission()))
gitSCMSource.traits.add(new RegexSCMHeadFilterTrait(scmFilterRegex))

PersistedList sources = mbp.getSourcesList()
sources.clear()
sources.add(branchSource)
