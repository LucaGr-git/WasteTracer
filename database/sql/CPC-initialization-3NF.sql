
 
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


drop table if exists COUNTRYREGION;
drop table if exists M49CODE;
drop table if exists FOOD;
drop table if exists LOSSSTAT;
drop table if exists TAKESPARTIN;
drop table if exists ACCOUNT;




PRAGMA foreign_keys = ON;
/*  Data Definition */



CREATE TABLE COUNTRYREGION
        (LOCATION       VARCHAR,
        PARENTLOCATION       VARCHAR,

        PRIMARY KEY (LOCATION),

        FOREIGN KEY (PARENTLOCATION) REFERENCES COUNTRYREGION(LOCATION) 
        )
        WITHOUT ROWID;

CREATE TABLE M49CODE
        (M49CODE       VARCHAR,
        COUNTRY       VARCHAR NOT NULL UNIQUE,

        PRIMARY KEY (M49CODE),

        FOREIGN KEY (COUNTRY) REFERENCES COUNTRYREGION(LOCATION) 
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

CREATE TABLE FOOD
        (FOODID        VARCHAR,
        FOODCODE       VARCHAR,
        DESCRIPTOR NOT NULL,

        CLASSCODE VARCHAR NOT NULL,
        GROUPCODE VARCHAR NOT NULL,

        PRIMARY KEY (FOODID),

        FOREIGN KEY (CLASSCODE) REFERENCES FOODCLASS(CLASSCODE),
        FOREIGN KEY (GROUPCODE) REFERENCES FOODGROUP(GROUPCODE)
        )
        WITHOUT ROWID;



CREATE TABLE LOSSSTAT
        (ROW_ID       INTEGER,
        YEAR       INTEGER NOT NULL,
        LOSSPERCENTAGE FLOAT NOT NULL,
        FOODSUPPLY VARCHAR,
        CAUSEOFLOSS VARCHAR,
        
        LOCATION VARCHAR NOT NULL,
        FOODID VARCHAR NOT NULL,


        PRIMARY KEY (ROW_ID),

        FOREIGN KEY (LOCATION) REFERENCES COUNTRYREGION(LOCATION),
        FOREIGN KEY (FOODID) REFERENCES FOOD(FOODID)

        )
        WITHOUT ROWID;

CREATE TABLE TAKESPARTIN
        (STATSROWID    INTEGER NOT NULL,
        ACTIVITY       VARCHAR NOT NULL,

        PRIMARY KEY (STATSROWID, ACTIVITY),

        FOREIGN KEY (STATSROWID) REFERENCES LOSSSTAT(ROW_ID)
        )
        WITHOUT ROWID;



CREATE TABLE ACCOUNT
        (USERNAME      VARCHAR,
        USERCODE       INTEGER UNIQUE NOT NULL,
        PASSCODE       VARCHAR,
        DESCRIPTOR     VARCHAR,
        STUDENTCODE    INTEGER,
        IMGLINKPATH    VARCHAR,   /* */

        PRIMARY KEY (USERCODE) /* */

        )
        WITHOUT ROWID;



/* Adding personas + student details to database */
INSERT INTO ACCOUNT (USERNAME, USERCODE, PASSCODE, DESCRIPTOR, STUDENTCODE, IMGLINKPATH) VALUES ('Penny Bartley', 103, 'example', 'Penny lives 
in north-Victoria in rural Mallee, she works on a small family grain farm however she is worried about the rise of large corporate 
farmers and what that means for the future of small grain farmers like herself. She wants to decrease her level of waste production
 as that is her main advantage over other larger farmers. To do this she needs to discover what parts of her farm are producing too 
 much waste. Overall, her job as a farmer is difficult and has long hours but she finds it is fulfilling.', NULL, 'src/main/resources/images/dbimgs/103.png');

INSERT INTO ACCOUNT (USERNAME, USERCODE, PASSCODE, DESCRIPTOR, STUDENTCODE, IMGLINKPATH) VALUES ('Grace Smith', 101, 'example', 
'Grace Smith is a university student in California and has a keen interest in climate change and activism. She regularly talks to 
family and friends about the importance of the environment and impacts of global climate change on the future, she has been 
successful in convincing people to change their carbon footprint and that success has followed her online in social media activism. 
She currently lives with her parents and is interested in the housing market. Grace is two years into her Business degree at UCLA 
and is worried about life after university.', NULL, 'src/main/resources/images/dbimgs/101.png');

INSERT INTO ACCOUNT (USERNAME, USERCODE, PASSCODE, DESCRIPTOR, STUDENTCODE, IMGLINKPATH) VALUES ('Bradley Johnson', 102, 'example', 
'Bradley Johnson is a high ranking employee of a large corporate farming company in Australia. As a father and part of his job at 
a corporate farming company, he’s interested in the growing movement to be ecologically responsible and protect the environment. 
He wants to know how he can join this movement to help both his family and company. He currently lives with his family and has 
been working for around 30 years. he works at and deals with a lot of executive meetings and communications which can be stressful,
 however his input is taken seriously.', NULL, 'src/main/resources/images/dbimgs/102.png');

INSERT INTO ACCOUNT (USERNAME, USERCODE, PASSCODE, DESCRIPTOR, STUDENTCODE, IMGLINKPATH) VALUES ('Luca Grosso', 104, 'example', 
'Luca Grosso is a student at RMIT', 's4093817', NULL);

INSERT INTO ACCOUNT (USERNAME, USERCODE, PASSCODE, DESCRIPTOR, STUDENTCODE, IMGLINKPATH) VALUES ('Joe Czerniecki', 105, 'example', 
'Joe Czerniecki is a student at RMIT', 's4072773', NULL);


