pipeline {
    agent any
    tools {
        jdk 'jdk8'
    }
    stages {
        stage ('Build') {
            steps {
                sh 'mvn -Dmaven.test.failure.ignore=true install'
				stash includes: 'src/main/**, pom.xml', name: 'ws-src' 
            }
            post {
                success {
                    junit 'target/surefire-reports/**/*.xml'
                }
            }
        }
        stage('SonarQube analysis') {
            steps {
                script {
                  // requires SonarQube Scanner 2.8+
                    scannerHome = tool 'sq-scanner'
                }
                withSonarQubeEnv('Local SonarQube') {
				    unstash 'ws-src'
                    sh "${scannerHome}/bin/sonar-runner -Dsonar.projectKey=me.david:paint-shop -Dsonar.sources=src/main/java,pom.xml"
                }
            }
        }
        //stage("SonarQube Quality Gate") {
        //    timeout(time: 1, unit: 'HOURS') {
        //       def qg = waitForQualityGate()
        //       if (qg.status != 'OK') {
        //         error "Pipeline aborted due to quality gate failure: ${qg.status}"
        //       }
        //    }
        //}
    }
}
