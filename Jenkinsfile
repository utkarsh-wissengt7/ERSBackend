pipeline {
    agent any

    tools {
        gradle 'Gradle'
        jdk 'JDK17'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/utkarsh-wissengt7/ERSBackend.git'
            }
        }

        stage('Build') {
            steps {
                bat 'gradlew clean build'  // Using bat instead of sh
            }
        }

        stage('Test') {
            steps {
                bat 'gradlew test'  // Using bat instead of sh
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    bat 'gradlew sonarqube'
                }
            }
        

        stage('Build Docker Image') {
            steps {
                bat 'docker build -t ers-backend .'  // Using bat instead of sh
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
