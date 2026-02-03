-- Creazione della tabella Prodotti
CREATE TABLE IF NOT EXISTS Prodotto (
    id VARCHAR(50) PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    prezzo DECIMAL(10, 2) NOT NULL
);

-- Creazione della tabella Utente (necessaria per il Login)
CREATE TABLE IF NOT EXISTS Utente (
    email VARCHAR(100) PRIMARY KEY,
    nome VARCHAR(100),
    password VARCHAR(100)
);

-- Creazione della tabella Ordine
CREATE TABLE IF NOT EXISTS Ordine (
    id SERIAL PRIMARY KEY,
    cliente_email VARCHAR(100) REFERENCES Utente(email),
    totale DECIMAL(10, 2),
    stato VARCHAR(50)
);

-- Tabella di Join per la relazione molti-a-molti tra Ordini e Prodotti
CREATE TABLE IF NOT EXISTS Ordine_Prodotti (
    id_ordine INT REFERENCES Ordine(id) ON DELETE CASCADE,
    id_prodotto VARCHAR(50) REFERENCES Prodotto(id),
    PRIMARY KEY (id_ordine, id_prodotto)
);