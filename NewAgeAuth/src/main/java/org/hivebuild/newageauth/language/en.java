package org.hivebuild.newageauth.language;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import org.hivebuild.newageauth.NewAgeAuth;

public class en extends Language {
	
	@Override
	public void createLanguageFile() {
		langFile = new File(NewAgeAuth.getInstance().getDataFolder(), "lang/en.lang");
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
			set("Login.Premium", "You have successfully logged in.");
			set("Login.Cracked", "You have been logged in. You can now only log in to this server with your current username.");
			set("Login.Key", "Please enter Key to proceed: ");
			
			set("Kick.AlreadyOnline", "A User with this username is already online.");
			set("Kick.CrackedMultiIP", "You connecting to the server with same user but mutliple IPs");
			set("Kick.CrackedMultiUsers", "You connected to the server with different minecraft accounts. Please connect with the same you have connected with before.");
			set("Kick.CrackedPremiumUser", "You connected to the server with a premium username but you are not currently logged in to that premium user. Please log in to that premium user or try a different username.");
			save();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
}
