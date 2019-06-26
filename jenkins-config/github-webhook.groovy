import jenkins.model.*
import hudson.util.Secret
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*
import org.jenkinsci.plugins.plaincredentials.impl.*


// Read the webhook from a secret mount
def secretFilePath = "/secrets/github-webhook/webhook"
def file = new File(secretFilePath)
String webhook = "somerandomdefaulttoken"
if(file.exists()) {
  webhook = file.text
}

def env = System.getenv()

def webhookId = (env['GITHUB_WEBHOOK_ID']) ? env['GITHUB_WEBHOOK_ID'] : 'github-webhook'
def webhookDescription = (env['GITHUB_WEBHOOK_DESCRIPTION']) ? env['GITHUB_WEBHOOK_DESCRIPTION'] : 'Token for Github webhook authentication'

// setup parameters
def jenkinsSecretTextParameters = [
  description: webhookDescription,
  id:          webhookId,
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
