import groovy.json.JsonSlurper

def job_name =  GIT_REPO + "-" + GIT_PROJECT + "-" + GIT_BRANCH +'/' + 'osh-helm-single-node'

def contentsAPI = new URL("https://api.github.com/repos/${GIT_REPO}/${GIT_PROJECT}/contents")
def repositoryContents = new groovy.json.JsonSlurper().parse(contentsAPI.newReader())

job(job_name) {
    label('ubuntu-16.04-slave')
    parameters {
        stringParam('GIT_PROJECT', 'openstack-helm')
        stringParam('GIT_REPO', 'openstack/openstack-helm')
        stringParam('GIT_URL', 'https://github.com/openstack/openstack-helm.git')
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
        gerrit {
            events {
                changeMerged()
                draftPublished()
            }
            project('openstack-helm', ['plain:refs/heads/master'])
            buildSuccessful(10, null)
        }
    }
    steps {
        shell(""" \
            export WORK_DIR=\$(pwd)
            export HOST_OS=\${ID}
            export INTEGRATION=aio
            export INTEGRATION_TYPE=basic
            export PVC_BACKEND=ceph
            ./tools/gate/setup_gate.sh | tee output.log
            OUTPUT=\$(cat output.log)
        """.stripIndent())

    }
    publishers {
        slackNotifier {
            teamDomain(SLACK_TEAM)
            authToken(SLACK_TOKEN)
            room(SLACK_ROOM)
            startNotification(false)
            notifyNotBuilt(false)
            notifyAborted(false)
            notifyFailure(true)
            notifySuccess(true)
            notifyUnstable(true)
            notifyBackToNormal(false)
            notifyRepeatedFailure(true)
            includeTestSummary(true)
            includeCustomMessage(true)
            customMessage('\$OUTPUT')
            sendAs(null)
            commitInfoChoice('NONE')
        }
    }
}
