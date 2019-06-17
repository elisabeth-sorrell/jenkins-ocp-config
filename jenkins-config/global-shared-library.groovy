import hudson.scm.SCM
import jenkins.model.Jenkins
import jenkins.plugins.git.GitSCMSource
import org.jenkinsci.plugins.workflow.libs.*
import org.jenkinsci.plugins.workflow.libs.LibraryConfiguration
import org.jenkinsci.plugins.workflow.libs.SCMSourceRetriever


def env = System.getenv()

def githubCred = ((env['GITHUB_BASIC_CRED_ID']) ? env['GITHUB_BASIC_CRED_ID'] : 'github')
def branch = ((env['GLOBAL_CONFIG_BRANCH']) ? env['GLOBAL_CONFIG_BRANCH'] : 'master')
def globalLibraryName = ((env['GLOBAL_LIBRARY_NAME']) ? env['GLOBAL_LIBRARY_NAME'] : 'bip-jenkins-lib')
def globalLibraryRepo = ((env['GLOBAL_LIBRARY_REPO']) ? env['GLOBAL_LIBRARY_REPO'] : 'https://github.ec.va.gov/EPMO/bip-jenkins-lib.git')

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
