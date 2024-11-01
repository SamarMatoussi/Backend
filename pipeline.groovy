pipelineJob('pipeline') {
    definition {
        cps {
            script("""
pipeline {
    agent {
        docker {
            image 'maven:3.8.6-openjdk-17'
            args '-u 0:0 -v /var/run/docker.sock:/var/run/docker.sock'
        }
    }
    environment {
        registry = "samarmatoussi/jcasc"
        registryCredential = 'dockerHub'
        dockerImage = ""
    }
    stages {
        stage('Clean workspace') {
            steps {
                sh 'git clean -xffd'
            }
        }
        stage('Clone repository') {
            steps {
                echo 'Cloning the repository'
                git branch: 'master', credentialsId: 'github-credentials', url: 'https://github.com/SamarMatoussi/Backend.git'
            }
        }
        stage('Maven Build') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }
        stage('Artifact Construction') {
            steps {
                sh 'mvn package'
            }
        }
        stage('Deploy to Nexus') {
            steps {
                sh 'mvn deploy -DskipTests -Dmaven.repo.local=$HOME/.m2/repository'
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    dockerImage = docker.build("${registry}:$BUILD_NUMBER")
                }
            }
        }
        stage('Deploy Docker Image') {
            steps {
                script {
                    docker.withRegistry('', registryCredential) {
                        dockerImage.push()
                    }
                }
            }
        }
    }
    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
""")
        }
    }
}
