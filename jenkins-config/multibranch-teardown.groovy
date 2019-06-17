
import jenkins.model.Jenkins
import com.fuzzpro.multibranchteardown.*

Jenkins jenkins = Jenkins.getInstance()
def env = System.getenv()

multibranchConfig = jenkins.getDescriptor('com.fuzzpro.multibranchteardown.JobTearDownConfiguration')
multibranchConfig.setTearDownJob(((env['TEAR_DOWN_JOB_NAME']) ? env['TEAR_DOWN_JOB_NAME'] : 'job-tear-down-executor') + "/master")

multibranchConfig.save()
jenkins.save()
