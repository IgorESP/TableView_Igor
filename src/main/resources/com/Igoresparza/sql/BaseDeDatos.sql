-- 1. Crear la base de datos (si no existe ya)
CREATE DATABASE IF NOT EXISTS Alumnos;

-- 2. Usar la base de datos
USE Alumnos;

-- 3. Crear la tabla persona
CREATE TABLE IF NOT EXISTS persona (
        person_id INT NOT NULL AUTO_INCREMENT,
        first_name VARCHAR(100) NOT NULL,
        last_name VARCHAR(100) NOT NULL,
        birth_date DATE,
        PRIMARY KEY (person_id)
);

-- 4. Insertar los datos de ejemplo (3 alumnos)
INSERT INTO persona (first_name, last_name, birth_date)
VALUES
('Laura', 'García', '1998-07-20'),
('Miguel', 'Hernández', '2001-11-05'),
('Sofía', 'Pérez', '1995-03-12');