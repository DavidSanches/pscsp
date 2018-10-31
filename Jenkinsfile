pipeline {
    agent any
    tools {
        jdk 'jdk8'
    }
    stages {
        stage ('Build') {
            //steps {
            //    sh "mvn -U clean test cobertura:cobertura -Dcobertura.report.format=xml"
            //}
            //post {
            //    always {
            //        junit '**/target/*-reports/TEST-*.xml'
            //        step([$class: 'CoberturaPublisher', coberturaReportFile: 'target/site/cobertura/coverage.xml'])
            //    }
            //}
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
            steps {
                script {
                  // requires SonarQube Scanner 2.8+
                    scannerHome = tool 'sq-scanner'
                }
                withSonarQubeEnv('Local SonarQube') {
                    sh "${scannerHome}/bin/sonar-runner -Dsonar.projectKey=paint-shop"
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
