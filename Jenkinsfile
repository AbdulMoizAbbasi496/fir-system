pipeline {
    agent any
    environment {
        APP_DIR  = 'fir-system'
        TEST_DIR = 'fir-selenium-tests'
    }
    stages {
        stage('Clone App') {
            steps {
                dir("${APP_DIR}") {
                    git branch: 'main',
                        credentialsId: 'github-cred',
                        url: 'https://github.com/AbdulMoizAbbasi496/fir-system.git'
                }
            }
        }
        stage('Start App') {
            steps {
                dir("${APP_DIR}") {
                    sh 'docker compose -f docker-compose.jenkins.yml down --remove-orphans || true'
                    sh 'docker volume rm fir-system_db_jenkins_data 2>/dev/null || true'
                    sh 'docker compose -f docker-compose.jenkins.yml up -d --build'
                    sh 'echo "Waiting 90 seconds for MySQL + Streamlit to initialize..."'
                    sh 'sleep 90'
                    sh 'docker compose -f docker-compose.jenkins.yml ps'
                    sh 'docker compose -f docker-compose.jenkins.yml logs --tail=20'
                }
            }
        }
        stage('Clone Tests') {
            steps {
                dir("${TEST_DIR}") {
                    git branch: 'main',
                        credentialsId: 'github-cred',
                        url: 'https://github.com/AbdulMoizAbbasi496/fir-selenium-tests.git'
                }
            }
        }
        stage('Run Selenium Tests') {
            steps {
                dir("${TEST_DIR}") {
                    sh '''
                        HOST_BASE=/var/lib/docker/volumes/jenkins-data/_data
                        CONTAINER_BASE=/var/jenkins_home
                        CURRENT=$(pwd)
                        HOST_PATH="${HOST_BASE}${CURRENT#$CONTAINER_BASE}"
                        echo "Host path: $HOST_PATH"
                        ls "$HOST_PATH"
                        docker run --rm \
                            --network host \
                            -v "$HOST_PATH":/workspace \
                            -w /workspace \
                            markhobson/maven-chrome:jdk-17 \
                            mvn test -Dapp.url=http://localhost:8090 || true
                    '''
                }
            }
            post {
                always {
                    dir("${TEST_DIR}") {
                        sh 'find . -name "*.xml" 2>/dev/null | head -10'
                        junit allowEmptyResults: true,
                              testResults: '**/target/surefire-reports/*.xml'
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
                    subject: "[Jenkins] ${currentBuild.currentResult} — FIR Pipeline #${env.BUILD_NUMBER}",
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
                    """
                )
            }
        }
    }
}
