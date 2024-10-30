# Start with the latest Jenkins image
FROM jenkins/jenkins:latest AS jenkins

# Switch to root user to perform installations
USER root

# Set environment variables
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"
ENV CASC_JENKINS_CONFIG="/var/jenkins_home/casc.yaml"

# Copy necessary files
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
COPY pipeline.groovy /var/jenkins_home/pipeline.groovy

# Install Jenkins plugins
RUN jenkins-plugin-cli -f /usr/share/jenkins/ref/plugins.txt

# Copy Configuration as Code (CasC) file
COPY casc.yaml /var/jenkins_home/casc.yaml

# Install required tools
RUN apt-get update && apt-get install -y curl maven && apt-get clean

# Verify Maven installation
RUN mvn --version

# Set the default working directory
WORKDIR /var/jenkins_home

# Set the default command to start Jenkins
CMD ["java", "-jar", "/usr/share/jenkins/jenkins.war"]
