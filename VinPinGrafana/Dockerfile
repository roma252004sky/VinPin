FROM grafana/grafana:latest

# Отключаем аутентификацию
ENV GF_AUTH_DISABLE_LOGIN_FORM="true"
ENV GF_AUTH_ANONYMOUS_ENABLED="true"
ENV GF_AUTH_ANONYMOUS_ORG_ROLE="Admin"
ENV GF_AUTH_BASIC_ENABLED="false"

USER 472

# Копируем конфигурационный файл
COPY --chown=472:472 ./custom/grafana.ini /etc/grafana/grafana.ini

# Объявляем volumes
VOLUME ["/var/lib/grafana/dashboards","/var/lib/grafana"]

EXPOSE 3000


