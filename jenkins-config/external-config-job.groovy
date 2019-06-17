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

// Bring some values in from ansible using the jenkins_script modules wierd "args" approach (these are not gstrings)
String jobName = (env['EXTERNAL_CONFIG_JOB_NAME']) ? env['EXTERNAL_CONFIG_JOB_NAME'] : ''
String jobScript = (env['EXTERNAL_CONFIG_JOB_SCRIPT_PATH']) ? env['EXTERNAL_CONFIG_JOB_SCRIPT_PATH'] : 'Jenkinsfile'
String gitRepo = (env['GITHUB_API_ENDPOINT']) ? env['GITHUB_API_ENDPOINT'] : 'https://github.com/default-app-endpoint/v2'
String gitRepoName = (env['EXTERNAL_CONFIG_REPO_NAME']) ? env['EXTERNAL_CONFIG_REPO_NAME'] : 'external-repo'
String credentialsId = (env['GITHUB_CREDENTIAL_ID']) ? env['GITHUB_CREDENTIAL_ID'] : 'default-github-auth'
String githubRepoOwner = (env['GITHUB_REPO_OWNER']) ? env['GITHUB_REPO_OWNER'] : 'default-owner'
String scmFilterRegex = (env['EXTERNAL_CONFIG_SCM_FILTER_REGEX']) ? env['EXTERNAL_CONFIG_SCM_FILTER_REGEX'] : '(master|development|PR-.*)'


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

gitSCMSource.traits.add(new BranchDiscoveryTrait(true, false))
gitSCMSource.traits.add(new OriginPullRequestDiscoveryTrait([ChangeRequestCheckoutStrategy.MERGE].toSet()))
gitSCMSource.traits.add(new ForkPullRequestDiscoveryTrait([ChangeRequestCheckoutStrategy.MERGE].toSet(), new TrustPermission()))
gitSCMSource.traits.add(new RegexSCMHeadFilterTrait(scmFilterRegex))

// Remove and replace?
PersistedList sources = mbp.getSourcesList()
sources.clear()
sources.add(branchSource)
