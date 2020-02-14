
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

			when (OperatingSystem.current()) {

				// linux makes things easy
				OperatingSystem.LINUX -> {
					// nothing to do =)
				}

				// windows makes things hard
				OperatingSystem.WINDOWS -> {
					// add the include folders to the compiler
					// (make sure to edit these paths to point to your install locations)
					compilerArgs.addAll(
						"/IC:\\VulkanSDK\\1.2.131.2\\Include",
						"/IC:\\glfw-3.3.2.bin.WIN64\\include"
					)
				}
			}
		}

		// tell the linker about the library dependencies
		if (this is ComponentWithSharedLibrary) {
			linkTask.get().apply {

				val flags = when (OperatingSystem.current()) {

					// linux makes things easy
					// just install the vulkan and glfw dev packages
					OperatingSystem.LINUX -> listOf("-lvulkan", "-lglfw")

					// windows makes things hard
					// download the vulkan SDK and the glfw binary distribution
					// rename glfw3.dll to glfw.dll using e.g.:
					// https://github.com/cmberryau/rename_dll/blob/master/rename_dll.py
					// (make sure to edit these paths to point to your install locations)
					OperatingSystem.WINDOWS -> listOf(
						"/LIBPATH:C:\\VulkanSDK\\1.2.131.2\\Lib",
						"/LIBPATH:C:\\glfw-3.3.2.bin.WIN64\\lib-vc2019",
						"vulkan-1.lib", "glfw.lib"
					)

					// TODO: OSX

					else -> throw UnsupportedOperationException("unsupported operating system: ${OperatingSystem.current()}")
				}

				linkerArgs.addAll(flags)
			}
		}
	}
}

