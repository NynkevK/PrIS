package model.rooster;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import model.klas.Klas;
import model.les.Les;
import model.persoon.Docent;

public class Rooster {	
	private ArrayList<Les> deLessen = new ArrayList<Les>();
	
	public Rooster() {
	}
	
	public void voegLesToe(Les les) {
		if(!(deLessen.contains(les))){
			getLessen().add(les);
		}
	}
	
	public Les getLes(LocalDate datum, LocalTime startTijd, String locatie){
		for(Les les : deLessen){
			ArrayList lesinfo = les.getInfo();
			if(lesinfo.get(0).equals(datum) &&
					lesinfo.get(1).equals(startTijd) &&
					lesinfo.get(5).equals(locatie))
				return les;
		}
		
		return null;
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
