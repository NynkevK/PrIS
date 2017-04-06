package model.rooster;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import model.les.Les;

public class TestRooster {

	public TestRooster() {}
	
	@Before
	public void setUp() throws Exception {
		String csvBestand = "././CSV/rooster.csv";
	  BufferedReader br = null;
	  String line = "";
	  ArrayList<Les> lessen = new ArrayList<Les>();

  	br = new BufferedReader(new FileReader(csvBestand));
  	while ((line = br.readLine()) != null) {

      Les lesObject = parseObjectFromString(line, Les.class);
  
      lessen.add(lesObject);
	  	} 
	}

	public static <T> T parseObjectFromString(String s, Class<T> clazz) throws Exception {
	    return clazz.getConstructor(new Class[] {String.class }).newInstance(s);
	    // naar PrIs verhuizen?
	}
	
@Test
	public void getLessen() throws Exception {
	  ArrayList<Les> lessen = new ArrayList<Les>();
	  
	  }
}
