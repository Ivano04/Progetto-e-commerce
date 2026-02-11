-- Cancella le tabelle vecchie per poterle ricreare con le nuove colonne
DROP TABLE IF EXISTS Ordine_Prodotti CASCADE;
DROP TABLE IF EXISTS Ordine CASCADE;
DROP TABLE IF EXISTS Prodotto CASCADE;
DROP TABLE IF EXISTS Utente CASCADE;

--Tabella Utente (Ora include is_admin per i permessi)
CREATE TABLE Utente (
    email VARCHAR(100) PRIMARY KEY,
    nome VARCHAR(100),
    password VARCHAR(100),
    is_admin BOOLEAN DEFAULT FALSE 
);

--Tabella Prodotto (Ora include immagine per le foto)
CREATE TABLE Prodotto (
    id VARCHAR(50) PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    prezzo DECIMAL(10, 2) NOT NULL
);

--Tabella Ordine
CREATE TABLE Ordine (
    id SERIAL PRIMARY KEY,
    cliente_email VARCHAR(100) REFERENCES Utente(email) ON DELETE CASCADE, 
    totale DECIMAL(10, 2),
    stato VARCHAR(50)
);

--Tabella di Join (Relazione Ordini-Prodotti)
CREATE TABLE Ordine_Prodotti (
    id_ordine INT REFERENCES Ordine(id) ON DELETE CASCADE,
    id_prodotto VARCHAR(50) REFERENCES Prodotto(id),
    PRIMARY KEY (id_ordine, id_prodotto)
);

CREATE TABLE IF NOT EXISTS Coupon (
    codice VARCHAR(50) PRIMARY KEY,
    percentuale_sconto INT NOT NULL CHECK (percentuale_sconto > 0 AND percentuale_sconto <= 100)
);