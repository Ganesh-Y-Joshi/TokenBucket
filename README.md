TokenBucket Package
The TokenBucket package provides a Java implementation of a token bucket algorithm, which is a rate-limiting mechanism used in various scenarios like controlling the frequency of requests to an API or limiting the rate of data transmission.

Overview
The TokenBucket class represents a token bucket, which has a certain capacity and fills up at a specified rate over time. The bucket can hold tokens, each representing permission for one action or request. When a request is made, it consumes a token from the bucket if available. If the bucket is empty, the request is either rejected or queued until tokens become available.

Features
Token Management: Tokens are added to the bucket at a configurable rate and consumed upon request.
Concurrency: The token bucket is thread-safe, allowing multiple threads to access and modify it concurrently.
Cleanup: Tokens associated with stale requests are automatically removed at regular intervals to prevent memory leaks.
Dynamic Configuration: Parameters such as bucket capacity, fill-up rate, cleanup interval, and shutdown duration are configurable.
Usage
To use the TokenBucket package, follow these steps:

Instantiate TokenBucket: Create a TokenBucket instance with the desired parameters, such as bucket capacity, fill-up rate, cleanup interval, and shutdown duration.
Add Requests: Add requests to the token bucket using the add method, specifying the request details such as IP address.
Use Tokens: Consume tokens from the bucket for each request using the useToken method.
Scheduled Operations: Schedule token refilling, cleanup, and shutdown using the schedule method.
Shutdown: Shut down the token bucket when no requests are pending or tokens are left.
Example
java
Copy code
TokenBucket tokenBucket = new TokenBucket(10, 5, Duration.ofSeconds(5),
        Duration.ofSeconds(1), Duration.ofSeconds(20));

// Add requests
RequestWrapper request1 = new RequestWrapper("192.168.0.1");
RequestWrapper request2 = new RequestWrapper("192.168.0.2");
System.out.println("Adding tokens for request1: " + tokenBucket.add(request1));
System.out.println("Adding tokens for request2: " + tokenBucket.add(request2));

// Use tokens
tokenBucket.useToken(request1);
tokenBucket.useToken(request1);
tokenBucket.useToken(request2);
tokenBucket.useToken(request2);

// Schedule operations
tokenBucket.schedule();

// Shutdown
tokenBucket.shutdown();
Dependencies
Java 8 or higher
Contributions
Contributions to the TokenBucket package are welcome. Feel free to fork the repository, make changes, and submit pull requests.
