pipeline {
    agent any

    stages {

        stage('Cleanup Workspace') {
            steps {
                script {
                    sh 'rm -rf *'
                }
            }
        }

        stage('Cleanup git') {
            steps {
                script {
                    sh 'rm -rf * .git'
                }
            }
        }

        stage('Clone Repository') {
            steps {
                script {
                    sh 'git clone https://github.com/Sufiiii001/Jenkins-Practice .'
                }
            }
        }

        stage('Install Dependencies') {
            steps {
                script {
                    dir('node-todo-cicd-master') {
                        sh 'sudo apt install -y nodejs npm'  
                        sh 'npm install'
                        sh 'nohup node app.js > output.log 2>&1 &' 
                    }
                }
            }
        }

        stage('Create Dockerfile') {
            steps {
                script {
                    dir('node-todo-cicd-master') {
                        writeFile file: 'Dockerfile', text: '''
FROM node:12.2.0-alpine
WORKDIR /app
COPY . .
RUN npm install
EXPOSE 8000
CMD ["node", "app.js"]
                        '''
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    dir('node-todo-cicd-master') {
                        sh "sudo docker build -t todo-application ."
                    }
                }
            }
        }

        stage('Tag and Push to Docker Hub') {
            steps {
                script {
                    dir('node-todo-cicd-master') {
                        sh "sudo docker tag todo-application sufi001/todo-application:latest"   
                        sh "sudo docker push sufi001/todo-application:latest"
                    }
                }
            }
        }

        stage('Run Container') {
            steps {
                script {
                    sh "sudo docker run -d --name node-todo-application -p 9000:8000 sufi001/todo-application:latest"
                }
            }
        }
    }
}
