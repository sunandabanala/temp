#!/usr/bin/groovy
@Library('k2glyph-shared@master') _ 

def imageTag= ""
def version=""
def changes
pipeline {
  agent {
    kubernetes {
      yamlFile 'ci/kubernetes-agent.yaml'
    }
  }
  environment {
    project_id="us.gcr.io/staging-auzmor"
    artifact="calendar-backend"
    liquibase_artifact="calendar-backend-liquibase"
    credentials_id="staging_auzmor"
    project_id_harbor="$registry_url/auzmor"
    credentials_id_harbor="harbor-registry"
    registry_url_harbor="http://$registry_url"
  }
  options {
      buildDiscarder(logRotator(numToKeepStr:'10'))
      disableConcurrentBuilds()
      timeout(time: 15, unit: 'MINUTES')
  }
  stages {
    stage("Start") {
        steps {
            script {
                changes=getChangeLog()
                sendNotificationUpdate status: "STARTED", email: 'false', changes: changes
           	 	if("${env.JOB_BASE_NAME}" == 'PR-*') {
                    sh "git merge --ff-only -"
                }
                def git_hash=sh returnStdout:true, script:'git rev-parse HEAD'
                version="${git_hash.trim()}.${env.BUILD_NUMBER}"
                imageTag="${env.project_id}/${env.artifact}:${version}"
            }
            container("docker") {
                withDockerRegistry(credentialsId: "${credentials_id_harbor}", url: "${registry_url_harbor}") {
                    sh "docker pull ${env.project_id_harbor}/${env.artifact}:latest > /dev/null && echo \"exists\" || echo \"doesn't exists\""
                    sh "docker pull ${env.project_id_harbor}/${env.liquibase_artifact}:latest > /dev/null && echo \"exists\" || echo \"doesn't exists\""
                }
            }
        }
    }
    stage('Build') {
        steps {
            container('docker') {
                withDockerRegistry(credentialsId: "gcr:${credentials_id}", url: 'https://us.gcr.io') {
                     sh "docker build --cache-from ${env.project_id_harbor}/${env.artifact}:latest --tag ${env.project_id_harbor}/${env.artifact}:latest ."
                     sh "docker tag ${env.project_id_harbor}/${env.artifact}:latest ${imageTag}"
                     sh "docker build -f Dockerfile.liquibase --cache-from ${env.project_id_harbor}/${env.liquibase_artifact}:latest --tag ${env.project_id_harbor}/${env.liquibase_artifact}:latest ."
                     sh "docker tag ${env.project_id_harbor}/${env.liquibase_artifact}:latest ${env.project_id}/${env.liquibase_artifact}:${version}"
                }
                withDockerRegistry(credentialsId: "${credentials_id_harbor}", url: "${registry_url_harbor}") {
                    sh "docker push ${env.project_id_harbor}/${env.artifact}:latest"
                    sh "docker push ${env.project_id_harbor}/${env.liquibase_artifact}:latest"
                }
            }
        }
    }
    stage("Push") {
        when { anyOf { branch 'develop'; branch 'qa'; branch 'staging'; branch 'master' } }
        steps {
            container("docker") {
                withDockerRegistry(credentialsId: "gcr:${credentials_id}", url: 'https://us.gcr.io') {
                    sh "docker push ${imageTag}"
                    sh "docker push ${env.project_id}/${env.liquibase_artifact}:${version}"
                }
            }
        }
    }
    stage("Deploy Dev") {
        when {
            branch "develop"
        }
        environment {
            GIT_CREDS = '2fd32807-2a2f-4551-b343-79482e2d7e9e'
        }
        steps {
            container("gcloud") {
                deployKubernetes credential_id: 'staging', cluster_name: 'dev-staging', zone_name: 'us-central1', project_name: 'staging-auzmor', namespace: 'development', type: "MIGRATE", grep: 'calendar-secret', version: version, job: "migrate"
                utility check: "jobs", namespace: "development", grep:"migrate"
                println("Migration job succeeded")
            }
            container('tools') {
              withCredentials([usernamePassword(credentialsId: env.GIT_CREDS, usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                script {
                    env.encodedPass=URLEncoder.encode(PASS, "UTF-8")
                }
                sh 'git clone https://${USER}:${encodedPass}@bitbucket.org/auzmorlms/k8s.git'
                sh "git config --global user.email 'ci@auzmor.com'"
                dir("k8s") {
                    sh "cd ./microservices/calendar/development && kustomize edit set image ${imageTag}"
                    sh "git commit -am 'Publish new version ${imageTag}' && git push --set-upstream origin master || echo 'no changes'"
                }
              } 
            }
        }
    }
    stage("Deploy QA") {
        when {
            branch "qa"
        }
        environment {
            GIT_CREDS = '2fd32807-2a2f-4551-b343-79482e2d7e9e'
        }
        steps {
            container("gcloud") {
                deployKubernetes credential_id: 'staging', cluster_name: 'dev-staging', zone_name: 'us-central1', project_name: 'staging-auzmor', namespace: 'qa', type: "MIGRATE", grep: 'calendar-secret', version: version, job: "migrate"
                utility check: "jobs", namespace: "qa", grep:"migrate"
                println("Migration job succeeded")
            }
            container('tools') {
              withCredentials([usernamePassword(credentialsId: env.GIT_CREDS, usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                script {
                    env.encodedPass=URLEncoder.encode(PASS, "UTF-8")
                }
                sh 'git clone https://${USER}:${encodedPass}@bitbucket.org/auzmorlms/k8s.git'
                sh "git config --global user.email 'ci@auzmor.com'"
                dir("k8s") {
                    sh "cd ./microservices/calendar/qa && kustomize edit set image ${imageTag}"
                    sh "git commit -am 'Publish new version ${imageTag}' && git push --set-upstream origin master || echo 'no changes'"
                }
              } 
            }
        }
    }
    stage("Deploy Staging") {
        when {
            branch "staging"
        }
        environment {
            GIT_CREDS = '2fd32807-2a2f-4551-b343-79482e2d7e9e'
        }
        steps {
            container("gcloud") {
                deployKubernetes credential_id: 'staging', cluster_name: 'dev-staging', zone_name: 'us-central1', project_name: 'staging-auzmor', namespace: 'staging', type: "MIGRATE", grep: 'calendar-secret', version: version, job: "migrate"
                utility check: "jobs", namespace: "staging", grep:"migrate"
                println("Migration job succeeded")
                deployKubernetes credential_id: 'staging', cluster_name: 'dev-staging', zone_name: 'us-central1', project_name: 'staging-auzmor', namespace: 'staging' ,deployment: 'calendar-backend', imageTag: imageTag
            }
            container('tools') {
              withCredentials([usernamePassword(credentialsId: env.GIT_CREDS, usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                script {
                    env.encodedPass=URLEncoder.encode(PASS, "UTF-8")
                }
                sh 'git clone https://${USER}:${encodedPass}@bitbucket.org/auzmorlms/k8s.git'
                sh "git config --global user.email 'ci@auzmor.com'"
                dir("k8s") {
                    sh "cd ./microservices/calendar/staging && kustomize edit set image ${imageTag}"
                    sh "git commit -am 'Publish new version ${imageTag}' && git push --set-upstream origin master || echo 'no changes'"
                }
              } 
            }
        }
    }
    stage("Manual Promotion") {
        when {
            branch 'master'
        }
        steps {
            milestone(1)
            timeout(time: 10, unit: 'MINUTES') {
                input message: "Does Staging/ Sandbox look good?"
            }
            milestone(2)
        }
    }
    stage("Deploy Production") {
        when {
            branch 'master'
        }
        steps {
            container("gcloud") {
                deployKubernetes credential_id: 'staging', cluster_name: 'dev-staging', zone_name: 'us-central1', project_name: 'staging-auzmor', namespace: 'staging', type: "MIGRATE", grep: 'calendar-secret', version: version, job: "migrate"
                utility check: "jobs", namespace: "production", grep:"migrate"
                println("Migration job succeeded")
                deployKubernetes credential_id: 'production', cluster_name: 'ats-prod-cluster', zone_name: 'us-central1-a', project_name: 'production-auzmor', namespace: 'production' ,deployment: 'calendar-backend', imageTag: imageTag
            }
        }
    }
  }
  post {
        success {
            sendNotificationUpdate status: "SUCCESS", email: 'false', changes: changes
        }
        failure {
            sendNotificationUpdate status: "FAILURE", email: 'false', changes: changes
        }

        aborted {
            sendNotificationUpdate status: "ABORTED", email: 'false', changes: changes
        }
  }
}
