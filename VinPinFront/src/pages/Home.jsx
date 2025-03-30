import React, { useState } from 'react';
import { FaTable, FaChartBar, FaSyncAlt } from 'react-icons/fa';  // Импорт иконок
import { Link } from 'react-router-dom';  // Для маршрутизации между страницами
import Table from '../components/Table';  // Компонент для таблицы
import logo from '../../public/apple.png';  // Путь к логотипу
import { Button, notification } from 'antd';

const Home = () => {

  const refreshPredictions = () => {
      notification.success({
        message: 'Успех',
        description: 'Данные успешно обновлены',
        placement: 'topRight',
      });
  };

  return (
    <div className="App">
      <div className="sidebar">
        <div className="sidebar-item">
          <Link to="/vinpin/front">
            <FaTable />
            <span>Таблица</span>
          </Link>
        </div>
        <div className="sidebar-item">
          <Link to="/vinpin/front/graph">
            <FaChartBar />
            <span>Dashboard</span>
          </Link>
        </div>
        
        {/* Логотип теперь внизу */}
        <div className="sidebar-footer">
          <img src={logo} alt="Logo" className="logo" />
        </div>
      </div>

      <div className="content">
        <div className="refresh-container">
          <Button 
            type="default" 
            icon={<FaSyncAlt />} 
            onClick={refreshPredictions}
            className="refresh-button"
          >
            Обновить данные
          </Button>
        </div>
        <Table />
        {/* <img className='background-woman' src="https://optim.tildacdn.com/tild6333-3135-4536-a330-613437303633/-/format/webp/t-shirt-mockup-of-a-.png.webp" alt="" /> */}
      </div>
    </div>
  );
};

export default Home;
