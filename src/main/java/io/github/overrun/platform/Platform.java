/*
 * MIT License
 *
 * Copyright (c) 2023 Overrun Organization
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

package io.github.overrun.platform;

/**
 * The native platform, identifying the operating system and the architecture.
 *
 * @author squid233
 * @since 1.0.0
 */
public sealed interface Platform {
    /**
     * {@return the current native platform}
     */
    static Platform current() {
        class Holder {
            private static final Platform CURRENT;

            static {
                final String osName = System.getProperty("os.name");
                if ("FreeBSD".equals(osName)) {
                    CURRENT = FreeBSD.INSTANCE;
                } else if (osName.startsWith("Linux") || osName.startsWith("SunOS") || osName.startsWith("Unit")) {
                    CURRENT = Linux.INSTANCE;
                } else if (osName.startsWith("Mac OS X") || osName.startsWith("Darwin")) {
                    CURRENT = MacOS.INSTANCE;
                } else if (osName.startsWith("Windows")) {
                    CURRENT = Windows.INSTANCE;
                } else {
                    CURRENT = Unknown.INSTANCE;
                }
            }
        }
        return Holder.CURRENT;
    }

    /**
     * {@return the family name of this platform}
     */
    String familyName();

    /**
     * Converts the given script path to a suitable format of this platform.
     *
     * @param scriptPath the original script path.
     * @return the processed script path.
     */
    default String scriptName(String scriptPath) {
        return scriptPath;
    }

    /**
     * {@return the suffix of the executable on this platform, including dot}
     */
    default String executableSuffix() {
        return "";
    }

    /**
     * Converts the given executable path to a suitable format of this platform.
     *
     * @param executablePath the original executable path.
     * @return the processed executable path.
     */
    default String executableName(String executablePath) {
        return executablePath;
    }

    /**
     * {@return the suffix of the shared library on this platform, including dot}
     */
    String sharedLibrarySuffix();

    /**
     * Converts the given shared library name to a suitable format of this platform.
     *
     * @param libraryName the original library name.
     * @return the processed library name.
     */
    default String sharedLibraryName(String libraryName) {
        return Platform.unixLibraryName(libraryName, sharedLibrarySuffix());
    }

    /**
     * {@return the suffix of the static library on this platform, including dot}
     */
    String staticLibrarySuffix();

    /**
     * Converts the given static library name to a suitable format of this platform.
     *
     * @param libraryName the original library name.
     * @return the processed library name.
     */
    default String staticLibraryName(String libraryName) {
        return Platform.unixLibraryName(libraryName, staticLibrarySuffix());
    }

    /**
     * Unknown
     *
     * @author squid233
     * @since 1.0.0
     */
    final class Unknown implements Platform {
        private static final Unknown INSTANCE = new Unknown();

        private Unknown() {
        }

        @Override
        public String familyName() {
            return "unknown";
        }

        @Override
        public String sharedLibrarySuffix() {
            return "";
        }

        @Override
        public String sharedLibraryName(String libraryName) {
            return libraryName;
        }

        @Override
        public String staticLibrarySuffix() {
            return "";
        }

        @Override
        public String staticLibraryName(String libraryName) {
            return libraryName;
        }

        @Override
        public String toString() {
            return familyName();
        }
    }

    /**
     * FreeBSD
     *
     * @author squid233
     * @since 1.0.0
     */
    final class FreeBSD implements Platform {
        private static final FreeBSD INSTANCE = new FreeBSD();

        private FreeBSD() {
        }

        @Override
        public String familyName() {
            return "freebsd";
        }

        @Override
        public String sharedLibrarySuffix() {
            return Linux.INSTANCE.sharedLibrarySuffix();
        }

        @Override
        public String staticLibrarySuffix() {
            return Linux.INSTANCE.staticLibrarySuffix();
        }

        @Override
        public String toString() {
            return familyName();
        }
    }

    /**
     * Linux
     *
     * @author squid233
     * @since 1.0.0
     */
    final class Linux implements Platform {
        private static final Linux INSTANCE = new Linux();

        private Linux() {
        }

        @Override
        public String familyName() {
            return "linux";
        }

        @Override
        public String sharedLibrarySuffix() {
            return ".so";
        }

        @Override
        public String staticLibrarySuffix() {
            return ".a";
        }

        @Override
        public String toString() {
            return familyName();
        }
    }

    /**
     * MacOS
     *
     * @author squid233
     * @since 1.0.0
     */
    final class MacOS implements Platform {
        private static final MacOS INSTANCE = new MacOS();

        private MacOS() {
        }

        @Override
        public String familyName() {
            return "macos";
        }

        @Override
        public String sharedLibrarySuffix() {
            return ".dylib";
        }

        @Override
        public String staticLibrarySuffix() {
            return ".a";
        }

        @Override
        public String toString() {
            return familyName();
        }
    }

    /**
     * Windows
     *
     * @author squid233
     * @since 1.0.0
     */
    final class Windows implements Platform {
        private static final Windows INSTANCE = new Windows();

        private Windows() {
        }

        @Override
        public String familyName() {
            return "windows";
        }

        @Override
        public String scriptName(String scriptPath) {
            return Platform.withExtension(scriptPath, ".bat");
        }

        @Override
        public String executableSuffix() {
            return ".exe";
        }

        @Override
        public String executableName(String executablePath) {
            return Platform.withExtension(executablePath, executableSuffix());
        }

        @Override
        public String sharedLibrarySuffix() {
            return ".dll";
        }

        @Override
        public String sharedLibraryName(String libraryName) {
            return Platform.withExtension(libraryName, sharedLibrarySuffix());
        }

        @Override
        public String staticLibrarySuffix() {
            return ".lib";
        }

        @Override
        public String staticLibraryName(String libraryName) {
            return Platform.withExtension(libraryName, staticLibrarySuffix());
        }

        @Override
        public String toString() {
            return familyName();
        }
    }

    private static String unixLibraryName(String libraryName, String suffix) {
        if (libraryName.endsWith(suffix)) {
            return libraryName;
        }
        int pos = libraryName.lastIndexOf('/');
        if (pos >= 0) {
            return libraryName.substring(0, pos + 1) + "lib" + libraryName.substring(pos + 1) + suffix;
        }
        return "lib" + libraryName + suffix;
    }

    private static String withExtension(String filePath, String extension) {
        if (filePath.toLowerCase().endsWith(extension)) {
            return filePath;
        }
        return removeExtension(filePath) + extension;
    }

    private static String removeExtension(String filePath) {
        final int name = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\'));
        final int ext = filePath.lastIndexOf('.');
        if (ext > name) {
            return filePath.substring(0, ext);
        }
        return filePath;
    }
}
