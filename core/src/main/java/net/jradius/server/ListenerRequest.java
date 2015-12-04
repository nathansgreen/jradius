/**
 * JRadius - A Radius Server Java Adapter
 * Copyright (C) 2004-2006 PicoPoint, B.V.
 * Copyright (C) 2006-2007 David Bird <david@coova.com>
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package net.jradius.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Map;

import org.apache.commons.pool2.ObjectPool;

/**
 * @param <E> some {@code JRadiusEvent}
 * @author David Bird
 */
public abstract class ListenerRequest<E extends JRadiusEvent>
{
    protected E event;
    protected Listener<E, ListenerRequest<E>> listener;
    private ObjectPool<? extends ListenerRequest<E>> borrowedFromPool;
    
    public ListenerRequest()
    {
    }

    public <L extends Listener<E, ? extends ListenerRequest<E>>> ListenerRequest(L listener)
    {
        this.listener = (Listener<E, ListenerRequest<E>>) listener;
    }
    
    public ByteBuffer getByteBufferIn() throws IOException
    {
        return null;
    }

    public ByteBuffer getByteBufferOut() throws IOException
    {
        return null;
    }

    public abstract InputStream getInputStream() throws IOException;

    public abstract OutputStream getOutputStream() throws IOException;

    public abstract Map<String, String> getServerVariables();

    public Listener getListener()
    {
        return listener;
    }

    public <L extends Listener<E, ? extends ListenerRequest<E>>> void setListener(L listener)
    {
        this.listener = (Listener<E, ListenerRequest<E>>)listener;
    }

    public E getEventFromListener() throws Exception
    {
        E e = listener.parseRequest(this, getByteBufferIn(), getInputStream());
        if (e == null) return null;
        e.setListener(listener);
        return e;
    }
    
    public E getRequestEvent() throws Exception
    {
        if (event == null)
        {
            event = getEventFromListener();
        }

        return event;
    }
    
    public void clear()
    {
        event = null;
    }

    public <R extends ListenerRequest<E>> ObjectPool<R> getBorrowedFromPool() {
        return (ObjectPool<R>)borrowedFromPool;
    }

    public  <R extends ListenerRequest<E>> void setBorrowedFromPool(ObjectPool<R> borrowedFromPool) {
        this.borrowedFromPool = borrowedFromPool;
    }
}
