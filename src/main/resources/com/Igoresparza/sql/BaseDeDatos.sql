CREATE TABLE persona (
        person_id INT NOT NULL AUTO_INCREMENT,
        first_name VARCHAR(100) NOT NULL,
        last_name VARCHAR(100) NOT NULL,
        birth_date DATE,
        PRIMARY KEY (person_id)
);