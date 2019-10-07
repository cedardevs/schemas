plugins {
    java
    jacoco
}

tasks.build {
    dependsOn("check")
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
//        tasks.withType<JacocoReport> {
//            executionData(fileTree(project.projectDir).include("build/jacoco/*.exec"))
//
//            reports {
//                xml.isEnabled = true
//                xml.destination = file("${project.buildDir}/reports/jacoco/report.xml")
//                html.isEnabled = true
//                html.destination = file("${project.buildDir}/reports/jacoco/html")
//            }
//            dependsOn(":test")
//        }

        tasks.check {
            dependsOn(":jacocoTestReport")
        }
    }
}