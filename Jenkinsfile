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
                    -Dsonar.token=%SONAR_TOKEN% \
                    -Dsonar.gradle.skipCompile=true \
                    --info
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
            jacoco(
                execPattern: '**/build/jacoco/test.exec',
                classPattern: '**/build/classes/java/main',
                sourcePattern: '**/src/main/java'
            )
        }
    }
}
