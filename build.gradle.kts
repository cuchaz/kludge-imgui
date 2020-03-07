
import org.gradle.internal.os.OperatingSystem
import com.sun.jna.Platform


buildscript {
	dependencies {
		classpath("net.java.dev.jna:jna:5.2.0")
	}
}

plugins {
	`cpp-library`
}

group = "cuchaz"
version = "1.75"


val os = OperatingSystem.current()


class Lib(val name: String) {

	val includeName = "paths.$name.include"
	val libName = "paths.$name.lib"

	fun get(prop: String) =
		System.getProperty(prop)
			?: throw NoSuchElementException("""
					|Need system property "$prop" on platform $os.
					|Add it to gradle.properties like this:
					|systemProp.$prop = /path/to/folder
				""".trimMargin())

	val include get() = get(includeName)
	val lib get() = get(libName)
}

inner class Paths {
	val vulkan = Lib("vulkan")
	val glfw = Lib("glfw")
}
val paths = Paths()

library {

	println("Platform: ${Platform.RESOURCE_PREFIX}")

	linkage.set(listOf(Linkage.SHARED))

	binaries.configureEach {

		// make the impl functions exportable using C conventions
		compileTask.get().apply {

			val api = when (os) {
				OperatingSystem.WINDOWS -> "extern \"C\" __declspec(dllexport)"
				else -> "extern \"C\""
			}
			compilerArgs.add("-DIMGUI_IMPL_API=$api")

			when (os) {

				// linux makes things easy
				OperatingSystem.LINUX -> {
					// nothing to do =)
				}

				// windows makes things hard
				OperatingSystem.WINDOWS -> {
					// add the include folders to the compiler
					compilerArgs.addAll(
						"/I${paths.vulkan.include}",
						"/I${paths.glfw.include}"
					)
				}

				// OSX makes things hard
				OperatingSystem.MAC_OS -> {
					// add the include folders to the compiler
					compilerArgs.addAll(
						"-I${paths.vulkan.include}",
						"-I${paths.glfw.include}"
					)
				}
			}
		}

		// tell the linker about the library dependencies
		if (this is ComponentWithSharedLibrary) {
			linkTask.get().apply {

				when (os) {

					// linux makes things easy
					// just install the vulkan and glfw dev packages
					OperatingSystem.LINUX -> {
						linkerArgs.addAll(
							"-lvulkan",
							"-lglfw"
						)
					}

					// windows makes things hard
					// download the vulkan SDK and the glfw binary distribution
					// rename glfw3.dll to glfw.dll using e.g.:
					// https://github.com/cmberryau/rename_dll/blob/master/rename_dll.py
					// (make sure to edit these paths to point to your install locations)
					OperatingSystem.WINDOWS -> {
						linkerArgs.addAll(
							"/LIBPATH:${paths.vulkan.lib}", "vulkan-1.lib",
							"/LIBPATH:${paths.glfw.lib}", "glfw.lib"
						)
					}

					// OSX makes things hard
					// download and install the Vulkan SDK for OSX, (using MoltenVK),
					// and the glfw binary distribution
					OperatingSystem.MAC_OS -> {
						linkerArgs.addAll(
							"-L${paths.vulkan.lib}", "-lMoltenVK",
							"-L${paths.glfw.lib}", "-lglfw.3",
							"-framework", "Cocoa",
							"-framework", "IOKit"
						)
					}
				}
			}
		}
	}
}

