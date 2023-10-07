# Quarkus - Hivemq Client
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![Daily Build](https://github.com/quarkiverse/quarkus-hivemq-client/actions/workflows/daily.yaml/badge.svg)](https://github.com/quarkiverse/quarkus-hivemq-client/actions/workflows/daily.yaml) [![All Contributors](https://img.shields.io/badge/all_contributors-1-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

## Introduction

This extension allow usage of the _HiveMQ MQTT Client_ inside a Quarkus App, in JVM and Native mode.

Added with the [SmallRye Reactive Messaging MQTT](https://smallrye.io/smallrye-reactive-messaging/4.3.0/mqtt/mqtt/) allows usage of a new connector type **smallrye-mqtt-hivemq** that will use _HiveMQ MQTT Client_ instead of Vertx MQTT client.

This adds some benefits to the original SmallRye MQTT:

* Battle tested MQTT Client outside of Vertx landscape.
* Management of external CA file for secure connections with self-signed certificates
* Backpressure support integrated with MQTT QOS.
* Automatic and configurable reconnect handling and message redelivery.
* Real Health Check against a configurable topic (defaults to the standard MQTT $SYS/broker/uptime) integrated in Quarkus HealthReport.
* Many others you can read in official documentation [here](https://hivemq.github.io/hivemq-mqtt-client/).

For more information about installation and configuration please read the documentation
[here](https://quarkiverse.github.io/quarkiverse-docs/quarkus-hivemq-client/dev/index.html).

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://github.com/masini"><img src="https://avatars.githubusercontent.com/u/2060870?v=4?s=100" width="100px;" alt=""/><br /><sub><b>masini</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-hivemq-client/commits?author=masini" title="Code">💻</a> <a href="#maintenance-masini" title="Maintenance">🚧</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/pjgg"><img src="https://avatars.githubusercontent.com/u/3541131?v=4" width="100px;" alt=""/><br /><sub><b>pjgg</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-hivemq-client/commits?author=pjgg" title="Code">💻</a> <a href="#maintenance-pjgg" title="Maintenance">🚧</a></td>
  </tr>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
