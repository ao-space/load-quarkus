# Load Test with Wrk

[wrk][wrk] is a pretty handy tool to find the proper rps rate for further performance testing. In this project, you can find three lua scripts which are used to run, measure and compare laod test with [wrk][wrk] tool. These scripts can be used for different purpose:

- `gin.lua`: It will be used to conduct a baseline load test to evaluate the performance of the web API built with the [gin][gin] & [gorm][gorm] framework, which are popular frameworks in Golang.
- `quarkus-imperative.lua`: It will be used to conduct a load test to evaluate the performance of the web API built with the [quarkus][quarkus] fromework using an **imperative** implemenation.
- `quarkus-reactive.lua`: It will be used to conduct a load test to evaluate the performance of the web API built with the [quarkus][quarkus] fromework using an **reactive** implemenation.

## Prepare

Fristly, you need to use bellow commands to start up the database with a prepared dataset: 

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
    ./mvnw package -Dnative && \
    ./target/load-quarkus-0.0.1-runner
    ```

*Note:* For native build of load-quarkus, there are serveral different ways to accomplish. You can use [this document](https://quarkus.io/guides/building-native-image) to get more details.


## Test

For testing, you can use different wrk parameters and relative lua script to test and get the measurement results.

*Note:* for knowning more details of test results, it 's better to use the `--latency` option to get "Latency Distribution". 

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
Memory: 20313MiB / 32768MiB 
```

[wrk]: <https://github.com/wg/wrk>
[gin]: <https://github.com/gin-gonic/gin>
[gorm]: <https://github.com/go-gorm/gorm>
[quarkus]: <https://github.com/quarkusio/quarkus>