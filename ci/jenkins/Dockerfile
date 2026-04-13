FROM jenkins/jenkins:lts-jdk21

USER root
RUN apt-get update \
  && apt-get install -y --no-install-recommends ca-certificates curl git gnupg docker.io \
  && curl -fsSL https://deb.nodesource.com/setup_22.x | bash - \
  && apt-get install -y --no-install-recommends nodejs \
  && apt-get clean \
  && rm -rf /var/lib/apt/lists/*

USER jenkins
