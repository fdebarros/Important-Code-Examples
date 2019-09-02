// A common pipeline for common jobs, no need for package anything, copy app codesource directly
/* 
Stages :
 Config : Load job configurations to use along with the next stages, usually from a YAM file.
 Build : Build a docker image
 Release : Publish a Github Release uand Publish the Docker image to Repository (Artifactory)
*/

def call(body) {
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()
	
    pipeline {
      agent any

        environment {        
            APP_CONFIG = credentials('job_config')            
        }  

      stages {
        stage('Config') {
            steps {                
                script {      
                  GIT_LAST_COMMIT_AUTHOR =   sh returnStdout: true, script: 'git show -s --pretty=%an'     
                  GIT_LAST_COMMIT_AUTHOR = GIT_LAST_COMMIT_AUTHOR.trim()
                  GIT_LAST_COMMIT_FILE = sh returnStdout: true, script: 'git show --pretty="" --name-only' 
                  GIT_LAST_COMMIT_FILE = GIT_LAST_COMMIT_FILE.trim()
                  echo "Loading YAML config for ${env.JOB_NAME}"
                  app_config_file = sh returnStdout: true, script: 'set x+ && cat $APP_CONFIG > job_config_file.yaml'                     
                  CONF = readYaml(file: "job_config_file.yaml")
                  
                  if ( CONF["artifactory"] != null)
                  {
                    ARTIFACTORY_API_KEY  = sh returnStdout: false, script: "set x+ && \$(echo ${CONF.artifactory.artifactory_key} >> artifactory_key)"
                    ARTIFACTORY_USER  = sh returnStdout: false, script: "set x+ && \$(echo ${CONF.artifactory.artifactory_write_user} >> artifactory_write_user)"
                    echo "Using Config File. VERSION: ${CONF.version}"                  
                  }
			
                }
            }
        }        
        stage('Build') {
            when { 

                not { expression { "${GIT_LAST_COMMIT_FILE}".endsWith(".md") } }

                allOf 
                    {   
                         not { expression { "${GIT_LAST_COMMIT_FILE}".equalsIgnoreCase("VERSION") } }                     
                     }                    
                }                      
          steps {
            script {
              echo "Building...."          
              message(CONF,"${STAGE_NAME}", "START")
              buildProject CONF
              message(CONF,"${STAGE_NAME}", "END")
            }
          }
        }
        stage('Release') {
            when { 
                not { expression { "${GIT_LAST_COMMIT_FILE}".endsWith(".md") } }
              
                allOf 
                    {   
                         not { expression { "${GIT_LAST_COMMIT_FILE}".equalsIgnoreCase("VERSION") } }                     
                     }                    
                }                                  
          steps {
            script {
              echo "Releasing...."          
              message(CONF,"${STAGE_NAME}", "START")
              release (CONF, pipelineParams.releaseType)
              message(CONF,"${STAGE_NAME}", "END")
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
  
      post {
              success {
                  message(CONF,"${STAGE_NAME}", "SUCCESS")
                  }

              failure {
                  message(CONF,"${STAGE_NAME}", "FAILURE")
                  deleteDir() 
              }       
              cleanup {
                  echo "Run cleanup workspace"
                  deleteDir() 
              }
          } 
    }
}
