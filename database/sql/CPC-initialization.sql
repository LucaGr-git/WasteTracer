
 
PRAGMA foreign_keys = OFF;

drop table if exists ACCOUNT;
drop table if exists ACTIVITY;
drop table if exists COUNTRY;
drop table if exists FOODCLASS;
drop table if exists FOODGROUP;
drop table if exists FOODSUBCLASS;

drop table if exists LOCATIO;
drop table if exists LOSSSTATS;

drop table if exists LOSSSTAT;
drop table if exists LOCATION;


drop table if exists TAKESPARTIN;


drop table if exists STUDENTCODE;
drop table if exists ACCOUNTS;
drop table if exists FOODSUPPLY;
drop table if exists CAUSEOFLOSS;



PRAGMA foreign_keys = ON;
/*  Data Definition */




CREATE TABLE COUNTRY
        (M49CODE      VARCHAR,
        COUNTRY       VARCHAR UNIQUE NOT NULL, 

        PRIMARY KEY (M49CODE)
        )
        WITHOUT ROWID;

CREATE TABLE LOCATION
        (REGION       VARCHAR UNIQUE,
        M49CODE       VARCHAR NOT NULL,

        PRIMARY KEY (REGION, M49CODE),

        FOREIGN KEY (M49CODE) REFERENCES COUNTRY(M49CODE) 
        )
        WITHOUT ROWID;





CREATE TABLE FOODGROUP
        (GROUPCODE           VARCHAR,
        GROUPDESCRIPTOR      VARCHAR NOT NULL, 

        PRIMARY KEY (GROUPCODE)
        )
        WITHOUT ROWID;

CREATE TABLE FOODCLASS
        (CLASSCODE       VARCHAR,
        GROUPCODE        VARCHAR,
        CLASSDESCRIPTOR  VARCHAR NOT NULL,

        PRIMARY KEY (CLASSCODE, GROUPCODE), /* */

        FOREIGN KEY (GROUPCODE) REFERENCES FOODGROUP(GROUPCODE) 
        )
        WITHOUT ROWID;

CREATE TABLE FOODSUBCLASS
        (SUBCLASSCODE   VARCHAR,
        CLASSCODE       VARCHAR, 
        GROUPCODE        VARCHAR, 
        DESCRIPTOR  VARCHAR,

        PRIMARY KEY (SUBCLASSCODE, CLASSCODE, GROUPCODE, DESCRIPTOR),       /* */

        FOREIGN KEY (GROUPCODE) REFERENCES FOODGROUP(GROUPCODE),
        FOREIGN KEY (CLASSCODE) REFERENCES FOODCLASS(CLASSCODE) 
        )
        WITHOUT ROWID;

CREATE TABLE LOSSSTAT
        (ROW_ID          INTEGER,
        
        LOSSPERCENTAGE   FLOAT NOT NULL,
        YEAR             INTEGER NOT NULL,

        GROUPCODE        VARCHAR NOT NULL,     
        CLASSCODE        VARCHAR NOT NULL,      
        SUBCLASSCODE     VARCHAR,              
        DESCRIPTOR       VARCHAR NOT NULL,
        CAUSEOFLOSS      VARCHAR,
        M49CODE          VARCHAR NOT NULL,
        COUNTRY          VARCHAR NOT NULL,
        REGION           VARCHAR,
        FOODSUPPLY       VARCHAR,
        

        PRIMARY KEY (ROW_ID),

        FOREIGN KEY (GROUPCODE) REFERENCES FOODGROUP(GROUPCODE),
        FOREIGN KEY (CLASSCODE) REFERENCES FOODCLASS(CLASSCODE),
        FOREIGN KEY (SUBCLASSCODE) REFERENCES FOODSUBCLASS(SUBCLASSCODE),
        FOREIGN KEY (DESCRIPTOR) REFERENCES FOODSUBCLASS(DESCRIPTOR),
        FOREIGN KEY (M49CODE) REFERENCES COUNTRY(M49CODE),
        FOREIGN KEY (COUNTRY) REFERENCES COUNTRY(COUNTRY),
        FOREIGN KEY (REGION) REFERENCES LOCATION(REGION)

        )
        WITHOUT ROWID;





