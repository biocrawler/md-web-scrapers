FROM  azul/zulu-openjdk:11.0.7

ENV APP=/opt/app/
WORKDIR ${APP}
ARG URL="https://chromedriver.storage.googleapis.com/87.0.4280.87/chromedriver_linux64.zip"

RUN cat /etc/apt/sources.list

RUN apt-get update -y

RUN apt-get install -y unzip wget libnss3-dev 

RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb;apt install -y ./google-chrome-stable_current_amd64.deb

RUN echo $URL

RUN wget $URL;unzip chromedriver_linux64.zip

COPY target ${APP}

COPY config ${APP}/config

COPY start.sh ${APP}

RUN chmod 755 ${APP}start.sh
ENTRYPOINT ["sh", "start.sh"]
