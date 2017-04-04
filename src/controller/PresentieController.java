package controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import model.PrIS;
import model.les.Les;
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
		}
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
		
		JsonObjectBuilder lJob =	Json.createObjectBuilder(); 
  	lJob.add("errorcode", 0);
   	//nothing to return use only errorcode to signal: ready!
  	String lJsonOutStr = lJob.build().toString();
 		conversation.sendJSONMessage(lJsonOutStr);
	}
}
