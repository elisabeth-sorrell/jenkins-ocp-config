import jenkins.model.*
import hudson.util.Secret
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*
import org.jenkinsci.plugins.plaincredentials.impl.*


// Read the webhook from a secret mount
def secretFilePath = "/secrets/github/webhook"
def file = new File(secretFilePath)
String webhook = "https://github.com/default-webhook"
if(file.exists()) {
  webhook = file.text
}


// setup parameters
def jenkinsSecretTextParameters = [
  description: 'Webhook for Github',
  id:          'github-webhook',
  secret:      webhook
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
