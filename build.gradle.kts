plugins {
    java
    jacoco
}

subprojects {
    afterEvaluate {
        if (project.plugins.hasPlugin("jacoco")) {
            tasks.jacocoTestReport {
                executionData(fileTree(projectDir).include("build/jacoco/*.exec"))

                reports {
                    xml.isEnabled = true
                    xml.destination = file("${buildDir}/reports/jacoco/report.xml")
                    html.isEnabled = true
                    html.destination = file("${buildDir}/reports/jacoco/html")
                }
            }
        }

        tasks.check {
            dependsOn(":jacocoTestReport")
        }

        tasks.build {
            dependsOn("check")
        }
    }
}