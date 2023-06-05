#!/usr/bin/env groovy
@Library('shared-library') _

pipeline {
  agent any
  options {
    buildDiscarder(logRotator(numToKeepStr:'2' , artifactNumToKeepStr: '2'))
    timestamps()
    }
  stages {
    stage('CheckOut') {
      steps {
        echo 'Checking out project from Bitbucket....'
        cleanWs()
        checkout([
          $class: 'GitSCM',
          branches: [[name: 'main']],
          userRemoteConfigs: [[url: 'git@github.com:vamsi8977/ant_sample.git']]
        ])
      }
    }
    stage('Build') {
      steps {
        script {
          withSonarQubeEnv('SonarQube') {
            ant()
          }
        }
      }
    }
  }
  post {
    success {
      echo "The build passed."
    }
    failure {
      echo "The build failed."
    }
    cleanup {
      deleteDir()
    }
  }
}