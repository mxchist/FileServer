PRAGMA foreign_keys = on;

CREATE TABLE user (
    user_id       INTEGER PRIMARY KEY AUTOINCREMENT,
    login    TEXT     UNIQUE,
    password INTEGER,
    nickname TEXT     UNIQUE,
    folder_to_download TEXT,
    folder_to_upload  TEXT
);

create table server_session (
    server_session_id integer primary key autoincrement
    , creation_time text not null constraint DF_creation_time default (datetime('now'))
);

CREATE TABLE user_session (
    user_session_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    server_session_id INTEGER NOT NULL  references server_session (server_session_id),
    creation_time     TEXT    NOT NULL
		CONSTRAINT DF_creation_time DEFAULT (datetime('now', 'localtime') ),
    nickname          TEXT);

create table messages_broadcast (
    id integer primary key autoincrement
    , creation_time     TEXT    NOT NULL
                              CONSTRAINT DF_creation_time DEFAULT (datetime('now', 'localtime') )
    , server_session_id integer references server_session (server_session_id)
    , user_session_id integer        references user_session (user_session_id)
    , message text
);

create table messages_personal (
    id integer primary key autoincrement
    , creation_time     TEXT    NOT NULL
                              CONSTRAINT DF_creation_time DEFAULT (datetime('now', 'localtime') )
    , sent_user_session_id integer        references user_session (user_session_id)
    , recieved_user_session_id integer    references user_session (user_session_id)
    , message text
);

create table server_options (
    id integer primary key autoincrement
    , folder_to_store_files TEXT
);
