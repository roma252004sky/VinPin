# Используем официальный образ Python
FROM python:3.9-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем необходимые файлы
COPY controller.py model.pth scaler_no_onetime.save ./

# Устанавливаем системные зависимости (если требуются)
RUN apt-get update && apt-get install -y --no-install-recommends gcc python3-dev

# Обновляем pip и устанавливаем зависимости
RUN pip install --upgrade pip && \
    pip install torch --index-url https://download.pytorch.org/whl/cpu && \
    pip install flask numpy scikit-learn joblib

# Открываем порт для доступа
EXPOSE 5000

# Запускаем приложение
CMD ["python", "controller.py"]