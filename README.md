# Vert.x Twitter

A very simple [Vert.x](http://vertx.io/) project, that works like Twitter!

This project deploys 2 verticles:

* HTTP Server - handles REST requests
* Persistence - handles backend for all operations

The project uses Vert.x event bus for non blocking, asynchronous handling.


## Usage

For the rest of the README, when I refer to lein use version appropriate for your OS (lein.bat for Windows and lein for xNix).

* Clone this repository.
* Run _lein deps_ - to pull dependencies
* Run _lein run_ - to start simple Twitter server that accepts REST requests on port 8008 (Hitting ENTER after it starts will exit server).


### REST

Endpoints:

**GET  /message/:user** - where user is the name of the user to get the messages for. Returns a list of Messages as JSON.
**POST /message/:user/:message** - posts new message for user. Returns a new message ID as a response.

**POST /follow/:who/:whom** - create new follow subscription for user :user to follow user :whom.

**GET /wall/:user** - get Wall for :user. Entries on the wall for users that the :user follows.



## License

Copyright © 2014 Greg Gigon

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
