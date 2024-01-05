object Dependencies {
    object Airfoil {
        val LibCommon = DependencySpec("io.airfoil:lib-common", Versions.Airfoil.LibCommon, SNAPSHOT_VERSION)
    }
    object Exposed {
        val Core = DependencySpec("org.jetbrains.exposed:exposed-core", Versions.Exposed)
        val Dao = DependencySpec("org.jetbrains.exposed:exposed-dao", Versions.Exposed)
        val Datetime = DependencySpec("org.jetbrains.exposed:exposed-kotlin-datetime", Versions.Exposed)
        val Jdbc = DependencySpec("org.jetbrains.exposed:exposed-jdbc", Versions.Exposed)
    }
    object Flyway {
        val Core = DependencySpec("org.flywaydb:flyway-core", Versions.Flyway)
    }
    object Kotest {
        val AssertionsCore = DependencySpec("io.kotest:kotest-assertions-core", Versions.Kotest.Core)
        val FrameworkDataset = DependencySpec("io.kotest:kotest-framework-datatest", Versions.Kotest.Core)
        val FrameworkEngine = DependencySpec("io.kotest:kotest-framework-engine", Versions.Kotest.Core)
        val Property = DependencySpec("io.kotest:kotest-property", Versions.Kotest.Core)
        val RunnerJunit5 = DependencySpec("io.kotest:kotest-runner-junit5", Versions.Kotest.Core)
    }
    object Kotlinx {
        val Datetime = DependencySpec("org.jetbrains.kotlinx:kotlinx-datetime", Versions.Kotlinx.Datetime)
    }
    object Ktor {
        object KotlinxSerialization {
            val Json = DependencySpec("io.ktor:ktor-serialization-kotlinx-json-jvm", Versions.Ktor)
        }
        object Server {
            val Auth = DependencySpec("io.ktor:ktor-server-auth-jvm", Versions.Ktor)
            val AuthJwt = DependencySpec("io.ktor:ktor-server-auth-jwt-jvm", Versions.Ktor)
            val Core = DependencySpec("io.ktor:ktor-server-core-jvm", Versions.Ktor)
        }
    }
    object MicroUtils {
        val KotlinLogging = DependencySpec("io.github.microutils:kotlin-logging-jvm", Versions.MicroUtils)
    }
}
