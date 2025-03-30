import joblib
import torch
import torch.nn as nn
import torch.optim as optim
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
import pandas as pd
import numpy as np

# Проверка доступности GPU
device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
print(f"Using device: {device}")

# Загрузка данных
df = pd.read_csv("bogdan_dataset.csv")
X = df.drop("risk_score", axis=1).values
y = df["risk_score"].values

# Нормализация данных
scaler = StandardScaler()
X_scaled = scaler.fit_transform(X)

# Разделение на train/test
X_train, X_test, y_train, y_test = train_test_split(
    X_scaled, y, test_size=0.2, random_state=42
)

# Конвертация в тензоры с переносом на GPU
X_train_tensor = torch.FloatTensor(X_train).to(device)
y_train_tensor = torch.FloatTensor(y_train).view(-1, 1).to(device)
X_test_tensor = torch.FloatTensor(X_test).to(device)
y_test_tensor = torch.FloatTensor(y_test).view(-1, 1).to(device)

# Архитектура нейросети
class ChurnPredictor(nn.Module):
    def __init__(self, input_size):
        super().__init__()
        self.fc1 = nn.Linear(input_size, 128)
        self.fc2 = nn.Linear(128, 64)
        self.fc3 = nn.Linear(64, 1)
        self.dropout = nn.Dropout(0.3)
        self.relu = nn.ReLU()

    def forward(self, x):
        x = self.relu(self.fc1(x))
        x = self.dropout(x)
        x = self.relu(self.fc2(x))
        x = self.dropout(x)
        x = self.fc3(x)
        return x

# Инициализация модели с переносом на GPU
model = ChurnPredictor(input_size=X_train.shape[1]).to(device)
criterion = nn.MSELoss()
optimizer = optim.Adam(model.parameters(), lr=0.0005)

# Обучение с использованием GPU
epochs = 30000
for epoch in range(epochs):
    model.train()
    optimizer.zero_grad()
    outputs = model(X_train_tensor)
    loss = criterion(outputs, y_train_tensor)
    loss.backward()
    optimizer.step()

    if (epoch + 1) % 20 == 0:
        print(f"Epoch [{epoch + 1}/{epochs}], Loss: {loss.item():.4f}")

# Сохранение модели (веса автоматически сохраняются с GPU)
torch.save(model.state_dict(), "model.pth")
joblib.dump(scaler, "scaler_no_onetime.save")

# Оценка на GPU
model.eval()
with torch.no_grad():
    test_preds = model(X_test_tensor)
    mae = torch.mean(torch.abs(test_preds - y_test_tensor))
    print(f"\nTest MAE: {mae.item():.2f} баллов")

# Дополнительно: перенос модели обратно на CPU для совместимости (опционально)
model.to('cpu')