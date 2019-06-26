import jenkins.model.*;
import org.jenkinsci.plugins.github_branch_source.*;
import java.util.*;


def env = System.getenv()
def apiEndpoint = (env['GITHUB_API_ENDPOINT']) ? env['GITHUB_API_ENDPOINT'] : '..........'
def apiDescription = (env['GITHUB_API_ENDPOINT_DESCRIPTION']) ? env['GITHUB_API_ENDPOINT_DESCRIPTION'] : 'EPMO API Endpoint'


GitHubConfiguration gitHubConfig = GlobalConfiguration.all().get(GitHubConfiguration.class)
Endpoint gheApiEndpoint = new Endpoint(apiEndpoint, apiDescription)
List<Endpoint> endpointList = new ArrayList<Endpoint>()
endpointList.add(gheApiEndpoint)
gitHubConfig.setEndpoints(endpointList)
