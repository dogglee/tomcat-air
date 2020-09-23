del /q bootstrap.jar
jar cvf0 bootstrap.jar -C bin com/douglee/tomcatair/Bootstrap.class -C bin com/douglee/tomcatair/classloader/CommonClassLoader.class
del /q lib/tomcat-air.jar
cd bin
jar cvf0 ../lib/tomcat-air.jar *
cd ..
java -cp bootstrap.jar com.douglee.tomcatair.Bootstrap
pause