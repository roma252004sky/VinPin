from flask import Flask, request, jsonify
import torch
import joblib
import numpy as np

from generate_data_v2 import generate_churn_objects

app = Flask(__name__)


# Загрузка модели и scaler
class ChurnPredictor(torch.nn.Module):
    def __init__(self, input_size):
        super().__init__()
        self.fc1 = torch.nn.Linear(input_size, 128)
        self.fc2 = torch.nn.Linear(128, 64)
        self.fc3 = torch.nn.Linear(64, 1)
        self.relu = torch.nn.ReLU()

    def forward(self, x):
        x = self.relu(self.fc1(x))
        x = self.relu(self.fc2(x))
        x = self.fc3(x)
        return x


model = ChurnPredictor(input_size=8)
model.load_state_dict(torch.load("model.pth"))
model.eval()

scaler = joblib.load("scaler_no_onetime.save")

# Правила для причин, рекомендаций и триггеров
RULES = {
    "high_risk": [
        {
            "condition": lambda data: data["days_since_last_login"] > 14,
            "reason": "Клиент не заходил в систему > 14 дней",
            "actions": ["Срочный звонок менеджера", "SMS с предложением новинок"],
            "triggers": ["call", "sms"]
        },
        {
            "condition": lambda data: data["days_until_sub_end"] == 0,
            "reason": "Отсутствие активной подписки",
            "actions": ["Срочный звонок менеджера", "SMS/Email: Бесплатный пробный период"],
            "triggers": ["sms", "email"]
        },
        {
            "condition": lambda data: data["support_satisfaction"] <3,
            "reason": "Низкая оценка работы поддержки",
            "actions": ["Срочный звонок менеджера", "Персональный анализ проблем"],
            "triggers": ["call"]
        },
    ],
    "medium_risk": [
        {
            "condition": lambda data: data["avg_catalogs_opened"] < 2,
            "reason": "Низкая активность в каталогах (<2 открытия)",
            "actions": ["Email с подборкой каталогов", "Push в ЛК"],
            "triggers": ["email", "push"]
        },
        {
            "condition": lambda data: data["manager_interactions"] <=1,
            "reason": "Нет контакта с менеджером развития",
            "actions": ["Звонок менеджера", "Отправить анкету обратной связи по Email"],
            "triggers": ["call", "email"]
        },
        {
            "condition": lambda data: data["avg_time_in_product"] <=15,
            "reason": "Короткие сессии (<15 минут)",
            "actions": ["Email - Отправить гайд по использованию продукта", "SMS о возможности обучения в Академии"],
            "triggers": ["email", "sms"]
        },
    ],
    "low_risk": [
        {
            "condition": lambda data: data["days_until_sub_end"] < 7,
            "reason": "Подписка истекает через <7 дней",
            "actions": ["Email с напоминанием", "Push в ЛК"],
            "triggers": ["email", "push"]
        },
        {
            "condition": lambda data: data["support_satisfaction"] <4,
            "reason": "Средняя оценка работы поддержки",
            "actions": ["Push оставить обратную связь",],
            "triggers": ["push"]
        },
    ]
}


def get_risk_details(input_data, risk_score):
    risk_level = "low_risk"
    if risk_score >= 10:
        risk_level = "high_risk"
    elif risk_score >= 5:
        risk_level = "medium_risk"

    reasons = []
    actions = []
    triggers = set()

    for rule in RULES[risk_level]:
        if rule["condition"](input_data):
            reasons.append(rule["reason"])
            actions.extend(rule["actions"])
            triggers.update(rule["triggers"])

    return {
        "risk_level": risk_level,
        "reasons": reasons,
        "recommendations": actions,
        "triggers": list(triggers)
    }


@app.route('/vinpin/ml/predict', methods=['POST'])
def predict():
    data_net = request.json
    output = []
    try:
        for data in data_net:
        # Подготовка данных
            input_data = [
                data["days_since_last_login"],
                data["avg_catalogs_opened"],
                data["products_in_tariff"],
                data["manager_interactions"],
                data["avg_time_in_product"],
                data["total_products_purchased"],
                data["support_satisfaction"],
                data["days_until_sub_end"]
            ]

            # Предсказание
            scaled_data = scaler.transform(np.array(input_data).reshape(1, -1))
            tensor_data = torch.FloatTensor(scaled_data)
            with torch.no_grad():
                risk_score = model(tensor_data).item()

            # Формирование ответа
            result = get_risk_details(data, risk_score)
            response = {
                "client_id": data["client_id"],
                "risk_score": round(risk_score, 2),
                **result
            }
            output.append(response)

        print("Данные отправлены")
        return jsonify(output)

    except Exception as e:
        return jsonify({"error": str(e)}), 400


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)