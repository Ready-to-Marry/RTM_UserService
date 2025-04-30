pipeline {
    agent any
    environment {
	    SERVICE_REPO_NAME = 'RTM_UserService.git'
	    DOCKER_IMAGE = 'rtm-user-service'
	    SERVICE_NAME = 'user-service'
	    SPRING_DATASOURCE_URL = credentials('postgres-url')
	    DB_CREDENTIALS = credentials('postgres-db-credentials')
	    GITHUB_CREDENTIALS = credentials('github-token')
	    HELM_REPO_URL = 'https://github.com/Ready-to-Marry/RTM_infra.git'
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: "https://github.com/Ready-to-Marry/${SERVICE_REPO_NAME}", credentialsId: 'github-token'
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    echo "🔨 Building Docker image: ${DOCKER_IMAGE}:${env.BUILD_NUMBER}"
                    docker.build("${DOCKER_IMAGE}:${env.BUILD_NUMBER}")
                }
            }
        }
        stage('Push Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
			echo "DOCKER_USERNAME: ${DOCKER_USERNAME}"
            	    	echo "DOCKER_IMAGE: ${DOCKER_IMAGE}"
            	    	echo "BUILD_NUMBER: ${env.BUILD_NUMBER}"
                        docker.withRegistry('', 'dockerhub') {
                            echo "📤 Pushing image to Docker Hub..."
                            docker.image("${DOCKER_USERNAME}/${DOCKER_IMAGE}:${env.BUILD_NUMBER}").push()
                        }
                    }
                }
            }
        }
        stage('Update Helm values.yaml') {
            steps {
                script {
                    echo "🛠️ Cloning Helm chart repo..."
                    sh 'rm -rf rtm-helm'
                    sh "git clone ${HELM_REPO_URL} rtm-helm"

                    echo "🔧 Updating image tag to ${env.BUILD_NUMBER}..."
                    sh """
                      sed -i 's/^  tag: .*/  tag: "${env.BUILD_NUMBER}"/' rtm-helm/${SERVICE_NAME}/values.yaml
                    """

                    dir('rtm-helm') {
                        sh 'git config user.name "hyundoo"'
                        sh 'git config user.email "hyundoo1006@gmail.com"'
                        withCredentials([usernamePassword(credentialsId: 'github-token', usernameVariable: 'GITHUB_USERNAME', passwordVariable: 'GITHUB_TOKEN')]) {
                            sh """
                            git remote set-url origin https://${GITHUB_USERNAME}:${GITHUB_TOKEN}@github.com/Ready-to-Marry/RTM_infra.git
                            git commit -am 'Update image tag to ${env.BUILD_NUMBER}'
                            git push origin HEAD:main
                            """
                        }
                    }
                }
            }
        }
    }
}
