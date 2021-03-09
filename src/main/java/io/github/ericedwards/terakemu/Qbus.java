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
 * Qbus.java - The center of all evil.
 */
package io.github.ericedwards.terakemu;

import java.util.ArrayList;
import java.util.ListIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Qbus implements QbusDevice {

    static Logger logger = LoggerFactory.getLogger(Qbus.class);
    private static Qbus qbus = null;                     // the Q-Bus singleton
    private final ArrayList<QbusDeviceInfo> devices;     // Q-Bus devices
    private ArrayList<QbusEvent> events;                 // Q-Bus device events
    private ArrayList<QbusInterrupt> interrupts;         // Q-Bus device interrupts

    private Qbus() {
        devices = new ArrayList<>();
        events = new ArrayList<>();
        interrupts = new ArrayList<>();
    }

    public static final synchronized Qbus instance() {
        if (qbus == null) {
            qbus = new Qbus();
        }
        return qbus;
    }

    public final void registerDevice(QbusDeviceInfo deviceInfo) {
        registerDevice(deviceInfo, false);
    }

    public final void registerDevice(QbusDeviceInfo deviceInfo, boolean isMemory) {
        // later check for overlap and any other conflicts
        if (isMemory) {
            devices.add(0, deviceInfo);
        } else {
            devices.add(deviceInfo);
        }
    }

    @Override
    public final void reset() {
        devices.forEach((d) -> {
            d.device.reset();
        });
        events = new ArrayList<>();
        interrupts = new ArrayList<>();
    }

    @Override
    public final short read(int addr) throws Trap {
        for (QbusDeviceInfo d : devices) {
            if ((addr >= d.base) && (addr < (d.base + (d.size * 2)))) {
                return d.device.read(addr);
            }
        }
        throw new Trap(Trap.UnibusTimeout);
    }

    @Override
    public final void write(int addr, short data) throws Trap {
        for (QbusDeviceInfo d : devices) {
            if ((addr >= d.base) && (addr < (d.base + (d.size * 2)))) {
                d.device.write(addr, data);
                return;
            }
        }
        throw new Trap(Trap.UnibusTimeout);
    }

    @Override
    public final void writebyte(int addr, byte data) throws Trap {
        for (QbusDeviceInfo d : devices) {
            if ((addr >= d.base) && (addr < (d.base + (d.size * 2)))) {
                d.device.writebyte(addr, data);
                return;
            }
        }
        throw new Trap(Trap.UnibusTimeout);
    }

    @Override
    public void eventService(int data) {
        // do nothing
    }

    @Override
    public void interruptService() {
        // do nothing
    }

    public synchronized void scheduleEvent(QbusDevice device,
            int eventDelay, int data) {
        QbusEvent e = new QbusEvent(device, eventDelay, data);
        for (int i = 0; i < events.size(); ++i) {
            if (e.getEventTime() < events.get(i).getEventTime()) {
                events.add(i, e);
                return;
            }
        }
        events.add(e);
    }

    public synchronized void cancelEvents(QbusDevice device) {
        ListIterator<QbusEvent> eventsIterator = events.listIterator();
        while (eventsIterator.hasNext()) {
            QbusEvent e = eventsIterator.next();
            if (e.getDevice() == device) {
                eventsIterator.remove();
            }
        }
    }

    public synchronized void runEvents(boolean jumpAhead) {
        if (events.isEmpty()) {
            return;
        }
        long currentTime = CPUDevice.instance().getCurrentTime();
        if (jumpAhead) {
            long temp = events.get(0).getEventTime() + 1;
            if (temp > currentTime) {
                currentTime = temp;
                CPUDevice.instance().setCurrentTime(currentTime);
            }
        }
        ListIterator<QbusEvent> eventsIterator = events.listIterator();
        while (eventsIterator.hasNext()) {
            QbusEvent event = eventsIterator.next();
            if (event.getEventTime() < currentTime) {
                event.getDevice().eventService(event.getData());
                eventsIterator.remove();
            } else {
                return;
            }
        }
    }

    public synchronized void scheduleInterrupt(QbusDevice device,
            int level, int vector) {
        QbusInterrupt n = new QbusInterrupt(device, level, vector);
        for (int i = 0; i < interrupts.size(); ++i) {
            if (interrupts.get(i).equals(n)) {
                // already there
                return;
            }
            if (interrupts.get(i).getLevel() < n.getLevel()) {
                interrupts.add(i, n);
                return;
            }
        }
        interrupts.add(n);
    }

    public synchronized void cancelInterrupt(QbusDevice device,
            int level, int vector) {
        QbusInterrupt interruptToCancel = new QbusInterrupt(device, level, vector);
        ListIterator<QbusInterrupt> interruptsIterator = interrupts.listIterator();
        while (interruptsIterator.hasNext()) {
            QbusInterrupt n = interruptsIterator.next();
            if (n.equals(interruptToCancel)) {
                interruptsIterator.remove();
            }
        }
    }

    public synchronized QbusInterrupt runInterrupts(int level) {
        if (interrupts.isEmpty()) {
            return null;
        }
        QbusInterrupt n = interrupts.get(0);
        if (n.getLevel() > level) {
            interrupts.remove(0);
            return n;
        } else {
            return null;
        }
    }

    public synchronized boolean waitingInterrupt(int level) {
        if (interrupts.isEmpty()) {
            return false;
        }
        QbusInterrupt n = interrupts.get(0);
        return (n.getLevel() > level);
    }

    public void dumpDevices() {
        devices.forEach((d) -> {
            System.out.println(d.name + " "
                    + d.device.getClass().getName() + " "
                    + Integer.toOctalString(d.base) + " "
                    + Integer.toOctalString(d.size));
        });
    }
}
