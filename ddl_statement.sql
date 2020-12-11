PRAGMA foreign_keys = on;

CREATE TABLE user (
    user_id       INTEGER PRIMARY KEY AUTOINCREMENT,
    login    TEXT     UNIQUE,
    password INTEGER,
    nickname TEXT     UNIQUE,
    folder_to_download TEXT,
    folder_to_upload  TEXT
);

-- логгируем, когда у сервера были сессии
create table server_session (
    server_session_id integer primary key autoincrement
    , creation_time text not null constraint DF_creation_time default (datetime('now'))
);


-- сопоставляем их с сессями пользователей
CREATE TABLE user_session (
    user_session_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    server_session_id INTEGER NOT NULL  references server_session (server_session_id),
    creation_time     TEXT    NOT NULL
		CONSTRAINT DF_creation_time DEFAULT (datetime('now', 'localtime') ),
    nickname          TEXT);

-- сообщения, которые видны всем в общем чате
create table messages_broadcast (
    id integer primary key autoincrement
    , creation_time     TEXT    NOT NULL
                              CONSTRAINT DF_creation_time DEFAULT (datetime('now', 'localtime') )
    , server_session_id integer references server_session (server_session_id)
    , user_session_id integer        references user_session (user_session_id)
    , message text
);

-- личные сообщения
create table messages_personal (
    id integer primary key autoincrement
    , creation_time     TEXT    NOT NULL
                              CONSTRAINT DF_creation_time DEFAULT (datetime('now', 'localtime') )
    , sent_user_session_id integer        references user_session (user_session_id)
    , recieved_user_session_id integer    references user_session (user_session_id)
    , message text
);

-- файл, откуда сервер будет брать свои настройки при запуске. Сделано в виде модели EAV
create table server_options (
    option_name text primary key
    , value TEXT
);

-- заполняю опции сервера
insert into server_options (option_name, value)
values ('folder_to_store_files', 'd:\Max\Documents\Учёба\GeekBrains\2019\FileServer\fileServerStorage');