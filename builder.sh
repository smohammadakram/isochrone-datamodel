#!/bin/bash

#-----------
# VARIABLES
#-----------

BASE_DIR="$(cd `dirname "${BASH_SOURCE[0]}"` && pwd)"
BUILD_DIR="$BASE_DIR/build"
BUILDER_DIR="$BUILD_DIR/builder"
BUS_DIR="$BUILDER_DIR/bus-network"
BUS_GTFS="$BUS_DIR/gtfs"
BUS_SQL="$BUS_DIR/sql"
BUS_VDV="$BUS_DIR/vdv"
CITY_NR=-1
CODE_JAR="$BUILD_DIR/libs/isochrone-datamodel-static.jar"
CODE_SQL="$BASE_DIR/src/main/resources/datamodel/db"
FORCE_GTFS_FILE=false
LINK_DIR="$BUILDER_DIR/link-network"
LINK_SQL="$LINK_DIR/sql"
LOG_DIR="$BUILDER_DIR/log"
OPTION_BUS=false
OPTION_DATABASE=false
OPTION_LINK=false
OPTION_STREET=false
OPTION_UPDATE=false
STREET_DIR="$BUILDER_DIR/street-network"
STREET_OSM="$STREET_DIR/osm"
STREET_PBF="$STREET_DIR/pbf"
STREET_SQL="$STREET_DIR/sql"
URL_GEOFABRIK="http://download.geofabrik.de"
URL_GTFS_DATAEXCHANGE="http://www.gtfs-data-exchange.com"

#-----------
# FUNCTIONS
#-----------
function fn_build_jar {
	if [ ! -f "$CODE_JAR" ]; then
		# This builds the CODE_JAR
		printf "%s" "[INFO] Compiling jar file"
		"$BASE_DIR/gradlew" -b "$BASE_DIR/build.gradle" jarAll
		fn_check_status
	fi
}

function fn_bus {
	if [ "$OPTION_BUS" == false ]; then
		return
	fi

	printf "%s\n" "[INFO] Processing bus network."
	fn_create_bus_network "$1" "$2"
}

function fn_check_status {
	local status="$?"

	if [ $status -ne 0 ]; then
		printf "%s\n" "failed"
		printf "%s\n" "[ERROR] An error occurred. The process will exit now with status $status" 
		exit $status;
	fi

	printf "%s\n" " done"
}

function fn_convert_vdv2gtfs {
	local city="${1,,}"
	local convert_extension="$2"
	local download_url="$3"
	local input_file="$BUS_VDV/$city.zip"
	local output_file="$BUS_GTFS/$city.zip"

	if $OPTION_UPDATE; then
		printf "%s" "[INFO] Deleting VDV file..."
		rm -rf "$input_file"
		printf "%s\n" " done"
	fi 

	printf "%s\n" "[INFO] Creating GTFS archive from VDV archive..."
	if [ ! -f "$input_file" ]; then
		printf "%s" "[INFO] Downloading VDV archive from $download_url..."
		wget -O "$input_file" "$download_url"
		fn_check_status
	fi

	if $convert_extension; then
		printf "%s" "[INFO] Extracting files from VDV archive..."
		unzip -o -q -d "$BUS_VDV/$city" "$input_file"
		fn_check_status
		
		printf "%s" "[INFO] Adjusting VDV files names (extension to lowercase)..."
		find "$BUS_VDV" -name "*.X10" | sed 's/\(.*\)\.X10/mv "\1.X10" "\1.x10"/' | sh
		fn_check_status
	
		printf "%s" "[INFO] Zipping files into VDV archive..."
		zip -j -r "$input_file" "$BUS_VDV/$city" > /dev/null 2>&1
		fn_check_status
	
		printf "%s" "[INFO] Removing converted files (only keeping archive)..."
		rm -rf "$BUS_VDV/$city"
		printf "%s\n" " done"
	fi

	printf "%s" "[INFO] Removing outdated GTFS conversion files..."
	rm -rf "$output_file"
	printf "%s\n" " done"

	printf "%s" "[INFO] Converting VDV to GTFS..."
	java -jar "$CODE_JAR" "vdv2gtfs" "$input_file" "$output_file" >> "$LOG_DIR/isochrone-datamodel.log" 2>&1
	fn_check_status

	FORCE_GTFS_FILE="$output_file"
}

