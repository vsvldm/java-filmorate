create table if not exists GENRES
(
    GENRE_ID    INTEGER auto_increment,
    GENRE_TITLE CHARACTER VARYING(64) not null,
    constraint GENRES_PK
        primary key (GENRE_ID)
);

create table if not exists MPA
(
    MPA_ID    INTEGER auto_increment,
    MPA_TITLE CHARACTER VARYING(15),
    constraint MPA_PK
        primary key (MPA_ID)
);

create table if not exists FILMS
(
    FILM_ID           INTEGER auto_increment,
    FILM_NAME         CHARACTER VARYING(64) not null,
    FILM_DESCRIPTION  CHARACTER VARYING(200),
    FILM_RELEASE_DATE DATE,
    FILM_DURATION     BIGINT,
    FILM_MPA    INTEGER,
    constraint FILMS_PK
        primary key (FILM_ID),
    constraint FILMS_RATINGS_MPA_FK
        foreign key (FILM_MPA) references MPA
);

create table if not exists FILM_GENRE
(
    FILM_ID  INTEGER not null,
    GENRE_ID INTEGER not null,
    constraint FILM_GENRE_FK
        foreign key (FILM_ID) references FILMS,
    constraint FILM_GENRE___FK
        foreign key (GENRE_ID) references GENRES
);

create table if not exists USERS
(
    USER_ID       INTEGER auto_increment,
    USER_NAME     CHARACTER VARYING(64),
    USER_LOGIN    CHARACTER VARYING(64) not null,
    USER_BIRTHDAY DATE,
    USER_EMAIL    CHARACTER VARYING(64) not null,
    constraint USERS_PK
        primary key (USER_ID)
);

create table if not exists FRIENDS
(
    USER_ID   INTEGER not null,
    FRIEND_ID INTEGER not null,
    constraint FRIENDS_USER_ID_FK
        foreign key (USER_ID) references USERS,
    constraint FRIENDS_FRIEND_ID_FK
        foreign key (FRIEND_ID) references USERS,
    constraint UNIQUE_FRIENDS unique (USER_ID, FRIEND_ID)
);

create table if not exists LIKES
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint LIKES_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS,
    constraint LIKES_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS
);

