import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TokenBucket {

    private final long bucketCapacity;
    private final int perEntryMaxToken;
    private final Duration cleanupInterval;
    private final Duration fillUpRate;
    private final Duration shutDownDuration;
    private final ConcurrentMap<RequestWrapper, Integer> bucket;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors()
    );

    public TokenBucket(long bucketCapacity, int perEntryMaxToken, Duration cleanupInterval, Duration fillUpRate, Duration shutDownDuration) {
        if (bucketCapacity <= 0 || cleanupInterval == null || fillUpRate == null
                || perEntryMaxToken <= 0 || shutDownDuration == null) {
            throw new IllegalArgumentException();
        }
        this.shutDownDuration = shutDownDuration;
        this.perEntryMaxToken = perEntryMaxToken;
        this.bucketCapacity = bucketCapacity;
        this.cleanupInterval = cleanupInterval;
        this.fillUpRate = fillUpRate;
        this.bucket = new ConcurrentHashMap<>();
    }

    public long getBucketCapacity() {
        return bucketCapacity;
    }

    public Duration getShutDownDuration() {
        return shutDownDuration;
    }

    public Duration getCleanupInterval() {
        return cleanupInterval;
    }

    public Duration getFillUpRate() {
        return fillUpRate;
    }

    public ConcurrentMap<RequestWrapper, Integer> getBucket() {
        return bucket;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public int getPerEntryMaxToken() {
        return perEntryMaxToken;
    }

    public int add(RequestWrapper wrapper) {
        if (wrapper.getIp().isEmpty()) {
            return -1;
        }


        if (bucket.containsKey(wrapper)) {
            int cToken = bucket.get(wrapper);
            if (cToken < perEntryMaxToken) {
                wrapper.setLastUpdatedTimeStamp(Instant.now());
                bucket.replace(wrapper, cToken + 1);
                return cToken + 1;
            }
        } else {
            wrapper.setLastUpdatedTimeStamp(Instant.now());
            bucket.put(wrapper, 1);
            return 1;
        }
        return -1;
    }

    public void refill() {
        bucket.forEach((k, v) -> {
            if (!k.getIp().isEmpty()) {
                if (Duration.between(Instant.now(), k.getLastUpdatedTimeStamp()).abs().minus(fillUpRate).isPositive()
                        && bucket.get(k) < perEntryMaxToken) {
                    bucket.replace(k, bucket.get(k) + 1);
                }
            }
        });

        System.out.println("R######################################################################################");
        for (var token: bucket.entrySet()) {
            System.out.println("Refilled Token: " + token.getKey().getIp() + " total current token " + token.getValue());
        }
        System.out.println("R######################################################################################");
    }

    public void cleanup() {
        bucket.forEach((k, v) -> {
            if (!k.getIp().isEmpty()) {

                if (Duration.between(Instant.now(), k.getLastUpdatedTimeStamp()).abs().minus(cleanupInterval).isPositive()) {
                    bucket.remove(k);
                }
            }
        });

        System.out.println("C######################################################################################");
        for (var token: bucket.entrySet()) {
            System.out.println("Cleaned Token: " + token.getKey().getIp());
        }
        System.out.println("C######################################################################################");
    }

    public void useToken(RequestWrapper wrapper) {
        if (wrapper.getIp().isEmpty()) {
            return;
        }

        if (bucket.get(wrapper) > 0)
            bucket.replace(wrapper, bucket.get(wrapper) - 1);
        System.out.println("Used Token for ip " + wrapper.getIp() + " current token left " + bucket.get(wrapper));
    }

    public void schedule() {
        executorService.scheduleWithFixedDelay(this::refill, 20, fillUpRate.toMillis(), TimeUnit.MILLISECONDS);
        executorService.scheduleWithFixedDelay(this::cleanup, 20, cleanupInterval.toMillis(), TimeUnit.MILLISECONDS);
        executorService.scheduleWithFixedDelay(this::shutdown, 20, shutDownDuration.toMillis(), TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        if (bucket.isEmpty()) {
            executorService.shutdown();
            if (!executorService.isShutdown()) {
                executorService.close();
            }
        }
    }

  // Example Usage
//     public static void main(String[] args) {
//         TokenBucket tokenBucket = new TokenBucket(10, 5, Duration.ofSeconds(10), Duration.ofSeconds(1), Duration.ofSeconds(60));
//         // Schedule token refilling, cleanup, and shutdown


//         // Add some requests
//         RequestWrapper request1 = new RequestWrapper("192.168.0.1");
//         RequestWrapper request2 = new RequestWrapper("192.168.0.2");

//         System.out.println("Adding tokens for request1: " + tokenBucket.add(request1));
//         System.out.println("Adding tokens for request2: " + tokenBucket.add(request2));

//         // Use some tokens
//         tokenBucket.useToken(request1);
//         tokenBucket.useToken(request1);
//         tokenBucket.useToken(request2);
//         tokenBucket.useToken(request2);

//         // Sleep for some time to see token refilling and cleanup
// //        try {
// //            Thread.sleep(20000);
// //        } catch (InterruptedException e) {
// //            e.printStackTrace();
// //        }

//         tokenBucket.schedule();
//         // Shutting down
//         tokenBucket.shutdown();
//     }
}
