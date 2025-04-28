pipeline {
    agent any
    
    tools {
        gradle 'Gradle'
        jdk 'JDK17'
    }

    environment {
        DOCKER_IMAGE = 'utkarshgt78/ers-backend'
        DOCKER_TAG = "${BUILD_NUMBER}"
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
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
        
        stage('Test & Coverage') {
            steps {
                bat 'gradlew clean test jacocoTestReport'
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
                    gradlew build sonar \
                    -Dsonar.projectKey=ers-backend-project \
                    -Dsonar.host.url=http://localhost:9000 \
                    -Dsonar.token=%SONAR_TOKEN% \
                    --info
                """
            }
}
        
        stage('Build Docker Image') {
            steps {
                script {
                    bat 'docker version'
                    bat 'docker info'
                    bat "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                    bat "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
                }
            }
        }


        stage('Push to DockerHub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', 
                                                    usernameVariable: 'DOCKER_USERNAME', 
                                                    passwordVariable: 'DOCKER_PASSWORD')]) {
                        bat 'echo %DOCKER_PASSWORD% | docker login -u %DOCKER_USERNAME% --password-stdin'
                        bat "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                        bat "docker push ${DOCKER_IMAGE}:latest"
                        bat 'docker logout'
                    }
                }
            }
        }
    }
    
    post {
        always {
            cleanWs()
            script {
                // Clean up Docker images
                bat "docker rmi ${DOCKER_IMAGE}:${DOCKER_TAG} || true"
                bat "docker rmi ${DOCKER_IMAGE}:latest || true"
            }
        }
        success {
            echo 'Build succeeded!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}
