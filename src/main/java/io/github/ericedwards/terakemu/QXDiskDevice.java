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
 * QXDiskDevice - Terak Disk Controller
 *
 * The device emulation and boot rom are based on the information in the 
 * Installation and User's Guide found here:
 *
 * http://bitsavers.trailing-edge.com/pdf/terak/50-0010-001_8510a_Graphics_Computer_System_Installation_and_Users_Guide_1980.pdf
 *
 * It isn't DMA capabile like the QB Variable Density Disk Controller.
 */
package io.github.ericedwards.terakemu;

public class QXDiskDevice implements QbusDevice {

    private static final int QX_BASE = 0777000;
    private static final int QX_SIZE = 2;
    private static final int QX_BOOT_BASE = 0773000;
    private static final int QX_BOOT_SIZE = 64;
    private static final int QX_VECTOR = 0250;  // default interrupt vector
    private static final int QX_BRLEVEL = 5;	// default bus request level
    private static final int QX_DELAY = 100;	// in instructions

    private static final short[] qx_boot_rom = {};

    public QXDiskDevice() {
        Qbus qbus = Qbus.instance();
        QbusDeviceInfo qbusDeviceInfo;
        qbusDeviceInfo = new QbusDeviceInfo(this, QX_BASE, QX_SIZE, "TERAK QX", true);
        qbus.registerDevice(qbusDeviceInfo, true);
        qbusDeviceInfo = new QbusDeviceInfo(this, QX_BOOT_BASE, QX_BOOT_SIZE, "TERAK QX BOOT", true);
        qbus.registerDevice(qbusDeviceInfo);
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public short read(int addr) throws Trap {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(int addr, short data) throws Trap {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void writebyte(int addr, byte data) throws Trap {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void eventService(int data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void interruptService() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
