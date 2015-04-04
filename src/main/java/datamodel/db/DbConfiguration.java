package datamodel.db;

import com.tngtech.configbuilder.ConfigBuilder;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertiesFiles;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertyExtension;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertyLocations;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertySuffixes;
import com.tngtech.configbuilder.annotation.valueextractor.DefaultValue;
import com.tngtech.configbuilder.annotation.valueextractor.PropertyValue;

import javax.validation.constraints.NotNull;

@PropertiesFiles({ "config", "config-test" })
@PropertyLocations(fromClassLoader = true)
@PropertyExtension("xml")
@PropertySuffixes(extraSuffixes = { }, hostNames = false)
public final class DbConfiguration {
	@NotNull
	@PropertyValue("db.database")
	private String database;

	@NotNull
	@DefaultValue("localhost")
	@PropertyValue("db.servername")
	private String host;

	@NotNull
	@PropertyValue("db.password")
	private String password;

	@NotNull
	@DefaultValue("5432")
	@PropertyValue("db.port")
	private int port;

	@NotNull
	@PropertyValue("db.username")
	private String user;

	private static DbConfiguration instance = null;

	public static synchronized DbConfiguration getInstance() {
		if (instance == null) {
			instance = new ConfigBuilder<>(DbConfiguration.class).build();
		}

		return instance;
	}

	// Constructor

	private DbConfiguration() { };

	// Getters

	public String getConnectionString() {
		return "jdbc:postgresql://" + host + ":" + port + "/" + database;
	}

	public String getDbCatalog() {
		return database;
	}

	public String getDbPassword() {
		return password;
	}

	public String getDbUser() {
		return user;
	}
}
