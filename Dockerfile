# Étape 1 : Partir de l'image Jenkins avec Java 17
FROM jenkins/jenkins:jdk17 AS jenkins

# Passer en mode root pour installer les dépendances
USER root

# Désactiver l'assistant d'installation initial
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false
ENV CASC_JENKINS_CONFIG /var/jenkins_home/casc.yaml

# Copier les fichiers nécessaires
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
COPY pipeline.groovy /var/jenkins_home/pipeline.groovy

# Installer les plugins Jenkins spécifiés
RUN jenkins-plugin-cli -f /usr/share/jenkins/ref/plugins.txt

# Copier le fichier Configuration as Code (CasC)
COPY casc.yaml /var/jenkins_home/casc.yaml

# Télécharger et installer Maven
ENV MAVEN_VERSION=3.6.3
ENV MAVEN_HOME=/usr/share/maven
ENV PATH=${MAVEN_HOME}/bin:${PATH}
RUN mkdir -p /usr/share/maven \
    && curl -fsSL https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz | tar -xzC /usr/share/maven --strip-components=1

# Vérifier l'installation de Maven
RUN mvn --version

# Définir le dossier de travail par défaut
WORKDIR /var/jenkins_home

# Lancer Jenkins par défaut
CMD ["jenkins"]
