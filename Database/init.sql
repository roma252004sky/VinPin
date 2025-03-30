-- Создание схемы vin_pin, если её нет
CREATE SCHEMA IF NOT EXISTS vin_pin;
ALTER SCHEMA vin_pin OWNER TO postgres;

-- Последовательность для statistics_full.id
CREATE SEQUENCE vin_pin.statistics_id_seq
    AS integer;
ALTER SEQUENCE vin_pin.statistics_id_seq OWNER TO postgres;

-- Таблица users
CREATE TABLE vin_pin.users (
    id       serial PRIMARY KEY,
    username varchar,
    password varchar,
    surname  varchar,
    name     varchar
);
ALTER TABLE vin_pin.users OWNER TO postgres;
insert into vin_pin.users (username, password, surname, name)
values ('admin','$2y$10$hed7Iq8cDEXvSF61D3dmC.Ln9hGytlvAVdeCvMVPlk2vvHbHsSsTm','Админ','Админ');
-- Таблица clients
CREATE TABLE vin_pin.clients (
    id          serial PRIMARY KEY,
    fio         varchar,
    phone       varchar,
    date_start  date,
    description varchar,
    enterprise  varchar
);
ALTER TABLE vin_pin.clients OWNER TO postgres;

-- Таблица statistics_full
CREATE TABLE vin_pin.statistics_full (
    id                       integer DEFAULT nextval('vin_pin.statistics_id_seq') NOT NULL PRIMARY KEY,
    days_since_last_login    integer,
    avg_catalogs_opened      integer,
    products_in_tariff       integer,
    manager_interactions     integer,
    avg_time_in_product      integer,
    total_products_purchased integer,
    support_satisfaction     integer,
    days_until_sub_end       integer,
    fk_client                integer REFERENCES vin_pin.clients(id),
    risk_score               double precision,
    reason                   varchar,
    recommendation           varchar,
    risk_level               varchar,
    triggers                 varchar,
    date_create              timestamp
);
ALTER TABLE vin_pin.statistics_full OWNER TO postgres;

-- Связь последовательности с таблицей
ALTER SEQUENCE vin_pin.statistics_id_seq OWNED BY vin_pin.statistics_full.id;

-- Индексы для statistics_full
CREATE INDEX idx_statistics_client_date ON vin_pin.statistics_full (fk_client ASC, date_create DESC);
CREATE INDEX statistics_full_fk_client_index ON vin_pin.statistics_full (fk_client);
CREATE INDEX statistics_full_client_date_idx ON vin_pin.statistics_full (fk_client ASC, date_create DESC);
CREATE INDEX statistics_full_covering_idx ON vin_pin.statistics_full (
    fk_client ASC, date_create DESC,
    days_since_last_login ASC, avg_catalogs_opened ASC,
    products_in_tariff ASC, manager_interactions ASC,
    avg_time_in_product ASC, total_products_purchased ASC,
    support_satisfaction ASC, days_until_sub_end ASC,
    risk_score ASC, reason ASC, recommendation ASC,
    risk_level ASC, triggers ASC
);
CREATE INDEX statistics_full_super_cover_idx ON vin_pin.statistics_full (
    fk_client ASC, date_create DESC,
    days_since_last_login ASC, avg_catalogs_opened ASC,
    products_in_tariff ASC, manager_interactions ASC,
    avg_time_in_product ASC, total_products_purchased ASC,
    support_satisfaction ASC, days_until_sub_end ASC,
    risk_score ASC, reason ASC, recommendation ASC,
    risk_level ASC, triggers ASC, id ASC
);

-- Представление statistics
CREATE VIEW vin_pin.statistics AS
SELECT 
    sf.id,
    sf.days_since_last_login,
    sf.avg_catalogs_opened,
    sf.products_in_tariff,
    sf.manager_interactions,
    sf.avg_time_in_product,
    sf.total_products_purchased,
    sf.support_satisfaction,
    sf.days_until_sub_end,
    sf.fk_client,
    sf.risk_score,
    sf.reason,
    sf.recommendation,
    sf.risk_level,
    sf.triggers,
    sf.date_create
FROM (
    SELECT DISTINCT fk_client 
    FROM vin_pin.statistics_full
) clients
JOIN LATERAL (
    SELECT *
    FROM vin_pin.statistics_full
    WHERE fk_client = clients.fk_client
    ORDER BY date_create DESC
    LIMIT 1
) sf ON true;
ALTER VIEW vin_pin.statistics OWNER TO postgres;