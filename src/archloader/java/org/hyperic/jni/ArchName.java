/*
 * Copyright (c) 2009 Hyperic, Inc.
 * Copyright (c) 2009 SpringSource, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hyperic.jni;

import java.util.Locale;

/**
 * Helper class for naming the jni library with platform/arch name for which it
 * is binary compatible.
 */
public class ArchName {

	private final static String osName = System.getProperty("os.name");
	public final static boolean IS_WINDOWS = osName.startsWith("Windows");
	public final static boolean IS_AIX = osName.equals("AIX");
	public final static boolean IS_HPUX = osName.equals("HP-UX");
	public final static boolean IS_SOLARIS = osName.equals("SunOS");
	public final static boolean IS_LINUX = osName.equals("Linux");
	public final static boolean IS_OSX = osName.equals("Mac OS X") || osName.equals("Darwin");
	public final static boolean IS_OSF1 = osName.equals("OSF1");
	public final static boolean IS_FREEBSD = osName.equals("FreeBSD");
	public final static boolean IS_OPENBSD = osName.equals("OpenBSD");
	public final static boolean IS_NETBSD = osName.equals("NetBSD");
	public final static boolean IS_NETWARE = osName.equals("NetWare");

	static boolean useDmalloc = System.getProperty("jni.dmalloc") != null;

	public static String getName() {
		String name = getOsAndArch();
		if (name != null && useDmalloc) {
			name += "-dmalloc";
		}
		return name;
	}

	public static boolean is64() {
		return "64".equals(System.getProperty("sun.arch.data.model"));
	}

	public static String getOsAndArch() {
		String name = System.getProperty("os.name");
		String version = System.getProperty("os.version");
		int ix = version.contains(".") ? version.indexOf(".") : version.length();
		String majorVersion = version.substring(0, ix); // 4.x, 5.x, etc.
		int ix2 = version.indexOf(".", ix);
		String minorVersion;
		if (ix2 > ix) {
			minorVersion = version.substring(ix, ix2);
		} else {
			minorVersion = version;
		}

		String arch = getArch();

		if (IS_LINUX) {
			return "linux-" + arch;
		} else if (IS_WINDOWS) {
			return "windows-" + arch;
		} else if (IS_SOLARIS) {
			return "solaris-" + arch;
		} else if (IS_HPUX) {
			return "hpux-" + minorVersion + "-" + arch;
		} else if (IS_AIX) {
			if (majorVersion.equals("6")) {
				// v5 binary is compatible with v6
				majorVersion = "5";
			}
			return  "aix-" + majorVersion + "-" + arch;
		} else if (IS_OSX) {
			if (is64()) {
				return "osx-" + arch;
			} else {
				return "osx-" + arch;
			}
		} else if (IS_FREEBSD) {
			// none of the 4,5,6 major versions are binary compatible
			return "freebsd-" + majorVersion + "-" + arch;
		} else if (IS_OPENBSD) {
			return "openbsd-" + majorVersion + "-" + arch;
		} else if (IS_NETBSD) {
			return "netbsd-" + majorVersion + "-" + arch;
		} else if (IS_OSF1) {
			return "osf1-" + majorVersion + "-" + arch;
		} else if (IS_NETWARE) {
			return "netware-" + majorVersion + "-" + arch;
		}

		return name + "-" + version + "-" + arch;
	}

	public static String getArch() {
		String arch = System.getProperty("os.arch");
		arch = arch.toLowerCase(Locale.ROOT);
		if (arch.contains("86") || arch.equals("amd64")) {
			if (is64()) {
				arch = "x64";
			} else {
				arch = "x86";
			}
		} else if (arch.contains("arm")) {
			if (is64()) {
				arch = "arm64";
			} else {
				arch = "arm";
			}
		} else if (arch.contains("power") || arch.contains("ppc")) {
			if (is64()) {
				arch = "ppc64";
			} else {
				arch = "ppc";
			}
		} else if (arch.startsWith("sparc")) {
			if (is64()) {
				arch = "sparc64";
			} else {
				arch = "sparc";
			}
		} else if (arch.startsWith("ia64")) {
			arch = "ia64";
		} else if (arch.startsWith("pa")) {
			if (is64()) {
				arch = "pa64";
			} else {
				arch = "pa";
			}
		}
		return arch;
	}

	private ArchName() {
	}

}
