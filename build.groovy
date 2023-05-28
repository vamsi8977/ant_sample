pipeline {
agent any
options {
    buildDiscarder(logRotator(numToKeepStr:'2' , artifactNumToKeepStr: '2'))
    timestamps()
    }
  stages {
    stage('SCM') {
      steps {
        cleanWs()
        echo 'Checking out project from Bitbucket....'
        git branch: 'main', url: 'git@github.com:vamsi8977/ant_sample.git'
      }
    }
    stage('Build') {
      steps {
        ansiColor('xterm') {
          echo 'Ant Build....'
          sh "ant -buildfile build.xml"
        }
      }
    }
    stage('SonarQube') {
      steps {
        withSonarQubeEnv('SonarQube') {
          sh "sonar-scanner"
        }
      }
    }
    stage('JFrog') {
      steps {
        ansiColor('xterm') {
          sh '''
            jf rt u build/jar/*.jar ant/
            jf scan build/jar/*.jar --fail-no-op --build-name=gradle --build-number=$BUILD_NUMBER
          '''
        }
      }
    }
  }//end stages
  post {
    success {
      archiveArtifacts artifacts: "build/jar/*.jar"
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