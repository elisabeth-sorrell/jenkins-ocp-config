import jenkins.model.*;
import org.jenkinsci.plugins.github_branch_source.*;
import java.util.*;


def env = System.getenv()
def apiEndpoint = "https://github.com/default-endpoint/v2"
def apiDescription = "Default Description"

if(env['GITHUB_API_ENDPOINT']) {
  apiEndpoint = env['GITHUB_API_ENDPOINT']
}

if(env['GITHUB_API_ENDPOINT_DESCRIPTION']) {
  apiDescription = env['GITHUB_API_ENDPOINT_DESCRIPTION']
}

GitHubConfiguration gitHubConfig = GlobalConfiguration.all().get(GitHubConfiguration.class)
Endpoint gheApiEndpoint = new Endpoint(apiEndpoint, apiDescription)
List<Endpoint> endpointList = new ArrayList<Endpoint>()
endpointList.add(gheApiEndpoint)
gitHubConfig.setEndpoints(endpointList)
