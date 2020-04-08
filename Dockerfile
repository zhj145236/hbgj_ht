FROM java:8
VOLUME /tmp
COPY target/hbgj.jar.jar hbgj.jar
RUN bash -c "touch /hbgj.jar"
EXPOSE 8080
ENTRYPOINT ["java","-jar","hbgj.jar"]
