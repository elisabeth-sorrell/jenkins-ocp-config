
import org.csanchez.jenkins.plugins.kubernetes.*
import jenkins.model.*
import hudson.model.Node
import org.csanchez.jenkins.plugins.kubernetes.volumes.*

def jenkins = Jenkins.getInstance()
def env = System.getenv()

println(jenkins.clouds)

if (jenkins.clouds) {

    def kcloud = jenkins.clouds[0]

    def podTemplate = new PodTemplate()

    podTemplate.setName((env['GIT2CONSUL_POD_NAME']) ? env['GIT2CONSUL_POD_NAME'] : 'git2consul')
    podTemplate.setLabel((env['GIT2CONSUL_POD_LABEL']) ? env['GIT2CONSUL_POD_LABEL'] : 'git2consul')
    podTemplate.setNodeUsageMode(Node.Mode.EXCLUSIVE)

    SecretVolume consulToken = new SecretVolume(((env['CONSUL_TOKEN_MOUNT_PATH']) ? env['CONSUL_TOKEN_MOUNT_PATH'] : '..........'),
                                                ((env['CONSUL_TOKEN_SECRET_NAME']) ? env['CONSUL_TOKEN_SECRET_NAME'] : 'consul-acl-token'))
    SecretVolume certs = new SecretVolume(((env['CERTS_MOUNT_PATH']) ? env['CERTS_MOUNT_PATH'] : '/secrets'),
                                          ((env['CERTS_SECRET_NAME']) ? env['CERTS_SECRET_NAME'] : 'va-cacerts'))
    SecretVolume sshConfig = new SecretVolume(((env['SSH_MOUNT_PATH']) ? env['SSH_MOUNT_PATH'] : '/home/jenkins/ssh-config'),
                                          ((env['SSH_CONFIG_SECRET']) ? env['SSH_CONFIG_SECRET'] : '..........'), ((env['SSH_CONFIG_DEFAULT_MODE']) ? env['SSH_CONFIG_DEFAULT_MODE'] : '420'))

    // add PersistentVolumeClaim with mountName, name, and isReadOnly parameters
    PersistentVolumeClaim jenkinsVolume = new PersistentVolumeClaim(((env['JENKINS_MAVEN_MOUNT_PATH']) ? env['JENKINS_MAVEN_MOUNT_PATH'] : '/home/jenkins/.m2'),
                                                                    ((env['JENKINS_MAVEN_NAME']) ? env['JENKINS_MAVEN_NAME'] : 'jenkins'), false)
    def volumes = [consulToken, certs, jenkinsVolume, sshConfig]
    podTemplate.setVolumes(volumes)

    // Container Template with name, image, command and args as parameters, in that order
    ContainerTemplate container = new ContainerTemplate(((env['GIT2CONSUL_CONTAINER_NAME']) ? env['GIT2CONSUL_CONTAINER_NAME'] : 'jnlp'),
                                                        ((env['GIT2CONSUL_CONTAINER_IMAGE']) ? env['GIT2CONSUL_CONTAINER_IMAGE'] : '..........'),
                                                        ((env['GIT2CONSUL_CONTAINER_COMMAND']) ? env['GIT2CONSUL_CONTAINER_COMMAND'] : ''),
                                                        ((env['GIT2CONSUL_CONTAINER_ARGS']) ? env['GIT2CONSUL_CONTAINER_ARGS'] : '${computer.jnlpmac} ${computer.name}'))
    container.setAlwaysPullImage(true)
    container.setWorkingDir(((env['GIT2CONSUL_CONTAINER_WORKING_DIR']) ? env['GIT2CONSUL_CONTAINER_WORKING_DIR'] : '/tmp'))

    podTemplate.setContainers([container])
    kcloud.templates << podTemplate
    kcloud = null
    jenkins.save()
} else {
  println("No cloud template exists, looks like this jenkins image wasn't built with OpenShift base image...?")
}
