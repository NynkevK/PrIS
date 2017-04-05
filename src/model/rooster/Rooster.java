package model.rooster;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import model.klas.Klas;
import model.les.Les;
import model.persoon.Docent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;


public class Rooster {	
	
	public Rooster() {
	}
	
	public void voegLesToe(Les les) {
		// voeg regel toe aan csv bestand rooster.csv
	}
	
	public static <T> T parseObjectFromString(String s, Class<T> clazz) throws Exception {
	    return clazz.getConstructor(new Class[] {String.class }).newInstance(s);
	    // naar PrIs verhuizen?
	}
	
	public Les getLes(LocalDate datum, LocalTime startTijd, String locatie){
		Les resultaat = null;
		
		return resultaat;
	}
	
	public ArrayList<Les> getLessen() {
		  String csvBestand = "rooster.csv";
		  BufferedReader br = null;
		  String line = "";
		  ArrayList<Les> lessen = new ArrayList<Les>();

		  try {

		   br = new BufferedReader(new FileReader(csvBestand));
		   while ((line = br.readLine()) != null) {

		    Les lesObject = parseObjectFromString(line, Les.class);

		    lessen.add(lesObject);
		    } 

		   } catch (IOException e) {
			e.printStackTrace();
		   } catch (Exception e) {
			e.printStackTrace();
		   }
		  return lessen;
		  }
		  
		public ArrayList<Les> getLessenVanKlas(Klas klas) {
			  String csvBestand = "rooster.csv";
			  BufferedReader br = null;
			  String line = "";
			  ArrayList<Les> lessenVanKlas = new ArrayList<Les>();
			  String klasCode = klas.getKlasCode();
			  
			  try {

			   br = new BufferedReader(new FileReader(csvBestand));
			   while ((line = br.readLine()) != null) {
				   String[] les = line.split(",");
				   
				   if (les[-1].equals(klasCode)) {
					   Les lesObject = parseObjectFromString(line, Les.class);
					   lessenVanKlas.add(lesObject);
				   }
			    } 

			   } catch (IOException e) {
				e.printStackTrace();
			   } catch (Exception e) {
				e.printStackTrace();
			   }
			  return lessenVanKlas;
			  }
		
		public ArrayList<Les> getLessenVanDocent(Docent docent) {
				// 
			  String csvBestand = "rooster.csv";
			  BufferedReader br = null;
			  String line = "";
			  ArrayList<Les> lessenVanDocent = new ArrayList<Les>();
			  String docentNummer = docent.getGebruikersnaam();
			  
			  try {
			   br = new BufferedReader(new FileReader(csvBestand));
			   while ((line = br.readLine()) != null) {
				   String[] les = line.split(",");
				   
				   if (les[4].equals(docentNummer)) {
					   Les lesObject = parseObjectFromString(line, Les.class);
					   lessenVanDocent.add(lesObject);
				   }
			    } 

			   } catch (IOException e) {
				e.printStackTrace();
			   } catch (Exception e) {
				e.printStackTrace();
			   }
			  return lessenVanDocent;
			  }
		
		}

