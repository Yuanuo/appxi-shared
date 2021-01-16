package org.appxi.util.ext;

import java.io.IOException;
import java.io.Reader;

/**
 * 扩展自{@link java.io.BufferedReader},主要是为了解决默认的{@link #skip(long)}方法并不完整的实现.
 * 主要是该原始实现依赖于当前缓存的buf,并不真正跳过给定需要跳过的字节数
 */
public class BufferedReader extends java.io.BufferedReader {
    /**
     * @see {@link java.io.BufferedReader#BufferedReader(Reader)}
     */
    public BufferedReader(final Reader in) {
        super(in);
    }

    /**
     * @see {@link java.io.BufferedReader#BufferedReader(Reader, int)}
     */
    public BufferedReader(final Reader in, final int sz) {
        super(in, sz);
    }

    /**
     * @see {@link java.io.BufferedReader#skip(long)}
     */
    @Override
    public synchronized long skip(long n) throws IOException {
        if (n <= 0)
            return 0;
        long realSkipped = 0, currSkipped, prevSkipped = -1;
        do {
            currSkipped = super.skip(n);
            if (currSkipped == 0 && prevSkipped > 0) {
                // maybe EOF?
                break;
            }
            prevSkipped = currSkipped;
            realSkipped += currSkipped;
            if (currSkipped == n)
                break;
            n = n - currSkipped;//
        } while (true);
        return realSkipped;
    }
}
