object Versions {
    const val kotlin_version = "1.2.51"
    const val mockito_version = "2.20.1"
    const val kluent_version = "1.38"
    const val espresso_version = "2.2.2"
    const val appcompat_version = "26.0.2"
    const val firebase_core_version = "10.2.1"
    const val firebase_messaging_version = "10.2.1"
    const val hamcrest_version = "1.3"
    const val support_test_version = "1.0.2"
}

object Libs {
    val appcompat = "com.android.support:appcompat-v7:${Versions.appcompat_version}"
    val support_annotations = "com.android.support:support-annotations:${Versions.appcompat_version}"
    val firebase_core = "com.google.firebase:firebase-core:${Versions.firebase_core_version}"
    val firebase_messaging = "com.google.firebase:firebase-messaging:${Versions.firebase_messaging_version}"
    val espresso_idling_resources = "com.android.support.test.espresso:espresso-idling-resource:${Versions.espresso_version}"
}

object TestLibs {
    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin_version}"
    val kluent = "org.amshove.kluent:kluent-android:${Versions.kluent_version}"
    val mockito_android = "org.mockito:mockito-android:${Versions.mockito_version}"
    val espresso_core = "com.android.support.test.espresso:espresso-core:${Versions.espresso_version}"
    val support_test_runner = "com.android.support.test:runner:${Versions.support_test_version}"
    val support_test_rules = "com.android.support.test:rules:${Versions.support_test_version}"
    val hamcrest_core = "org.hamcrest:hamcrest-core:${Versions.hamcrest_version}"
    val hamcrest_integration = "org.hamcrest:hamcrest-integration:${Versions.hamcrest_version}"
    val hamcrest_library = "org.hamcrest:hamcrest-library:${Versions.hamcrest_version}"
}