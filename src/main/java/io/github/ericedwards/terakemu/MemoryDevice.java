/*
 * Copyright (c) 2019  Eric A. Edwards
 *
 * This file is part of TerakEMU.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * MemoryDevice.java - Implements main memory. Assumes it starts at zero.
 */
package io.github.ericedwards.terakemu;

public class MemoryDevice implements QbusDevice {

    private static final int MEMSIZE = 124;       // size in Kwords
    private final short mem[];                    // the memory array

    public MemoryDevice() {
        mem = new short[MEMSIZE * 1024];
        for (int x = 0; x < (MEMSIZE * 1024); ++x) {
            mem[x] = (short) (x & 0177777);
        }
        Qbus qbus = Qbus.instance();
        QbusDeviceInfo info = new QbusDeviceInfo(this, 0, mem.length, "MS11", true);
        qbus.registerDevice(info, true);
    }

    @Override
    public void reset() {
        // do nothing
    }

    @Override
    public short read(int addr) throws Trap {
        return mem[addr >> 1];
    }

    @Override
    public void write(int addr, short data) throws Trap {
        mem[addr >> 1] = data;
    }

    @Override
    public void writebyte(int addr, byte data) throws Trap {
        int t = mem[addr >> 1];
        int s = data & 0377;
        if ((addr & 1) == 0) {
            t &= 0177400;
            t |= s;
        } else {
            t &= 0377;
            t |= s << 8;
        }
        mem[addr >> 1] = (short) t;
    }

    @Override
    public void eventService(int data) {
        // do nothing
    }

    @Override
    public void interruptService() {
        // do nothing
    }

}
