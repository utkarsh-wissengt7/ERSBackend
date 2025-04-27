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
                bat 'gradlew clean build'
            }
        }
        
        stage('Test') {
            steps {
                bat 'gradlew test'
            }
        }
        
        stage('SonarQube Analysis') {
            environment {
                SONAR_TOKEN = credentials('SONAR_TOKEN')
            }
            steps {
                bat """
                    gradlew sonarqube \
                    -Dsonar.host.url=http://localhost:9000 \
                    -Dsonar.login=%SONAR_TOKEN%
                """
            }
        }
        
        stage('Build Docker Image') {
            steps {
                bat 'docker build -t ers-backend .'
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
    }
}
