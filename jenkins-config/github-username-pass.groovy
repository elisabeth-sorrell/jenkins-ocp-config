import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.Domain
import com.cloudbees.plugins.credentials.impl.*
import hudson.util.Secret
import jenkins.model.Jenkins


def secretUsernameFilePath = "/secrets/github/username"
def secretPasswordFilePath = "/secrets/github/password"

def usernameFile = new File(secretUsernameFilePath)
def passwordFile = new File(secretPasswordFilePath)

String username = "default"
String password = "password"

if(usernameFile.exists()) {
  username = usernameFile.text
}

if (passwordFile.exists()) {
  password = passwordFile.text
}

def env = System.getenv()
def githubId = ((env['GITHUB_BASIC_CRED_ID']) ? env['GITHUB_BASIC_CRED_ID'] : 'github')

// parameters
def jenkinsKeyUsernameWithPasswordParameters = [
  description:  ((env['GITHUB_BASIC_CRED_DESCRIPTION']) ? env['GITHUB_BASIC_CRED_DESCRIPTION'] : 'Username/Password for Github Authentication'),
  id:           githubId,
  secret:       password,
  userName:     username
]

// get Jenkins instance
Jenkins jenkins = Jenkins.getInstance()

// get credentials domain
def domain = Domain.global()

// get credentials store
def store = jenkins.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

// define Bitbucket secret
def jenkinsKeyUsernameWithPassword = new UsernamePasswordCredentialsImpl(
  CredentialsScope.GLOBAL,
  jenkinsKeyUsernameWithPasswordParameters.id,
  jenkinsKeyUsernameWithPasswordParameters.description,
  jenkinsKeyUsernameWithPasswordParameters.userName,
  jenkinsKeyUsernameWithPasswordParameters.secret
)

// add credential to store
store.addCredentials(domain, jenkinsKeyUsernameWithPassword)

// save to disk
jenkins.save()
