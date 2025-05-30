# Spring Artemis Stock Portfolio

A Spring Boot application demonstrating asynchronous messaging with Apache ActiveMQ Artemis. This project implements a simple stock portfolio service where clients send commands to buy, sell, add securities, or query the current portfolio via JMS queues.

## Table of Contents

- [Project Overview](#project-overview)  
- [Prerequisites](#prerequisites)  
- [Getting Started](#getting-started)  
  - [1. Start Artemis Broker](#1-start-artemis-broker)  
  - [2. Run the Spring Boot App](#2-run-the-spring-boot-app)  
- [Message Formats](#message-formats)  
- [Examples](#examples)  
- [Testing](#testing)  
- [License](#license)  

## Project Overview

The goal of this project is to learn how to integrate Spring Boot with Apache ActiveMQ Artemis for asynchronous message queues. The application reads commands from an inbound queue (`INQ`), processes them against an in-memory stock portfolio, and writes responses to an outbound queue (`OUTQ`). :contentReference[oaicite:0]{index=0}

Supported operations:

| Command      | Description                                  |
|--------------|----------------------------------------------|
| `BUY`        | Purchase a quantity of a given stock.        |
| `SELL`       | Sell a quantity of a given stock.            |
| `ADD`        | Add a new stock symbol to the portfolio.     |
| `PORTFOLIO`  | Retrieve current holdings of all securities. |

## Prerequisites

- Java 21 (or later)  
- Maven 3.6+  
- Docker (optional, for running Artemis in a container)  
- Apache ActiveMQ Artemis 2.x :contentReference[oaicite:1]{index=1}  

## Getting Started

### 1. Start Artemis Broker

You can run Artemis locally or via Docker.

#### Local installation

1. Download and extract Artemis:
   ```bash
   wget https://activemq.apache.org/components/artemis/download/ -O artemis.tar.gz
   tar xzf artemis.tar.gz

2. Create a broker instance:

   ```bash
   ./artemis/bin/artemis create broker-instance --silent --allow-anonymous
   ```
3. Start the broker:

   ```bash
   cd broker-instance
   ./artemis/bin/artemis run
   ```
4. Verify queues `INQ` and `OUTQ` exist via the web console at `http://localhost:8161/console`.&#x20;

#### Docker

```bash
docker run -d --name artemis \
  -p 61616:61616 -p 8161:8161 \
  vromero/activemq-artemis
```

### 2. Run the Spring Boot App

1. Build the project:

   ```bash
   mvn clean package
   ```
2. Run:

   ```bash
   java -jar target/spring-artemis-stock-portfolio.jar
   ```

The app will auto-connect to `tcp://localhost:61616` and listen on `INQ`, sending replies to `OUTQ`.

## Message Formats

All messages use a simple space-delimited format. Arguments are upper-case strings or positive integers. Responses include a status code:

```
<COMMAND> <SECURITY> <AMOUNT>
```

| Command   | Request Example | Response on Success            | Error Responses                                    |
| --------- | --------------- | ------------------------------ | -------------------------------------------------- |
| BUY       | `BUY AAPL 10`   | `BUY AAPL 0 Trade successful`  | `Unknown security 1` <br> `Not enough positions 2` |
| SELL      | `SELL MSFT 5`   | `SELL MSFT 0 Trade successful` | `Unknown security 1` <br> `Not enough positions 2` |
| ADD       | `ADD GOOGL`     | `ADD GOOGL 0 Success`          | `Unknown security 1`                               |
| PORTFOLIO | `PORTFOLIO`     | `TSLA 5 \| AAPL 12 \| ... 0`   | N/A                                                |

> Note: The final “0” in each success response indicates “no error.”&#x20;

## Examples

1. **Add a new stock**

   * **Send**: `ADD TSLA`
   * **Receive**: `0 Success`

2. **Buy shares**

   * **Send**: `BUY TSLA 5`
   * **Receive**: `0 Trade successful`

3. **Sell shares**

   * **Send**: `SELL TSLA 3`
   * **Receive**: `0 Trade successful`

4. **Query portfolio**

   * **Send**: `PORTFOLIO`
   * **Receive**: `TSLA 2 | AAPL 10 | ... 0`

## Testing

You can manually test via the Artemis console:

1. Navigate to `Queues` → `INQ` → **Send Messages**.
2. Paste a command (e.g., `BUY AAPL 1`) and send.
3. Check `OUTQ` for the response.&#x20;

