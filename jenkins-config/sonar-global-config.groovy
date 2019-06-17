import jenkins.model.*
import hudson.plugins.sonar.*
import hudson.plugins.sonar.model.*

def env = System.getenv()
def inst = Jenkins.getInstance()

def sonarQubeName = ((env['SONARQUBE_NAME']) ? env['SONARQUBE_NAME'] : 'SonarQube')
def sonarqubeUrl = ((env['SONARQUBE_URL']) ? env['SONARQUBE_URL'] : 'https://sonarqube.dev.bip.va.gov')
def sonarToken = ((env['SONARQUBE_TOKEN']) ? env['SONARQUBE_TOKEN'] : 'somerandomdefaulttoken')

// TODO: get the vacerts path stuff configured here.
def sonarlAdditionalProperties = ((env['SONARQUBE_ADDITIONAL_PROPERTIES']) ? env['SONARQUBE_ADDITIONAL_PROPERTIES'] : '')
def desc = inst.getDescriptor("hudson.plugins.sonar.SonarGlobalConfiguration")

def sinst = new SonarInstallation(
  sonarQubeName,
  sonarqubeUrl,
  sonarToken,
  "",  // mojoVersion
  sonarlAdditionalProperties,
  new TriggersConfig(),  // triggers
  ""
)
desc.setInstallations(sinst)

desc.save()
