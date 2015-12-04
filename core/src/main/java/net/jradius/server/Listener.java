package net.jradius.server;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;

import net.jradius.server.config.ListenerConfigurationItem;

/**
 *
 * @param <E> some {@code JRadiusEvent}
 * @param <R> some {@code ListenerRequest}
 */
public interface Listener<E extends JRadiusEvent, R extends ListenerRequest<E>> {

    public void setConfiguration(ListenerConfigurationItem cfg) throws Exception;

    public void setRequestQueue(BlockingQueue<R> queue);
    
    //public void setRequestObjectPool(ObjectPool pool);

    public String getName();

    public E parseRequest(R listenerRequest, ByteBuffer byteBuffer, InputStream inputStream) throws Exception;

    public void start();

    public void stop();

    public boolean getActive();

    public void setActive(boolean active);
}
