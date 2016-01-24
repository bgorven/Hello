/*
 * Copyright (c) 2009 Hyperic, Inc.
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

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;

public class ArchLoader implements AutoCloseable {

	private final String name;
	private final Class<?> loaderClass;
	private Path tmpFile;

	/**
	 * Extracts the appropriate library file from the classpath, and returns it
	 * to be loaded, e.g: <code><pre>
	 * static {
	 *   try (ArchLoader loader = new ArchLoader(Native.class)) {
	 *     System.load(loader.getLibFile());
	 *   }
	 * }
	 * </pre></code>
	 * 
	 * @param loaderClass
	 *            the default classpath location will be derived from this
	 *            class.
	 * @throws NullPointerException
	 *             if {@link Class#getPackage()} returns null.
	 */
	public ArchLoader(Class<?> loaderClass) throws NullPointerException {
		this(loaderClass, null);
	}

	/**
	 * Extracts the appropriate library file from the classpath, and returns it
	 * to be loaded. <code><pre>
	 * static {
	 *   try (ArchLoader loader = new ArchLoader(Native.class, "1.0.0")) {
	 *     System.load(loader.getLibFile());
	 *   }
	 * }
	 * </pre></code>
	 * 
	 * @param loaderClass
	 *            the default classpath location will be derived from this
	 *            class.
	 * @param version
	 *            optional version to append
	 * @throws NullPointerException
	 *             if {@link Class#getPackage()} returns null.
	 */
	public ArchLoader(Class<?> loaderClass, String version) throws NullPointerException {
		this.loaderClass = loaderClass;
		String pname = loaderClass.getPackage().getName();
		int ix = pname.lastIndexOf(".");
		String name = pname.substring(ix + 1);
		if (version != null) {
			name = name + "-" + version;
		}
		this.name = System.mapLibraryName(name);
	}

	/**
	 * The default lib name will be derived from the last segment of the class'
	 * package name, plus the version, if one was supplied, separated by a
	 * hyphen.
	 * 
	 * @return the base library name, as mapped by
	 *         {@link System#mapLibraryName(String)}
	 */
	public String getName() {
		return name;
	}

	protected Class<?> getLoaderClass() {
		return loaderClass;
	}

	/**
	 * Returns a location for native libraries that can be configured on a
	 * per-package basis, or a default location for the current architecture.
	 * 
	 * @return the value of the system property "${packageName}.nativelibs", or
	 *         {@link ArchName#getName()} if unset.
	 */
	public String getLibLocation() {
		return System.getProperty(getLoaderClass().getPackage().getName() + ".nativelibs",
				"lib/" + ArchName.getName());
	}

	/**
	 * Returns the file name of the native library to be loaded.
	 * 
	 * @return the value of the system property "${className}.nativelib", or
	 *         {@link #getName()} if unset.
	 */
	public String getLibName() {
		return System.getProperty(getLoaderClass().getName() + ".nativelib", getName());
	}

	/**
	 * Lib path will be a file with name {@link #getLibName()} within directory
	 * {@link ArchLoader#getLibLocation()}
	 * 
	 * @return a relative path to the lib file; this will typically be resolved
	 *         against classpath root.
	 */
	public String getLibPath() {
		String libLoc = trimSeparators(getLibLocation());
		String libName = trimSeparators(getLibName());
		return libLoc + "/" + libName;
	}

	private static String trimSeparators(String path) {
		while (path.startsWith(File.separator) || path.startsWith("/")) {
			path = path.substring(1);
		}
		while (path.endsWith(File.separator) || path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}

	/**
	 * Finds the file at {@link #getLibPath()} from the classpath, extracts to a
	 * temporary file if necessary, and returns the absolute location as a
	 * string. Note that any temporary file created will be cleaned up by
	 * {@link #close()}.
	 * 
	 * @return the absolute path name of the loadable library file.
	 * @throws ArchNotSupportedException
	 *             if the library was not found
	 * @throws ArchLoaderException
	 *             if an error occurs extracting the file.
	 */
	public synchronized String getLibFile() throws ArchLoaderException {
		if (tmpFile != null) {
			return tmpFile.toAbsolutePath().toString();
		}
		final String libPath = getLibPath();
		try {
			URI uri = loaderClass.getClassLoader().getResource(libPath).toURI();
			if ("file".equals(uri.getScheme())) {
				return new File(uri).getAbsolutePath();
			}
		} catch (NullPointerException | URISyntaxException e) {
			/* Continue on with the next option */ }

		try (InputStream resourceAsStream = loaderClass.getClassLoader().getResourceAsStream(libPath)) {
			if (resourceAsStream == null) {
				throw new ArchNotSupportedException(libPath + " not found in classpath.");
			}
			this.tmpFile = Files.createTempFile("", new File(libPath).getName());
			Files.copy(resourceAsStream, tmpFile, StandardCopyOption.REPLACE_EXISTING);
			return tmpFile.toAbsolutePath().toString();
		} catch (IOException e) {
			throw new ArchLoaderException(e.getMessage(), e);
		}
	}

	@Override
	public synchronized void close() throws ArchLoaderException {
		if (this.tmpFile != null) {
			try {
				Files.deleteIfExists(tmpFile);
			} catch (IOException e) {
				// Hoping to avoid this, as it leaks memory by design.
				tmpFile.toFile().deleteOnExit();
			} finally {
				tmpFile = null;
			}
		}
	}
}
