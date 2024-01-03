object Dependencies {
    object Airfoil {
        val LibCommon = DependencySpec("io.airfoil:lib-common", Versions.Airfoil.LibCommon, SNAPSHOT_VERSION)
    }
    object Kotest {
        val AssertionsCore = DependencySpec("io.kotest:kotest-assertions-core", Versions.Kotest.Core)
        val FrameworkDataset = DependencySpec("io.kotest:kotest-framework-datatest", Versions.Kotest.Core)
        val FrameworkEngine = DependencySpec("io.kotest:kotest-framework-engine", Versions.Kotest.Core)
        val Property = DependencySpec("io.kotest:kotest-property", Versions.Kotest.Core)
        val RunnerJunit5 = DependencySpec("io.kotest:kotest-runner-junit5", Versions.Kotest.Core)
    }
}
