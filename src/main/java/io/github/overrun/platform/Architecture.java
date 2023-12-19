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

import java.util.Locale;

/**
 * The architecture of the {@link Platform}.
 *
 * @author squid233
 * @since 1.0.0
 */
public enum Architecture {
    /**
     * x64
     */
    X64,
    /**
     * x86
     */
    X86,
    /**
     * arm64
     */
    ARM64,
    /**
     * arm32
     */
    ARM32,
    /**
     * ppc64le
     */
    PPC64LE,
    /**
     * riscv64
     */
    RISCV64,
    /**
     * Unknown architecture
     */
    UNKNOWN;

    private final String toStringValue = name().toLowerCase(Locale.ROOT);

    /**
     * {@return the current architecture of the current {@linkplain Platform platform}}
     */
    public static Architecture current() {
        class Holder {
            private static final Architecture CURRENT;

            static {
                final Platform platform = Platform.current();
                final String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
                CURRENT = switch (platform) {
                    case Platform.FreeBSD freeBSD -> X64;
                    case Platform.Linux linux -> {
                        if (arch.startsWith("arm") || arch.startsWith("aarch64")) {
                            if (arch.contains("64") || arch.startsWith("armv8")) {
                                yield ARM64;
                            } else {
                                yield ARM32;
                            }
                        } else if (arch.startsWith("ppc")) {
                            yield PPC64LE;
                        } else if (arch.startsWith("riscv")) {
                            yield RISCV64;
                        } else {
                            yield X64;
                        }
                    }
                    case Platform.MacOS macOS -> arch.startsWith("aarch64") ? ARM64 : X64;
                    case Platform.Windows windows when arch.contains("64") -> arch.startsWith("aarch64") ? ARM64 : X64;
                    case Platform.Windows windows -> X86;
                    case Platform.Unknown unknown -> UNKNOWN;
                };
            }
        }
        return Holder.CURRENT;
    }

    @Override
    public String toString() {
        return toStringValue;
    }
}
