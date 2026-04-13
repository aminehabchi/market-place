FROM jenkins/jenkins:lts-jdk21

USER root
RUN apt-get update \
  && apt-get install -y --no-install-recommends docker.io curl git ca-certificates \
  && apt-get clean \
  && rm -rf /var/lib/apt/lists/*

USER jenkins
