import jenkins.model.*
import hudson.plugins.sonar.*
import hudson.plugins.sonar.model.*

// Script to set the Sonar Server config in jenkins
// Can be used with the jenkins CLI by calling
// java -jar jenkins-cli.jar groovy sonar_global_config.groovy


def sonarQubeName = "SonarQube"
def sonarqubeUrl = "http://some-sonar-server:9000"
def sonarToken = "defaultToken"
def sonarlAdditionalProperties = ""

def env = System.getenv()

if(env['SONARQUBE_NAME']) {
  sonarQubeName = env['SONARQUBE_NAME']
}

if(env['SONARQUBE_URL']) {
  sonarqubeUrl = env['SONARQUBE_URL']
}

if(env['SONARQUBE_TOKEN']){
  sonarToken = env['SONARQUBE_TOKEN']
}

if(env['SONARQUBE_ADDITIONAL_PROPERTIES']) {
  sonarlAdditionalProperties = env['SONARQUBE_ADDITIONAL_PROPERTIES']
}

def inst = Jenkins.getInstance()

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
