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
package com.imagero.uio.buffer.arrays;

import com.imagero.uio.buffer.MemoryAccessManager;


/**
 * Gives possibility to read and write an double array using RandomAccess interface
 *
 * @author Andrei Kouznetsov
 *         Date: 01.06.2004
 *         Time: 22:35:58
 */
public class DoubleArrayBufferManager extends AbstractArrayBufferManager {

    double[] data;

    /**
     * Create FloatArrayBufferManager with standard tile size
     *
     * @param data int array
     */
    public DoubleArrayBufferManager(double[] data) {
        this(data, Math.min(TILE_SIZE, data.length));
    }

    /**
     * Create FloatArrayBufferManager with specified tile size
     *
     * @param data int array
     * @param size tile length (for Buffer)
     */
    public DoubleArrayBufferManager(double[] data, int size) {
        this(data, 0, data.length, size);
    }

    /**
     * Create FloatArrayBufferManager with standard tile size
     *
     * @param data   int array
     * @param offset start offset in <code>data</code>
     * @param length length to read
     */
    public DoubleArrayBufferManager(double[] data, int offset, int length) {
        this(data, offset, length, TILE_SIZE);
    }

    /**
     * Create FloatArrayBufferManager with specified tile size
     *
     * @param data   int array
     * @param offset start offset in <code>data</code>
     * @param length length to read
     * @param size   tile length (for Buffer)
     */
    public DoubleArrayBufferManager(double[] data, int offset, int length, int size) {
        this.data = data;
        this.tileSize = size;
        this.unitSize = 8;
        this.length = length;
        this.offset = offset;
        accessManager = MemoryAccessManager.createMemoryAccessManager();
    }

    /**
     * write appropriate unit (int) in LITTLE_ENDIAN order
     *
     * @param source       source byte array
     * @param sourceOffset offset in source array
     * @param destOffset   offset in destination array
     *
     * @return new offset in source array (for next writeUnitXX)
     */
    protected int writeUnitBE(byte[] source, int sourceOffset, int destOffset) {
        long v = ((long) (source[sourceOffset++] & 0xFF) << 56)
                | ((long) (source[sourceOffset++] & 0xFF) << 48)
                | ((long) (source[sourceOffset++] & 0xFF) << 40)
                | ((long) (source[sourceOffset++] & 0xFF) << 32)
                | ((long) (source[sourceOffset++] & 0xFF) << 24)
                | ((long) (source[sourceOffset++] & 0xFF) << 16)
                | ((long) (source[sourceOffset++] & 0xFF) << 8)
                | (source[sourceOffset++] & 0xFF);
        data[destOffset] = Double.longBitsToDouble(v);
        return sourceOffset;
    }

    /**
     * write appropriate unit (int) in BIG_ENDIAN order
     *
     * @param source       source byte array
     * @param sourceOffset offset in source array
     * @param destOffset   offset in destination array
     *
     * @return new offset in source array (for next writeUnitXX)
     */
    protected int writeUnitLE(byte[] source, int sourceOffset, int destOffset) {
        long v = (source[sourceOffset++] & 0xFF)
                | ((long) (source[sourceOffset++] & 0xFF) << 8)
                | ((long) (source[sourceOffset++] & 0xFF) << 16)
                | ((long) (source[sourceOffset++] & 0xFF) << 24)
                | ((long) (source[sourceOffset++] & 0xFF) << 32)
                | ((long) (source[sourceOffset++] & 0xFF) << 40)
                | ((long) (source[sourceOffset++] & 0xFF) << 48)
                | ((long) (source[sourceOffset++] & 0xFF) << 56);
        data[destOffset] = Double.longBitsToDouble(v);
        return sourceOffset;
    }

    /**
     * read unit (int) in  BIG_ENDIAN order
     *
     * @param offset     offset in source array
     * @param dest       byte array (destination)
     * @param destOffset offset in destination array
     *
     * @return offset in destination array (updated)
     */
    protected int readUnitBE(int offset, byte[] dest, int destOffset) {
        double d = data[offset];
        long v = Double.doubleToLongBits(d);
        dest[destOffset++] = (byte) ((v >>> 56) & 0xFF);
        dest[destOffset++] = (byte) ((v >>> 48) & 0xFF);
        dest[destOffset++] = (byte) ((v >>> 40) & 0xFF);
        dest[destOffset++] = (byte) ((v >>> 32) & 0xFF);
        dest[destOffset++] = (byte) ((v >>> 24) & 0xFF);
        dest[destOffset++] = (byte) ((v >>> 16) & 0xFF);
        dest[destOffset++] = (byte) ((v >>> 8) & 0xFF);
        dest[destOffset++] = (byte) (v & 0xFF);
        return destOffset;
    }

    /**
     * read appropriate unit (int) in  LITTLE_ENDIAN order
     *
     * @param offset     offset in source array
     * @param dest       byte array (destination)
     * @param destOffset offset in destination array
     *
     * @return offset in destination array (updated)
     */
    protected int readUnitLE(int offset, byte[] dest, int destOffset) {
        double d = data[offset];
        long v = Double.doubleToLongBits(d);
        dest[destOffset++] = (byte) (v & 0xFF);
        dest[destOffset++] = (byte) ((v >>> 8) & 0xFF);
        dest[destOffset++] = (byte) ((v >>> 16) & 0xFF);
        dest[destOffset++] = (byte) ((v >>> 24) & 0xFF);
        dest[destOffset++] = (byte) ((v >>> 32) & 0xFF);
        dest[destOffset++] = (byte) ((v >>> 40) & 0xFF);
        dest[destOffset++] = (byte) ((v >>> 48) & 0xFF);
        dest[destOffset++] = (byte) ((v >>> 56) & 0xFF);
        return destOffset;
    }
}
