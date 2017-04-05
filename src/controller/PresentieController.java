package controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import model.PrIS;
import model.afmelding.Afmelding;
import model.klas.Klas;
import model.les.Les;
import model.persoon.Docent;
import model.persoon.Persoon;
import model.persoon.Student;
import model.rooster.Rooster;
import server.Conversation;
import server.Handler;

public class PresentieController implements Handler{
	private PrIS informatieSysteem;
	
	public PresentieController(PrIS infoSys) {
		informatieSysteem = infoSys;
	}

	@Override
	public void handle(Conversation conversation){
		if(conversation.getRequestedURI().startsWith("/les/presentie/ophalen")){
			ophalenLes(conversation);
		} else if (conversation.getRequestedURI().startsWith("/les/presentie/opslaan")){
			opslaanLes(conversation);
		} else if (conversation.getRequestedURI().startsWith("/persoon/presentie/opslaan"));
	}

	private void opslaanLes(Conversation conversation) {
		JsonObject lJsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
		Rooster hetRooster = informatieSysteem.getRooster();
		
		// info verzamelen om de les mee op te vragen
		
		// calendar object converten naar LocalDate en LocalTime
		@SuppressWarnings("static-access")
		Calendar lDatum_afmeldingInCal = PrIS.standaardDatumStringToCal(lJsonObjectIn.getString("date")).getInstance();
		LocalDateTime dateTime = LocalDateTime.ofInstant(lDatum_afmeldingInCal.toInstant(), ZoneId.systemDefault());
		LocalDate lDatum_afmeldingInLD = dateTime.toLocalDate();
		LocalTime lStartTijd_afmelding = dateTime.toLocalTime();
		
		// locatie van de les van de afmelding uit de request halen
		String lLocatie_afmelding = lJsonObjectIn.getString("location");
		
		// les ophalen waarvoor de presentie opgeslagen moet worden
		Les lLesVan_afmelding = hetRooster.getLes(lDatum_afmeldingInLD, lStartTijd_afmelding, lLocatie_afmelding);
		
		JsonArray lAfmeldingen_jArray = lJsonObjectIn.getJsonArray("non-appearance");
		if(lAfmeldingen_jArray != null){
			for(int i=0;i<lAfmeldingen_jArray.size();i++){
				JsonObject lAfmelding_jsonObj = lAfmeldingen_jArray.getJsonObject(i);
				
				// info van afmelding ophalen
				int lStudentNrVanAfmelding = lAfmelding_jsonObj.getInt("student-number");
				Student lStudentVanAfmelding = informatieSysteem.getStudent(lStudentNrVanAfmelding);
				String lRedenVanAfmelding = lAfmelding_jsonObj.getString("reason");
				
				// afmelding toevoegen aan les
				lLesVan_afmelding.voegAfmeldingToe(lStudentVanAfmelding, lRedenVanAfmelding);
			}
		}
		
		// output json object aanmaken en errorcode toevoegen
		JsonObjectBuilder lJob =	Json.createObjectBuilder(); 
	  lJob.add("errorcode", 0);
	  // nothing to return use only errorcode to signal: ready!
	  String lJsonOutStr = lJob.build().toString();
	 	conversation.sendJSONMessage(lJsonOutStr);
	}
	
	private void ophalenLes(Conversation conversation) {
		JsonObject lJsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
		Rooster hetRooster = informatieSysteem.getRooster();
		
		// info verzamelen om de les mee op te vragen
		
		// calendar object converten naar LocalDate en LocalTime
		@SuppressWarnings("static-access")
		Calendar lDatum_LesInCal = PrIS.standaardDatumStringToCal(lJsonObjectIn.getString("date")).getInstance();
		LocalDateTime dateTime = LocalDateTime.ofInstant(lDatum_LesInCal.toInstant(), ZoneId.systemDefault());
		LocalDate lDatum_LesInLD = dateTime.toLocalDate();
		LocalTime lStartTijd_Les = dateTime.toLocalTime();
		
		// locatie van de les de request halen
		String lLocatie_Les = lJsonObjectIn.getString("location");
		
		// les ophalen waarvoor de presentie opgehaald moet worden
		Les lLes = hetRooster.getLes(lDatum_LesInLD, lStartTijd_Les, lLocatie_Les);
		
		JsonArrayBuilder lJsonArrayBuilder = Json.createArrayBuilder();
		ArrayList<Afmelding> lAfmeldingenVanLes = lLes.getAfmeldingen();
		
		for(Afmelding lAfmelding : lAfmeldingenVanLes){
			Persoon tempPersoon = lAfmelding.getAfgemelde();
			
			// als de afgemelde niet een student is gaat hij naar de volgende afmelding
			if(!(tempPersoon instanceof Student)){
				continue;
			}
			
			// info van afmelding ophalen
			Student persoonVanAfmelding = (Student) tempPersoon;
			int nummerVanPersoon = persoonVanAfmelding.getStudentNummer();
			String naamVanPersson = persoonVanAfmelding.getVoornaam() + " " + persoonVanAfmelding.getVolledigeAchternaam();
			
			JsonObjectBuilder lJsonObjectBuilderVoorAfmelding = Json.createObjectBuilder();
			
			// info in json object zetten
			lJsonObjectBuilderVoorAfmelding
				.add("number", nummerVanPersoon)
				.add("name", naamVanPersson)
				.add("type", lAfmelding.getReden());
			
			// json object aan json array toevoegen
			lJsonArrayBuilder.add(lJsonObjectBuilderVoorAfmelding);
		}
		
		// en dan als alle afmeldingen gecheckt zijn antwoorden met de json array
		String lJsonOutStr = lJsonArrayBuilder.build().toString();												// maak er een string van
		conversation.sendJSONMessage(lJsonOutStr);
	}