CREATE TABLE TAKESPARTIN
        (STATSROWID    INTEGER NOT NULL,
        ACTIVITY       VARCHAR NOT NULL,

        PRIMARY KEY (STATSROWID, ACTIVITY),

        FOREIGN KEY (STATSROWID) REFERENCES LOSSSTAT(ROW_ID)
        )
        WITHOUT ROWID;

CREATE TABLE PERSONA
        (USERNAME      VARCHAR,
        USERCODE       INTEGER UNIQUE NOT NULL,
        PASSCODE       VARCHAR,
        IMGLINKPATH     VARCHAR,
        DESCRIPTOR     VARCHAR,

        PRIMARY KEY (USERCODE) /* */

        )
        WITHOUT ROWID;

CREATE TABLE STUDENT
        (USERNAME      VARCHAR,
        USERCODE       INTEGER UNIQUE NOT NULL,
        PASSCODE       VARCHAR,
        STUDENTCODE     VARCHAR,
        DESCRIPTOR     VARCHAR,

        PRIMARY KEY (USERCODE) /* */

        )
        WITHOUT ROWID;



/* Adding personas + student details to database */
INSERT INTO PERSONA (USERNAME, USERCODE, PASSCODE, DESCRIPTOR, IMGLINKPATH) VALUES ('Penny Bartley', 103, 'example', 'Penny lives 
in north-Victoria in rural Mallee, she works on a small family grain farm however she is worried about the rise of large corporate 
farmers and what that means for the future of small grain farmers like herself. She wants to decrease her level of waste production 
as that is her main advantage over other larger farmers. To do this she needs to discover what parts of her farm are producing 
too much waste. Overall, her job as a farmer is difficult and has long hours but she finds it is fulfilling.', 'src/main/resources/images/dbimgs/103.png');

INSERT INTO PERSONA (USERNAME, USERCODE, PASSCODE, DESCRIPTOR, IMGLINKPATH) VALUES ('Grace Smith', 101, 'example', 
'Grace Smith is a young activist for climate change both online and in person and a student of Medical Science at UCLA. 
She has great people skills and often finds herself convincing others to make changes for the better of the environment. 
In her experience being an online and in-person activist she has found success in drawing the attention of other interested 
people however she is struggling to captivate the ordinary person into making changes for the better. Often she finds that 
her articles she shares are wordy and difficult to understand. She lives with her siblings and parents, however, 
is interested in moving out at some stage and is two years into her Medical Science degree at UCLA.', 'src/main/resources/images/dbimgs/101.png');

INSERT INTO PERSONA (USERNAME, USERCODE, PASSCODE, DESCRIPTOR, IMGLINKPATH) VALUES ('Bradley Johnson', 102, 'example', 
'Bradley Johnson is a high-ranking employee of a large corporate farming company in Australia. As a father and part of 
his job at a corporate farming company, he’s interested in the growing movement to be ecologically responsible and protect 
the environment. He wants to know how he can join this movement to help both his family and company. He currently lives 
with his family and has been working for around 30 years. He works at and deals with a lot of executive meetings and 
communications which can be stressful, however his input is taken seriously.', 'src/main/resources/images/dbimgs/102.png');

INSERT INTO STUDENT (USERNAME, USERCODE, PASSCODE, DESCRIPTOR, STUDENTCODE) VALUES ('Luca Grosso', 104, 'example', 
'Luca Grosso is a student at RMIT', 's4093817');

INSERT INTO STUDENT (USERNAME, USERCODE, PASSCODE, DESCRIPTOR, STUDENTCODE) VALUES ('Joe Czerniecki', 105, 'example', 
'Joe Czerniecki is a student at RMIT', 's4072773');

