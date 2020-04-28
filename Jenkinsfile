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
    project_id_harbor="$harbor_url/auzmor"
    credentials_id_harbor="harbor-registry"
    registry_url_harbor="http://$harbor_url"
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
            cluster="dev-staging"
            zone="us-central1"
            project="staging-auzmor"
            namespace="development"
            cred_id="staging"
        }
        steps {
            container("gcloud") {
                loginKubernetes credential_id: env.cred_id, cluster_name: env.cluster, zone_name: env.zone, project_name: env.project
                deployKubernetes namespace: env.namespace, type: "MIGRATE", grep: 'calendar-secret', version: version, job: "migrate"
                utility check: "jobs", namespace: env.namespace, grep:"migrate"
                println("Migration job succeeded")
                println("Deploying to ${env.cluster}...") 
                deployKubernetes  namespace: env.namespace ,deployment: 'calendar-backend', imageTag: imageTag
            }
        }
    }
    stage("Deploy QA") {
        when {
            branch "qa"
        }
        steps {
            container("gcloud") {
                deployKubernetes credential_id: 'staging', cluster_name: 'dev-staging', zone_name: 'us-central1', project_name: 'staging-auzmor', namespace: 'qa', type: "MIGRATE", grep: 'calendar-secret', version: version, job: "migrate"
                utility check: "jobs", namespace: "qa", grep:"migrate"
                println("Migration job succeeded")
                deployKubernetes credential_id: 'staging', cluster_name: 'dev-staging', zone_name: 'us-central1', project_name: 'staging-auzmor', namespace: 'qa' ,deployment: 'calendar-backend', imageTag: imageTag
            }
        }
    }
    stage("Deploy Staging") {
        when {
            branch "staging"
        }
        environment {
            cluster="dev-staging"
            zone="us-central1"
            project="staging-auzmor"
            namespace="staging"
            cred_id="staging"
        }
        steps {
            container("gcloud") {
                loginKubernetes credential_id: env.cred_id, cluster_name: env.cluster, zone_name: env.zone, project_name: env.project
                deployKubernetes namespace: env.namespace, type: "MIGRATE", grep: 'calendar-secret', version: version, job: "migrate"
                utility check: "jobs", namespace: env.namespace, grep:"migrate"
                println("Migration job succeeded")
                println("Deploying to ${env.cluster}...") 
                deployKubernetes  namespace: env.namespace ,deployment: 'calendar-backend', imageTag: imageTag
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
            // time out manual approval after ten minutes
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
            container("gcloud") {
                loginKubernetes credential_id: env.cred_id, cluster_name: env.cluster, zone_name: env.zone, project_name: env.project
                deployKubernetes namespace: env.namespace, type: "MIGRATE", grep: 'calendar-secret', version: version, job: "migrate"
                utility check: "jobs", namespace: env.namespace, grep:"migrate"
                println("Migration job succeeded")
                println("Deploying to ${env.cluster}...") 
                deployKubernetes  namespace: env.namespace ,deployment: 'calendar-backend', imageTag: imageTag
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
