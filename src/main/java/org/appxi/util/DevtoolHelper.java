package org.appxi.util;

public abstract class DevtoolHelper {
    public static final int MB = 1024 * 1024;

    private DevtoolHelper() {
    }

    public static MemoryInfo memoryInfo() {
        final Runtime instance = Runtime.getRuntime();
        final long total = instance.totalMemory();
        final long free = instance.freeMemory();
        final long max = instance.maxMemory();
        return new MemoryInfo(total / MB, free / MB, (total - free) / MB, max / MB);
    }

    public static final record MemoryInfo(long total, long free, long used, long max) {
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("MemoryInfo{");
            sb.append("total=").append(total).append("MiB");
            sb.append(", free=").append(free).append("MiB");
            sb.append(", used=").append(used).append("MiB");
            sb.append(", max=").append(max).append("MiB");
            sb.append('}');
            return sb.toString();
        }
    }
}
