
FROM alpine:3.3

RUN wget -c http://ardikars.com/mirrors/java/jre-8u131-linux-x64.tar.gz -O /tmp/jre.tar.gz

RUN mkdir -p -m 755 /root/apps

RUN tar -xzvf /tmp/jre.tar.gz -C /root/apps

RUN mv /root/apps/jre1.8.0_131 /root/apps/jre

RUN rm -rf /tmp/jre.tar.gz

ENV JAVA_HOME /root/apps/jre

RUN mkdir -p -m 755 /root/apps/opennetcut

WORKDIR /root/apps/opennetcut

RUN wget -c https://ardikars.com/downloads/opennetcut/opennetcut-latest.jar -O /root/apps/opennetcut/opennetcut.jar

CMD ["/root/apps/jre/bin/java", "-jar", "/root/apps/opennetcut/opennetcut.jar"]

