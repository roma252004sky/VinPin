
import numpy as np
import pandas as pd

def generate_churn_data(num_samples=5000, seed=None):  # Исправлен порядок аргументов
    """
    Генерирует синтетические данные для прогнозирования оттока клиентов.
    Учитывает только подписки, без разовых продуктов.
    """
    np.random.seed(seed)

    data = {
        "days_since_last_login": np.random.randint(0, 180, num_samples),
        "avg_catalogs_opened": np.round(np.random.uniform(0.5, 10.0, num_samples), 1),
        "products_in_tariff": np.random.randint(1, 6, num_samples),
        "manager_interactions": np.random.randint(0, 10, num_samples),
        "avg_time_in_product": np.random.randint(5, 120, num_samples),
        "total_products_purchased": np.random.randint(1, 8, num_samples),
        "support_satisfaction": np.round(np.random.uniform(1.0, 5.0, num_samples), 1),
        "days_until_sub_end": np.random.randint(0, 365, num_samples),
    }

    df = pd.DataFrame(data)

    df["risk_score"] = (
            (df["days_since_last_login"] > 14) * 4 +
            (df["avg_catalogs_opened"] < 2.0) * 3 +
            (df["products_in_tariff"] == 0) * 3 +
            (df["manager_interactions"] == 0) * 2 +
            (df["avg_time_in_product"] < 15) * 2 +
            (df["total_products_purchased"] == 0) * 2 +
            (df["support_satisfaction"] < 3.0) * 1 +
            (df["days_until_sub_end"] < 7) * 1
    )

    return df

# Генерация 1,000,000 строк (200 итераций по 5000)
chunks = []
for i in range(3):  # Меняем сид на каждой итерации
    chunk = generate_churn_data(num_samples=5000, seed=i+62)
    chunks.append(chunk)
    print(f"Generated chunk {i+1}/200", end="\r")

df = pd.concat(chunks, ignore_index=True)

# Сохранение данных
df.to_csv("bogdan_dataset.csv", index=False)
print("\nData saved to million_rows_dataset.csv")