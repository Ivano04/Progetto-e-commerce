-- 1. Pulizia completa per garantire un avvio pulito
DELETE FROM Ordine_Prodotti;
DELETE FROM Ordine;
DELETE FROM Prodotto;
DELETE FROM Utente;

-- ADMIN PREFISSATO 
INSERT INTO utente (email, password, nome, is_admin) 
VALUES ('admin@google.it', 'admin', 'Admin', TRUE);

-- 2. Catalogo Prodotti (Totale: 8 articoli)
INSERT INTO Prodotto (id, nome, prezzo) VALUES ('P01', 'Laptop Pro', 1500.00);
INSERT INTO Prodotto (id, nome, prezzo) VALUES ('P02', 'Smartphone Plus', 800.00);
INSERT INTO Prodotto (id, nome, prezzo) VALUES ('P03', 'Monitor Gaming 27', 350.00);
INSERT INTO Prodotto (id, nome, prezzo) VALUES ('P04', 'Tastiera Meccanica RGB', 120.00);
INSERT INTO Prodotto (id, nome, prezzo) VALUES ('P05', 'Mouse Wireless Ergonomico', 85.50);
INSERT INTO Prodotto (id, nome, prezzo) VALUES ('P06', 'Cuffie Noise Cancelling', 299.00);
INSERT INTO Prodotto (id, nome, prezzo) VALUES ('P07', 'Webcam 4K UltraHD', 180.00);
--INSERT INTO Prodotto (id, nome, prezzo) VALUES ('P08', 'Sedia da Ufficio Pro', 245.00);

-- 3. Utente di test per il login
INSERT INTO Utente (email, nome, password) 
VALUES 
('Rossi', 'Mario', 'Rossi'),
('giuseppeverdi@unifi.it', 'Giuseppe', '5678'),
('MarcoVerdi@unifi.it', 'Marco', '1234')

ON CONFLICT (email) DO NOTHING;