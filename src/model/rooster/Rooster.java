package model.rooster;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import model.PrIS;
import model.klas.Klas;
import model.les.Les;
import model.persoon.Docent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

public class Rooster {	
	private ArrayList<Les> deLessen = new ArrayList<Les>();
	
	public Rooster() {
	}
	
	public void voegLesToe(Les les) {
		getLessen().add(les);
	}
	
	public static <T> T parseObjectFromString(String s, Class<T> clazz) throws Exception {
	    return clazz.getConstructor(new Class[] {String.class }).newInstance(s);
	    // naar PrIs verhuizen?
	}
		
	public ArrayList<Les> getLessen() {
			return deLessen;
	}
	
	public ArrayList<Les> getLessenVanKlas(Klas klas) {
		ArrayList<Les> lessenVanKlas = new ArrayList<Les>();
		
			for (Les les : deLessen) {
				if (les.getInfo().get(6).equals(klas)) {
					lessenVanKlas.add(les);
				}
		}
		return lessenVanKlas;
		
	}
	
	public ArrayList<Les> getLessenVanDocent(Docent docent) {
		ArrayList<Les> lessenVanDocent = new ArrayList<Les>();
		
		for (Les les : deLessen) {
			if (les.getInfo().get(5).equals(docent)) {
				lessenVanDocent.add(les);
			}
	}
		
	return lessenVanDocent;
	}
}
