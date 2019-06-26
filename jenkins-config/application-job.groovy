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
import com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger

def env = System.getenv()

String jobName = (env['APP_JOB_NAME']) ? env['APP_JOB_NAME'] : 'app'
String jobScript = (env['APP_JOB_SCRIPT_PATH']) ? env['APP_JOB_SCRIPT_PATH'] : 'Jenkinsfile'
String gitRepoName = (env['APP_REPO_NAME']) ? env['APP_REPO_NAME'] : 'app-repo'
String gitAPIEndpoint = (env['GITHUB_API_ENDPOINT']) ? env['GITHUB_API_ENDPOINT'] : '..........'
String credentialsId = (env['GITHUB_BASIC_CRED_ID']) ? env['GITHUB_BASIC_CRED_ID'] : 'github'
String githubRepoOwner = (env['GITHUB_REPO_OWNER']) ? env['GITHUB_REPO_OWNER'] : '..........'
String scmFilterRegex = (env['APP_SCM_FILTER_REGEX']) ? env['APP_SCM_FILTER_REGEX'] : '(master|development|PR-.*)'
String intervalScan = (env['APP_REPO_SCAN_INTERVAL']) ? env['APP_REPO_SCAN_INTERVAL'] : '15'

Jenkins jenkins = Jenkins.instance // saves some typing

// Multibranch creation
WorkflowMultiBranchProject mbp = jenkins.createProject(WorkflowMultiBranchProject.class, jobName)

// Configure the script this MBP uses
mbp.getProjectFactory().setScriptPath(jobScript)

// Add git repo
GitHubSCMSource gitSCMSource = new GitHubSCMSource(githubRepoOwner, gitRepoName)
gitSCMSource.setCredentialsId(credentialsId)
gitSCMSource.setApiUri(gitAPIEndpoint)
BranchSource branchSource = new BranchSource(gitSCMSource)

PeriodicFolderTrigger trigger = new PeriodicFolderTrigger(intervalScan)
mbp.addTrigger(trigger)

// Add all the traits for how we want to pull from the repo.
gitSCMSource.traits.add(new BranchDiscoveryTrait(true, false))
gitSCMSource.traits.add(new OriginPullRequestDiscoveryTrait([ChangeRequestCheckoutStrategy.MERGE].toSet()))
gitSCMSource.traits.add(new ForkPullRequestDiscoveryTrait([ChangeRequestCheckoutStrategy.MERGE].toSet(), new TrustPermission()))
gitSCMSource.traits.add(new RegexSCMHeadFilterTrait(scmFilterRegex))

PersistedList sources = mbp.getSourcesList()
sources.clear()
sources.add(branchSource)
