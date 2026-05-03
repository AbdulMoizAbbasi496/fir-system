pipeline {
    agent any

    environment {
        APP_DIR  = '/home/ubuntu/fir-system'
        TEST_DIR = '/home/ubuntu/fir-selenium-tests'
    }

    stages {

        stage('Clone App') {
            steps {
                dir("${APP_DIR}") {
                    git branch: 'main',
                        credentialsId: 'github-creds',
                        url: 'https://github.com/AbdulMoizAbbasi496/fir-system.git'
                }
            }
        }

        stage('Start App') {
            steps {
                dir("${APP_DIR}") {
                    sh 'docker compose -f docker-compose.jenkins.yml down --remove-orphans || true'
                    sh 'docker compose -f docker-compose.jenkins.yml up -d --build'
                    sh 'echo "Waiting for Streamlit + MySQL to be ready..."'
                    sh 'sleep 55'
                    sh 'docker compose -f docker-compose.jenkins.yml ps'
                    sh 'curl -s -o /dev/null -w "App HTTP status: %{http_code}\\n" http://localhost:8090 || true'
                }
            }
        }

        stage('Clone Tests') {
            steps {
                dir("${TEST_DIR}") {
                    git branch: 'main',
                        credentialsId: 'github-creds',
                        url: 'https://github.com/AbdulMoizAbbasi496/fir-selenium-tests.git'
                }
            }
        }

        stage('Run Selenium Tests') {
            steps {
                dir("${TEST_DIR}") {
                    sh '''
                        docker run --rm \
                            --network host \
                            -v $(pwd):/workspace \
                            -w /workspace \
                            markhobson/maven-chrome:jdk-17 \
                            mvn test -Dapp.url=http://localhost:8090
                    '''
                }
            }
            post {
                always {
                    dir("${TEST_DIR}") {
                        junit '**/target/surefire-reports/*.xml'
                    }
                }
            }
        }

        stage('Stop App') {
            steps {
                dir("${APP_DIR}") {
                    sh 'docker compose -f docker-compose.jenkins.yml down'
                }
            }
        }
    }

    post {
        always {
            script {
                def pusherEmail = sh(
                    script: "cd ${APP_DIR} && git log -1 --format='%ae'",
                    returnStdout: true
                ).trim()

                emailext(
                    to: pusherEmail,
                    subject: "[Jenkins] ${currentBuild.currentResult} — FIR System Pipeline #${env.BUILD_NUMBER}",
                    mimeType: 'text/html',
                    body: """
                        <h2 style='color:${currentBuild.currentResult == 'SUCCESS' ? 'green' : 'red'}'>
                            Pipeline ${currentBuild.currentResult}
                        </h2>
                        <table border='1' cellpadding='8' style='border-collapse:collapse'>
                          <tr><td><b>Job</b></td><td>${env.JOB_NAME}</td></tr>
                          <tr><td><b>Build</b></td><td>#${env.BUILD_NUMBER}</td></tr>
                          <tr><td><b>Triggered by</b></td><td>${pusherEmail}</td></tr>
                          <tr><td><b>Duration</b></td><td>${currentBuild.durationString}</td></tr>
                          <tr><td><b>Test Report</b></td>
                              <td><a href='${env.BUILD_URL}testReport/'>Click to View</a></td></tr>
                          <tr><td><b>Console Log</b></td>
                              <td><a href='${env.BUILD_URL}console'>Click to View</a></td></tr>
                        </table>
                        <br><p style='color:grey;font-size:12px'>
                        Punjab Police FIR System — DevOps Assignment — COMSATS Islamabad</p>
                    """
                )
            }
        }
    }
}
