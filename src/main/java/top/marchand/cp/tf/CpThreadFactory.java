/**
 * This Source Code Form is subject to the terms of
 * the Mozilla Public License, v. 2.0. If a copy of
 * the MPL was not distributed with this file, You
 * can obtain one at https://mozilla.org/MPL/2.0/.
 */
package top.marchand.cp.tf;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import top.marchand.xml.protocols.cp.Handler;

/**
 * A ThreadFactory that set the <tt>top.marchand.xml.protocols.cp.Handler</tt>'s class loader.
 * @author <a href="mailto:christophe@marchand.top">Christophe Marchand</a>
 */
public class CpThreadFactory implements ThreadFactory {

    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private final ClassLoader cl;

    public CpThreadFactory(ClassLoader cl) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = "pool-"+ POOL_NUMBER.getAndIncrement()+ "-thread-";
        this.cl=cl;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0) {
            @Override
            public void run() {
                Handler.setClassLoader(cl);
                super.run(); 
            }
        };
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
