//checked
package model.persoon;



public class Student extends Persoon {
	private int studentNummer;
	private String groepId;

	public Student(
		String pVoornaam, 
		String pTussenvoegsel, 
		String pAchternaam, 
		String pWachtwoord, 
		String pGebruikersnaam,
		int pStudentNummer) {
  		super(
  			pVoornaam, 
  			pTussenvoegsel, 
  			pAchternaam, 
  			pWachtwoord, 
  			pGebruikersnaam);
  		this.setStudentNummer(pStudentNummer);
  		this.setGroepId("");
  	}


 public String getGroepId() {
    return this.groepId;	
  }
 
  public void setGroepId(String pGroepId) {
    this.groepId= pGroepId;	
  }
 
	public int getStudentNummer() {
		return this.studentNummer;
	}

	private void setStudentNummer(int pStudentNummer) {
		this.studentNummer = pStudentNummer;
	}
	
	public boolean equals(Object obj){
		boolean gelijkenObjecten = false;
		if  (obj instanceof Student){
			Student andereStudent = (Student) obj;
			if (studentNummer == andereStudent.studentNummer &&  groepId == andereStudent.groepId && super.equals(obj)){
				gelijkenObjecten = true;
			}
		}
		return gelijkenObjecten;
	}
	
}
