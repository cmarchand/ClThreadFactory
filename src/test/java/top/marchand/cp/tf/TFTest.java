/**
 * This Source Code Form is subject to the terms of
 * the Mozilla Public License, v. 2.0. If a copy of
 * the MPL was not distributed with this file, You
 * can obtain one at https://mozilla.org/MPL/2.0/.
 */
package top.marchand.cp.tf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import top.marchand.xml.protocols.cp.Handler;

/**
 *
 * @author cmarchand
 */
public class TFTest {
    
    static ClassLoader cl;
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        File testResourcesFile = new File("src/test/resources/in");
        URL[] urls = { testResourcesFile.toURI().toURL() };
        cl = new URLClassLoader(urls);
    }
    
    @Test
    public void testResource() throws Exception {
        Runnable rr = new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(null, "cp:/top/marchand/cp/tf/res.txt", new Handler());
                    InputStream is = url.openStream();
                    byte[] buffer = new byte[1024];
                    int read = is.read(buffer);
                    while(read>0) {
                        System.out.print(new String(buffer,0,read));
                        read = is.read(buffer);
                    }
                    is.close();
                } catch(IOException ex) {
                    Assert.fail("fail to load resource from class loader : cp:/top/marchand/cp/tf/res.txt");
                }
            }
        };
        ExecutorService service = Executors.newFixedThreadPool(1, new CpThreadFactory(cl));
        service.submit(rr);
        service.shutdown();
        service.awaitTermination(10, TimeUnit.SECONDS);
        Assert.assertTrue(true);
    }
    
    
    
}
