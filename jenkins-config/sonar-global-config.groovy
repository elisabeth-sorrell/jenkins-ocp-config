import jenkins.model.*
import hudson.plugins.sonar.*
import hudson.plugins.sonar.model.*

// Script to set the Sonar Server config in jenkins
// Can be used with the jenkins CLI by calling
// java -jar jenkins-cli.jar groovy sonar_global_config.groovy

def inst = Jenkins.getInstance()

def desc = inst.getDescriptor("hudson.plugins.sonar.SonarGlobalConfiguration")

def sinst = new SonarInstallation(
  "SonarQube",   // name
  "http://some-sonar-server:9000",      // serverUrl
  "someToken",   // serverAuthenticationToken
  "",  // mojoVersion
  "-Djavax.net.ssl.trustStore=/secrets/va-cacerts -Djavax.net.ssl.trustStorePassword=changeit -X -Djavax.net.debug=\"ssl,handshake\"",  // additionalProperties
  new TriggersConfig(),  // triggers
  ""  // AdditionalAnalysisProperties
)
desc.setInstallations(sinst)

desc.save()