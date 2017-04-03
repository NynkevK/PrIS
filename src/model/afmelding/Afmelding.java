package model.afmelding;

import model.persoon.Student;

public class Afmelding {
	private String reden;
	private Student deAfgemelde;
	
	public Afmelding(String iReden, Student afgemelde){
		reden = iReden;
		deAfgemelde = afgemelde;
	}
	
	public void setReden(String nieuweReden){
		reden = nieuweReden;
	}
	
	public String getReden(){	return reden;}
	
	public Student getAfgemelde(){ return deAfgemelde;}
	
	public boolean equals(Object object){
		boolean gelijkeObjecten = false;
		
		if(object instanceof Afmelding){
			Afmelding andereAfmelding = (Afmelding)object;
			
			if(this.reden.equals(andereAfmelding.getReden()) &&
					this.deAfgemelde.equals(andereAfmelding.getAfgemelde())){
				gelijkeObjecten = true;
			}
		}
		
		return gelijkeObjecten;
	}
}
