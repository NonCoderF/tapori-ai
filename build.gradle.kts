allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

val rootBuildDirectory = layout.buildDirectory.dir("build").get()
layout.buildDirectory.set(rootBuildDirectory)

subprojects {
    val subprojectBuildDirectory = rootBuildDirectory.dir(project.name)
    layout.buildDirectory.set(subprojectBuildDirectory)
}

subprojects {
    evaluationDependsOn(":app")
}

tasks.register<Delete>("clean") {
    delete(rootBuildDirectory)
}

