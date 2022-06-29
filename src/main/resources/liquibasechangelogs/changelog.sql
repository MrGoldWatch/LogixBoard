--liquibase formatted sql

--changeset Hosein:logixboard
CREATE TABLE IF NOT EXISTS shipment (
    reference_id text,
    organizations text[],
    arrival_time text
    );

CREATE TABLE IF NOT EXISTS organization (
    id uuid,
    code text
    );

CREATE TABLE IF NOT EXISTS transportpacks (
    reference_id text,
    weight bigint,
    unit text
    );

--changeset Hosein:primery-key
ALTER TABLE "public"."organization"
    ADD PRIMARY KEY (id);

--changeset Hosein:primery-key-2
ALTER TABLE "public"."shipment"
    ADD PRIMARY KEY (reference_id);

--changeset Hosein:primery-key-3
ALTER TABLE "public"."transportpacks"
    ADD PRIMARY KEY (reference_id,weight,unit);