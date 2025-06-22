package org.appxi.util.ext;

import java.io.*;
import java.io.BufferedReader;
import java.util.*;
import java.util.function.Supplier;

public class CompositeReader extends Reader {
    private final Queue<Supplier<Reader>> readers = new LinkedList<>();
    private Reader currentReader;

    public void addReader(Supplier<Reader> readerSupplier) {
        readers.add(readerSupplier);
    }

    private void initNextReader() {
        if (!readers.isEmpty()) {
            Supplier<Reader> nextReaderSupplier = readers.poll();
            try {
                currentReader = nextReaderSupplier.get();
            } catch (Exception e) {
                // Handle missing file or continue with next supplier if applicable
                initNextReader();
            }
        } else {
            currentReader = null;
        }
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (currentReader == null && !readers.isEmpty()) {
            initNextReader();
        }
        if (currentReader == null) {
            return -1; // EOF for all files
        }

        int charsRead = currentReader.read(cbuf, off, len);
        if (charsRead == -1) {
            // Current file is exhausted, move to the next file
            currentReader.close();
            initNextReader();
            if (currentReader != null) {
                return read(cbuf, off, len); // Recursively read from the next file
            } else {
                return -1; // No more files to read
            }
        }
        return charsRead;
    }

    @Override
    public void close() throws IOException {
        if (currentReader != null) {
            currentReader.close();
        }
    }

    @Deprecated
    public String readLine() throws IOException {
        if (currentReader == null && !readers.isEmpty()) {
            initNextReader();
        }
        if (currentReader == null) {
            return null; // EOF for all files
        }

        String line = ((BufferedReader) currentReader).readLine();
        if (line == null) {
            // Current file is exhausted, move to the next file
            currentReader.close();
            initNextReader();
            if (currentReader != null) {
                return readLine(); // Recursively read from the next file
            } else {
                return null; // No more files to read
            }
        }
        return line;
    }
}