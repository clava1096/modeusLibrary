/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package utmn.modeus.org;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;
import static utmn.modeus.org.Credentials.login;

public class LibraryTest {
    @Test public void someLibraryMethod() throws ParseException, ExecutionException, InterruptedException, IOException {
        String BearerToken = login("x","x");
        Modeus modeus = new Modeus(BearerToken);
        LocalDateTime dateone = LocalDateTime.of(2023,6,4,18,0);
        LocalDateTime datetwo = LocalDateTime.of(2023,6,10,18,0);
        JSONObject myTimeTable = modeus.getMyTimetable(dateone, datetwo, false);
        try(FileWriter fw = new FileWriter("testTimetalbe.txt")) {
            fw.write(myTimeTable.toJSONString());
        }
        System.out.println("Done!");
        assertNotNull(myTimeTable.toJSONString());
    }
}