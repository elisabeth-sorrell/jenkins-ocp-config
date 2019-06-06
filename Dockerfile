FROM registry.access.redhat.com/openshift3/jenkins-2-rhel7:v3.11


WORKDIR $HOME
USER root
COPY jenkins-config/* /opt/openshift/configuration/
RUN mv /opt/openshift/configuration/*.groovy /opt/openshift/configuration/init.groovy.d/

RUN chown -R 1001:0 $HOME /var/lib/jenkins && \
    chmod -R g+rw $HOME /var/lib/jenkins

RUN /usr/local/bin/install-plugins.sh /opt/openshift/configuration/plugins.txt

USER 1001