function fn_create_bus_network {
	local city="${1,,}"
	local download_url="$2"
	local file="$BUS_GTFS/$city.zip"

	if [ "$FORCE_GTFS_FILE" == false ] && $OPTION_UPDATE; then
		printf "%s" "[INFO] Deleting GTFS files..."
		rm -rf "$file"
		printf "%s\n" " done"
	fi

	if [ "$FORCE_GTFS_FILE" == false ] && [ ! -f "$file" ]; then
		printf "%s" "[INFO] Downloading GTFS archive from $download_url..."
		wget -O "$file" "$download_url"
		fn_check_status
	fi

	if [ ! "$FORCE_GTFS_FILE" == false ]; then
		file="$FORCE_GTFS_FILE"
	fi 

	printf "%s" "[INFO] Generating bus network scripts for \"$1\"..."
	java -jar "$CODE_JAR" "busscript" "$BUS_SQL" "$city" >> "$LOG_DIR/isochrone-datamodel.log" 2>&1
	fn_check_status
	
	printf "%s" "[INFO] Creating table for bus network..."
	PGPASSWORD="spatial" psql -h localhost -p 5432 -U spatial -d spatial -f "$BUS_SQL/${city}_bus_network.sql" --quiet
	fn_check_status
	
	printf "%s" "[INFO] Creating temporary database..."
	PGPASSWORD="spatial" psql -h localhost -p 5432 -U spatial -d spatial -f "$CODE_SQL/tmp-create.sql" --quiet
	fn_check_status
	
	printf "%s" "[INFO] Populating temporary database..."
	java -jar "$CODE_JAR" "busnet" "$file" "$city" >> "$LOG_DIR/isochrone-datamodel.log" 2>&1
	fn_check_status
	
	printf "%s" "[INFO] Creating views for temporary database..."
	PGPASSWORD="spatial" psql -h localhost -p 5432 -U spatial -d spatial -f "$CODE_SQL/tmp-views.sql" --quiet
	fn_check_status
	
	printf "%s" "[INFO] Inserting bus nodes and edges..."
	PGPASSWORD="spatial" psql -h localhost -p 5432 -U spatial -d spatial -f "$BUS_SQL/${city}_bus_nodes_edges.sql" --quiet
	fn_check_status
	
	printf "%s" "[INFO] Inserting bus schedule..."
	PGPASSWORD="spatial" psql -h localhost -p 5432 -U spatial -d spatial -f "$BUS_SQL/${city}_bus_trip_schedule.sql" --quiet
	fn_check_status
	
	printf "%s" "[INFO] Removing temporary database..."
	PGPASSWORD="spatial" psql -h localhost -U spatial -d spatial -f "$CODE_SQL/tmp-drop.sql" --quiet
	fn_check_status
}

function fn_create_link_network {
	local city="${1,,}"

	printf "%s" "[INFO] Generating link network scripts for \"$1\"..."
	java -jar "$CODE_JAR" "linkscript" "$LINK_SQL" "$city" >> "$LOG_DIR/isochrone-datamodel.log" 2>&1
	fn_check_status
	
	printf "%s" "[INFO] Creating link table..."
	PGPASSWORD="spatial" psql -h localhost -p 5432 -U spatial -d spatial -f "$LINK_SQL/${city}_link_network.sql" --quiet
	fn_check_status
	
	printf "%s" "[INFO] Populating link table..."
	java -jar "$CODE_JAR" "linknet" "$city" >> "$LOG_DIR/isochrone-datamodel.log" 2>&1
	fn_check_status
}

function fn_create_street_network {
	local $city="${1,,}"

	printf "%s" "[INFO] Generating street network scripts for \"$1\"..."
	java -jar "$CODE_JAR" "streetscript" "$STREET_SQL" "${city}" >> "$LOG_DIR/isochrone-datamodel.log" 2>&1
	fn_check_status
	
	printf "%s" "[INFO] Creating database schemas..."
	PGPASSWORD="spatial" psql -h localhost -p 5432 -U spatial -d spatial -f "$STREET_SQL/${city}_street_network.sql" --quiet
	fn_check_status
	
	printf "%s" "[INFO] Parsing .pbf source file..."
	java -Xms512m -Xmx2048m -jar "$CODE_JAR" "streetnet" "$STREET_SQL" "${city}" "$STREET_PBF/${city}.osm.pbf" >> "$LOG_DIR/isochrone-datamodel.log" 2>&1
	fn_check_status
	
	printf "%s" "[INFO] Inserting street nodes into database..."
	PGPASSWORD="spatial" psql -h localhost -p 5432 -U spatial -d spatial -f "$STREET_SQL/${city}_street_nodes_import.sql" --quiet
	fn_check_status
	
	printf "%s" "[INFO] Inserting street edges into database..."
	PGPASSWORD="spatial" psql -h localhost -p 5432 -U spatial -d spatial -f "$STREET_SQL/${city}_street_edges_import.sql" --quiet
	fn_check_status

# TODO: Why is there no update_edges_length created?
#	printf "%s" "[INFO] Updating edges lengths..."
#	PGPASSWORD="spatial" psql -h localhost -p 5432 -U spatial -d spatial -f "$STREET_SQL/${city}_update_edges_lengths.sql" --quiet
#	fn_check_status
}

