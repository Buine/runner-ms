FROM openjdk:11 as builder
WORKDIR /opt/app
COPY . .
RUN ./gradlew --no-daemon bootJar

FROM openjdk:11-jre
RUN mkdir /opt/app
RUN groupadd -g 61000 msgroup
RUN useradd -g 61000 -l -M -s /bin/false -u 61000 microservice
COPY --from=builder /opt/app/build/libs/* /opt/app

WORKDIR /opt/app
RUN chown -R microservice:msgroup /opt/app
USER microservice

CMD ["sh",  "-c" ,"java -jar ./*SNAPSHOT.jar" ]
