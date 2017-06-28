import groovy.json.JsonSlurper

def project = GIT_REPO + "-" + GIT_PROJECT
def job_name = project +'/' + 'osh-helm-single-node'

String repo = 'sheehan/grails-example'


def contentsAPI = new URL("https://api.github.com/repos/${GIT_REPO}/${GIT_PROJECT}/contents")
def repositoryContents = new groovy.json.JsonSlurper().parse(contentsAPI.newReader())


job(job_name) {
    label('ubuntu-16.04-slave')
    parameters {
        stringParam('GIT_PROJECT', 'openstack-helm')
        stringParam('GIT_REPO', 'slfletch/openstack-helm-1')
        stringParam('GIT_URL', 'https://github.com/slfletch/openstack-helm-1.git')
        stringParam('GIT_BRANCH', 'master')
        stringParam('SERVER_ID','ArtifactoryPro')
        stringParam('PATCH_VERSION', '0')
        stringParam('MINOR_VERSION', '1')
        stringParam('MAJOR_VERSION', '0')
        stringParam('HELM_VERSION', 'v2.3.1')
        stringParam('KUBE_VERSION', 'v1.6.2')
        stringParam('KUBEADM_IMAGE_VERSION', 'v1.6')
        stringParam('KUBEADM_IMAGE', 'openstackhelm/kubeadm-aio:\$KUBEADM_IMAGE_VERSION')
        stringParam('KUBE_CONFIG', '/home/jenkins/.kubeadm-aio/admin.conf')
    }
    scm{
        github('${GIT_URL}', '${GIT_BRANCH}')
    }
    triggers {
        scm 'H/30 * * * *'
    }
    steps {
        shell(""" \
            export WORK_DIR=\$(pwd)
            export HOST_OS=\${ID}
            export INTEGRATION=aio
            export INTEGRATION_TYPE=notbasic
            export PVC_BACKEND=ceph
            ./tools/gate/setup_gate.sh
        """)
        dsl("""
            println "End!"
            print "ls".execute().text
        """)
    }
}

