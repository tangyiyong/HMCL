/*
 * Hello Minecraft! Launcher.
 * Copyright (C) 2017  huangyuhui <huanghongxun2008@126.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see {http://www.gnu.org/licenses/}.
 */
package org.jackhuang.hmcl.event;

import java.util.EventObject;

/**
 *
 * @author huangyuhui
 */
public class Event extends EventObject {

    public Event(Object source) {
        super(source);
    }

    private boolean canceled;

    /**
     * true if this event is canceled.
     *
     * @throws UnsupportedOperationException if trying to cancel a non-cancelable event.
     */
    public final boolean isCanceled() {
        return canceled;
    }

    /**
     *
     * @param canceled new value
     * @throws UnsupportedOperationException if trying to cancel a non-cancelable event.
     */
    public final void setCanceled(boolean canceled) {
        if (!isCancelable())
            throw new UnsupportedOperationException("Attempted to cancel a non-cancelable event: " + getClass());
        this.canceled = canceled;
    }

    /**
     * true if this Event this cancelable.
     */
    public boolean isCancelable() {
        return false;
    }

    public boolean hasResult() {
        return false;
    }

    private Result result = Result.DEFAULT;

    /**
     * Retutns the value set as the result of this event
     */
    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        if (!hasResult())
            throw new UnsupportedOperationException("Attempted to set result on a no result event: " + this.getClass() + " of type.");
        this.result = result;
    }

    public enum Result {
        DENY,
        DEFAULT,
        ALLOW
    }
}
