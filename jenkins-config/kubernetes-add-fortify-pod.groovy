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

    podTemplate.setName((env['FORTIFY_POD_NAME']) ? env['FORTIFY_POD_NAME'] : 'fortify-sca')
    podTemplate.setLabel((env['FORTIFY_POD_LABEL']) ? env['FORTIFY_POD_LABEL'] : 'fortify-sca')
    podTemplate.setNodeUsageMode(Node.Mode.EXCLUSIVE)

    // add PersistentVolumeClaim with mountName, name, and isReadOnly parameters
    PersistentVolumeClaim jenkinsVolume = new PersistentVolumeClaim(((env['JENKINS_MAVEN_MOUNT_PATH']) ? env['JENKINS_MAVEN_MOUNT_PATH'] : '/home/jenkins/.m2'),
                                                                    ((env['JENKINS_MAVEN_NAME']) ? env['JENKINS_MAVEN_NAME'] : 'jenkins'), false)
    def volumes = [jenkinsVolume]
    podTemplate.setVolumes(volumes)

    // Container Template with name, image, command and args as parameters, in that order
    ContainerTemplate container = new ContainerTemplate(((env['FORTIFY_CONTAINER_NAME']) ? env['FORTIFY_CONTAINER_NAME'] : 'jnlp'),
                                                        ((env['FORTIFY_CONTAINER_IMAGE']) ? env['FORTIFY_CONTAINER_IMAGE'] : '..........'),
                                                        ((env['FORTIFY_CONTAINER_COMMAND']) ? env['FORTIFY_CONTAINER_COMMAND'] : ''),
                                                        ((env['FORTIFY_CONTAINER_ARGS']) ? env['FORTIFY_CONTAINER_ARGS'] : '${computer.jnlpmac} ${computer.name}'))
    container.setAlwaysPullImage(true)
    container.setWorkingDir(((env['FORTIFY_CONTAINER_WORKING_DIR']) ? env['FORTIFY_CONTAINER_WORKING_DIR'] : '/home/jenkins'))

    podTemplate.setContainers([container])
    kcloud.templates << podTemplate
    kcloud = null
    jenkins.save()
} else {
  println("No cloud template exists, looks like this jenkins image wasn't built with OpenShift base image...?")
}
