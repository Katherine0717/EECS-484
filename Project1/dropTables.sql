DROP TRIGGER CITY_ID_TRIGGER;
DROP SEQUENCE CITY_ID_SEQUENCE;

DROP TRIGGER PROGRAMS_ID_TRIGGER;
DROP SEQUENCE PROGRAMS_ID_SEQUENCE;

DROP TRIGGER Order_Friend_Pairs;

ALTER TABLE ALBUMS DROP CONSTRAINT ALBUM_IN_PHOTO;
ALTER TABLE PHOTOS DROP CONSTRAINT PHOTO_IN_ALBUMS;

DROP TABLE USERS CASCADE CONSTRAINTS;
DROP TABLE FRIENDS CASCADE CONSTRAINTS;
DROP TABLE CITIES CASCADE CONSTRAINTS;
DROP TABLE USER_CURRENT_CITIES CASCADE CONSTRAINTS;
DROP TABLE USER_HOMETOWN_CITIES CASCADE CONSTRAINTS;
DROP TABLE MESSAGES CASCADE CONSTRAINTS;
DROP TABLE PROGRAMS CASCADE CONSTRAINTS;
DROP TABLE EDUCATION CASCADE CONSTRAINTS;
DROP TABLE USER_EVENTS CASCADE CONSTRAINTS;
DROP TABLE PARTICIPANTS CASCADE CONSTRAINTS;
DROP TABLE ALBUMS CASCADE CONSTRAINTS;
DROP TABLE PHOTOS CASCADE CONSTRAINTS;
DROP TABLE TAGS CASCADE CONSTRAINTS;