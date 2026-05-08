pipeline {
    agent any

    triggers {
        pollSCM('H/5 * * * *')
    }

    environment {
        APP_URL = "http://44.212.91.126:8090"
    }

    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'main',
                    credentialsId: 'github-cred',
                    url: 'https://github.com/AbdulMoizAbbasi496/fir-system.git'
            }
        }

        stage('Start Application') {
            steps {
                sh 'docker compose -f docker-compose.jenkins.yml down --remove-orphans || true'
                sh 'docker volume rm fir-system_db_jenkins_data 2>/dev/null || true'
                
                sh 'docker compose -f docker-compose.jenkins.yml up -d --build'
                
                sh '''
                    echo "Waiting for Flask app to start..."
                    for i in {1..40}; do
                        if curl -s --connect-timeout 5 $APP_URL > /dev/null; then
                            echo "✅ Application is UP at $APP_URL"
                            exit 0
                        fi
                        echo "Waiting... ($i/40)"
                        sleep 6
                    done
                    echo "❌ App failed to start"
                    exit 1
                '''
            }
        }

        stage('Run Selenium Tests') {
            steps {
                dir('tests') {
                    sh '''
                        echo "=== Running Selenium Tests ==="
                        echo "Target URL: ${APP_URL}"
                        
                        docker run --rm \
                            --network host \
                            -v $(pwd):/tests \
                            -w /tests \
                            -e APP_URL=${APP_URL} \
                            markhobson/maven-chrome:jdk-17 \
                            mvn clean test \
                                -Dapp.url=${APP_URL} \
                                -Dtest=FirTests \
                                --no-transfer-progress
                    '''
                }
            }
        }
    }

    post {
    always {
        script {
            junit testResults: 'tests/target/surefire-reports/*.xml', allowEmptyResults: true

            def pusherEmail = sh(script: "git log -1 --format='%ae'", returnStdout: true).trim()

            // Extract test results from JUnit action
            def testResultAction = currentBuild.rawBuild.getAction(hudson.tasks.junit.TestResultAction)

            def total = 0
            def failed = 0
            def passed = 0

            if (testResultAction != null) {
                total = testResultAction.totalCount
                failed = testResultAction.failCount
                passed = testResultAction.totalCount - testResultAction.failCount
            }

            emailext(
                to: pusherEmail,
                subject: "[Jenkins] ${currentBuild.currentResult} - FIR System #${env.BUILD_NUMBER}",
                mimeType: 'text/html',
                body: """
                    <h2 style="color:${currentBuild.currentResult == 'SUCCESS' ? 'green' : 'red'}">
                        Pipeline ${currentBuild.currentResult}
                    </h2>

                    <h3>Test Summary</h3>
                    <table border='1' cellpadding='8' style='border-collapse:collapse'>
                        <tr><td><b>Total Tests</b></td><td>${total}</td></tr>
                        <tr><td><b>Passed</b></td><td style='color:green'>${passed}</td></tr>
                        <tr><td><b>Failed</b></td><td style='color:red'>${failed}</td></tr>
                    </table>

                    <br>
                    <p><b>Application:</b> <a href="${APP_URL}">${APP_URL}</a></p>
                    <p><b>Triggered By:</b> ${pusherEmail}</p>
                    <p><b>Build URL:</b> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                """
            )
        }
    }
}
}