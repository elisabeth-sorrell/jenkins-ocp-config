import hudson.scm.SCM
import jenkins.model.Jenkins
import jenkins.plugins.git.GitSCMSource
import org.jenkinsci.plugins.workflow.libs.*
import org.jenkinsci.plugins.workflow.libs.LibraryConfiguration
import org.jenkinsci.plugins.workflow.libs.SCMSourceRetriever


def env = System.getenv()

def githubCred = 'default-github-cred-id'
if(env['GITHUB_CREDENTIAL_ID']) {
  githubCred = env['GITHUB_CREDENTIAL_ID']
}

def branch = "master"
if(env['GLOBAL_CONFIG_BRANCH']) {
  branch = env['GLOBAL_CONFIG_BRANCH']
}

def globalLibraryName = "global-jenkins-lib"
if(env['GLOBAL_LIBRARY_NAME']) {
  globalLibraryName = env['GLOBAL_LIBRARY_NAME']
}

def globalLibraryRepo = "https://github.com/some-default-repo-that-dont-exist.git"
if(env['GLOBAL_LIBRARY_REPO']) {
  globalLibraryRepo = env['GLOBAL_LIBRARY_REPO']
}

// parameters
def globalLibrariesParameters = [
  branch:               branch,
  credentialId:         githubCred,
  implicit:             false,
  name:                 globalLibraryName,
  repository:           globalLibraryRepo
]

// define global library
GitSCMSource gitSCMSource = new GitSCMSource(
  "global-shared-library",
  globalLibrariesParameters.repository,
  globalLibrariesParameters.credentialId,
  "*",
  "",
  false
)

// define retriever
SCMSourceRetriever sCMSourceRetriever = new SCMSourceRetriever(gitSCMSource)

// get Jenkins instance
Jenkins jenkins = Jenkins.getInstance()

// get Jenkins Global Libraries
def globalLibraries = jenkins.getDescriptor("org.jenkinsci.plugins.workflow.libs.GlobalLibraries")

// define new library configuration
LibraryConfiguration libraryConfiguration = new LibraryConfiguration(globalLibrariesParameters.name, sCMSourceRetriever)
libraryConfiguration.setDefaultVersion(globalLibrariesParameters.branch)
libraryConfiguration.setImplicit(globalLibrariesParameters.implicit)

// set new Jenkins Global Library
globalLibraries.get().setLibraries([libraryConfiguration])

// save current Jenkins state to disk
jenkins.save()
