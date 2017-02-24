
FROM resin/rpi-raspbian:jessie-20160831
RUN apt-get update && apt-get install git --no-install-recommends && rm -rf /var/cache/apt/archives/* 
RUN cd /usr/local/src/ && \
        git clone -b master git://github.com/ardikars/OpenNetcut.git
WORKDIR /usr/local/src/OpenNetcut
RUN ./gradlew build --info 2>&1 | tee build.log
ENTRYPOINT ["/usr/bin/java", "/usr/local/src/OpenNetcut/lib/OpenNetcut-all-*.jar"]
CMD ["eth0", "false"]

