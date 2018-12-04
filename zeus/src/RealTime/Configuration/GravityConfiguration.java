package RealTime.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import lombok.Getter;

@Getter
public class GravityConfiguration {
	// Path where all the Storgae of Gravity is going to Happen.
	// For Local Use System.getProperty("user.dir");
	// private final String gravityStoragaePath = System.getProperty("catalina.home") + File.separator + "Gravity";
	private String gravityStoragaePath;
	// GIT Fields.
	private String gitConnectionURL;

//	GravityConfiguration() {
//		// Configurable Properties Object.
//		final Properties properties = new Properties();
//
//		final File propertiesFile = new File("gravity.properties");
//		try (final FileInputStream fis = new FileInputStream(propertiesFile)) {
//			properties.load(fis);
//			this.gitConnectionURL = properties.getProperty("git.ip");
//			this.gravityStoragaePath = properties.getProperty("gravity.storage.path");
//			//			FileOutputStream ops = new FileOutputStream(propertiesFile);
//			//			properties.store(ops, "Gravity Config");
//			fis.close();
//			//	ops.close();
//		} catch (IOException e) {
//			// log.warn("File not found -> {}", file.getAbsolutePath());
//		}
//	}
	public static void main(String args[]){

		// Configurable Properties Object.
		final Properties properties = new Properties();

		final File propertiesFile = new File("gravity.properties");
		try (final FileInputStream fis = new FileInputStream(propertiesFile)) {
			properties.load(fis);
			System.out.println( properties.getProperty("git.ip"));
			System.out.println(properties.getProperty("gravity.storage.path"));
			//			FileOutputStream ops = new FileOutputStream(propertiesFile);
			//			properties.store(ops, "Gravity Config");
			fis.close();
			//	ops.close();
		} catch (IOException e) {
			System.out.println("error");
			// log.warn("File not found -> {}", file.getAbsolutePath());
		}

	}

}
