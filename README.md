# lein-nspect

[![Clojars Project](https://clojars.org/lein-nspect/latest-version.svg)](http://clojars.org/lein-nspect)

[![CircleCI](https://circleci.com/gh/micahelliott/lein-nspect.svg?style=svg)](https://circleci.com/gh/micahelliott/lein-nspect)
[![GitHub license](https://img.shields.io/badge/license-3%E2%80%92Clause%20BSD-blue.svg)](https://raw.githubusercontent.com/micahelliott/lein-nspect/master/LICENSE.txt)

A Clojure Leiningen plugin to inspect namespaces for consistency and
conformance with conventions.

## Usage

For user-level:

Put `[lein-nspect "0.1.0-SNAPSHOT"]` into the `:plugins` vector of your `:user`
profile.

For project-level:

Put `[lein-nspect "0.1.0-SNAPSHOT"]` into the `:plugins` vector of your
`project.clj`.

``` shell
% lein nspect
```

## License

Copyright © 2019 Micah Elliott

Distributed under the [3-Clause BSD License](https://raw.githubusercontent.com/micahelliott/lein-nspect/master/LICENSE.txt).
