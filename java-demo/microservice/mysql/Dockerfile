FROM swr.cn-north-4.myhuaweicloud.com/rg/mysql:latest
COPY ./micro.sql /docker-entrypoint-initdb.d/
ENV MYSQL_ROOT_PASSWORD=123456
ENV LANG=C.UTF-8
VOLUME /var/lib/mysql
EXPOSE 3306