package org.example.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// simulation of Apache dubbo DefaultFuture
public class MyDefaultFuture {


    private static final Map<Long, MyDefaultFuture> FUTURES = new ConcurrentHashMap<>();

    // invoke id.
    private final long id;
    private final int timeout;
    private final Request request;
    private final Lock lock = new ReentrantLock();
    private final Condition done = lock.newCondition();
    private final long start = System.currentTimeMillis();
    private volatile long sent;

    private  Response response;
//    private volatile Response response;

    private MyDefaultFuture(Request request, int timeout) {
        this.request = request;
        this.id = request.getId();
        this.timeout = timeout;
        // put into waiting map.
        FUTURES.put(id, this);
    }

    public static MyDefaultFuture newFuture(Request request, int timeout) {
        final MyDefaultFuture future = new MyDefaultFuture(request, timeout);
        return future;
    }

    public static MyDefaultFuture getFuture(long id) {
        return FUTURES.get(id);
    }

    public static void received( Response response) {
        try {
            MyDefaultFuture future = FUTURES.remove(response.getId());
            if (future != null) {
                future.doReceived(response);
            } else {
                System.out.println("The timeout response finally returned at "
                        + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()))
                        + ", response " + response);
            }
        } finally {
        }
    }

    private void doReceived(Response res) {
        lock.lock();
        try {
            response = res;
            if (done != null) {
                //signal a thread randomly,
                //although we don't know which one would be signalled
                //but we got respond id, so we always can get the right response
//                System.out.println("signal thread :"
//                        + Thread.currentThread().getName());
                done.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    public Object get()  {
        return get(timeout);
    }

    public Object get(int timeout)  {
        if (timeout <= 0) {
            timeout = 10000;
        }
        if (!isDone()) {
            long start = System.currentTimeMillis();
            //
            lock.lock();
            try {
//                System.out.println("lock thred: " + Thread.currentThread().getName());
                while (!isDone()) {
//                    System.out.println("await thred: " + Thread.currentThread().getName());
                    //multiple threads is waiting here
                    done.await(timeout, TimeUnit.MILLISECONDS);
//                    System.out.println("wake up thred: " + Thread.currentThread().getName());
                    if (isDone() || System.currentTimeMillis() - start > timeout) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
            if (!isDone()) {
                System.out.println(" !isDone");
                Response resp = new Response();
                resp.setData("response time out");
                return resp.getData();
            }
        }
        return returnFromResponse();
    }

    private Object returnFromResponse() {
        Response res = response;
        return res.getData();
    }


    public boolean isDone() {
        return response != null;
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public long getId() {
        return id;
    }

    public int getTimeout() {
        return timeout;
    }

    public Lock getLock() {
        return lock;
    }

    public Condition getDone() {
        return done;
    }

    public long getStart() {
        return start;
    }

    public long getSent() {
        return sent;
    }

    public void setSent(long sent) {
        this.sent = sent;
    }
}
