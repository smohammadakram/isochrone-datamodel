DROP TABLE IF EXISTS time_expanded.tmp_calendar CASCADE;
DROP TABLE IF EXISTS time_expanded.tmp_routes CASCADE;
DROP TABLE IF EXISTS time_expanded.tmp_stop_times CASCADE;
DROP TABLE IF EXISTS time_expanded.tmp_stops CASCADE;
DROP TABLE IF EXISTS time_expanded.tmp_trips CASCADE;

CREATE TABLE time_expanded.tmp_calendar (
	service_id           varchar(32) NOT NULL,
	start_date           text,
	end_date             text,
	monday               bool,
	tuesday              bool,
	wednesday            bool,
	thursday             bool,
	friday               bool,
	saturday             bool,
	sunday               bool  
 );

CREATE TABLE time_expanded.tmp_routes ( 
	route_id             varchar(32) NOT NULL,
	long_name            varchar(64),
	short_name           varchar(16)  
 );

CREATE TABLE time_expanded.tmp_stop_times ( 
	trip_id              varchar(32) NOT NULL,
	stop_id              varchar(32) NOT NULL,
	arrival_time         text,
	departure_time       text,
	stop_sequence        integer  
 );

CREATE TABLE time_expanded.tmp_stops ( 
	stop_id              varchar(32) NOT NULL,
	stop_name            varchar(128),
	stop_lat             numeric(9,6),
	stop_long            numeric(9,6),
	CONSTRAINT pk_stops PRIMARY KEY ( stop_id )
 );

CREATE TABLE time_expanded.tmp_trips ( 
	route_id             varchar(32) NOT NULL,
	t_id                 varchar(32) NOT NULL,
	service_id           varchar(32) NOT NULL,
	CONSTRAINT idx_trips UNIQUE ( route_id, t_id ) 
 );
