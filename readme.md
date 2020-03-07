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

Prerequisites: Make sure the Vulkan and GLFW SDKs are installed for your platform.

On Windows and OSX, create a `gradle.properties` file with the paths to your installation locations
for header files (`include`), and dynamic libraries (`lib`):
```
systemProp.paths.vulkan.include = /path/to/folder
systemProp.paths.glfw.include = /path/to/folder

systemProp.paths.vulkan.lib = /path/to/folder
systemProp.paths.glfw.lib = /path/to/folder
```
On Linux, specifying these paths via `gradle.properties` is not necessary, since the package
manager will tell the compiler where they are.

Finally, to build Kluge-ImGUI, simply run:
```./gradlew assembleRelease```

If the compilation worked, the binary for your platform should appear in `build/lib/main/release`
