# slim-my-invoice Server + Web Application

This repository contains the source code for the Server and the Web Application of slim-my-invoice.
slim-my-invoice is Web Application that aims at facilitating invoices encoding using OCR.


## License

* [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

## Building

### With Maven

The easiest way to build is to install [Maven](http://maven.apache.org/download.html)
v3.+ in your development environment. 

Building is achieved with the following command:
* Run `mvn clean install` from the root directory 

This command fetches all the project dependencies and creates a WAR file named `slimmyinvoice.war` in the `/target` folder.

## Deployment
In order to deploy the server, please follow the next steps:
* Install [Java 6](http://www.oracle.com/technetwork/java/javase/downloads/java-archive-downloads-javase6-419409.html) and [Tomcat 7](https://tomcat.apache.org/download-70.cgi) on a machine
* Create a folder `/var/lib/slimmyinvoice/databases/` and allow read/write permissions on that folder
* Rename the WAR file to `ROOT.war`
* Move it in the folder `/var/lib/tomcat7/webapps/` on the machine

Tomcat will then automatically deploy the server and the web application should be available a few seconds later.

The following command may be useful to display the logs:
* `cat /var/log/tomcat7/catalina.out`


## Configuration
For the passwrod recovery functionnality to work, you need to configure the `Properties` class with an email address, and SMTP configuration.

The path to the database can also be modified in this class. 
Ensure that the provided directory has read/write privileges.


## Acknowledgements

This project uses many other open source libraries such as:

* [Tesseract](https://code.google.com/p/tesseract-ocr)
* [Tess4J](http://tess4j.sourceforge.net)
* [Bootstrap](http://getbootstrap.com/)
* [ORMLite](http://ormlite.com/)

The entire list of dependencies is listed in the [Maven file](https://github.com/a7-software/slim-my-invoice/blob/master/pom.xml) of the project.

## Contributing

Please fork this repository and contribute back using
[pull requests](https://github.com/a7-software/slim-my-invoice/pulls).

Any contributions, large or small, major features, bug fixes, language translations, 
unit/integration tests are welcomed and appreciated
but will be reviewed and discussed.