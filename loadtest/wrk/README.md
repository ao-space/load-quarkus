# Load Test with Wrk

[wrk][wrk] is a pretty handy tool to find the proper rps rate for further performance testing. In this project, you can find three lua scripts which are used to run, measure and compare laod test with [wrk][wrk] tool. These scripts can be used for different purpose:

- `gin.lua`: It will be used to conduct a baseline load test to evaluate the performance of the web API built with the [gin][gin] & [gorm][gorm] framework, which are popular frameworks in Golang.
- `quarkus-imperative.lua`: It will be used to conduct a load test to evaluate the performance of the web API built with the [quarkus][quarkus] framework using an **imperative** implementation.
- `quarkus-reactive.lua`: It will be used to conduct a load test to evaluate the performance of the web API built with the [quarkus][quarkus] framework using a **reactive** implementation.

## Prepare

Firstly, you need to use bellow commands to start up the database with a prepared dataset: 

```shell script
cd loadtest/database && docker compose up
```

When you finish the tests, you can clean the database resources with bellow commands:

```shell script
cd loadtest/database && docker compose down
```

After starting the database, you need to start the web server that you are going to test with:

- For starting load-gin server:

    ```shell script
    cd load-gin && go build && ./load-gin
    ```

- For starting load-quarkus server of JVM version:

    ```shell script
    ./mvnw package -Dquarkus.package.type=uber-jar && \
    java -jar target/load-quarkus-0.0.1-runner.jar 
    ```

- For starting load-quarkus server of native version:

    ```shell script
    ./mvnw package -Dnative -Dquarkus.native.monitoring=jvmstat,heapdump && \
    ./target/load-quarkus-0.0.1-runner
    ```