function fn_database {
	if [ "$OPTION_DATABASE" == false ]; then
		return
	fi

	local db_user="$1"
	local db_password="$2"
	if [ -z "$db_user" ]; then
		db_user="postgres"
	fi
	
	stty -echo
	read -p "[REQUEST] Enter password for database user $db_user:" db_password; echo
	stty echo
	
	#----------------
	# Database Setup
	#----------------
	printf "%s" "[INFO] Creating database \"spatial\"..."
	PGPASSWORD="$db_password" psql -h localhost -p 5432 -U $db_user -d postgres -f "$CODE_SQL/db-create.sql" --quiet
	fn_check_status

	printf "%s" "[INFO] Removing default schema \"public\"..."
	PGPASSWORD="$db_password" psql -h localhost -p 5432 -U $db_user -d spatial -f "$CODE_SQL/schema-drop.sql" --quiet
	fn_check_status

	printf "%s" "[INFO] Creating schemas..."
	PGPASSWORD="spatial" psql -h localhost -p 5432 -U spatial -d spatial -f "$CODE_SQL/schema-create.sql" --quiet
	fn_check_status

	printf "%s" "[INFO] Creating extension \"PostGIS\"..."
	PGPASSWORD="$db_password" psql -h localhost -p 5432 -U $db_user -d spatial -f "$CODE_SQL/extension-create.sql" --quiet
	fn_check_status

	printf "\n"
}

function fn_help {
	printf "%s\t%s\n" "-b" "Build bus network for chosen city (if available)."
	printf "%s\t%s\n" "-d" "Setup the database schema."
	printf "%s\t%s\n" "-h" "List the options available."
	printf "%s\t%s\n" "-l" "Build link network for chosen city (if both street and bus have been created)."
	printf "%s\t%s\n" "-s" "Build street network for chosen city."
}

function fn_link {
	if [ "$OPTION_BUS" == false ] || [ "$OPTION_LINK" == false ] || [ "$OPTION_STREET" == false ]; then
		return
	fi

	printf "%s\n" "[INFO] Processing link network."
	fn_create_link_network "$1"
}

