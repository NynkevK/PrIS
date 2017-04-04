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
import javax.swing.LayoutFocusTraversalPolicy;

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
		if(conversation.getRequestedURI().startsWith("/presentie/opslaan")){
			opslaan(conversation);
		} else if (conversation.getRequestedURI().startsWith("/presentie/ophalen")){
			ophalen(conversation);
		}
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
		
		JsonArray lAfmeldingen_jArray = lJsonObjectIn.getJsonArray("non-appearance");
		
		if(lRol == "student"){
			Student afmeldende = informatieSysteem.getStudent(lGebruikersnaam);
			if(lAfmeldingen_jArray != null){
				for(int i=0;i<lAfmeldingen_jArray.size();i++){
					JsonObject lAfmelding_jsonObj = lAfmeldingen_jArray.getJsonObject(i);
					
					//calendar object converten naar LocalDate en LocalTime
					@SuppressWarnings("static-access")
					Calendar lDatum_afmeldingInCal = PrIS.standaardDatumStringToCal(lAfmelding_jsonObj.getString("date")).getInstance();
					LocalDateTime dateTime = LocalDateTime.ofInstant(lDatum_afmeldingInCal.toInstant(), ZoneId.systemDefault());
					LocalDate lDatum_afmeldingInLD = dateTime.toLocalDate();
					LocalTime lStartTijd_afmelding = dateTime.toLocalTime();
					
					String lLocatie_afmelding = lAfmelding_jsonObj.getString("location");
					
					Les lLesVan_afmelding = hetRooster.getLes(lDatum_afmeldingInLD, lStartTijd_afmelding, lLocatie_afmelding);
					
					String lReden_afmelding = lAfmelding_jsonObj.getString("reason");
					
					lLesVan_afmelding.voegAfmeldingToe(afmeldende, lReden_afmelding);
				}
			}
		}
		
		if(lRol == "docent"){
			if(lAfmeldingen_jArray != null){
				for(int i=0;i<lAfmeldingen_jArray.size();i++){
					JsonObject lAfmelding_jsonObj = lAfmeldingen_jArray.getJsonObject(i);
					Persoon afgemelde = informatieSysteem.getStudent(lAfmelding_jsonObj.getString("username"));
					if(afgemelde == null){
						afgemelde = informatieSysteem.getDocent(lAfmelding_jsonObj.getString("username"));
					}
					
				//calendar object converten naar LocalDate en LocalTime
					@SuppressWarnings("static-access")
					Calendar lDatum_afmeldingInCal = PrIS.standaardDatumStringToCal(lAfmelding_jsonObj.getString("date")).getInstance();
					LocalDateTime dateTime = LocalDateTime.ofInstant(lDatum_afmeldingInCal.toInstant(), ZoneId.systemDefault());
					LocalDate lDatum_afmeldingInLD = dateTime.toLocalDate();
					LocalTime lStartTijd_afmelding = dateTime.toLocalTime();
					
					String lLocatie_afmelding = lAfmelding_jsonObj.getString("location");
					
					Les lLesVan_afmelding = hetRooster.getLes(lDatum_afmeldingInLD, lStartTijd_afmelding, lLocatie_afmelding);
					
					String lReden_afmelding = lAfmelding_jsonObj.getString("reason");
					
					lLesVan_afmelding.voegAfmeldingToe(afgemelde, lReden_afmelding);
				}
			}
		}
		
		JsonObjectBuilder lJob =	Json.createObjectBuilder(); 
  	lJob.add("errorcode", 0);
   	//nothing to return use only errorcode to signal: ready!
  	String lJsonOutStr = lJob.build().toString();
 		conversation.sendJSONMessage(lJsonOutStr);
	}
}
