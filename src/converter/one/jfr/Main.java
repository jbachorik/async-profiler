package one.jfr;

import one.jfr.event.Event;
import one.jfr.event.ExecutionSample;

import java.util.concurrent.atomic.LongAccumulator;
import java.io.IOException;

public final class Main {
    public static void main(String[] args) throws IOException {
        JfrReader reader = new JfrReader(args[0]);
//        reader.registerEvent("jdk.ExecutionSample", ExecutionSample.class);

        Event e;
        int count = 0;
        LongAccumulator sum = new LongAccumulator(Long::sum, 0);
        while ((e = reader.readEvent()) != null) {
            if (e instanceof ExecutionSample) {
                ExecutionSample event = (ExecutionSample) e;
                if (event.tid == 0) {
                    throw new RuntimeException();
                }
                sum.accumulate(event.tid);
                StackTrace st = reader.stackTraces.get(event.stackTraceId);
//                System.out.println("===> #: " + st.methods.length);
                sum.accumulate(st.methods.length);
                count++;
            }
        }
        System.out.println("Total events: " + count);
        System.out.println("Sum of thread ids: " + sum.get());
    }
}