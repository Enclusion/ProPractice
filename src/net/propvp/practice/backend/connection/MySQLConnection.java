package net.propvp.practice.backend.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.bukkit.scheduler.BukkitRunnable;

import net.propvp.practice.DatabaseCredentials;
import net.propvp.practice.Practice;

public class MySQLConnection {

	private DatabaseCredentials dbCredentials;
	private AtomicInteger openTransactions;

	public MySQLConnection(DatabaseCredentials dbCredentials) {
		this.openTransactions = new AtomicInteger(0);
		this.dbCredentials = dbCredentials;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("No MySQL driver found.", e);
		}
	}

	public Connection openConnection() {
		try {
			return DriverManager.getConnection(this.dbCredentials.getJDBCURL(), this.dbCredentials.getUsername(), this.dbCredentials.getPassword());
		} catch (SQLException e) {
			throw new RuntimeException("Database unavailable.", e);
		}
	}

	public boolean testConnection() {
		try (Connection ignored = this.openConnection()) {
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void executeAsyncOperation(Consumer<Connection> callback) {
		new BukkitRunnable() {
			Connection conn;
			Throwable t2;
			
			public void run() {
				waitForSlot();
				openTransactions.incrementAndGet();

				try {
					conn = openConnection();
					try {
						callback.accept(conn);
					}
					catch (Throwable t) {
						throw t;
					}
					finally {
						if (conn != null) {
							if (t2 != null) {
								try {
									conn.close();
								}
								catch (Throwable t3) {
									t2.addSuppressed(t3);
								}
							}
							else {
								conn.close();
							}
						}
					}
				}
				catch (Exception e) {
					throw new RuntimeException("This shouldn't happen (database error)", e);
				}
				finally {
					openTransactions.decrementAndGet();
				}
			}
		}.runTaskLaterAsynchronously(Practice.getInstance(), 20L);
	}

	public DatabaseCredentials getCredentials() {
		return this.dbCredentials;
	}

	private void waitForSlot() {
		while (this.openTransactions.get() >= 5) {
			try {
				Thread.sleep(50L);
			}
			catch (InterruptedException ex) {}
		}
	}

	public void waitUntilFlushed() {
		while (this.openTransactions.get() > 0) {
			try {
				Thread.sleep(50L);
			}
			catch (InterruptedException ex) {}
		}
	}
}