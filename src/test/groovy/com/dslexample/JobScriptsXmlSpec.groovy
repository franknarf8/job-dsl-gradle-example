package com.dslexample

import groovy.io.FileType
import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.plugin.JenkinsJobManagement
import javaposse.jobdsl.plugin.MemoryJobManagementCustom
import org.junit.ClassRule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Tests that all dsl scripts in the jobs directory will compile.
 */
class JobScriptsXmlSpec extends Specification {
    @Shared
    @ClassRule
    JenkinsRule jenkinsRule = new JenkinsRule()

    def writeFile(File dir, String name, String xml) {
        List tokens = name.split('/')
        File folderDir = dir
        tokens[0..<tokens.size() - 1].each { String token ->
            folderDir = new File(folderDir, token)
        }
        folderDir.mkdirs()

        File xmlFile = new File(folderDir, "${tokens[-1]}.xml")
        xmlFile.text = xml
        println new File('.').toURI().relativize(xmlFile.toURI()).toString()
    }

    @Unroll
    void 'test script #file.name'(File file) {
        given:
        MemoryJobManagementCustom jm = new MemoryJobManagementCustom(System.out, [:], new File('.'))

        new File('resources').eachFileRecurse(FileType.FILES) {
            jm.availableFiles[it.path.replaceAll('\\\\', '/')] = it.text
        }

        when:
        new DslScriptLoader(jm).runScript(file.text)

        File outputDir = new File('./build/debug-xml')
        outputDir.deleteDir()

        if (jm.savedConfigs) {
            File dir = new File(outputDir, 'jobs')

            jm.savedConfigs.each { String name, String xml ->
                writeFile dir, name, xml
            }
        }

        if (jm.savedViews) {
            File dir = new File(outputDir, 'views')
            dir.mkdirs()

            jm.savedViews.each { String name, String xml ->
                writeFile dir, name, xml
            }
        }

        then:
        noExceptionThrown()

        where:
        file << jobFiles
    }

    static List<File> getJobFiles() {
        return [new File('jobs/example1Jobs.groovy')]
    }
}