*Note:* For native build of load-quarkus, there are several different ways to accomplish. You can use [this document](https://quarkus.io/guides/building-native-image) to get more details.


## Test

For testing, you can use different wrk parameters and relative lua script to test and get the measurement results.

*Note:* for knowing more details of test results, it 's better to use the `--latency` option to get "Latency Distribution". 

```shell script
wrk % wrk -c100 -t8 -d30s -s ./gin.lua --latency "http://localhost:5444"
```

```shell script
wrk -c100 -t8 -d30s -s ./quarkus-reactive.lua --latency  "http://localhost:5555"
```

## Result

I used my laptop computer to test and obtained the following results. My computer has the following information.

```
OS: macOS 14.0 23A344 x86_64 
Host: MacBookPro16,2 
Kernel: 23.0.0 
Shell: zsh 5.9 
CPU: Intel i7-1068NG7 (8) @ 2.30GHz 
GPU: Intel Iris Plus Graphics 
Memory: 32768MiB 
```

You can use the `monitor.sh` script to periodically print CPU and memory(rss) information during the test. The script is located in the `loadtest` folder. 


For all result, we used the same thread count and duration, and they are: 8 threads and 30 seconds. The cpu and memory are rough average value which means they are not very accurate but good enough to illustate the actual usage. The matrix of testing test for gin and quarkus-reactive(native) are following:

|no| case | connect| cpu(avg %) | memory(rss KiB)| qps (Requests/sec)| 99% delay percentile| errors|
|-|----|-----|------|-------|------|----|--|
|1|gin | `-c100` | 150 | 26212 | 7608.42 | 158.43ms|0|
|2|quarkus |`-c100` | 150 | 72884 | 3839.28 | 39.23ms |0|
|3|gin |`-c200` | 150 | 32708 | 7609.25 | 440.41ms |read: 49|
|4|quarkus |`-c200` | 150 | 72800 | 3926.05 | 74.29ms |read 46|
|5|gin |`-c300` | 150 | 36068 | 8106.72 | 748.97ms |read: 139|
|6|quarkus |`-c300` | 150 | 75688 | 3932.40 | 108.36ms |read 200|
|7|gin |`-c500` | 150 | 48964 | 8202.28 | 1.19s |read: 422, timeout 64|
|8|quarkus |`-c500` | 150 | 88184 | 3878.22 | 188.08ms |read 462|
|9|gin |`-c1000` | 150 | 77260 | 7653.94 | 1.42s |read: 3056, timeout 1385|
|10|quarkus |`-c1000` | 150 | 155596 | 3706.55 | 381.61ms |read 3342|

The original test result:

1. gin with `-c100`:

    ```shell
    wrk -c100 -t8 -d30s -s ./gin.lua --latency "http://localhost:5444"
    Running 30s test @ http://localhost:5444
    8 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
        Latency    21.47ms   31.42ms 401.07ms   89.41%
        Req/Sec     0.96k   159.62     1.48k    71.04%
    Latency Distribution
        50%    9.06ms
        75%   22.48ms
        90%   55.57ms
        99%  158.43ms
    228532 requests in 30.04s, 52.70MB read
    Requests/sec:   7608.42
    Transfer/sec:      1.75MB
    ```

2. quarkus with `-c100`:

    ```shell
    wrk -c100 -t8 -d30s -s ./quarkus-reactive.lua --latency  "http://localhost:5555"
    Running 30s test @ http://localhost:5555
    8 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
        Latency    24.99ms    4.40ms  99.70ms   81.91%
        Req/Sec   482.25     58.58   606.00     71.58%
    Latency Distribution
        50%   23.89ms
        75%   26.64ms
        90%   30.20ms
        99%   39.23ms
    115352 requests in 30.05s, 22.51MB read
    Requests/sec:   3839.28
    Transfer/sec:    767.03KB
    ```

3. gin with `-c200`:

    ```shell
    wrk -c200 -t8 -d30s -s ./gin.lua --latency "http://localhost:5444"
    Running 30s test @ http://localhost:5444
    8 threads and 200 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
        Latency    59.51ms   94.34ms   1.10s    87.95%
        Req/Sec     0.96k   177.69     1.59k    69.38%
    Latency Distribution
        50%   19.20ms
        75%   63.70ms
        90%  179.65ms
        99%  440.41ms
    228564 requests in 30.04s, 52.72MB read
    Socket errors: connect 0, read 49, write 0, timeout 0
    Requests/sec:   7609.25
    Transfer/sec:      1.76MB
    ```

4. quarkus with `-c200`:

    ```shell
    wrk -c200 -t8 -d30s -s ./quarkus-reactive.lua --latency  "http://localhost:5555"
    Running 30s test @ http://localhost:5555
    8 threads and 200 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
        Latency    50.84ms    6.67ms 144.53ms   77.61%
        Req/Sec   493.28     55.94   680.00     71.75%
    Latency Distribution
        50%   49.07ms
        75%   53.72ms
        90%   59.38ms
        99%   74.29ms
    117981 requests in 30.05s, 23.02MB read
    Socket errors: connect 0, read 46, write 0, timeout 0
    Requests/sec:   3926.05
    Transfer/sec:    784.50KB
    ```

5. gin with `-c300`:

    ```shell
    wrk -c300 -t8 -d30s -s ./gin.lua --latency "http://localhost:5444"              
    Running 30s test @ http://localhost:5444
    8 threads and 300 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
        Latency    89.39ms  152.93ms   1.70s    88.97%
        Req/Sec     1.02k   185.24     1.65k    69.83%
    Latency Distribution
        50%   26.89ms
        75%   90.07ms
        90%  264.64ms
        99%  748.97ms
    243474 requests in 30.03s, 56.16MB read
    Socket errors: connect 0, read 139, write 0, timeout 0
    Requests/sec:   8106.72
    Transfer/sec:      1.87MB
    ```

6. quarkus with `-c300`:

    ```shell
    wrk -c300 -t8 -d30s -s ./quarkus-reactive.lua --latency  "http://localhost:5555"
    Running 30s test @ http://localhost:5555
    8 threads and 300 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
        Latency    75.08ms    9.67ms 147.88ms   79.57%
        Req/Sec   494.15     61.32   660.00     70.25%
    Latency Distribution
        50%   72.77ms
        75%   80.07ms
        90%   86.84ms
        99%  108.36ms
    118193 requests in 30.06s, 23.06MB read
    Socket errors: connect 0, read 200, write 0, timeout 0
    Requests/sec:   3932.40
    Transfer/sec:    785.57KB
    ```

7. gin with `-c500`:

    ```shell
    wrk -c500 -t8 -d30s -s ./gin.lua --latency "http://localhost:5444"
    Running 30s test @ http://localhost:5444
    8 threads and 500 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
        Latency   138.46ms  236.39ms   1.99s    89.56%
        Req/Sec     1.03k   171.15     1.61k    68.58%
    Latency Distribution
        50%   45.37ms
        75%  142.16ms
        90%  390.06ms
        99%    1.19s 
    246329 requests in 30.03s, 56.83MB read
    Socket errors: connect 0, read 422, write 0, timeout 64
    Requests/sec:   8202.28
    Transfer/sec:      1.89MB
    ```

8. quarkus with `-c500`:

    ```shell
    wrk -c500 -t8 -d30s -s ./quarkus-reactive.lua --latency  "http://localhost:5555"
    Running 30s test @ http://localhost:5555
    8 threads and 500 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
        Latency   127.36ms   18.16ms 232.96ms   79.29%
        Req/Sec   488.18     79.17   626.00     69.79%
    Latency Distribution
        50%  122.99ms
        75%  135.12ms
        90%  150.70ms
        99%  188.08ms
    116551 requests in 30.05s, 22.74MB read
    Socket errors: connect 0, read 462, write 0, timeout 0
    Requests/sec:   3878.22
    Transfer/sec:    774.81KB
    ```

9. gin with `-c1000`:

    ```shell
    wrk -c1000 -t8 -d30s -s ./gin.lua --latency "http://localhost:5444"             
    Running 30s test @ http://localhost:5444
    8 threads and 1000 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
        Latency   192.69ms  286.96ms   2.00s    88.80%
        Req/Sec     0.97k   174.28     1.68k    73.01%
    Latency Distribution
        50%   75.21ms
        75%  232.27ms
        90%  522.52ms
        99%    1.42s 
    229962 requests in 30.04s, 53.04MB read
    Socket errors: connect 0, read 3056, write 0, timeout 1385
    Requests/sec:   7653.94
    Transfer/sec:      1.77MB
    ```

10. quarkus with `-c1000`:

    ```shell
    wrk -c1000 -t8 -d30s -s ./quarkus-reactive.lua --latency  "http://localhost:5555"
    Running 30s test @ http://localhost:5555
    8 threads and 1000 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
        Latency   266.11ms   33.97ms 468.69ms   77.31%
        Req/Sec   468.26    108.32   710.00     71.64%
    Latency Distribution
        50%  260.23ms
        75%  284.77ms
        90%  308.12ms
        99%  381.61ms
    111418 requests in 30.06s, 21.75MB read
    Socket errors: connect 0, read 3342, write 0, timeout 0
    Requests/sec:   3706.55
    Transfer/sec:    740.94KB
    ```

[wrk]: <https://github.com/wg/wrk>
[gin]: <https://github.com/gin-gonic/gin>
[gorm]: <https://github.com/go-gorm/gorm>
[quarkus]: <https://github.com/quarkusio/quarkus>