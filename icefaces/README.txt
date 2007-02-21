
ICEfaces provides extensions to the standard JavaServer Faces (JSF)
environment, enabling a new class of web applications that exhibit
the following characteristics:

  * Smooth, incremental page update with in-place editing versus
    full-page refresh.
  * User context preservation during page update, including scrollbar
    position and input focus.
  * Asynchronous page update driven from inside the standard JSF lifecycle.
  * Fine-grained user interaction during form entry.



Quick Start

1. Build the ICEfaces libraries and sample applications by invoking "ant"
   in the installation directory. Version 1.6.5 or later of ant is
   required.

2. Copy the WAR files in the "dist/samples" directory to the "webapps"
   directory of Apache Tomcat.

3. Try the sample applications at the following URLs
   http://localhost:8080/auctionMonitor/
   http://localhost:8080/address/
   http://localhost:8080/component-showcase/

4. Build the tutorials individually by invoking "ant" in the individual
   tutorial directories. You also have the option to build against
   the MyFaces (open source) implementation of JSF by invoking ant
   with the -Dmyfaces="" option.

5. Install the tutorials by copying the WAR files from the tutorial
   "dist" directories to the "webapps" directory of Apache Tomcat.
   Consult ReleaseNotes.html in the docs folder for known deployment
   issues and work arounds relating to the Sun Java System Application
   Server and the BEA WegLogic Server.

6. Try the tutorial applications; for instance:
   http://localhost:8080/basicInputText/    (basic usage)
   http://localhost:8080/timezone1/    (JSF without ICEfaces)
   http://localhost:8080/timezone2/    (ICEfaces conversion)
   http://localhost:8080/timezone5/    (enchanced version)
   http://localhost:8080/timezone6/    (Facelets conversion)
   http://localhost:8080/timezone7/    (enhanced Facelets conversion)
   http://localhost:8080/tiles/        (Tiles conversion)
   http://localhost:8080/dragdrop2/    (Drag and Drop)



Further Information

Consult the documentation in the docs folder:
  ReleaseNotes.html
  ICEfacesDevelopersGuide.pdf

The latest information is always available at
  http://www.icesoft.com/