function fn_main {
	if [ "$OPTION_BUS" == false ] && [ "$OPTION_LINK" == false ] && [ "$OPTION_STREET" == false ]; then
		return
	fi

	printf "%s\n" "[REQUEST] What city/region do you want to import into your PostgreSQL database?"
	printf "\t%s\t\t%s\t\t%s\n" "[1]  Barcelona" "[11] Japan" "[21] Cape Town"
	printf "\t%s\t\t%s\n" "[2]  OberBayern" "[12] Madrid"
	printf "\t%s\t\t%s\n" "[3]  Munich" "[13] Merano-Bolzano"
	printf "\t%s\t\t%s\n" "[4]  Bilbao" "[14] Milano"
	printf "\t%s\t\t%s\n" "[5]  Bologna" "[15] Paris "
	printf "\t%s\t\t%s\n" "[6]  Bolzano" "[16] SanFrancisco"
	printf "\t%s\t\t%s\n" "[7]  California" "[17] Spain"
	printf "\t%s\t%s\n" "[8]  Greater-London" "[18] Tokyo"
	printf "\t%s\t%s\n" "[9]  Ile-De-France" "[19] Sydney"
	printf "\t%s\t\t%s\n" "[10] Italy" "[20] Perth"
	printf "%s" "[INFO] Your choice: "
	read CITY_NR
	printf "\n"

	fn_build_jar
	fn_setup_build_dir

	case $CITY_NR in 
		1)  fn_street "Barcelona" "Spain" "$URL_GEOFABRIK/europe/spain-latest.osm.pbf" 41.48698 1.91711 41.25923 2.28928 ;;
		2)  fn_street "OberBayern" "OberBayern" "$URL_GEOFABRIK/europe/oberbayern-latest.osm.pbf" ;;
		3)  fn_street "Munich" "OberBayern" "$URL_GEOFABRIK/europe/oberbayern-latest.osm.pbf" 48.23657 11.33926 48.01841 11.79520 ;;
		4)  fn_street "Bilbao" "Spain" "$URL_GEOFABRIK/europe/spain-latest.osm.pbf" 43.38010 -3.12836 43.22769 -2.82692 ;;
		5)  fn_street "Bologna" "Italy" "$URL_GEOFABRIK/europe/italy-latest.osm.pbf" 44.55574 11.26373 44.47250 11.40038 ;;
		6)  fn_street "Bolzano" "Italy" "$URL_GEOFABRIK/europe/italy-latest.osm.pbf" 46.5177683 11.2905121 46.3507194 11.3928223 ;;
		7)  fn_street "California" "California" "$URL_GEOFABRIK/north-america/us/california-latest.osm.pbf" ;;
		8)  fn_street "GreaterLondon" "GreaterLondon" "$URL_GEOFABRIK/europe/great-britain/england/greater-london-latest.osm.pbf" ;;
		9)  fn_street "IleDeFrance" "IleDeFrance" "$URL_GEOFABRIK/europe/france/ile-de-france-latest.osm.pbf" ;;
		10) fn_street "Italy" "Italy" "$URL_GEOFABRIK/europe/italy-latest.osm.pbf" ;;
		11) fn_street "Japan" "Japan" "$URL_GEOFABRIK/asia/japan-latest.osm.pbf" ;;
		12) fn_street "Madrid" "Spain" "$URL_GEOFABRIK/europe/spain-latest.osm.pbf" 40.50753 -3.82599 40.34550 -3.55644 ;;
		13) fn_street "MeBo" "Italy" "$URL_GEOFABRIK/europe/italy-latest.osm.pbf" 46.7351547 11.0591125 46.3080471 11.4710999
			fn_convert_vdv2gtfs "MeBo" true "http://open.sasabz.it/files/vdv.zip"
			fn_bus "MeBo"
			fn_link "MeBo"
			;;
		14) fn_street "Milano" "Italy" "$URL_GEOFABRIK/europe/italy-latest.osm.pbf" 45.56214 9.02596 45.36228 9.35005 ;;
		15) fn_street "Paris" "IleDeFrance" "$URL_GEOFABRIK/europe/france/ile-de-france-latest.osm.pbf" 49.0297648 2.0791626 48.7308322 2.6202393 ;;
		16) fn_street "SanFrancisco" "SanFrancisco" "$URL_GEOFABRIK/north-america/us/california-latest.osm.pbf" 38.00698 -121.75323 37.20408 -122.66510
			fn_bus "SanFrancisco" "$URL_GTFS_DATAEXCHANGE/agency/san-francisco-municipal-transportation-agency/latest.zip"
			fn_link "SanFrancisco"
			;;
		17) fn_street "Spain" "Spain" "$URL_GEOFABRIK/europe/spain-latest.osm.pbf" ;;
		18) fn_street "Tokyo" "Japan" "$URL_GEOFABRIK/asia/japan-latest.osm.pbf" 37.08586 138.674930 35.14237 141.03149 ;;
		19) fn_street "Sydney" "Australia-Oceania" "$URL_GEOFABRIK/australia-oceania-latest.osm.pbf" -33.6546377 150.6280518 -34.1129416 151.3627625 ;;
		20) fn_street "Perth" "Australia-Oceania" "$URL_GEOFABRIK/australia-oceania-latest.osm.pbf" -31.5960829 150.6280518 -32.1918844 116.124115 ;;
		21) fn_street "CapeTown" "Africa" "$URL_GEOFABRIK/africa-latest.osm.pbf" -32.1918844 18.2400513 -33.767732 18.7646484 ;;
	esac
}

function fn_mkdir {
	if [ ! -e "${1}" ]; then
		mkdir -p "$1"
		chmod 775 "$1"
	fi
}

