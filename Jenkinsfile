#!groovy
pipeline {

    agent any

    environment {
        PATH_IN_WS = "${WORKSPACE}/source/towsar/"
    }
    tools {
        maven 'MVN3'
    }
    stages {
        stage('Descargando código de SCM') {
            steps {
                sh 'rm -rf *'
                checkout scm
            }
        }

        stage("Building") {
            steps {
                dir(env.PATH_IN_WS) {
                    sh 'mvn clean compile'
                }
            }
        }

        stage('Analysis') {

            parallel {
                stage('Junit') {
                    steps {
                        dir(env.PATH_IN_WS) {
                            sh 'mvn test'
                        }
                    }
                }

                stage('SonarQu') {
                    steps {
                        dir(env.PATH_IN_WS) {
                            withSonarQubeEnv('SonarLocal') {
                                sh 'mvn clean package sonar:sonar'
                            }
                        }
                    }

                }
            }

        }


        stage("Quality Gate") {
            when {
                branch 'master'  //only run these steps on the master branch
            }
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }

        }


        stage('Deploy') {
            steps {
                sh 'mvn jboss-as:deploy'
            }
        }

        stage('Archivar') {
            steps {
                step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar, **/target/*.war', fingerprint: true])
            }
        }

        stage('Example') {
            input {
                message "Can we Proceed?"
                ok "Yes"
                submitter "Digital Varys"
                parameters {
                    string(name: 'PERSON', defaultValue: 'DigiralVarys', description: 'Member')
                }
            }
            steps {
                echo "${PERSON}, is proceeding..."
            }
        }
    }
    post {
        failure {
            // notify users when the Pipeline fails
            mail to: 'rllayus@gmail.com',
                    subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
                    body: "Something is wrong with ${env.BUILD_URL}"
        }

    }
}