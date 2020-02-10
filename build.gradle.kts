
import org.gradle.internal.os.OperatingSystem
import com.sun.jna.Platform

val os = OperatingSystem.current()

buildscript {
	dependencies {
		classpath("net.java.dev.jna:jna:5.2.0")
	}
}

plugins {
	`cpp-library`
}

group = "cuchaz"
version = "1.67"

library {

	linkage.set(listOf(Linkage.SHARED))

	binaries.configureEach {

		// make the impl functions exportable using C conventions
		compileTask.get().apply {

			println("Compiling for platform: ${Platform.RESOURCE_PREFIX}")

			val api = when (OperatingSystem.current()) {
				OperatingSystem.WINDOWS -> "extern \"C\" __declspec(dllexport)"
				else -> "extern \"C\""
			}
			compilerArgs.add("-DIMGUI_IMPL_API=$api")
		}

		// tell the linker about the library dependencies
		if (this is ComponentWithSharedLibrary) {
			linkTask.get().apply {

				val flags = when (OperatingSystem.current()) {

					// linux makes things easy
					// just install the vulkan and glfw dev packages
					OperatingSystem.LINUX -> listOf("-lvulkan", "-lglfw")

					// windows makes things hard
					// download the vulkan and glfw SDKs and make sure these library files are available to the linker
					// put them in e.g.: C:\Program Files (x86)\Microsoft Visual Studio\2019\Community\VC\Tools\MSVC\14.22.27905\lib
					// also, rename glfw3dll.lib to glfw.lib using e.g.:
					// https://github.com/cmberryau/rename_dll/blob/master/rename_dll.py
					OperatingSystem.WINDOWS -> listOf("vulkan-1.lib", "glfw.lib")

					// TODO: OSX

					else -> throw UnsupportedOperationException("unsupported operating system: ${OperatingSystem.current()}")
				}

				linkerArgs.addAll(flags)
			}
		}
	}
}

