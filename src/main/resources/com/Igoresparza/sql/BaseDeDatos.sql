CREATE TABLE IF NOT EXISTS PERSONA (
        person_id INT NOT NULL AUTO_INCREMENT,
        first_name VARCHAR(100) NOT NULL,
        last_name VARCHAR(100) NOT NULL,
        birth_date DATE,
        PRIMARY KEY (person_id)
);

insert into PERSONA (person_id, first_name, last_name, birth_date)
values ("1","Ruben","Albeniz",'02-02-2001'),
       ("2","Alberto","García",'02-01-1999');