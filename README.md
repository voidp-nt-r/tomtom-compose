# TomTom Maps Compose 📍
This is an **unofficial** library for integrating TomTom map components into **Jetpack Compose** projects.

## Requirements:
- Android API level 26+
- Java version 8+
- Kotlin version 1.8.0+
- Support for OpenGL 3.0
- Your API key from [tomtom developer portal](https://developer.tomtom.com/).

## Installation:
To use this library, include the necessary TomTom dependencies by adding the official TomTom repository and this repository.

### Step 1 Add Repositories:
In your `gradle.settings.kt` file, locate the `dependencyResolutionManagement` block and add the TomTom and JitPack repositories:
```kt
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://repositories.tomtom.com/artifactory/maven") // TomTom repository.
        }
        maven {
            url = uri("https://jitpack.io") // JitPack reposotory for tomtom-compose library.
        }
    }
}
```

### Step 2 Add dependencies:
<details>
    <summary> Build dependecy management </summary>

Locate in to your `build.gradle.kt` project level file and add the following lines of code:

```kt
    val tomtomComposeVersion = "v0.1.0"
    val tomtomMapDisplay = "1.15.0"

    dependencies {
        
        implementation("com.github.voidp-nt-r:tomtom-compose:$tomtomComposeVersion") 
        implementation("com.tomtom.sdk.maps:map-display:$tomtomMapDisplay")
    }
```

</details>

<details>
<summary> Version catalog </summary>

If you are using version catalog, then locate in your `libs.versions.toml` and add the following versions and dependencies:
```toml
[versions]
tomtomMapDisplay = "1.15.0"
tomtomCompose = "v0.1.0"

#...

[libraries]
map-display = { module = "com.tomtom.sdk.maps:map-display", version.ref = "tomtomMapDisplay" }
tomtom-compose = { module= "com.github.voidp-nt-r:tomtom-compose", version.ref = "tomtomCompose" }

```
Now go to your `build.gradle.kts` module/project level file and add the following lines of code:
```kt
dependencies {
    implementation(libs.map.display)
    implementation(libs.tomtom.compose)
}
```

</details>

### Step 3 Prevent Library Duplication:
To avoid issues with duplicated `libc++_shared.so` libraries, include the following in your app module's `build.gradle`:
```kt
android {
    packaging {
        jniLibs.pickFirsts.add("lib/**/libc++_shared.so")
    }
}
```


## Documentation:
You can find al the documentation about the library [here](https://voidp-nt-r.github.io/tomtom-compose/).

## Usage:
Set up a map view composable:
```kt
val mapOptions = MapOptions(mapKey = "YOUR API KEY")
val cameraState = rememberCamerastate()

TomTomMap(
        modifier = Modifier.fillMaxSize(),
        mapOptions = mapOptions,
        cameraState = cameraState
    ) {
       // Map content goes here.
    }
```
> [!WARNING]
> Avoid using a string literal for your API key, please refer the official documentation for the recommended setup of your API key.




## Roadmap to `v1.0.0`🏷:

- [x] Create a composable from MapView.
- [ ] Manage MapView clik listeners.
- [ ] Manage camera state (currently, is possible to modify the camera position but retrieving the current camera position is not).
- [ ] Integrate full UI controls of the MapView.
- [ ] Integrate a location provider.
- [x] Integrate marker as a TomTomMap composable.
- [x] Integrate map overlays as a composables.
- [x] Integrate route as a TomTomMap composable.
- [ ] Adding clustering feature to group map components.

## Contributing:
There's plenty of work to be done, so any contributions are welcome!
