/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tisoft2;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ASUS
 */
class Test {

    public static void main(String args[]) throws InterruptedException {
        //        Timer time = new Timer(); // Instantiate Timer Object
        //        ScheduledTask st = new ScheduledTask(); // Instantiate SheduledTask class
        //        time.schedule(st, 0, 1000 * 60 * 3); // Create Repetitively task for every 1 secs
//        String pattern = ".*\\< \\d{2}\\+REON\\+000\\+0\\](\\d{20})\\](\\d{1,2}\\/\\d{1,2}\\/\\d{4})\\s(\\d{2}\\:\\d{2}\\:\\d{2})\\]2\\]2\\]2.*";
       String pattern = ".*- 01\\+RH\\+000\\+\\d{2}\\/\\d{2}\\/\\d{2} \\d{2}\\:\\d{2}:\\d{2}\\]\\d{2}\\/\\d{2}\\/\\d{2}\\]\\d{2}\\/\\d{2}\\/\\d{2}.*";
        Pattern r = Pattern.compile(pattern);
        ComandaController controller = new ComandaController();
        
//        String pergunta = "<08+REON+000+0]00000000000021707730]18/05/2018 10:43:12]2]2]2C1";
        String pergunta = "- 01+RH+000+18/05/18 11:25:00]00/00/00]00/00/00";
        Matcher m = r.matcher(pergunta);
        
        System.out.println(m.matches());
    }
}
