package io.dbkf.rowingtiming;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class RowingTiming {

	private final Collection<MillisToColor> millisToColors = Arrays.asList(
			new MillisToColor(1000, Color.ORANGE), new MillisToColor(2000, Color.GRAY));
	private Color currentColor;
	
	private static class MillisToColor {
		private final long millis;
		private final Color color;

		public MillisToColor(long millis, Color color) {
			this.millis = millis;
			this.color = color;
		}

		public long getMillis() {
			return millis;
		}
		
		public Color getColor() {
			return color;
		}
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new RowingTiming().createAndShowGUI();
            }
        });
	}

	private void createAndShowGUI() {
		GraphicsEnvironment env =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice dev = env.getDefaultScreenDevice();
    	
        JFrame frame = new JFrame("RowingTiming");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
    	frame.setVisible(true);
    	dev.setFullScreenWindow(frame);

        frame.pack();
        frame.setVisible(true);
        
        JPanel container = new JPanel();
        container.setLayout(new GridBagLayout());
        container.setBackground(millisToColors.iterator().next().getColor());
        
        frame.add(container);
        
        long maxMillis = getMaxMillis(millisToColors);
        long startTime = System.currentTimeMillis();
        
        JLabel label = new JLabel(formatSeconds(getDiff(startTime) % maxMillis));
        label.setFont(new Font("Ubuntu Condensed", Font.PLAIN, 350));
        container.add(label);
        
        new Timer(33, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				long diff = getDiff(startTime);
				long cuttedDiff = diff % maxMillis;
				label.setText(formatSeconds(cuttedDiff));
				Color color = getColor(cuttedDiff, millisToColors);
				if (!color.equals(currentColor))
					currentColor = color;

				container.setBackground(currentColor);
			}
        }).start();
	}
	private Color getColor(long diff, Collection<MillisToColor> millisToColors) {
		long accum = 0;
		for (MillisToColor millisToColor : millisToColors) {
			accum += millisToColor.getMillis();
			if (diff <= accum)
				return millisToColor.getColor();
		}
		throw new IllegalArgumentException(
				"Can't get style for diff = " + diff + ". millisToColors = " + millisToColors);
	}

	// 2575 to "2.5"
	private String formatSeconds(long diff) {
		long ss = diff / 1000;
		long ms = diff % 1000 / 100;
		String result = ss + "." + ms;
		return result;
	}

	private long getMaxMillis(Collection<MillisToColor> msToStyles) {
		long result = 0;
		for (MillisToColor millisToStyle : msToStyles) {
			result += millisToStyle.getMillis();
		}
		return result;
	}

	private long getDiff(long startTime) {
		return System.currentTimeMillis() - startTime;
	}
}
