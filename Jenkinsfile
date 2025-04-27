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
                bat 'gradlew clean build -x test'
            }
        }
        
        stage('Test') {
            steps {
                bat 'gradlew test jacocoTestReport'
                junit '**/build/test-results/test/*.xml'
                recordCoverage(
                    tools: [[parser: 'JACOCO']],
                    sourceDirectories: [[path: 'src/main/java']],
                    id: 'java-coverage',
                    name: 'Java Coverage'
                )
            }
        }
        
        stage('SonarQube Analysis') {
            environment {
                SONAR_TOKEN = credentials('SONAR_TOKEN')
            }
            steps {
                bat """
                    gradlew sonar \
                    -Dsonar.projectKey=ers-backend-project \
                    -Dsonar.host.url=http://localhost:9000 \
                    -Dsonar.token=%SONAR_TOKEN%
                """
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    bat 'docker version'  // Check if Docker is available
                    bat 'docker info'     // Check Docker system info
                    bat 'docker build -t ers-backend .'
                }
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        success {
            echo 'Build succeeded!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}
