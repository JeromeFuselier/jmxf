/******************************************************************************
 *  
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation; either version 3 of the License, or (at your option) 
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Author     : Jerome Fuselier
 * Creation   : June 2011
 *  
 *****************************************************************************/

package uk.ac.liv.util;


/*
	LogFactory.log(Level.SEVERE, "severe");
	LogFactory.log(Level.WARNING, "warning");
	LogFactory.log(Level.INFO, "info");
	LogFactory.log(Level.CONFIG, "config");
	LogFactory.log(Level.FINE, "fine");
	LogFactory.log(Level.FINER, "finer");
	LogFactory.log(Level.FINEST, "finest");
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LogFactory {
	
	private static Logger mxfLogger = null;   
	
//	private static Level level = Level.OFF;
//	private static Level level = Level.SEVERE;
//	private static Level level = Level.WARNING;
//	private static Level level = Level.INFO;
//	private static Level level = Level.CONFIG;
//	private static Level level = Level.FINE;
//	private static Level level = Level.FINER;
//	private static Level level = Level.FINEST;
//	private static Level level = Level.ALL;
	
	public static Level level = Level.INFO;
	public static boolean useLogFile = true;
	private static String logFile = "log/mxf.log";
	
	public static boolean debug() {
		// If level is higher than INFO (== CONFIG, FINE, ...) then debug
		// mode is activated too
		return level.intValue() <= Level.INFO.intValue();
	}
	
	

	public static void log(Level l, String msg)	{
		try {
			mxfLogger.log(l, msg);
		// If logger doesn't exist -> we initialize it
		} catch (NullPointerException e) {
			LogFactory.initialize();
			mxfLogger.log(l, msg);
		}
	}


	
	public static void initialize() {
		mxfLogger = Logger.getLogger("uk.ac.liv.mxf");
		
		// Remove defautl ConsoleHandler
		mxfLogger.setUseParentHandlers(false);
		
		// Modify level (handler + logger)
		mxfLogger.setLevel(level);
		
		ConsoleHandler ch = new ConsoleHandler();
		ch.setFormatter(new Formatter() {
		      public String format(LogRecord record) {
		        return record.getMessage() + "\n";
		      }
		    });
		
		mxfLogger.addHandler(ch);
		ch.setLevel(level);
		
		// Add a handler for a file
		if (useLogFile) {
			FileHandler fh;
			try {
				fh = new FileHandler(logFile);
				fh.setFormatter(new Formatter() {
			      public String format(LogRecord record) {
				        return record.getMessage() + "\n";
				      }
				    });
				mxfLogger.addHandler(fh);
				fh.setLevel(level);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	    		
		
	}

	
}