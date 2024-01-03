rootProject.name = "lib-airfoil-plugin-auth"

pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
        maven("https://plugins.gradle.org/m2/")
	}
}

include(
	"lib"
)
