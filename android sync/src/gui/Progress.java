package gui;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Progress{

	JFrame frame;
	JTextArea textArea;
	JProgressBar bar;
	JLabel transfer;
	public Progress() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		frame=new JFrame("Progress");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		frame.add(panel);
		bar=bar(1000);
		panel.add(bar);
		
		
		transfer=new JLabel();
		updateInfo("0","0","0");
		panel.add(transfer);
		
		textArea = new JTextArea(5, 30);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(450, 110));

		panel.add(scrollPane);
		
		frame.pack();
		
		frame.setLocation(width/2-frame.getWidth()/2, height/3-frame.getHeight()/2);
		frame.setVisible(true);
	
	}
	
	JProgressBar bar(int size){
		JProgressBar bar = new JProgressBar(0, size);

		bar.setStringPainted(true);
		
		bar.setValue(0);
		return bar;
		
		
	}
	
	public void update(int progress, int max){
		bar.setMaximum(max);
		bar.setValue(progress);
	}
	
	public void update(int progress, int max, ArrayList<String> lines){
		bar.setMaximum(max);
		update(progress, lines);
		
	}
	
	public void update(int progress, ArrayList<String> lines){
		bar.setValue(progress);
		for (String line : lines) {
			addLine(line);
		}
		
		
	}
	public void addLine(String line){
		textArea.append(line+"\n");
	}
	
	
	public void updateInfo(String transferRate, String estimate, String size){
		transfer.setText("<html>Transfer rate: "+transferRate+" <br>Estimated time left: "+estimate+" s<br>Size left: "+size);
	}
	public static void main(String[] args) {
		new Progress();
	}
}
