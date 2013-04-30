package org.hivebuild.newageauth.language;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.hivebuild.newageauth.NewAgeAuth;


public class de extends Language {

	private static YamlConfiguration lang;
	private static File langFile;
	
	@Override
	public void createLanguageFile() {
		langFile = new File(NewAgeAuth.getInstance().getDataFolder(), "lang/de.lang");
		if (!langFile.exists()) {
			try {
				langFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void updateLanguage() {
		lang = new YamlConfiguration();
		try {
			lang.load(langFile);
			set("Login.Premium", "Du hast dich erfolgreich eingeloggt.");
			set("Login.Cracked", "Du hast dich erfolgreich eingeloggt. Du darfst dich absofort auf diesem Server nur noch mit diesem Benutzernamen einloggen.");
			set("Login.Key", "Bitte gib deinen Schlüssel ein um fortzufahren: ");
			
			set("Kick.AlreadyOnline", "Ein Spieler mit dem selben Benutzernamen ist schon online.");
			set("Kick.CrackedMultiIP", "Du versuchst dich auf diesem Server mit unterschiedlichen IPs einzuloggen.");
			set("Kick.CrackedMultiUsers", "Du versuchst dich auf diesem Server mit einem anderen Benutzernamen einzuloggen. Bitte verbinde dich mit dem selben Namen wie vorher.");
			set("Kick.CrackedPremiumUser", "Du hast dich mit einem Benutzernamen verbunden, welcher mit einem Premium Account verknüpft ist. Bitte wähle einen anderen Namen oder logge dich mit dem offiziellen Launcher ein.");
			lang.save(langFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String translate(String key) {
		return lang.getString(key);
	}

	@Override
	public File getFile() {
		return langFile;
	}

	@Override
	public YamlConfiguration getKeys() {
		return lang;
	}
	
	@Override
	public String getName() {
		return "de";
	}

	@Override
	public void set(String key, String value) {
		if (lang.get(key) == null) lang.set(key, value);
	}
	
}
