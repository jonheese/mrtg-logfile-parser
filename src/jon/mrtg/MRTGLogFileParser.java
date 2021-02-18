package jon.mrtg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

public class MRTGLogFileParser {
	public static LogFile parseMRTGLogFile(File in) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(in));
			LogFile logFile = new LogFile();
			String line = br.readLine();
			if (line != null) {
				try {
					String timestamp = "0", incoming = "0", outgoing = "0";
					StringTokenizer st = new StringTokenizer(line, " \t\n");
					if (st.hasMoreTokens()) timestamp = st.nextToken();
					if (st.hasMoreTokens()) incoming = st.nextToken();
					if (st.hasMoreTokens()) outgoing = st.nextToken();
					LogHeader logHeader = new LogHeader(Long.parseLong(timestamp), Long.parseLong(incoming), Long.parseLong(outgoing));
					logFile.setLogHeader(logHeader);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				line = br.readLine();
				while (line != null) {
					String timestamp = "0", avgIn = "0", avgOut = "0", maxIn = "0", maxOut = "0";
					StringTokenizer st = new StringTokenizer(line, " \t\n");
					if (st.hasMoreTokens()) timestamp = st.nextToken();
					if (st.hasMoreTokens()) avgIn = st.nextToken();
					if (st.hasMoreTokens()) avgOut = st.nextToken();
					if (st.hasMoreTokens()) maxIn = st.nextToken();
					if (st.hasMoreTokens()) maxOut = st.nextToken();
					LogDatum logDatum = new LogDatum(Long.parseLong(timestamp), Integer.parseInt(avgIn), Integer.parseInt(avgOut), Integer.parseInt(maxIn), Integer.parseInt(maxOut));
					logFile.addLogDatum(logDatum);
					line = br.readLine();
				}
			}
			return logFile;
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
	}
	
	public static void printMRTGLogFile(String name) {
		try {
			LogFile logFile = parseMRTGLogFile(new File(name));
			System.out.println("MRTG File Output:");
			System.out.println(logFile.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void removeSpikes(String name, int maxAvgIn, int maxAvgOut, int maxMaxIn, int maxMaxOut, boolean zero) {
		removeSpikes(new File(name), maxAvgIn, maxAvgOut, maxMaxIn, maxMaxOut, zero);
	}
	
	public static void removeSpikes(File file, int maxAvgIn, int maxAvgOut, int maxMaxIn, int maxMaxOut, boolean zero) {
		removeSpikes(file, maxAvgIn, maxAvgOut, maxMaxIn, maxMaxOut, zero, null);
	}
		
	public static void removeSpikes(File file, int maxAvgIn, int maxAvgOut, int maxMaxIn, int maxMaxOut, boolean zero, ProgressListener listener) {
		try {
			if (listener != null) {
				listener.setProgressBarEnabled(true);
			}
			LogFile logFile = parseMRTGLogFile(file);
			Vector logData = logFile.getLogData();
			int index = 0, changes = 0;
			int total = logData.size();
			for (Enumeration e = logData.elements(); e.hasMoreElements();) {
				LogDatum logDatum = (LogDatum)e.nextElement();
				if (logDatum.getAverageIn() > maxAvgIn) {
					logDatum.setAverageIn(zero ? 0 : maxAvgIn);
					changes = changes + 1;
				}
				if (logDatum.getAverageOut() > maxAvgOut) {
					logDatum.setAverageOut(zero ? 0 : maxAvgOut);
					changes = changes + 1;
				}
				if (logDatum.getMaxIn() > maxMaxIn) {
					logDatum.setMaxIn(zero ? 0 : maxMaxIn);
					changes = changes + 1;
				}
				if (logDatum.getMaxOut() > maxMaxOut) {
					logDatum.setMaxOut(zero ? 0 : maxMaxOut);
					changes = changes + 1;
				}
				if (listener != null)
					listener.setProgressValue((int)((((double)index) / total) * 100));
				index = index + 1;
			}
			if (changes > 0) {
				file.renameTo(new File(file.getAbsolutePath()+".bak"));
				writeMRTGLogFile(logFile, file, listener, changes);
			}
			else if (listener != null) {
				listener.setProgressBarEnabled(false);
				listener.progressFinished(0);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void writeMRTGLogFile(LogFile logFile, File file) {
		writeMRTGLogFile(logFile, file, null);
	}
	
	public static void writeMRTGLogFile(LogFile logFile, File file, ProgressListener listener) {
		writeMRTGLogFile(logFile, file, listener, 0);
	}
	
	public static void writeMRTGLogFile(LogFile logFile, File file, ProgressListener listener, int changes) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			out.println(logFile.getLogHeader().toString());
			int index = 0;
			int total = logFile.getLogData().size();
			for (Enumeration e = logFile.getLogData().elements(); e.hasMoreElements();) {
				out.println(((LogDatum)e.nextElement()).toString());
				if (listener != null)
					listener.setProgressValue((int)((((double)index) / total) * 100));
			}
			out.flush();
			out.close();
			if (listener != null) {
				listener.setProgressBarEnabled(false);
				listener.progressFinished(changes);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			MRTGLogFileParser.removeSpikes(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), new Boolean(args[5]).booleanValue());
			//MRTGLogFileParser.printMRTGLogFile(args[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}