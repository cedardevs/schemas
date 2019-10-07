plugins {
    java
    jacoco
}

subprojects {
    afterEvaluate {
        tasks.withType<JacocoReport> {
            executionData(fileTree(projectDir).include("build/jacoco/*.exec"))

            reports {
                xml.isEnabled = true
                xml.destination = file("${buildDir}/reports/jacoco/report.xml")
                html.isEnabled = true
                html.destination = file("${buildDir}/reports/jacoco/html")
            }
            dependsOn(":test")
        }

        tasks.check {
            dependsOn(":jacocoTestReport")
        }
    }
}