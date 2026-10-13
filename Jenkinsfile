pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                bat 'mvn clean test'
            }
        }

        stage('Package') {
            steps {
                bat 'mvn package'
            }
        }

        stage('Publish Test Results') {
            steps {
                junit 'course-registration-app/target/surefire-reports/TEST-*.xml'
            }
        }

        stage('Archive JAR') {
            steps {
                archiveArtifacts artifacts: 'course-registration-app/target/*.jar',
                                 fingerprint: true
            }
        }
    }

    post {
        always {
            echo 'Pipeline execution completed.'
        }
    }
}