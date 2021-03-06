/*
 * Copyright (c) 2001, 2019  Eric A. Edwards
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
 * QbusInterrupt.java
 */
package io.github.ericedwards.terakemu;

public class QbusInterrupt {

    private QbusDevice device;
    private int level;
    private int vector;

    public QbusInterrupt() {
    }

    QbusInterrupt(QbusDevice device, int level, int vector) {
        this.device = device;
        this.level = level;
        this.vector = vector;
    }

    public QbusDevice getDevice() {
        return device;
    }

    public void setDevice(QbusDevice device) {
        this.device = device;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getVector() {
        return vector;
    }

    public void setVector(int vector) {
        this.vector = vector;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof QbusInterrupt) {
            QbusInterrupt n = (QbusInterrupt) o;
            return ((this.device == n.getDevice())
                    && (this.level == n.getLevel())
                    && (this.vector == n.getVector()));
        } else {
            return false;
        }
    }
}
