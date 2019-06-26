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

String jobName = (env['EXTERNAL_CONFIG_JOB_NAME']) ? env['EXTERNAL_CONFIG_JOB_NAME'] : '..........'
String jobScript = (env['EXTERNAL_CONFIG_JOB_SCRIPT_PATH']) ? env['EXTERNAL_CONFIG_JOB_SCRIPT_PATH'] : 'Jenkinsfile'
String gitRepoName = (env['EXTERNAL_CONFIG_REPO_NAME']) ? env['EXTERNAL_CONFIG_REPO_NAME'] : '..........'
String gitAPIEndpoint = (env['GITHUB_API_ENDPOINT']) ? env['GITHUB_API_ENDPOINT'] : '..........'
String credentialsId = (env['GITHUB_BASIC_CRED_ID']) ? env['GITHUB_BASIC_CRED_ID'] : 'github'
String githubRepoOwner = (env['GITHUB_REPO_OWNER']) ? env['GITHUB_REPO_OWNER'] : '..........'
String scmFilterRegex = (env['EXTERNAL_CONFIG_SCM_FILTER_REGEX']) ? env['EXTERNAL_CONFIG_SCM_FILTER_REGEX'] : 'master'


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

gitSCMSource.traits.add(new BranchDiscoveryTrait(true, false))
gitSCMSource.traits.add(new OriginPullRequestDiscoveryTrait([ChangeRequestCheckoutStrategy.MERGE].toSet()))
gitSCMSource.traits.add(new ForkPullRequestDiscoveryTrait([ChangeRequestCheckoutStrategy.MERGE].toSet(), new TrustPermission()))
gitSCMSource.traits.add(new RegexSCMHeadFilterTrait(scmFilterRegex))

// Remove and replace?
PersistedList sources = mbp.getSourcesList()
sources.clear()
sources.add(branchSource)
