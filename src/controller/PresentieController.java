package controller;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import model.PrIS;
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
	
	public void opslaan(Conversation conservation){
		JsonObject lJsonObjectIn = (JsonObject) conservation.getRequestBodyAsJSON();
		String lRol = lJsonObjectIn.getString("rol");
		String lGebruikersnaam = lJsonObjectIn.getString("username");
		Rooster hetRooster = informatieSysteem.getRooster();
		
		JsonArray lAfmeldingen_jArray = lJsonObjectIn.getJsonArray("non-appearance");
		
		if(lRol == "student"){
			Student afmeldende = informatieSysteem.getStudent(lGebruikersnaam);
			if(lAfmeldingen_jArray != null){
				for(int i=0;i<lAfmeldingen_jArray.size();i++){
					JsonObject lAfmelding_jsonObj = lAfmeldingen_jArray.getJsonObject(i);
					
				}
			}
		}
	}
}
