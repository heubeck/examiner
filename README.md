# Examiner

Project to experiment with http request scenarios like service-meshs or deployment-strategies.

## Respond

The 𝕰𝖝𝖆𝖒𝖎𝖓𝖊𝖗 responds to every GET request on arbitrary paths with an optional, preconfigured value, set via the `ECHO_VALUE` environment variable.

## Log

The 𝕰𝖝𝖆𝖒𝖎𝖓𝖊𝖗 logs every POST request on arbitrary paths to its log output.

## Parameter

The following query parameter are supported:

* `status`: Status value used for the response, range: [200-600[
* `delay`: Milliseconds to delay the response:
  * a single number e.g. "1337" for the given delay of milliseconds
  * a range e.g. "42..667" for a random delay of milliseconds
* `load`: Stress the CPU at a level from 1 (some load) to 100 (high load) for the given `delay`
* `allocation`: Create memory pressure at a level from 1 (slow rate) to 100 (get what's there) by running string allocations over the given `delay`.
