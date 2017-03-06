package net.propvp.practice;

public class DatabaseCredentials {
	
	private final String hostname;
	private final int port;
	private final String username;
	private final String password;
	private final String databaseName;

	public DatabaseCredentials(final String hostname, final int port, final String username, final String password, final String databaseName) {
		this.hostname = hostname;
		this.port = port;
		this.username = username;
		this.password = password;
		this.databaseName = databaseName;
	}

	public String getHostname() {
		return this.hostname;
	}

	public int getPort() {
		return this.port;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public String getDatabaseName() {
		return this.databaseName;
	}

	public String getJDBCURL() {
		return String.format("jdbc:mysql://%s:%d/%s", this.hostname, this.port, this.databaseName);
	}

}