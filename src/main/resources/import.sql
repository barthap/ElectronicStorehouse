INSERT INTO Categories (id, `name`, parent_id) VALUES
(1, 'Elementy pasywne', null),
(2, 'Rezystory', 1),
(3, 'Kondensatory', 1),
(4, 'Elektrolityczne', 3),
(5, 'Ceramiczne', 3),
(6, 'Półprzewodniki', null);

INSERT INTO Locations (id, `name`, parent_id, description) VALUES
(1, 'Pokój', null, ''),
(2, 'Pojemnik na rezystory', 1, ''),
(3, 'Szafka z szufladkami', 1, ''),
(4, 'Szufladka mała 1', 3, ''),
(5, 'Szufladka średnia 2', 3, ''),
(6, 'Szufladka duża 3', 3, ''),
(7, 'Warsztat', null, ''),
(8, 'Biurko', 7, 'Biurko w warsztacie'),
(9, 'Szafka lewa', 8, 'Lewa szafka w biurku w warsztacie'),
(10, 'Szafka prawa', 8, 'Prawa szafka w biurku w warsztacie'),
(11, 'Skrzynia ZASTAVA', 7, 'Drewniana skrzynka na różności');

INSERT INTO Items (id, category_id, `name`, quantity, location_id) VALUES
(1, 2, 'Rezystor 33k', 10, 2),
(2, 2, 'Rezystor 68 Ohm', 20, 2),
(3, 5, '100nF', 50, 2),
(4, 6, 'IRLZ44N', 6, 4),
(5, 4, '1000uF', 5, 5);

INSERT INTO Items (id, description, name, quantity, category_id) VALUES
(6, 'Bardzo długi opis. Ogólnie to zamiast 70 sztuk zamówiłem 700 przypadkowo no i mam, nie wiem co z tym zrobić', 'Rezystor 100R', 600, 2);