	public void ophalen(Conversation conversation){
		JsonObject lJsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
		String lRol = lJsonObjectIn.getString("rol");
		String lGebruikersnaam = lJsonObjectIn.getString("username");
		
		Persoon dePersoon = null;
		ArrayList<Les> roosterVanPersoon = null;
		
		// kijk wat voor rol de opvrager heeft, en haal dan het juiste rooster op
		if(lRol == "student"){
			Student opvragende = informatieSysteem.getStudent(lGebruikersnaam);
			dePersoon = opvragende;
			Klas klasVanStudent = informatieSysteem.getKlasVanStudent(opvragende);
			roosterVanPersoon = informatieSysteem.getRooster().getLessenVanKlas(klasVanStudent);
		} else if(lRol == "docent"){
			Docent opvragende = informatieSysteem.getDocent(lGebruikersnaam);
			dePersoon = opvragende;
			roosterVanPersoon = informatieSysteem.getRooster().getLessenVanDocent(opvragende);
		}
		
		JsonArrayBuilder lJsonArrayBuilder = Json.createArrayBuilder();
		
		for(Les lLes : roosterVanPersoon){
			ArrayList<Afmelding> deAfmeldingen = lLes.getAfmeldingen();
			
			Afmelding afmeldingVanPersoon = null;
			
			// kijk of er een afmelding van de persoon is
			for(Afmelding lAfmelding : deAfmeldingen){
				if(lAfmelding.getAfgemelde() == dePersoon){
					afmeldingVanPersoon = lAfmelding;
				} else {
					continue;
				}
			}
			
			JsonObjectBuilder lJsonObjectBuilderVoorLes = Json.createObjectBuilder();
			
			// verzamel de info van de les
			@SuppressWarnings("rawtypes")
			ArrayList lesInfo = lLes.getInfo();
			String datumVanLesString = lesInfo.get(0).toString();
			String startTijdVanLesString = lesInfo.get(1).toString();
			String eindTijdVanLesString = lesInfo.get(2).toString();
			String vakVanLes = (String)lesInfo.get(3);
			Docent docentVanLes = (Docent)lesInfo.get(4);
			String gebruikersNaamVanDocent = docentVanLes.getGebruikersnaam();
			String locatieVanLes = (String)lesInfo.get(5);
			Klas klasVanLes = (Klas)lesInfo.get(6);
			String klasCodeVanLes = klasVanLes.getKlasCode();
			
			// voeg de info toe aan het json object
			lJsonObjectBuilderVoorLes
				.add("date", datumVanLesString)
				.add("start-time", startTijdVanLesString)
				.add("end-time", eindTijdVanLesString)
				.add("course", vakVanLes)
				.add("teacher", gebruikersNaamVanDocent)
				.add("location", locatieVanLes)
				.add("class-code", klasCodeVanLes);
			
			// voeg de afmelding toe aan het json object als die er is
			if(afmeldingVanPersoon != null){
				JsonObjectBuilder lJsonObjectBuilderVoorAfmelding = Json.createObjectBuilder();
				lJsonObjectBuilderVoorAfmelding.add("reason", afmeldingVanPersoon.getReden());
				lJsonObjectBuilderVoorLes.add("non-appearance",lJsonObjectBuilderVoorAfmelding);
			}
			
			// voeg het json object toe aan de json array
			lJsonArrayBuilder.add(lJsonObjectBuilderVoorLes);
			}
		// maak er een string van en stuur de terug
		String lJsonOutStr = lJsonArrayBuilder.build().toString();
		conversation.sendJSONMessage(lJsonOutStr);
	}
	