function fn_setup_build_dir {
	directories=("$STREET_OSM" "$STREET_SQL" "$STREET_PBF" "$BUS_VDV" "$BUS_GTFS" "$BUS_SQL" "$LINK_SQL" "$LOG_DIR")
	for v in ${directories[@]} ; do
		fn_mkdir "${v}"
	done
}

function fn_split_network {
	local city="${1,,}"
	local region_pbf="$2"
	local top="$3"
	local left="$4"
	local bottom="$5"
	local right="$6"

	local city_file="$STREET_OSM/$city.osm"
	local city_pbf="$STREET_PBF/$city.osm.pbf"

	printf "%s" "[INFO] Extracting data for $1..."
	osmosis --read-pbf "$region_pbf" --bounding-box top=$top left=$left bottom=$bottom right=$right completeWays=yes --write-xml "$city_file" > "$LOG_DIR/osmosis.log" 2>&1
	printf "%s\n" " done"
	chmod 666 "$city_file"

	printf "%s" "[INFO] Converting OSM to PBF..."
	osmconvert "$city_file" --out-pbf -o="$city_pbf" > "$LOG_DIR/osmconvert.log" 2>&1
	printf "%s\n" " done"

	printf "%s" "[INFO] Removing OSM file..."
	rm -rf "$city_file"
	printf "%s\n" " done"
}

function fn_street {
	if [ "$OPTION_STREET" == false ]; then
		return
	fi

	local city="${1,,}"
	local region="${2,,}"
	local download_url="$3"
	local coord_top="$4"
	local coord_left="$5"
	local coord_bottom="$6"
	local coord_right="$7"

	local city_file="$STREET_PBF/$city.osm.pbf"
	local region_file="$STREET_PBF/$region.osm.pbf"
	local coords_set=false
	if [ -n "$4" ]; then
		coords_set=true
	fi

	printf "%s\n" "[INFO] Processing street network."
	if $OPTION_UPDATE; then
		printf "%s" "[INFO] Deleting outdated files..."
		rm -rf "$city_file";
		rm -rf "$region_file";
		printf "%s\n" " done"
	fi

	if [ ! -f "$city_file" ]; then
		printf "%s\n" "[INFO] Source file for $1 not found."
		if [ ! -f "$region_file" ]; then
			printf "%s" "[INFO] Downloading data for region $2..."
			wget -O "$region_file" "$download_url"
			fn_check_status
		fi

		# This will create the file $city.osm.pbf from $region.osm.pbf
		printf "%s\n" "[INFO] Source file for $1 is now extracted from region $2"
		if $coord_set ; then
			fn_split_network "$1" "$region_file" $coord_top $coord_left $coord_bottom $coord_right
		fi
	fi

	fn_create_street_network "$1"
}

#--------------------
# PARAMETER CHECKING
#--------------------
if [ $# -lt 1 ]; then
	printf "%s\n" "[ERROR] Usage: \"bash net-builder.sh OPTION(S)\", where OPTION(S) are:"
	fn_help
	exit 0;
fi

printf "%s\n" "Welcome to isochrone-datamodel import tool"
#---------------------------------
# FILL VARIABLES FROM CMD-OPTIONS
#---------------------------------
#set -- $(getopt uslb: "$@")
for cmd in "$@" ; do
	case "$cmd" in 
		# create bus network 
		(-b | b ) 
		printf "%s\n" "[DEBUG] Bus network will be built."
		OPTION_BUS=true 
		shift
		;;

		# create database
		(-d | d)
		printf "%s\n" "[DEBUG] Database schema will be created."
		OPTION_DATABASE=true
		shift
		;;

		# display options available
		(-h | h)
		fn_help
		exit 0
		;;

		# create link network
		(-l | l) 
		printf "%s\n" "[DEBUG] Link network will be built."
		OPTION_LINK=true 
		shift
		;;

		# create street network
		(-s | s) 
		printf "%s\n" "[DEBUG] Street network will be built."
		OPTION_STREET=true 
		shift
		;;

		# download data from web
		(-u | u) 
		printf "%s\n" "[DEBUG] Data will be updated."		
		OPTION_UPDATE=true 
		shift
		;;

		# go on if an invalid options is used
		(*)
		break
		;;

		# catch option errors
		(-*)
		printf "%s\n" "[ERROR] Error: command not recognized"
		exit 1
		;;
	esac
done

printf "\n"
fn_database "$DB_USERNAME"
fn_main
printf "%s\n" "Successfully finished"
exit 0
