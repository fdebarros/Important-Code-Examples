// A common pipeline for common jobs, no need for package anything, copy app codesource directly
/* 
Stages :
 Config : Load job configurations to use along with the next stages, usually from a YAM file.
 Build : Build a jar file using maven
 Release : Publish a Github Release and Publish the jar file to Repository (Artifactory)
*/

def call(body) {
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()
  
    pipeline {
    
     agent {
         docker {
            image 'maven:3.6.0-jdk-12-alpine'
            args '-v $WORKSPACE:/tmp/ws1 -u="root" -w /tmp/ws1'
            args '-v /root/.m2:/root/.m2'
        }
    }
       
    environment {
        MVN_LOCAL_REPO = '.repository'
        JAVA_TOOL_OPTIONS= '-Dfile.encoding=windows-1252'
        APP_CONFIG = credentials('settings-xml')  
        SETTINGS_FILE_CRED = credentials('settings-xml')     
    }

    stages {
        
        stage ('Build Stage') {
        
            steps {
                script {
                    SETTINGS_FILE = sh returnStdout: false, script: 'cat $SETTINGS_FILE_CRED > /root/.m2/settings.xml'                 
                    sh 'mvn  clean install -Dspring.profiles.active=prod -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn'
                }
            }
        }
        
        stage ('Compile Stage') {

            steps {
                script {
                    sh 'mvn clean compile'
                }
            }
        }

        stage ('Testing Stage') {

            steps {
                script {
                    sh 'mvn test'
                }
            }
        }


        stage ('Deployment Stage') {
            steps {
                script {
                    sh 'mvn deploy'
                }
            }
        }

        stage('Build Dependent Projects') {
        steps {
                echo "Checking Building Dependent Projects"
                message(CONF,"CUSTOM", "Checking building dependent projects")
                script 
                {    
                    if (pipelineParams["dependentProjects"] != null)     
                        {
                          pipelineParams.dependentProjects.split(',').each {
                          BUILD_ITEM="${it}"
                          echo "Building Dependent project ${it}"
                          build job: "${BUILD_ITEM}", propagate: false, wait: false
                        }
                    }            
                }
        }
      }
    } 
  }
}