	public void opslaan(Conversation conversation){
		JsonObject lJsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
		String lRol = lJsonObjectIn.getString("rol");
		String lGebruikersnaam = lJsonObjectIn.getString("username");
		Rooster hetRooster = informatieSysteem.getRooster();
		
		// JsonArray van nieuwe afmeldingen uit de request halen
		JsonArray lAfmeldingen_jArray = lJsonObjectIn.getJsonArray("non-appearance");
		
		// als de rol van de persoon die de afmelding(en) verstuurd student is wordt die persoon voor de afmeldingen gebruikt
		if(lRol == "student"){
			Student afmeldende = informatieSysteem.getStudent(lGebruikersnaam);
			if(lAfmeldingen_jArray != null){
				for(int i=0;i<lAfmeldingen_jArray.size();i++){
					JsonObject lAfmelding_jsonObj = lAfmeldingen_jArray.getJsonObject(i);
					
					// info verzamelen om de les mee op te vragen
					
					// calendar object converten naar LocalDate en LocalTime
					@SuppressWarnings("static-access")
					Calendar lDatum_afmeldingInCal = PrIS.standaardDatumStringToCal(lAfmelding_jsonObj.getString("date")).getInstance();
					LocalDateTime dateTime = LocalDateTime.ofInstant(lDatum_afmeldingInCal.toInstant(), ZoneId.systemDefault());
					LocalDate lDatum_afmeldingInLD = dateTime.toLocalDate();
					LocalTime lStartTijd_afmelding = dateTime.toLocalTime();
					
					// locatie van de les van de afmelding uit de request halen
					String lLocatie_afmelding = lAfmelding_jsonObj.getString("location");
					
					// les ophalen waarvoor afgemeld is
					Les lLesVan_afmelding = hetRooster.getLes(lDatum_afmeldingInLD, lStartTijd_afmelding, lLocatie_afmelding);
					
					// reden van de afmelding uit de request halen
					String lReden_afmelding = lAfmelding_jsonObj.getString("reason");
					
					// afmelding toevoegen aan les
					lLesVan_afmelding.voegAfmeldingToe(afmeldende, lReden_afmelding);
				}
			}
		}
		
		if(lRol == "docent"){
			if(lAfmeldingen_jArray != null){
				for(int i=0;i<lAfmeldingen_jArray.size();i++){
					JsonObject lAfmelding_jsonObj = lAfmeldingen_jArray.getJsonObject(i);
					// afgemelde als student proberen op te halen
					Persoon afgemelde = informatieSysteem.getStudent(lAfmelding_jsonObj.getString("username"));
					// als de afgemelde geen student als docent op halen
					if(afgemelde == null){
						afgemelde = informatieSysteem.getDocent(lAfmelding_jsonObj.getString("username"));
					}
					// info verzamelen om de les mee op te vragen
					// calendar object converten naar LocalDate en LocalTime
					@SuppressWarnings("static-access")
					Calendar lDatum_afmeldingInCal = PrIS.standaardDatumStringToCal(lAfmelding_jsonObj.getString("date")).getInstance();
					LocalDateTime dateTime = LocalDateTime.ofInstant(lDatum_afmeldingInCal.toInstant(), ZoneId.systemDefault());
					LocalDate lDatum_afmeldingInLD = dateTime.toLocalDate();
					LocalTime lStartTijd_afmelding = dateTime.toLocalTime();
					
					// locatie van de les van de afmelding uit de request halen
					String lLocatie_afmelding = lAfmelding_jsonObj.getString("location");
					
					// les ophalen waarvoor afgemeld is
					Les lLesVan_afmelding = hetRooster.getLes(lDatum_afmeldingInLD, lStartTijd_afmelding, lLocatie_afmelding);
					
					// reden van de afmelding uit de request halen
					String lReden_afmelding = lAfmelding_jsonObj.getString("reason");
					
					// afmelding toevoegen aan les
					lLesVan_afmelding.voegAfmeldingToe(afgemelde, lReden_afmelding);
				}
			}
		}
		
		// output json object aanmaken en errorcode toevoegen
		JsonObjectBuilder lJob =	Json.createObjectBuilder(); 
  	lJob.add("errorcode", 0);
   	//nothing to return use only errorcode to signal: ready!
  	String lJsonOutStr = lJob.build().toString();
 		conversation.sendJSONMessage(lJsonOutStr);
	}
}
