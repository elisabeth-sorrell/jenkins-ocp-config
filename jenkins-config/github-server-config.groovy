
import jenkins.model.Jenkins
import org.jenkinsci.plugins.github.config.*

def env = System.getenv()

// get Jenkins instance
Jenkins jenkins = Jenkins.getInstance()

// get Git plugin
jenkinsGitConfig = jenkins.getDescriptor("github-plugin-configuration")

GitHubServerConfig config = new GitHubServerConfig((env['GITHUB_WEBHOOK_ID']) ? env['GITHUB_WEBHOOK_ID'] : 'github-webhook')

config.setName(((env['GITHUB_SERVER_CONFIG_NAME']) ? env['GITHUB_SERVER_CONFIG_NAME'] : 'default'))
config.setApiUrl(((env['GITHUB_API_URL']) ? env['GITHUB_API_URL'] : 'https://api.github.com/v3'))
config.setManageHooks(true)

jenkinsGitConfig.setConfigs([config])
jenkinsGitConfig.save()


// parameters
def gitParameters = [
  globalConfigEmail:  ((env['GITHUB_GLOBAL_EMAIL']) ? env['GITHUB_GLOBAL_EMAIL'] : 'default'),
  globalConfigName:   ((env['GITHUB_GLOBAL_CONFIG_NAME']) ? env['GITHUB_GLOBAL_CONFIG_NAME'] : 'default.name')
]

// get Git plugin
jenkinsGitConfig = jenkins.getDescriptor("hudson.plugins.git.GitSCM")

// set Git plugin parameters
jenkinsGitConfig.setGlobalConfigName(gitParameters.globalConfigName)
jenkinsGitConfig.setGlobalConfigEmail(gitParameters.globalConfigEmail)

// save current Jenkins state to disk
jenkinsGitConfig.save()
jenkins.save()
