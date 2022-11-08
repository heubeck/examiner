# 𝕰𝖝𝖆𝖒𝖎𝖓𝖊𝖗

![Build&Test](https://github.com/heubeck/examiner/actions/workflows/ci.yaml/badge.svg)

Project to experiment with http request scenarios like service-meshs or deployment-strategies.

## Respond

The 𝕰𝖝𝖆𝖒𝖎𝖓𝖊𝖗 responds to every GET request at or below the `/examine` path with an optional, preconfigured value, set via the `ECHO_VALUE` environment variable.

## Log

The 𝕰𝖝𝖆𝖒𝖎𝖓𝖊𝖗 logs every POST request on arbitrary paths to its log output by default.
The logging can be customized using the following env:
* `LOG_METHODS`: Comma separated list of http methods to be logged
* `LOG_HEADERS`: Comma separated list of regex values for http header names to be logged, defaulting to `X-.*`

## Config

There are some environment variables to configure default behavior:

* `ECHO_VALUE`: Text value to be responded on GET `/examine` requests
* `SERVER_ERROR_RATE`: Int value as probability in percent (0 - 100) that HTTP 500 codes are responded.
* `REQUEST_DELAY`: Default millisecond delay for every request.

## Parameter

The following query parameter are supported and have precedence over the default configuration:

* `status`: Status value used for the response, range: [200-600[
* `delay`: Milliseconds to delay the response:
  * a single number e.g. "1337" for the given delay of milliseconds
  * a range e.g. "42..667" for a random delay of milliseconds
* `load`: Stress the CPU at a level from 1 (some load) to 100 (high load) for the given `delay`
* `allocation`: Create memory pressure at a level from 1 (slow rate) to 100 (get what's there) by running string allocations over the given `delay`.

## K8s probes

Liveness and readiness probes are always successful at `/.well-known/live` and `/.well-known/ready`.

## Silence! I kill you!

The 𝕰𝖝𝖆𝖒𝖎𝖓𝖊𝖗 dies when called with a DELETE request on the path `/poison-pill`.
The exit code can be configured by a query parameter `exit`: `DELETE /poison-pill?exit=1`, default is exit code `0` (in words: zero).
Only regular supported query parameter for this method is the `delay` to wait before dying.
