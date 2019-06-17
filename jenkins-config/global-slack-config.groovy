import jenkins.model.Jenkins


Jenkins jenkins = Jenkins.getInstance()
def env = System.getenv()

def slack = jenkins.getExtensionList(
        jenkins.plugins.slack.SlackNotifier.DescriptorImpl.class
    )[0]

// Setup most of the configuration for slack
slack.baseUrl = ((env['SLACK_BASE_URL']) ? env['SLACK_BASE_URL'] : 'https://bipva.slack.com')
slack.teamDomain = ((env['SLACK_TEAM_DOMAIN']) ? env['SLACK_TEAM_DOMAIN'] : 'bipva')
slack.tokenCredentialId = ((env['SLACK_CREDENTIAL_ID']) ? env['SLACK_CREDENTIAL_ID'] : 'slack-token')
slack.botUser = true
slack.room = ((env['SLACK_TEAM_ROOM']) ? env['SLACK_TEAM_ROOM'] : 'default')
slack.save()


// Set up the Slack Webhook
def slackWebhook = jenkins.getDescriptor('jenkins.plugins.slack.webhook.GlobalConfig')
slackWebhook.setSlackOutgoingWebhookToken(((env['SLACK_CREDENTIAL_ID']) ? env['SLACK_CREDENTIAL_ID'] : 'slack-token'))
slackWebhook.setSlackOutgoingWebhookURL(((env['SLACK_WEBHOOK_URL']) ? env['SLACK_WEBHOOK_URL'] : 'https://default-value.slack.com/webhook'))
slackWebhook.save()
jenkins.save()
