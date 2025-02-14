pipeline {
    agent any

    stages {
        stage('Install Docker') {
            steps {
                script {
                    sh '''
                    if ! command -v docker &> /dev/null
                    then
                        echo "Docker not found. Installing Docker..."
                        sudo apt update
                        sudo apt install -y docker.io
                        sudo systemctl start docker
                        sudo systemctl enable docker
                        sudo usermod -aG docker jenkins
                        echo "Docker installed successfully."
                    else
                        echo "Docker is already installed."
                    fi
                    '''
                }
            }
        }

        stage('Clone Repository') {
            steps {
                script {
                    sh 'git clone https://github.com/Sufiiii001/Jenkins-Practice'
                }
            }
        }

        stage('Install Dependencies') {
            steps {
                script {
                    dir('my_project') {
                        sh 'sudo apt install nodejs' 
                        sh 'sudo apt install npm'  
                        sh 'sudo npm install'
                        sh 'sudo node app.js'
                    }
                }
            }
        }

        stage('Create Dockerfile') {
            steps {
                script {
                    dir('my_project') {
                        writeFile file: 'Dockerfile', text: '''
FROM node:12.2.0-alpine
WORKDIR app
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
                    dir('my_project') {
                        sh "docker build -t todo-application ."
                    }
                }
            }
        }

        stage('Run Container') {
            steps {
                script {
                    sh "docker run -d --name node-todo-application -p 8000:8000 todo-application"
                }
            }
        }
    }
}
