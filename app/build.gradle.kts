import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.legaciesscreenprintersapk"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        // The app's permanent identity on Google Play (Play rejects "com.example...").
        applicationId = "za.co.legaciesscreenprinters.app"
        minSdk = 26
        // Play requires a recent target API level. Check the current one in Play Console.
        targetSdk = 36
        // versionCode must increase with every upload to Play; versionName is what users see.
        versionCode = 2
        versionName = "2.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Google sign-in: the OAuth *web* client id from Google Cloud Console.
        val googleWebClientId = prop("googleWebClientId") ?: ""
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$googleWebClientId\"")
    }

    // API address: debug uses apiBaseUrl (default is the emulator address), release needs an https apiBaseUrlRelease.
    val debugApiUrl = prop("apiBaseUrl") ?: "http://10.0.2.2:5000/"
    val releaseApiUrl = prop("apiBaseUrlRelease") ?: prop("apiBaseUrl") ?: ""

    // Release signing (upload key).
    // Reference: Android Developers (2025) Sign your app - https://developer.android.com/studio/publish/app-signing
    val signing = releaseSigning()
    signingConfigs {
        if (signing != null) {
            create("release") {
                storeFile = file(signing.getValue("storeFile"))
                storePassword = signing.getValue("storePassword")
                keyAlias = signing.getValue("keyAlias")
                keyPassword = signing.getValue("keyPassword")
            }
        }
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_BASE_URL", "\"$debugApiUrl\"")
        }
        release {
            buildConfigField("String", "API_BASE_URL", "\"$releaseApiUrl\"")
            // R8 shrinks and obfuscates the code and removes unused resources.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (signing != null) {
                signingConfig = signingConfigs.getByName("release")
            } else {
                logger.warn("Release signing not configured (no keystore.properties or KEYSTORE_* variables): release builds will be unsigned.")
            }
        }
        // QA build: same as release (R8 on) but uses the local API and debug key. Not for upload.
        create("qa") {
            initWith(getByName("release"))
            applicationIdSuffix = ".qa"
            versionNameSuffix = "-qa"
            buildConfigField("String", "API_BASE_URL", "\"$debugApiUrl\"")
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    testOptions {
        // Lets unit tests call android.util.Log without crashing.
        unitTests.isReturnDefaultValues = true
    }
}

/** Reads a value from the untracked local.properties file, if present. */
fun localProperty(key: String): String? {
    val file = rootProject.file("local.properties")
    if (!file.exists()) return null
    val props = Properties()
    file.inputStream().use { props.load(it) }
    return props.getProperty(key)?.takeIf { it.isNotBlank() }
}

/** Release signing values from keystore.properties (local) or KEYSTORE_* environment variables (CI). */
fun releaseSigning(): Map<String, String>? {
    val file = rootProject.file("keystore.properties")
    val values = if (file.exists()) {
        val props = Properties().apply { file.inputStream().use { load(it) } }
        mapOf(
            "storeFile" to props.getProperty("storeFile"),
            "storePassword" to props.getProperty("storePassword"),
            "keyAlias" to props.getProperty("keyAlias"),
            "keyPassword" to props.getProperty("keyPassword"),
        )
    } else {
        mapOf(
            "storeFile" to System.getenv("KEYSTORE_FILE"),
            "storePassword" to System.getenv("KEYSTORE_PASSWORD"),
            "keyAlias" to System.getenv("KEY_ALIAS"),
            "keyPassword" to System.getenv("KEY_PASSWORD"),
        )
    }
    return if (values.values.all { !it.isNullOrBlank() }) values.mapValues { it.value!! } else null
}

/** A build setting from -P / gradle.properties first, then local.properties. */
fun prop(key: String): String? =
    (project.findProperty(key) as String?)?.takeIf { it.isNotBlank() } ?: localProperty(key)

// Release builds must talk to the API over HTTPS: stop the build with a clear message otherwise.
val checkReleaseApiUrl by tasks.registering {
    val url = prop("apiBaseUrlRelease") ?: prop("apiBaseUrl") ?: ""
    doLast {
        require(url.startsWith("https://") && url.endsWith("/")) {
            "Release builds need an HTTPS API address ending in '/'. Set apiBaseUrlRelease=https://<host>/ " +
                "in local.properties or pass -PapiBaseUrlRelease=... (current value: '$url')."
        }
    }
}
tasks.matching { it.name == "preReleaseBuild" }.configureEach { dependsOn(checkReleaseApiUrl) }

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    testImplementation(libs.junit)
    testImplementation(libs.okhttp.mockwebserver)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}