CREATE SEQUENCE customer_id_seq;

CREATE TABLE customer (
    id INTEGER DEFAULT NEXTVAL('customer_id_seq') PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    age INTEGER NOT NULL
)