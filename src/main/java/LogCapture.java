import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

/**
 * Created by 1085950 on 5/27/2016.
 */
public class LogCapture {

    private final CountDownLatch latch = new CountDownLatch(1);
    private PipedInputStream pin = new PipedInputStream();
    private PipedOutputStream pout = new PipedOutputStream();


    public InputStream getInputStream() {
        InputStream in = new InputStream() {

            @Override
            public int read() throws IOException {
                return pin.read();
            }

            @Override
            public void close() throws IOException {
                super.close();
                latch.countDown();
            }
        };
        return in;
    }

    public OutputStream getOutputStream() {
        OutputStream out = new OutputStream(){
            @Override
            public void write(int b) throws IOException {
                pout.write(b);
            }
            @Override
            public void close() throws IOException {
                while(latch.getCount()!=0) {
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        //too bad
                    }
                }
                super.close();
            }
        };
        return out;
    }

    public void log() {
        Thread t = new Thread(() -> {
            try {
                pin.connect(pout);
                String newLine = System.getProperty("line.separator");
                BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));

                StringBuilder result = new StringBuilder();
                String line; boolean flag = false;

                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    result.append(flag? newLine: "").append(line);
                    flag = true;
                }
                pin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
    }
}
