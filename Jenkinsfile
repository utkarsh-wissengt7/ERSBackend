pipeline {
    agent any

    tools {
        gradle 'Gradle'
        jdk 'JDK17'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/utkarsh-wissengt7/ERSBackend.git'
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build -x test'
            }
        }

        stage('Test') {
            steps {
                sh './gradlew test'
            }
            post {
                always {
                    junit '**/build/test-results/test/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh './gradlew sonarqube'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t ers-backend .'
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}