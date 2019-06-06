import jenkins.model.*
import hudson.util.Secret
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*
import org.jenkinsci.plugins.plaincredentials.impl.*

// Read the token from a secret mount
def secretTokenFilePath = "/secrets/slack/token"
def tokenFile = new File(secretTokenFilePath)
String token = "default"
if(tokenFile.exists()) {
  token = tokenFile.text
}

// setup parameters

def jenkinsSecretTextParameters = [
  description: 'Token for Slack Authentication',
  id:          'slack-token',
  secret:      token
]


// get Jenkins instance
Jenkins jenkins = Jenkins.getInstance()

domain = Domain.global()
store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

secretText = new StringCredentialsImpl(
CredentialsScope.GLOBAL,
jenkinsSecretTextParameters.id,
jenkinsSecretTextParameters.description,
Secret.fromString(jenkinsSecretTextParameters.secret))

// add credentials to store
store.addCredentials(domain, secretText)


// save changes
jenkins.save()
