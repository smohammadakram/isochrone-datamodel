DROP TABLE IF EXISTS time_expanded.<city>_links CASCADE;

CREATE TABLE IF NOT EXISTS time_expanded.<city>_links (
	link_id serial, 
	link_source bigint NOT NULL,
	link_source_mode integer NOT NULL,
	link_destination bigint NOT NULL,
	link_destination_mode integer NOT NULL,
	PRIMARY KEY(link_id)
);