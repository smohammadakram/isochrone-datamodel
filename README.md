# Isochrone-datamodel

This allows the creation of a database model that contains a street network
from OpenStreetMap (OSM) and schedules of public transportation systems so
that the datbase can be used for isochrone calculation.
The schedules can either use the VDV-452 format or GTFS.   

## 1. Required Packages

- Java > 1.8
- PostgreSQL > 9.3
- PostGIS > 2.1 (follow this link: http://postgis.net/install/)
- Osmosis 
- Osmconvert

## 2. Executable script (builder.sh)

Run "./builder.sh [OPTIONS...]".

### 2.1 Run with a specific database user

By default the script will be run with the database user "postgres". To call
the script with another database user (e.g. when creating database tables) use
a bash variable:
  "DB_USERNAME=niko ./builder.sh [OPTIONS...]"

### 2.2 Possible options

To list all the option available, run ":/builder.sh -h".

#### 2.2.1 Create the database

Run "./builder.sh -d".
This will create a new PostgreSQL user called "spatial", with password
"spatial", and a database (again) called spatial. After that two
schemas are created inside "spatial" database:
 - time_expanded
 - vdv_gtfs_tmp (auxiliary for bus data).

#### 2.2.2 Create the Street Network

Run "./builder -s".

#### 2.2.3 Create the Bus Network

Run "./builder -b".

#### 2.2.4 Create the Link Network

Run "./builder -s -b -l".
Note that running the program with option "-l" is not enough, since the street
and bus network are needed to build the links
