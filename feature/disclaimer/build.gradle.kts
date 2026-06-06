plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.disclaimer"
}

dependencies {
    implementation(projects.shared.data.core)
    implementation(projects.shared.ui.core)
    implementation(projects.shared.ui.navigation)
    implementation(libs.appcompat.activity)
    implementation(libs.material)

    testImplementation(projects.shared.ui.testing)
}
