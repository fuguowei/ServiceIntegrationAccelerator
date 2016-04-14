/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package qut.edu.au.visualisation;

/**
 *
 * @author sih
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Test extends JPanel{
   public void paint(Graphics g) {
      Graphics2D g2 = (Graphics2D)g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON);
      Font font = new Font("Serif", Font.PLAIN, 96);
      g2.setFont(font);
      g2.drawString("Text", 40, 120);
      
      g2.setColor(Color.blue);
      g2.drawRect(75,75,300,200);      
   }
   public static void main(String[] args) {
      JFrame f = new JFrame();
      f.getContentPane().add(new Test());
      f.setSize(300, 200);
      f.setVisible(true);
   }
}