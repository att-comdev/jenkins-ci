#!/usr/bin/env groovy

properties([
    [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false],
    parameters([
        string(defaultValue: 'nova glance kestone',
            description: 'components with custom images',
            name: 'COMPONENTS_LIST'
            ),
            booleanParam(
                defaultValue: true,
                description: 'Install/Re-install openstack-kolla, If your slave already have it installed you can set it to false (uncheck)',
                name: 'INSTALL_KOLLA'
                ),
            booleanParam(
                defaultValue: true,
                description: 'Install/Re-install openstack-helm, If your slave already have it installed you can set it to false (uncheck)',
                name: 'INSTALL_OSH'
                )
    ]),

    [$class: 'ThrottleJobProperty', categories: [],
        limitOneJobWithMatchingParams: false, 
        maxConcurrentPerNode: 0,
        maxConcurrentTotal: 0,
        paramsToUseForLimit: '',
        throttleEnabled: false,
        throttleOption: 'project'
    ],
    pipelineTriggers([])
])

node('slave') {
    stage('slave precheck') {
        sh '''#!/bin/bash -xe
              sudo apt-get install -y python-pip libpython-all-dev libpython3-all-dev libffi-dev libssl-dev gcc git ntp tox ansible docker.io
              if [ -z "${COMPONENTS_LIST}" ];then
                echo "ERROR: COMPONENTS_LIST parameter is empty"
                exit 1
              fi
              echo ${COMPONENT_LIST}
              echo 'Precheck complete!'
            '''
    }


    stage('Results') {
        sh '''#!/bin/bash -xe
            echo "${COMPONENT_LIST}"
            echo "OK"
        '''
        slackSend channel: '#test-jenkins',
                  color: 'good',
                  message: "started ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)",
                  teamDomain: 'att-comdev',
                  tokenCredentialId: 'comdev-slack'
    }

        slackSend channel: '#test-jenkins',
                  color: 'good',
                  message: 'SUCCESS!',
                  teamDomain: 'att-comdev',
                  tokenCredentialId: 'comdev-slack'
}
