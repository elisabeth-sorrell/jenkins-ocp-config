import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.Domain
import com.cloudbees.plugins.credentials.impl.*
import hudson.util.Secret
import java.nio.file.Files
import jenkins.model.Jenkins
import net.sf.json.JSONObject
import org.jenkinsci.plugins.plaincredentials.impl.*


def secretUsernameFilePath = "/secrets/github-ssh/username"
def secretPasswordFilePath = "/secrets/github-ssh/password"
def secretPrivateKeyFilePath = "/secrets/github-ssh/private-key.key"

def usernameFile = new File(secretUsernameFilePath)
def passwordFile = new File(secretPasswordFilePath)
def secretPrivateKeyFile = new File(secretPrivateKeyFilePath)

String username = "default"
String password = "password"
String privateKey = "---- THIS IS NOT A PRIVATE KEY -----"

if(usernameFile.exists()) {
  username = usernameFile.text
}

if (passwordFile.exists()) {
  password = passwordFile.text
}

if (secretPrivateKeyFile.exists()) {
  privateKey = secretPrivateKeyFile.text
}

def env = System.getenv()

// parameters
def jenkinsMasterKeyParameters = [
  description:  'SSH Private key for Github',
  id:           ((env['GITHUB_SSH_ID']) ? env['GITHUB_SSH_ID'] : 'github-ssh'),
  secret:       password,
  userName:     username,
  key:          new BasicSSHUserPrivateKey.DirectEntryPrivateKeySource(privateKey)
]

// get Jenkins instance
Jenkins jenkins = Jenkins.getInstance()

// get credentials domain
def domain = Domain.global()

// get credentials store
def store = jenkins.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

// define private key
def privateKeyCredential = new BasicSSHUserPrivateKey(
  CredentialsScope.GLOBAL,
  jenkinsMasterKeyParameters.id,
  jenkinsMasterKeyParameters.userName,
  jenkinsMasterKeyParameters.key,
  jenkinsMasterKeyParameters.secret,
  jenkinsMasterKeyParameters.description
)

// add credential to store
store.addCredentials(domain, privateKeyCredential)

// save to disk
jenkins.save()
