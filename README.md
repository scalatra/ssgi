# SSGI: the Scala Server Gateway Interface

SSGI is a low-level API for developing web applications and web frameworks in [Scala](http://www.scala-lang.org/).  

## Similar frameworks

SSGI is influenced by server gateways in several other languages:

* Ruby's [Rack](http://rack.rubyforge.org/)
* Python's [WSGI](http://www.python.org/dev/peps/pep-333/)
* Perl's [PSGI/Plack](http://plackperl.org/) 
* JavaScript's [JSGI/Jack](http://jackjs.org/)
* Clojure's [Ring](http://github.com/mmcgrana/ring)

## Infrequently Asked Questions

### Why not just use the Java Servlet API?

As a JVM language, the [Java Servlet API](http://www.oracle.com/technetwork/java/index-jsp-135475.html) is a viable option for Scala web development.  However, we do not find that the Servlet API is suitable for _idiomatic_ Scala development, with its mutable variables, null-returning methods, and archaic collection types.  SSGI lets you deploy to servlet containers without coupling your app to the Servlet specification.

### How is this project related to Scalatra?

This project was initially conceived by the [Scalatra](http://scalatra.org/) development team.  It may in the future be used to break Scalatra's hard dependency on the Servlet API.  It may flourish as a separate project without ever being integrated into Scalatra.  It may prove to be a horrendous idea and be left to rot on GitHub.  Only time will tell.
