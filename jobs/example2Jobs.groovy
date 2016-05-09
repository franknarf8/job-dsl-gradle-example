// import hudson.plugins.promoted_builds.integrations.jobdsl.*

freeStyleJob('test-job') {
    properties{
        promotions {
            promotion {
                name('Development')
                conditions {
                    manual('tester')
                }
                actions {
                    shell('echo hello;')
                }
            }
        }
    }
}