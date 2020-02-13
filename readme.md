# Kludge-ImGUI

*Kludge-ImGUI provides platform-specific binaries that integrate
[Kludge](https://github.com/cuchaz/kludge) with [Dear ImGUI](https://github.com/ocornut/imgui).*

---

Kludge-ImGUI provides an interface for native code suitable for integrating with the JVM via the
[JNA](https://github.com/java-native-access/jna) library.
Kludge-ImGUI exposes code directly from ImGUI when compatible, but also automatically-generated
C-style wrappers from [cimgui](https://github.com/cimgui/cimgui).



## License

[MIT](license.txt)


## Building

Since I'm still avoiding learning CMake, this project uses [Gradle](https://gradle.org/)
to build platform-specific binaries. Gradle's support for building C/C++ projects
seems to be [rather new and experimental](https://blog.gradle.org/introducing-the-new-cpp-plugins),
but so far it seems to work pretty well.

Kludge is in very early development and is not yet included in any artifact repository.

Prerequisites: Make sure the Vulkan SDK and the GLFW SDKs are installed.

To build Kluge-ImGUI, simply run:
```./gradlew assembleRelease```

NOTE: Tragically, building on Windows is much much more complicated.
See the comments in `build.gradle.kts` for more instructions.

If the compilation worked, the binary for your platform should appear in `build/lib/main/release`
