#!/bin/sh

# Копируем конфиг аутентификации
cp /etc/grafana/override/grafana.ini /etc/grafana/grafana.ini

# Запускаем Grafana
exec /run.sh