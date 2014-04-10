/*
 * Copyright (c) Andrey Kuznetsov. All Rights Reserved.
 *
 * http://uio.imagero.com
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of imagero Andrei Kouznetsov nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.imagero.uio.buffer;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Implementation of Buffer for RandomAccessRO.
 *
 * @author Andrei Kouznetsov
 *         Date: 05.01.2004
 *         Time: 20:59:01
 */
public class RAFBufferRO implements Buffer {

    protected RandomAccessFile raf;

    long offset;
    int length;
    byte[] data;

    public RAFBufferRO(RandomAccessFile raf, long offset, int length) {
        this.raf = raf;
        if(offset < 0) {
            throw new IllegalArgumentException("negative offset:" + offset);
        }
        this.offset = offset;
        this.length = length;
    }

    /**
     * read specified part of data
     *
     * @return data
     *
     */
    public byte[] getData() throws IOException {
        if(data == null) {
            readData();
        }
        return data;
    }

    public byte[] getData(byte[] d) throws IOException {
        if(data == null) {
            readData();
        }
        System.arraycopy(data, 0, d, 0, Math.min(data.length, d.length));
        return d;
    }

    public int length() {
        return length;
    }

    protected void readData() throws IOException {
        long currentOffset = raf.getFilePointer();
        raf.seek(offset);
        long len = raf.length();
        int maxlen = (int) Math.min(length, len - offset);
        if(maxlen < 0) {
            maxlen = 0;
        }
        data = new byte[length];
        raf.readFully(data, 0, maxlen);
        raf.seek(currentOffset);
    }

    public boolean isDirty() {
        return false;
    }
}
