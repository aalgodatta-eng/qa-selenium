FROM maven:3.9.8-eclipse-temurin-17
WORKDIR /app
COPY . /app
RUN mvn -q -DskipTests=true -pl framework-core,framework-ui,framework-api,tests -am clean install

ENV ENV=qa
ENV RUNMODE=local
ENV BROWSER=chrome
ENV HEADLESS=true
ENV GRIDURL=http://selenium-hub:4444/wd/hub
ENV TAGS=

CMD mvn -q test -pl tests   -Denv=${ENV} -DrunMode=${RUNMODE} -Dbrowser=${BROWSER} -Dheadless=${HEADLESS} -DgridUrl=${GRIDURL}   -Dcucumber.filter.tags="${TAGS}"
