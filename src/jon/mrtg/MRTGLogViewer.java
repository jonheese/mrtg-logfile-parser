package jon.mrtg;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

public class MRTGLogViewer extends JFrame implements WindowListener, ProgressListener {
	private String currentDir = null;
	private File currentLogFile = null;
	private JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	private JTextField avgIn = new JTextField("0"), avgOut = new JTextField("0"), maxIn = new JTextField("0"), maxOut = new JTextField("0");
	private JButton closeButton = new JButton(new CloseLogAction()), removeSpikesButton = new JButton(new RemoveSpikesAction());
	private JCheckBox zero = new JCheckBox("Zero value(s) above specified maxima", false);
	private JProgressBar progressBar = new JProgressBar(0, 100);
	
	public MRTGLogViewer() {
		super("MRTG Log Viewer");
		addWindowListener(this);
		loadConfig();
		setupMenus();
		setupLayout();
		setSize(500, 500);
		Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getConfigurations()[0].getBounds();
		setBounds(new Rectangle(screen.x + (screen.width / 2) - (getBounds().width / 2),
				screen.y + (screen.height/ 2) - (getBounds().height / 2),
				getBounds().width, getBounds().height));
		setVisible(true);
	}
	
	private void loadConfig() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.home") + File.separator + ".mrtgLogViewer"));
			String line = br.readLine();
			if (line != null)
				currentDir = line;
		} catch (Exception e) {
			if (!(e instanceof FileNotFoundException))
				e.printStackTrace();
		}
	}
	
	private void saveConfig() {
		if (currentDir != null) {
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(System.getProperty("user.home") + File.separator + ".mrtgLogViewer")));
				out.print(currentDir);
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void setupLayout() {
		splitPane.setDividerLocation(355);
		LogFileTable table = new LogFileTable(null);
		JScrollPane scroller = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		splitPane.setLeftComponent(scroller);
		
		JPanel inputPanel = new JPanel(new GridLayout(1,4,10,0));
		avgIn.setPreferredSize(new Dimension(112, avgIn.getPreferredSize().height+3));
		avgOut.setPreferredSize(new Dimension(112, avgOut.getPreferredSize().height+3));
		maxIn.setPreferredSize(new Dimension(112, maxIn.getPreferredSize().height+3));
		maxOut.setPreferredSize(new Dimension(112, maxOut.getPreferredSize().height+3));
		setComponentsEnabled(false);
		inputPanel.add(avgIn);
		inputPanel.add(avgOut);
		inputPanel.add(maxIn);
		inputPanel.add(maxOut);
		
		progressBar.setPreferredSize(new Dimension(465, progressBar.getPreferredSize().height));
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
		zero.setToolTipText("If this is checked, log values greater than the specified maxima will be changed to zero, otherwise they'll be changed to the maximum value");
		buttonPanel.add(zero);
		buttonPanel.add(removeSpikesButton);
		buttonPanel.add(closeButton);
		buttonPanel.setPreferredSize(new Dimension(490, buttonPanel.getPreferredSize().height));
		
		JPanel lowerPanel = new JPanel();
		lowerPanel.add(inputPanel, BorderLayout.NORTH);
		lowerPanel.add(progressBar, BorderLayout.CENTER);
		lowerPanel.add(buttonPanel, BorderLayout.SOUTH);
		splitPane.setRightComponent(lowerPanel);
		getContentPane().add(splitPane);
	}
	
	private void refreshTable() {
		LogFile logFile;
		if (currentLogFile != null && currentLogFile.exists()) {
			logFile = MRTGLogFileParser.parseMRTGLogFile(currentLogFile);
			setComponentsEnabled(true);
		} else {
			logFile = new LogFile();
			avgIn.setText("0");
			avgOut.setText("0");
			maxIn.setText("0");
			maxOut.setText("0");
			setComponentsEnabled(false);
		}
		LogFileTable table = new LogFileTable(logFile.getLogData());
		JScrollPane scroller = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		splitPane.setLeftComponent(scroller);
		splitPane.setDividerLocation(355);
		validate();
		repaint();
	}
	
	private void setupMenus() {
		LoadLogAction load = new LoadLogAction();
		//load.setMnemonic(KeyEvent.VK_L);
		ExitAction exit = new ExitAction();
		//exit.setMnemonic(KeyEvent.VK_X);
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.add(load);
		fileMenu.add(exit);
		JMenu blankMenu = new JMenu("");
		JMenuBar bar = new JMenuBar();
		bar.add(blankMenu);
		bar.add(fileMenu);
		setJMenuBar(bar);
	}
	
	public void setComponentsEnabled(boolean enabled) {
		avgIn.setEnabled(enabled);
		avgOut.setEnabled(enabled);
		maxIn.setEnabled(enabled);
		maxOut.setEnabled(enabled);
		zero.setEnabled(enabled);
		closeButton.setEnabled(enabled);
		removeSpikesButton.setEnabled(enabled);
	}
	
	public void setProgressValue(int value) {
		progressBar.setValue(value);
	}
		
	public int getProgressValue() {
		return progressBar.getValue();
	}
	
	public void progressFinished(int changes) {
		progressBar.setValue(0);
		setComponentsEnabled(true);
		refreshTable();
		DoneDialog doneDialog = new DoneDialog(changes);
		doneDialog.show();
	}
		
	public void setProgressBarEnabled(boolean enabled) {
		progressBar.setEnabled(enabled);
	}
	
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	
	public void windowClosing(WindowEvent e) {
		shutDown();
	}
	
	public void shutDown() {
		saveConfig();
		System.exit(0);
	}
	
	public static void main(String[] args) {
		new MRTGLogViewer();
	}
	
	public class LogFileChooser extends JFileChooser {
		private JDialog dialog;
		
		public LogFileChooser() {
			super();
			setup();
		}
		
		public LogFileChooser(String currentDir) {
			super(currentDir);
			setup();
		}
		
		public void approveSelection() {
			MRTGLogViewer.this.currentLogFile = getSelectedFile(); 
			MRTGLogViewer.this.currentDir = getCurrentDirectory().getAbsolutePath();
			dialog.dispose();
			MRTGLogViewer.this.refreshTable();
		}
		
		public void cancelSelection() {
			dialog.dispose();
		}
			
		private void setup() {
			setDialogType(OPEN_DIALOG);
			setFileFilter(new FileFilter() {
				public boolean accept(File file) {
					return file.isDirectory() || file.getName().endsWith(".log");
				}
				
				public String getDescription() {
					return "MRTG Log Files";
				}
			});
			dialog = new JDialog(MRTGLogViewer.this, "Open a Log file...", true);
			dialog.setSize(new Dimension(400,400));
			dialog.getContentPane().add(this);
			Rectangle parentBounds = MRTGLogViewer.this.getBounds();
			dialog.setBounds(new Rectangle(parentBounds.x + (parentBounds.width / 2) - 200,
				parentBounds.y + (parentBounds.height/ 2) - 200,
				400, 400));
			dialog.show();
		}
	}
	
	private class DoneDialog extends JDialog implements ActionListener {
		private JButton okay = new JButton("Okay");
		
		public DoneDialog(int changes) {
			super(MRTGLogViewer.this, "Changes Finished", true);
			setSize(new Dimension(250, 200));
			okay.addActionListener(this);
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
			panel.add(new JLabel(changes + " changes made."), BorderLayout.NORTH);
			panel.add(okay, BorderLayout.CENTER);
			getContentPane().add(panel);
			Rectangle parentBounds = MRTGLogViewer.this.getBounds();
			setBounds(new Rectangle(parentBounds.x + (parentBounds.width / 2) - (getBounds().width / 2),
				parentBounds.y + (parentBounds.height/ 2) - (getBounds().height / 2),
				getBounds().width, getBounds().height));
		}
		
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == okay)
				dispose();
		}
	}
		
	
	private class CloseLogAction extends AbstractAction {
		public CloseLogAction() {
			super("Close");
		}
		
		public void actionPerformed(ActionEvent e) {
			MRTGLogViewer.this.currentLogFile = null;
			MRTGLogViewer.this.refreshTable();
		}
	}
	
	private class RemoveSpikesAction extends AbstractAction {
		private int maxAvgIn = 0, maxAvgOut = 0, maxMaxIn = 0, maxMaxOut = 0;
		
		public RemoveSpikesAction() {
			super("Remove Spikes");
		}
		
		public void actionPerformed(ActionEvent e) {
			if (MRTGLogViewer.this.currentLogFile != null) {
				maxAvgIn = (avgIn.getText() != null && !"".equals(avgIn.getText())) ? Integer.parseInt(avgIn.getText()) : 0;
				maxAvgOut = (avgOut.getText() != null && !"".equals(avgOut.getText())) ? Integer.parseInt(avgOut.getText()) : 0;
				maxMaxIn = (maxIn.getText() != null && !"".equals(maxIn.getText())) ? Integer.parseInt(maxIn.getText()) : 0;
				maxMaxOut = (maxOut.getText() != null && !"".equals(maxOut.getText())) ? Integer.parseInt(maxOut.getText()) : 0;
				MRTGLogViewer.this.setComponentsEnabled(false);
				Thread t = new Thread() {
					public void run() {
						MRTGLogFileParser.removeSpikes(MRTGLogViewer.this.currentLogFile, RemoveSpikesAction.this.maxAvgIn, 
							RemoveSpikesAction.this.maxAvgOut, RemoveSpikesAction.this.maxMaxIn,
							RemoveSpikesAction.this.maxMaxOut, MRTGLogViewer.this.zero.isSelected(), MRTGLogViewer.this);
					}
				};
				t.start();
			}
		}
	}
	
	private class LoadLogAction extends AbstractAction {
		public LoadLogAction() {
			super("Load Log File...");
		}
		
		public void actionPerformed(ActionEvent e) {
			if (MRTGLogViewer.this.currentDir != null)
				new LogFileChooser(MRTGLogViewer.this.currentDir);
			else
				new LogFileChooser();
		}
	}
	
	private class ExitAction extends AbstractAction {
		public ExitAction() {
			super("Exit");
		}
		
		public void actionPerformed(ActionEvent e) {
			MRTGLogViewer.this.shutDown();
		}
	}
}