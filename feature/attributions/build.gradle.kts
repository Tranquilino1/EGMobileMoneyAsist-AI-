plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.attributions"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.domain)
    implementation(projects.shared.ui.core)
    implementation(projects.shared.ui.navigation)
    implementation(libs.appcompat.activity)
    implementation(libs.material)

    testImplementation(projects.shared.ui.testing)
}
