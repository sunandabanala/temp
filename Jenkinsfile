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
    project_id="asia.gcr.io/staging-auzmor"
    artifact="calendar-backend"
    credentials_id="staging_auzmor"
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
        }
    }
    stage('Build') {
        steps {
            script {
                version="integration-${env.BUILD_NUMBER}"
                imageTag="${env.project_id}/${env.artifact}:${version}"
            }
            container('docker') {
                 script {
                    withDockerRegistry(credentialsId: 'harbor-registry', url: 'https://harbor.cicd.auzmor.com') {
                         sh "docker pull harbor.cicd.auzmor.com/auzmor/${artifact}:latest > /dev/null && echo \"exists\" || echo \"doesn't exists\""
                    }
                    withDockerRegistry(credentialsId: "gcr:${credentials_id}", url: 'https://asia.gcr.io') {
                         sh "docker build --cache-from harbor.cicd.auzmor.com/auzmor/${artifact}:latest --tag harbor.cicd.auzmor.com/auzmor/${artifact}:latest ."
                    }
                    withDockerRegistry(credentialsId: 'harbor-registry', url: 'https://harbor.cicd.auzmor.com') {
                         sh "docker tag harbor.cicd.auzmor.com/auzmor/${artifact}:latest ${imageTag}"
                         sh "docker push harbor.cicd.auzmor.com/auzmor/${artifact}:latest"
                         sh "docker tag harbor.cicd.auzmor.com/auzmor/${artifact}:latest  harbor.cicd.auzmor.com/auzmor/${artifact}:${version}"
                         sh "docker push harbor.cicd.auzmor.com/auzmor/${artifact}:${version}"
                    }
                }
            }
        }
    }
    stage("Push") {
        when { anyOf { branch 'develop'; branch 'staging'; branch 'master' } }
        steps {
            container("docker") {
                withDockerRegistry(credentialsId: "gcr:${credentials_id}", url: 'https://asia.gcr.io') {
                    sh "docker push ${imageTag}"
                }
            }
        }
    }
    stage("Deploy Dev") {
        when {
            branch "develop"
        }
        steps {
            deployKubernetes credential_id: 'staging', cluster_name: 'dev-staging', zone_name: 'us-central1', project_name: 'staging-auzmor', namespace: 'development' ,deployment: 'calendar-backend', imageTag: imageTag
        }
    }
    stage("Deploy Staging") {
        when {
            branch "staging"
        }
        steps {
            deployKubernetes credential_id: 'staging', cluster_name: 'dev-staging', zone_name: 'us-central1', project_name: 'staging-auzmor', namespace: 'staging' ,deployment: 'calendar-backend', imageTag: imageTag
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
            deployKubernetes credential_id: 'production', cluster_name: 'ats-prod-cluster', zone_name: 'us-central1-a', project_name: 'production-auzmor', namespace: 'production' ,deployment: 'calendar-backend', imageTag: imageTag
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