-- Pulizia per evitare duplicati
DELETE FROM Prodotto;

-- Inserimento catalogo iniziale
INSERT INTO Prodotto (id, nome, prezzo) VALUES ('P01', 'Laptop Pro', 1500.00);
INSERT INTO Prodotto (id, nome, prezzo) VALUES ('P02', 'Smartphone Plus', 800.00);
INSERT INTO Prodotto (id, nome, prezzo) VALUES ('P03', 'Cuffie Noise Cancelling', 250.00);
-- Aggiungi altri prodotti a piacere...
-- Inseriamo l'utente di test per permettere il salvataggio degli ordini
INSERT INTO Utente (email, nome, password) 
VALUES ('Rossi', 'Mario', 'Rossi')
ON CONFLICT (email) DO NOTHING; -- Evita errori se l'utente esiste già