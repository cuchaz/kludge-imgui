
import org.gradle.internal.os.OperatingSystem

val os = OperatingSystem.current()

plugins {
	`cpp-library`
}

group = "cuchaz"
version = "1.67"

components.withType<CppBinary> {

	val releaseType =
		when (isOptimized) {
			true -> "Release"
			false -> "Debug"
		}

	fun findLib(libname: String): File {
		val filename = System.mapLibraryName(libname)
		for (path in System.getProperty("java.library.path").split(":")) {
			val file = File(path, filename)
			if (file.exists()) {
				return file
			}
		}
		throw NoSuchElementException("can't find lib $libname")
	}

	project.dependencies {

		fun link(spec: Any) = add("nativeLink$releaseType", spec)
		fun runtime(spec: Any) = add("nativeRuntime$releaseType", spec)

		link(files(findLib("glfw")))
		link(files(findLib("vulkan")))
	}
}

library {

	linkage.set(listOf(Linkage.SHARED))

	binaries.configureEach {

		// make the impl functions exportable using C conventions
		compileTask.get().apply {
			val api = when (OperatingSystem.current()) {
				OperatingSystem.WINDOWS -> "extern \"C\" __declspec(dllexport)"
				else -> "extern \"C\""
			}
			compilerArgs.add("-DIMGUI_IMPL_API=$api")
		}
	}
}
