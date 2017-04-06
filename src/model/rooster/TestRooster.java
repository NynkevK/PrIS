package model.rooster;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import model.les.Les;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;


public class TestRooster {

    @Test
    public void testWrite() throws IOException {
        Rooster unit = new Rooster();

        Les output = new Les();

        unit.getLessen();

        String string = new String(output.toString());
        assertEquals(output, string);
    }
}