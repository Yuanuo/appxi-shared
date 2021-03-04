package org.appxi.file;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher implements Runnable {
    protected static final List<WatchService> watchServices = new ArrayList<>(4);
    protected final List<WatchListener> watchListeners = new ArrayList<>();
    private Path folder, fileName;
    private WatchKey watchKey;

    public FileWatcher(Path path) {
        if (Files.notExists(path))
            return;
        if (Files.isDirectory(path))
            this.folder = path;
        else {
            this.folder = path.getParent();
            this.fileName = path.getFileName();
        }
    }

    public FileWatcher watching() {
        if (Files.notExists(this.folder))
            return this;
        final Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
        return this;
    }

    public FileWatcher cancel() {
        if (null != this.watchKey)
            this.watchKey.cancel();
        return this;
    }

    @Override
    public void run() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            this.watchKey = this.folder.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            watchServices.add(watchService);
            boolean poll = true;
            while (poll) {
                final WatchKey key = watchService.take();
                final Path watchPath = (Path) key.watchable();
                for (java.nio.file.WatchEvent<?> event : key.pollEvents()) {
                    final Path watchFile = watchPath.resolve((Path) event.context());

                    if (null != this.fileName && !Objects.equals(this.fileName, watchFile.getFileName()))
                        continue;

                    final WatchEvent watchEvent = new WatchEvent(WatchType.of(event.kind()),
                            watchFile);
                    this.watchListeners.forEach(l -> l.onWatchEvent(watchEvent));
                    if (watchEvent.type == WatchType.CREATE && Files.isDirectory(watchFile)) {
                        new FileWatcher(watchFile).setListeners(watchListeners).watching();
                    }
                }
                poll = key.reset();
            }
        } catch (IOException | InterruptedException | ClosedWatchServiceException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public FileWatcher addListener(WatchListener watchListener) {
        watchListeners.add(watchListener);
        return this;
    }

    public FileWatcher removeListener(WatchListener watchListener) {
        watchListeners.remove(watchListener);
        return this;
    }

    public List<WatchListener> getListeners() {
        return watchListeners;
    }

    public FileWatcher setListeners(List<WatchListener> watchListeners) {
        this.watchListeners.clear();
        this.watchListeners.addAll(watchListeners);
        return this;
    }

    public static List<WatchService> getWatchServices() {
        return Collections.unmodifiableList(watchServices);
    }

    public enum WatchType {
        CREATE, DELETE, MODIFY;

        public static WatchType of(java.nio.file.WatchEvent.Kind<?> kind) {
            if (kind == ENTRY_CREATE)
                return CREATE;
            if (kind == ENTRY_MODIFY)
                return MODIFY;
            if (kind == ENTRY_DELETE)
                return DELETE;
            return null;
        }
    }

    public static class WatchEvent extends EventObject {
        private static final long serialVersionUID = -7638001329129704669L;

        public final WatchType type;

        public WatchEvent(WatchType type, Path source) {
            super(source);
            this.type = type;
        }

        @Override
        public Path getSource() {
            return (Path) super.getSource();
        }
    }

    @FunctionalInterface
    public interface WatchListener extends EventListener {
        void onWatchEvent(WatchEvent watchEvent);
    }
}