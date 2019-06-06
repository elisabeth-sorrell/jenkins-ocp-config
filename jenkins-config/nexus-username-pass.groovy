import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.Domain
import com.cloudbees.plugins.credentials.impl.*
import hudson.util.Secret
import jenkins.model.Jenkins


def secretUsernameFilePath = "/secrets/nexus/username"
def secretPasswordFilePath = "/secrets/nexus/password"

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



// parameters
def jenkinsKeyUsernameWithPasswordParameters = [
  description:  'Nexus username and password authentication',
  id:           'nexus-auth',
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
