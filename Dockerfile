# syntax=docker/dockerfile:1
FROM ubuntu:20.04
USER root

#variables
ENV DEBIAN_FRONTEND noninteractive
ENV USER="root"
ENV PASSWORD="Zgredek7"
ARG path=Dockerfile
ARG ur_rtde
ARG zabbix

#enviroment
RUN apt-get update && apt-get install -y \
  ubuntu-server \
  curl \
  sudo \
  && rm -rf /var/lib/apt/lists/*

#repositories
RUN curl -fsSL https://cli.github.com/packages/githubcli-archive-keyring.gpg | sudo dd of=/usr/share/keyrings/githubcli-archive-keyring.gpg
RUN echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/githubcli-archive-keyring.gpg] https://cli.github.com/packages stable main" | sudo tee /etc/apt/sources.list.d/github-cli.list > /dev/null
RUN add-apt-repository ppa:sdurobotics/ur-rtde

#dependencies
RUN apt-get update && apt-get install -y \
  apt-utils \
  net-tools \
  wget \
  gnupg2 \
  iputils-ping \
  nano \
  build-essential \
  zabbix-agent \
  openssh-server sudo \
  python3-pip \
  libboost-all-dev \
  libssl-dev \
  libblas-dev \
  liblapack-dev \
  cmake \
  gh \
  && rm -rf /var/lib/apt/lists/*
  
#set user,and auth token
RUN export GH_TOKEN="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"
RUN echo $USER:$PASSWORD | chpasswd

#ZEROTIER as host or bound to docker triggered as argument? @ddominet 
RUN curl -s https://install.zerotier.com | bash || true

#sshd config
RUN mkdir -p /run/sshd
RUN ssh-keygen -A
RUN sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/g' /etc/ssh/sshd_config
RUN echo Port 7002 >> /etc/ssh/sshd_config
RUN service ssh restart




#git authentication
RUN gh auth login
RUN gh auth setup-git

#building ur_rtde
RUN if [ "$ur_rtde" = "build" ]; then \
    cd ~/ && \
    git clone https://gitlab.com/sdurobotics/ur_rtde.git && \
    cd ~/ur_rtde && \
    git submodule update --init --recursive && \
    mkdir build && \
    cd ~/ur_rtde/build && \
    cmake .. && \
    make && \
    make install; \
  elseif [ "$ur_rtde" = "clone" ]; then \
    echo "UR_RTDE build FLAG NOT DETECTED: SKIPPING" && \
    cd ~/ur_rtde/ && \
    git clone https://github.com/hub-raum/UR-RTDE-Python-wrapper; \
  else \
    echo "UR_RTDE clone FLAG NOT DETECTED: SKIPPING" && \
    echo "UR-RTDE installation from PPA" && \
  #todo #55:
    apt-get update && \ 
  #install UR RTDE libs
    apt install librtde librtde-dev \
    && rm -rf /var/lib/apt/lists/*; \
  fi

#building ur_ikfast
WORKDIR ~/
RUN gh repo clone hub-raum/Dedicated-Inverse-Kinematics-for-UR3e ~/ur_ikfast/
RUN pip3 install --user numpy Cython
RUN pip3 install scipy
RUN pip3 install -e ~/ur_ikfast/

#cloning main-Robotic-Arm-Code
WORKDIR ~/main-code
RUN gh repo clone hub-raum/main-Robotic-Arm-Code ~/main-code

#zabbix integration
COPY $path /etc/zabbix/zabbix_agentd.conf
RUN pip3 install py-zabbix
RUN mkdir ~/Zabbix
WORKDIR ~/Zabbix
#RUN ln -s /etc/zabbix/zabbix_agentd.conf ~/Zabbix/zabbix_agentd.conf
RUN ln -s main-code/docker/Zabbix Zabbix
#RUN wget -O ~/Zabbix/measure_latencies_Zabbix.sh https://raw.githubusercontent.com/hub-raum/main-Robotic-Arm-Code/develop/measure_latencies_Zabbix.sh?token=GHSAT0AAAAAABWSQ2QTMGEJWKVBJKT5JSC4YWPCMCQ
RUN chmod +x ~/Zabbix/measure_latencies_Zabbix.sh
RUN if [[ -n "$zabbix" ]]; then \
    cd ~/Zabbix/zabbix_config_templates/ && \
    cp $zabbix /etc/zabbix/zabbix_agentd.conf \
    fi

#run zabbix service
RUN service zabbix-agent restart
RUN zabbix_agentd

#vscode extentions
RUN mkdir ~/vscode-temp
WORKDIR ~/vscode-temp
RUN wget https://az764295.vo.msecnd.net/stable/b06ae3b2d2dbfe28bca3134cc6be65935cdfea6a/code_1.69.1-1657615746_amd64.deb
RUN apt install ./code_1.69.1-1657615746_amd64.deb -y 
RUN wget -O ~/vscode-temp/extentions https://raw.githubusercontent.com/hub-raum/main-Robotic-Arm-Code/develop/docker/vscode_extentions?token=GHSAT0AAAAAABWSQ2QTO57GOPQ7UI52IO22YWPE7EQ
RUN wget -O ~/vscode-temp/extentions.py https://raw.githubusercontent.com/hub-raum/main-Robotic-Arm-Code/develop/docker/extentions_install.py?token=GHSAT0AAAAAABWSQ2QSJDQTDYOFOXMD6BUEYWPFD2Q
RUN python3 ~/vscode-temp/extentions.py
RUN rm -rf ~/vscode-temp

#github onfig
RUN git config --global user.email "github@external.hubraum.com"
RUN git config --global user.name "MakerspaceKrk"

#port exposure
EXPOSE 7002
EXPOSE 10050
ENTRYPOINT ["/usr/sbin/sshd","-D"]