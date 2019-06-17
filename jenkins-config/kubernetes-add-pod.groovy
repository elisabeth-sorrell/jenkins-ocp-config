
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

    podTemplate.setName((env['POD_NAME']) ? env['POD_NAME'] : 'default')
    podTemplate.setLabel((env['POD_LABEL']) ? env['POD_LABEL'] : 'default')
    podTemplate.setNodeUsageMode(Node.Mode.EXCLUSIVE)

    SecretVolume consulToken = new SecretVolume(((env['POD_CONSUL_TOKEN']) ? env['POD_CONSUL_TOKEN'] : 'default-token'),
                                                ((env['POD_CONSUL_TOKEN_MOUNT']) ? env['POD_CONSUL_TOKEN_MOUNT'] : '/default/secrets'))
    SecretVolume certs = new SecretVolume(((env['POD_CERTS_NAME']) ? env['POD_CERTS_NAME'] : 'default-certs'),
                                          ((env['POD_CERTS_MOUNT']) ? env['POD_CERTS_MOUNT'] : '/default'))

    // add PersistentVolumeClaim with mountName, name, and isReadOnly parameters
    PersistentVolumeClaim jenkinsVolume = new PersistentVolumeClaim(((env['POD_JENKINS_MAVEN_MOUNT']) ? env['POD_JENKINS_MAVEN_MOUNT'] : '/home/jenkins/.m2'),
                                                                    ((env['POD_JENKINS_MAVEN_NAME']) ? env['POD_JENKINS_MAVEN_NAME'] : 'jenkins-default'), false)
    def volumes = [consulToken, certs, jenkinsVolume]
    podTemplate.setVolumes(volumes)

    // Container Template with name, image, command and args as parameters, in that order
    ContainerTemplate container = new ContainerTemplate(((env['CONTAINER_NAME']) ? env['CONTAINER_NAME'] : 'jnlp'),
                                                        ((env['CONTAINER_IMAGE']) ? env['CONTAINER_IMAGE'] : 'someimage'),
                                                        ((env['CONTAINER_COMMAND']) ? env['CONTAINER_COMMAND'] : 'touch default'),
                                                        ((env['CONTAINER_ARGS']) ? env['CONTAINER_ARGS'] : 'default'))
    container.setAlwaysPullImage(true)
    container.setWorkingDir(((env['CONTAINER_WORKING_DIR']) ? env['CONTAINER_WORKING_DIR'] : '/tmp'))

    podTemplate.setContainers([container])
    kcloud.templates << podTemplate
    kcloud = null
    jenkins.save()
} else {
  println("No cloud template exists, looks like this wasn't started with OpenShift")
}
