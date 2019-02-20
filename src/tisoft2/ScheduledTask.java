/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tisoft2;

import java.util.Date;
import java.util.TimerTask;

/**
 *
 * @author ASUS
 */
public class ScheduledTask extends TimerTask {

	Date now; // to display current time

	// Add your task here
	public void run() {
		now = new Date(); // initialize date
		System.out.println("Time is :" + now); // Display current time
                frmMain.sendComand("01+RH+00");
	}
}