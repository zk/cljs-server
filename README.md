# cljs-server


## Usage

Setup's a bit convoluted right now:

1. `mkdir cljs-stuff`
2. `cd cljs-stuff`
3. `git clone https://github.com/zkim/nsfw.git`
4. `git clone https://zkim@github.com/zkim/cljs.git`
5. `git clone https://zkim@github.com/zkim/cljs-server.git`
6. `cd nsfw && lein install`
7. `cd ../cljs && lein install`
8. `cd ../cljs-server`
9. `lein deps && lein swank`
10. Connect to swank
11. repl=> `(use cljs.watch)`
12. repl=> `(start-watch-project "./project.clj")`. This starts the cljs compiler, which recompiles cljs -> js on source changes in the `src/cljs` directory.
13. repl=> `(use cljs-server.dev)`. This will start the server at port 8080.
14. http://localhost:8080

cljs sources are found in `cljs-server/src/cljs/`.


## License

Copyright (C) 2010 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
