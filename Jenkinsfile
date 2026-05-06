pipeline {
    agent any

    environment {
        APP_DIR  = 'fir-system'
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
                    sh 'echo "Waiting for services..."'
sh '''
until curl -s http://localhost:8090 > /dev/null; do
  echo "Waiting for app..."
  sleep 5
done
'''
                    sh 'docker compose -f docker-compose.jenkins.yml ps'
                }
            }
        }

        stage('Run Selenium Tests') {
            steps {
                dir("${APP_DIR}") {
                    sh '''
echo "===building tests==="
docker build -t fir-tests .

echo "===Running Tests==="
                docker run --rm \
                --network fir-system_default \
                -v "$PWD/tests":/tests \
                -w /tests \
                markhobson/maven-chrome:jdk-17 \
                mvn clean test -Dapp.url=http://fir-system-web-1:8501


                        echo "=== Test Execution Completed ==="
                    '''
                }
            }
        }
    }

    post {
        always {

            dir("${APP_DIR}") {

                script {

                    def pusherEmail = sh(
                        script: "git log -1 --format='%ae'",
                        returnStdout: true
                    ).trim()

                    // safer test extraction
                    def report = sh(
                        script: """
                            if ls target/surefire-reports/*.xml 1> /dev/null 2>&1; then
                                grep -h "<testsuite" target/surefire-reports/*.xml | \
                                awk -F'"' '{tests+=\$2; failures+=\$4; errors+=\$6} END {print tests, failures, errors}'
                            else
                                echo "0 0 0"
                            fi
                        """,
                        returnStdout: true
                    ).trim()

                    def parts = report.tokenize(' ')
                    def total = parts[0].toInteger()
                    def failed = (parts.size() > 2) ? (parts[1].toInteger() + parts[2].toInteger()) : 0
                    def passed = total - failed

                    emailext(
                        to: pusherEmail,
                        subject: "[Jenkins] ${currentBuild.currentResult} — FIR Pipeline #${env.BUILD_NUMBER}",
                        mimeType: 'text/html',
                        body: """
                            <h2 style='color:${currentBuild.currentResult == 'SUCCESS' ? 'green' : 'red'}'>
                                Pipeline ${currentBuild.currentResult}
                            </h2>

                            <h3>Test Summary</h3>
                            <table border='1' cellpadding='8' style='border-collapse:collapse'>
                              <tr><td><b>Total Tests</b></td><td>${total}</td></tr>
                              <tr><td><b>Passed</b></td><td style='color:green'>${passed}</td></tr>
                              <tr><td><b>Failed</b></td><td style='color:red'>${failed}</td></tr>
                            </table>

                            <br>

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

                            <br>
                            <p>App: <a href='http://44.212.91.126:8090'>http://44.212.91.126:8090</a></p>
                        """
                    )
                }
            }
        }
    }
}
