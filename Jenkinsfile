pipeline {
    agent any
    tools {
        jdk 'jdk8'
    }
    stages {
        stage ('Build') {
            steps {
                sh 'mvn -Dmaven.test.failure.ignore=true install'
            }
            post {
                success {
                    junit 'target/surefire-reports/**/*.xml'
                }
            }
        }
        stage('SonarQube analysis') {
            withSonarQubeEnv('Local SonarQube') {
              sh "/opt/sonar-runner/bin/sonar-runner -X -e"
            }
            step([$class: 'JacocoPublisher',
                    execPattern:'**/**.exec',
                    classPattern: '**/classes/main',
                    sourcePattern: '**/src/main/java',
                    exclusionPattern: '**/*Test*.class'])
        }
        stage("SonarQube Quality Gate") {
            timeout(time: 1, unit: 'HOURS') {
               def qg = waitForQualityGate()
               if (qg.status != 'OK') {
                 error "Pipeline aborted due to quality gate failure: ${qg.status}"
               }
            }
        }
    }
}
