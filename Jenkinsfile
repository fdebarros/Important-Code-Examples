library identifier: 'jenkins-lib@branch', retriever: modernSCM(github(apiUri: 'https://github.com/api/v3',  credentialsId: 'credential-key', repoOwner: 'org', repository: 'jenkins'))

    print "Starting pipeline"
    print "Starting simple pipeline"
    simplePipelineDelivery {
        releaseType = "push-only"     
    } 
