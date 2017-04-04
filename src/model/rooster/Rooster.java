package model.rooster;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import model.klas.Klas;
import model.les.Les;
import model.persoon.Docent;

public class Rooster {	
	private Les deLessen;
	
	public Rooster() {
		
	}
	
	public void voegLesToe(Les les) {
		
	}
	
	public Les getLes(LocalDate datum, LocalTime startTijd, String locatie){
		Les resultaat = null;
		
		return resultaat;
	}
	
	public ArrayList<Les> getLessen() {
		
	}
	
	public ArrayList<Les> getLessenVanKlas(Klas klas) {
		
	}
	
	public ArrayList<Les> getLessenVanDocent(Docent docent) {
		
	}
	
}
