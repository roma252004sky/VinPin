import React, { useState, useEffect } from 'react';
import { Table, Button, Input, notification } from 'antd';
import ActionModal from './ActionModal';

const TableComponent = () => {
  const [data, setData] = useState([]);
  const [selectedRow, setSelectedRow] = useState(null);
  const [riskFilterValue, setRiskFilterValue] = useState(null); 
  const [fioSearchText, setFioSearchText] = useState('');
  const [pageSize, setPageSize] = useState(10); 

  const handlePageSizeChange = (value) => {
    setPageSize(value);  // Обновляем состояние pageSize
  };

  useEffect(() => {
    fetch('http://vinpin.ru/vinpin/main/getClients')  // Заменить на реальный API
      .then((response) => {
        if (!response.ok) {
          throw new Error('Ошибка при загрузке данных');
        }
        return response.json();
      })
      .then((data) => {
        setData(data);
      })
      .catch((error) => {
        const r_data = [
          { id: 1, fio: 'Иванов Иван Иванович', risk: 'Высокий уровень риска', riskScore: 85, reason: 'Недовольство услугами', action: 'Улучшение качества обслуживания' },
          { id: 2, fio: 'Петров Петр Петрович', risk: 'Средний уровень риска', riskScore: 65, reason: 'Высокая цена', action: 'Пересмотр ценовой политики' },
          { id: 3, fio: 'Сидоров Сидор Сидорович', risk: 'Низкий уровень риска', riskScore: 45, reason: 'Конкурентное предложение', action: 'Предложение дополнительных бонусов' },
          { id: 4, fio: 'Кузнецов Алексей Викторович', risk: 'Минимальный уровень риска', riskScore: 25, reason: 'Недовольство услугами', action: 'Повышение качества обслуживания' },
          { id: 5, fio: 'Федоров Михаил Александрович', risk: 'Высокий уровень риска', riskScore: 90, reason: 'Высокая цена', action: 'Пересмотр цен' },
          { id: 6, fio: 'Смирнов Сергей Викторович', risk: 'Средний уровень риска', riskScore: 60, reason: 'Конкурентное предложение', action: 'Анализ конкурентов' },
          { id: 7, fio: 'Дмитриева Наталья Ивановна', risk: 'Низкий уровень риска', riskScore: 40, reason: 'Недовольство услугами', action: 'Улучшение качества продукта' },
          { id: 8, fio: 'Алексеева Екатерина Александровна', risk: 'Минимальный уровень риска', riskScore: 30, reason: 'Высокая цена', action: 'Предложение скидок' },
          { id: 9, fio: 'Григорьев Олег Николаевич', risk: 'Высокий уровень риска', riskScore: 85, reason: 'Конкурентное предложение', action: 'Разработка новой маркетинговой стратегии' },
          { id: 10, fio: 'Лебедев Артем Валерьевич', risk: 'Средний уровень риска', riskScore: 70, reason: 'Недовольство услугами', action: 'Проведение опросов среди клиентов' },
          { id: 11, fio: 'Новиков Валерий Петрович', risk: 'Низкий уровень риска', riskScore: 50, reason: 'Высокая цена', action: 'Оптимизация цен' },
          { id: 12, fio: 'Ильина Марина Сергеевна', risk: 'Минимальный уровень риска', riskScore: 35, reason: 'Конкурентное предложение', action: 'Анализ конкурентов и улучшение предложения' },
          { id: 13, fio: 'Захарова Татьяна Викторовна', risk: 'Высокий уровень риска', riskScore: 95, reason: 'Недовольство услугами', action: 'Обратная связь и улучшение сервиса' },
          { id: 14, fio: 'Ковалев Александр Михайлович', risk: 'Средний уровень риска', riskScore: 55, reason: 'Высокая цена', action: 'Снижение стоимости' },
          { id: 15, fio: 'Мартынов Виктор Иванович', risk: 'Низкий уровень риска', riskScore: 45, reason: 'Конкурентное предложение', action: 'Предложение выгодных условий' },
          { id: 16, fio: 'Романова Ольга Дмитриевна', risk: 'Минимальный уровень риска', riskScore: 20, reason: 'Недовольство услугами', action: 'Повышение качества обслуживания' },
          { id: 17, fio: 'Васильев Павел Александрович', risk: 'Высокий уровень риска', riskScore: 80, reason: 'Высокая цена', action: 'Пересмотр ценовой политики' },
          { id: 18, fio: 'Щербина Ирина Олеговна', risk: 'Средний уровень риска', riskScore: 60, reason: 'Конкурентное предложение', action: 'Предложение скидок' },
          { id: 19, fio: 'Соловьев Вячеслав Павлович', risk: 'Низкий уровень риска', riskScore: 50, reason: 'Недовольство услугами', action: 'Обратная связь с клиентами' },
          { id: 20, fio: 'Яковлева Дарина Михайловна', risk: 'Минимальный уровень риска', riskScore: 30, reason: 'Высокая цена', action: 'Предложение акций' },
          { id: 21, fio: 'Фролов Николай Вячеславович', risk: 'Высокий уровень риска', riskScore: 87, reason: 'Конкурентное предложение', action: 'Улучшение продуктовой линейки' },
          { id: 22, fio: 'Королева Наталья Ивановна', risk: 'Средний уровень риска', riskScore: 72, reason: 'Высокая цена', action: 'Проведение маркетинговых акций' },
          { id: 23, fio: 'Боровик Артем Олегович', risk: 'Низкий уровень риска', riskScore: 48, reason: 'Недовольство услугами', action: 'Обратная связь с клиентами' },
          { id: 24, fio: 'Михайлова Юлия Васильевна', risk: 'Минимальный уровень риска', riskScore: 32, reason: 'Конкурентное предложение', action: 'Анализ конкурентов' },
          { id: 25, fio: 'Горбунова Ирина Анатольевна', risk: 'Высокий уровень риска', riskScore: 88, reason: 'Недовольство услугами', action: 'Пересмотр обслуживания' },
          { id: 26, fio: 'Чернов Сергей Павлович', risk: 'Средний уровень риска', riskScore: 77, reason: 'Конкурентное предложение', action: 'Предложение скидок' },
          { id: 27, fio: 'Дмитриева Ольга Александровна', risk: 'Низкий уровень риска', riskScore: 41, reason: 'Недовольство услугами', action: 'Предложение бонусов' },
          { id: 28, fio: 'Калинина Ирина Геннадьевна', risk: 'Минимальный уровень риска', riskScore: 29, reason: 'Высокая цена', action: 'Снижение стоимости' },
          { id: 29, fio: 'Павлов Сергей Викторович', risk: 'Высокий уровень риска', riskScore: 91, reason: 'Конкурентное предложение', action: 'Пересмотр цен' },
          { id: 30, fio: 'Воронова Екатерина Анатольевна', risk: 'Средний уровень риска', riskScore: 65, reason: 'Недовольство услугами', action: 'Улучшение качества обслуживания' },
          { id: 31, fio: 'Гусев Алексей Васильевич', risk: 'Низкий уровень риска', riskScore: 42, reason: 'Конкурентное предложение', action: 'Предложение выгодных условий' },
          { id: 32, fio: 'Смирнова Надежда Игоревна', risk: 'Минимальный уровень риска', riskScore: 28, reason: 'Высокая цена', action: 'Снижение стоимости' },
          { id: 33, fio: 'Иванова Валентина Александровна', risk: 'Высокий уровень риска', riskScore: 86, reason: 'Недовольство услугами', action: 'Изменение сервиса' },
          { id: 34, fio: 'Тимофеева Виктория Андреевна', risk: 'Средний уровень риска', riskScore: 74, reason: 'Конкурентное предложение', action: 'Повышение качества' },
          { id: 35, fio: 'Кириллова Анастасия Дмитриевна', risk: 'Низкий уровень риска', riskScore: 47, reason: 'Высокая цена', action: 'Предложение скидок' },
          { id: 36, fio: 'Киселев Артем Игоревич', risk: 'Минимальный уровень риска', riskScore: 36, reason: 'Конкурентное предложение', action: 'Предложение скидок' },
          { id: 37, fio: 'Жукова Юлия Игоревна', risk: 'Высокий уровень риска', riskScore: 92, reason: 'Высокая цена', action: 'Пересмотр ценовой политики' },
          { id: 38, fio: 'Никитин Сергей Андреевич', risk: 'Средний уровень риска', riskScore: 70, reason: 'Недовольство услугами', action: 'Повышение качества обслуживания' },
          { id: 39, fio: 'Васильева Ирина Владимировна', risk: 'Низкий уровень риска', riskScore: 44, reason: 'Конкурентное предложение', action: 'Анализ конкурентов' },
          { id: 40, fio: 'Максимова Лариса Николаевна', risk: 'Минимальный уровень риска', riskScore: 34, reason: 'Высокая цена', action: 'Снижение цен' },
          { id: 41, fio: 'Шмидт Евгения Александровна', risk: 'Высокий уровень риска', riskScore: 89, reason: 'Конкурентное предложение', action: 'Обратная связь и улучшение сервиса' },
          { id: 42, fio: 'Петрова Светлана Викторовна', risk: 'Средний уровень риска', riskScore: 63, reason: 'Недовольство услугами', action: 'Улучшение обслуживания' },
          { id: 43, fio: 'Королева Ирина Петровна', risk: 'Низкий уровень риска', riskScore: 50, reason: 'Конкурентное предложение', action: 'Анализ конкурентов' },
          { id: 44, fio: 'Воронцов Роман Павлович', risk: 'Минимальный уровень риска', riskScore: 31, reason: 'Высокая цена', action: 'Предложение скидок' },
          { id: 45, fio: 'Захарова Людмила Борисовна', risk: 'Высокий уровень риска', riskScore: 90, reason: 'Недовольство услугами', action: 'Обратная связь с клиентами' },
          { id: 46, fio: 'Остапенко Евгений Иванович', risk: 'Средний уровень риска', riskScore: 62, reason: 'Конкурентное предложение', action: 'Предложение скидок' },
          { id: 47, fio: 'Дмитриев Михаил Александрович', risk: 'Низкий уровень риска', riskScore: 43, reason: 'Недовольство услугами', action: 'Анализ конкурентов' },
          { id: 48, fio: 'Исаев Сергей Павлович', risk: 'Минимальный уровень риска', riskScore: 27, reason: 'Конкурентное предложение', action: 'Анализ конкурентов и улучшение предложения' },
          { id: 49, fio: 'Гусева Алевтина Владимировна', risk: 'Высокий уровень риска', riskScore: 93, reason: 'Недовольство услугами', action: 'Пересмотр обслуживания' },
          { id: 50, fio: 'Семенова Екатерина Ивановна', risk: 'Средний уровень риска', riskScore: 68, reason: 'Конкурентное предложение', action: 'Разработка новой маркетинговой стратегии' }
        ]
        ;
        setData(r_data);
        
        notification.error({
          message: 'Ошибка загрузки',
          description: error.message,
          placement: 'topRight',
        });
      });
  }, []);

  const handleOpenModal = (row) => {
    setSelectedRow(row);
  };

  const handleCloseModal = () => {
    setSelectedRow(null);
  };

  const handleFioSearchChange = (e) => {
    setFioSearchText(e.target.value);
  };

  // Фильтрация данных
  const filteredData = data.filter((item) => {
    const matchesFio = item.fio.toLowerCase().includes(fioSearchText.toLowerCase());
    const matchesRisk = riskFilterValue
      ? item.risk === riskFilterValue
      : true;  // Если фильтр не выбран, показываем все записи
    return matchesFio && matchesRisk;
  });

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', sorter: (a, b) => a.id - b.id, align: 'center' },
    {
      title: 'ФИО',
      dataIndex: 'fio',
      key: 'fio',
      filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters }) => (
        <div style={{ padding: 8 }}>
          <Input
            placeholder="Поиск по ФИО"
            value={fioSearchText}
            onChange={handleFioSearchChange}
            onPressEnter={() => {
              setSelectedKeys([fioSearchText]);
              confirm();
            }}
          />
        </div>
      ),
      onFilter: (value, record) => record.fio.toLowerCase().includes(value.toLowerCase()),
    },
    {
      title: 'Риск оттока',
      dataIndex: 'risk',
      key: 'risk',
      align: 'center',
      filters: [
        { text: 'Высокий уровень риска', value: 'Высокий уровень риска' },
        { text: 'Средний уровень риска', value: 'Средний уровень риска' },
        { text: 'Низкий уровень риска', value: 'Низкий уровень риска' },
        { text: 'Минимальный уровень риска', value: 'Минимальный уровень риска' },
      ],
      onFilter: (value, record) => record.risk === value,
    },
    { title: 'Риск оттока (баллы)', dataIndex: 'riskScore', key: 'riskScore', sorter: (a, b) => b.riskScore - a.riskScore, defaultSortOrder: 'ascend', align: 'center' },
    { title: 'Возможные причины', dataIndex: 'reason', key: 'reason' },
    { title: 'Рекомендованное действие', dataIndex: 'recommendation', key: 'recommendation' },
    {
      title: 'Действия',
      key: 'actions',
      align: 'center',
      render: (_, record) => (
        <div className="centered-button">
          <Button type="primary" onClick={() => handleOpenModal(record)}>
            Подробнее
          </Button>
        </div>
      ),
    },
  ];

  return (
    <div className="table-container">
      <Table
        columns={columns}
        dataSource={filteredData}  // Отображаем отфильтрованные данные
        rowKey="id"
        pagination={{
          pageSize: pageSize,  // Устанавливаем количество записей на странице из состояния
          showSizeChanger: true,  // Показываем возможность выбора количества записей
          pageSizeOptions: ['5', '10', '20', '50'],  // Доступные варианты для выбора количества записей на странице
          onShowSizeChange: (current, size) => setPageSize(size),  // Обработчик изменения количества записей
        }}
        bordered
        rowClassName={record => {
          if (record.risk === 'Высокий уровень риска') return 'high-risk-row';
          if (record.risk === 'Средний уровень риска') return 'medium-risk-row';
          if (record.risk === 'Низкий уровень риска') return 'low-risk-row';
          return '';
        }}
        
      />
      {selectedRow && <ActionModal id={selectedRow.id} onClose={handleCloseModal} />}
    </div>
  );
};

export default TableComponent;
