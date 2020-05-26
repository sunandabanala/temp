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
    artifact="calendar"
    credentials_id="staging_auzmor"
    project_id_harbor="$registry_url/auzmor"
    credentials_id_harbor="harbor-registry"
    registry_url_harbor="http://$registry_url"
    argocd_server="argocd.auzmor.com"
  }
  options {
    buildDiscarder(logRotator(numToKeepStr:'10'))
    disableConcurrentBuilds()
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
            container('docker') {
                withDockerRegistry(credentialsId: "gcr:${credentials_id}", url: 'https://us.gcr.io') {
                     sh "docker build --tag ${env.project_id_harbor}/${env.artifact}:latest ."
                     sh "docker tag ${env.project_id_harbor}/${env.artifact}:latest ${imageTag}"
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
            APP_NAME="calendar-development"
        }
        steps {
            container('tools') {
              withCredentials([usernamePassword(credentialsId: env.GIT_CREDS, usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                script {
                    env.encodedPass=URLEncoder.encode(PASS, "UTF-8")
                }
                sh 'git clone https://${USER}:${encodedPass}@bitbucket.org/auzmorlms/k8s.git'
                sh "git config --global user.email 'ci@auzmor.com'"
                dir("k8s") {
                    sh "cd ./microservices/calendar/development && kustomize edit set image ${imageTag}"
                    sh "git commit -am 'Publish new version ${imageTag}' && git pull origin master && git push --set-upstream origin master || echo 'no changes'"
                }
              }
              withCredentials([string(credentialsId: "argocd", variable: 'ARGOCD_AUTH_TOKEN')]) {
                    sh '''
                    ARGOCD_SERVER=$argocd_server argocd --grpc-web app set $APP_NAME
                    
                    # Deploy to ArgoCD
                    ARGOCD_SERVER=$argocd_server argocd --grpc-web app sync $APP_NAME --force
                    ARGOCD_SERVER=$argocd_server argocd --grpc-web app wait $APP_NAME --timeout 600
                    '''
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
            APP_NAME="calendar-qa"
        }
        steps {
            container('tools') {
              withCredentials([usernamePassword(credentialsId: env.GIT_CREDS, usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                script {
                    env.encodedPass=URLEncoder.encode(PASS, "UTF-8")
                }
                sh 'git clone https://${USER}:${encodedPass}@bitbucket.org/auzmorlms/k8s.git'
                sh "git config --global user.email 'ci@auzmor.com'"
                dir("k8s") {
                    sh "cd ./microservices/calendar/qa && kustomize edit set image ${imageTag}"
                    sh "git commit -am 'Publish new version ${imageTag}' && git pull origin master && git push --set-upstream origin master || echo 'no changes'"
                }
              }
              withCredentials([string(credentialsId: "argocd", variable: 'ARGOCD_AUTH_TOKEN')]) {
                    sh '''
                    ARGOCD_SERVER=$argocd_server argocd --grpc-web app set $APP_NAME
                    
                    # Deploy to ArgoCD
                    ARGOCD_SERVER=$argocd_server argocd --grpc-web app sync $APP_NAME --force
                    ARGOCD_SERVER=$argocd_server argocd --grpc-web app wait $APP_NAME --timeout 600
                    '''
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
            APP_NAME="calendar-development"
        }
        steps {
            container('tools') {
              withCredentials([usernamePassword(credentialsId: env.GIT_CREDS, usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                script {
                    env.encodedPass=URLEncoder.encode(PASS, "UTF-8")
                }
                sh 'git clone https://${USER}:${encodedPass}@bitbucket.org/auzmorlms/k8s.git'
                sh "git config --global user.email 'ci@auzmor.com'"
                dir("k8s") {
                    sh "cd ./microservices/calendar/staging && kustomize edit set image ${imageTag}"
                    sh "git commit -am 'Publish new version ${imageTag}' && git pull origin master && git push --set-upstream origin master || echo 'no changes'"
                }
              }
              withCredentials([string(credentialsId: "argocd", variable: 'ARGOCD_AUTH_TOKEN')]) {
                    sh '''
                    ARGOCD_SERVER=$argocd_server argocd --grpc-web app set $APP_NAME
                    
                    # Deploy to ArgoCD
                    ARGOCD_SERVER=$argocd_server argocd --grpc-web app sync $APP_NAME --force
                    ARGOCD_SERVER=$argocd_server argocd --grpc-web app wait $APP_NAME --timeout 600
                    '''
              }
            }
        }
    }
    stage("Manual Promotion") {
        when {
            branch 'master'
        }
        steps {
            // we need a first milestone step so that all jobs entering this stage are tracked an can be aborted if needed
            milestone(1)
            timeout(time: 10, unit: 'MINUTES') {
                input message: "Does Staging/ Sandbox look good?"
            }
            // this will kill any job which is still in the input step
            milestone(2)
        }
    }
    stage("Deploy Production") {
        when {
            branch 'master'
        }
        environment {
            cluster="ats-prod-cluster"
            zone="us-central1-a"
            project="production-auzmor"
            namespace="production"
            cred_id="production"
        }
        steps {
            loginKubernetes credential_id: env.cred_id, cluster_name: env.cluster, zone_name: env.zone, project_name: env.project
            println("Deploying to ${env.cluster}...") 
            deployKubernetes namespace: env.namespace ,deployment: 'calendar-backend', imageTag: imageTag
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