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
        foreign key (FILM_MPA) references MPA ON DELETE CASCADE
);

create table if not exists FILM_GENRE
(
    FILM_ID  INTEGER not null,
    GENRE_ID INTEGER not null,
    constraint FILM_GENRE_FK
        foreign key (FILM_ID) references FILMS ON DELETE CASCADE,
    constraint FILM_GENRE___FK
        foreign key (GENRE_ID) references GENRES ON DELETE CASCADE,
    constraint UNIQUE_GENRES unique (FILM_ID, GENRE_ID)
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
        foreign key (USER_ID) references USERS ON DELETE CASCADE,
    constraint FRIENDS_FRIEND_ID_FK
        foreign key (FRIEND_ID) references USERS ON DELETE CASCADE,
    constraint UNIQUE_FRIENDS unique (USER_ID, FRIEND_ID)
);

create table if not exists LIKES
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint LIKES_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS ON DELETE CASCADE,
    constraint LIKES_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS ON DELETE CASCADE
);

create table if not exists REVIEWS
(
    REVIEW_ID      INTEGER auto_increment,
    REVIEW_CONTENT CHARACTER VARYING(2000) not null,
    REVIEW_TYPE    BOOLEAN not null,
    REVIEW_USER_ID INTEGER not null,
    REVIEW_FILM_ID INTEGER not null,
    REVIEW_USEFUL  INTEGER default 0,
    constraint REVIEWS_PK
    primary key (REVIEW_ID),
    constraint REVIEWS_FILMS_FILM_ID_FK
    foreign key (REVIEW_FILM_ID) references FILMS ON DELETE CASCADE,
    constraint REVIEWS_USERS_USER_ID_FK
    foreign key (REVIEW_USER_ID) references USERS ON DELETE CASCADE
    );

create table if not exists DIRECTORS
(
    DIRECTOR_ID INTEGER auto_increment,
    DIRECTOR_NAME CHARACTER VARYING(64) not null,
    constraint DIRECTORS_PK
        primary key (DIRECTOR_ID)
);

create table if not exists FILM_DIRECTOR
(
    FILM_ID  INTEGER not null,
    DIRECTOR_ID INTEGER not null,
    CONSTRAINT FILM_DIRECTOR_PK PRIMARY KEY (FILM_ID, DIRECTOR_ID),
    constraint FILM_DIRECTOR_FK
        foreign key (FILM_ID) references FILMS ON DELETE CASCADE ON UPDATE CASCADE,
    constraint DIRECTOR_FILM_FK
        foreign key (DIRECTOR_ID) references DIRECTORS ON DELETE CASCADE ON UPDATE CASCADE
);

create table if not exists REVIEW_LIKES
(
    REVIEW_ID INTEGER not null,
    USER_ID   INTEGER not null,
    IS_LIKE   boolean,
    constraint REVIEW_LIKES_REVIEWS_REVIEW_ID_FK
        foreign key (REVIEW_ID) references REVIEWS ON DELETE CASCADE,
    constraint REVIEW_LIKES_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS ON DELETE CASCADE
);
create table if not exists FEED
(
     EVENT_ID INTEGER not null AUTO_INCREMENT,
     USER_ID INTEGER not null,
     ENTITY_ID INTEGER not null,
     EVENT_TYPE CHARACTER VARYING(6) not null,
     OPERATION CHARACTER VARYING(6) not null,
     FEED_TIMESTAMP BIGINT not null,


	 CONSTRAINT FEED_PK PRIMARY KEY (EVENT_ID),
	 CONSTRAINT FEED_USERS_FK FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(USER_ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);
