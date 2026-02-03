-- Pulizia per evitare duplicati
DELETE FROM Prodotto;

-- Inserimento catalogo iniziale
INSERT INTO Prodotto (id, nome, prezzo) VALUES ('P01', 'Laptop Pro', 1500.00);
INSERT INTO Prodotto (id, nome, prezzo) VALUES ('P02', 'Smartphone Plus', 800.00);
INSERT INTO Prodotto (id, nome, prezzo) VALUES ('P03', 'Cuffie Noise Cancelling', 250.00);
INSERT INTO Prodotto (id, nome, prezzo) VALUES ('P04', 'Monitor Gaming 27', 350.00);
INSERT INTO Prodotto (id, nome, prezzo) VALUES ('P05', 'Tastiera Meccanica RGB', 120.00);
INSERT INTO Prodotto (id, nome, prezzo) VALUES ('P06', 'Mouse Wireless Ergonomico', 85.50);
INSERT INTO Prodotto (id, nome, prezzo) VALUES ('P07', 'Webcam 4K UltraHD', 199.00);
INSERT INTO Prodotto (id, nome, prezzo) VALUES ('P08', 'Sedia da Ufficio Pro', 249.00);
-- l'utente di test per permettere il salvataggio degli ordini
INSERT INTO Utente (email, nome, password) 
VALUES ('Rossi', 'Mario', 'Rossi')
ON CONFLICT (email) DO NOTHING; -- Evita errori se l'utente esiste già