import javax.swing.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.filechooser.*;
import javax.swing.SwingUtilities.*;

/** 
 * The DisplayPanel class is a JPanel which carries
 *  2 JPanels holding JTextFields and JButtons.
 */
public class DisplayPanel extends JPanel {
	/** The 4 main action JButtons*/
	private JButton add, remove, save, load;
	
	/** The JPanel holding the JTextFields.*/
	private JPanel jsonPanel;
	
	/** The JFrame which DisplayPanel is on, so that we can change the JFrame content.*/
	private JFrame frame;				
	
	/** 
	 * The index of the currently selected Component in the array of JTextFields in 
	 * jsonPanel.	 
	 */
	private int selected;
	/** The state whether the selected JTextField is in a JSON structure or not*/
	private boolean selectInner;
	/** If selectInner is true, this is the index of the selected JTextField in the JSON structure*/
	private int innerIndex;
	
	
	/** The constructor for a DisplayPanel, taking in the JFrame as an argument.*/
	public DisplayPanel(JFrame arg) {
		frame = arg;
		selectInner = false;
		innerIndex = 0;
		
		// Instantiates and adds the JPanel which will hold the JTextFields.
		jsonPanel = new JPanel();
		add(jsonPanel, BorderLayout.WEST);
		
		/* 
		 * Instantiates a JPanel and adds the 4 JButtons to it with 4
		 * separate listeners.
		 */
		JPanel buttonPanel = new JPanel();
		
		add = new JButton("Add");
		add.addActionListener(new addListener(this));
		buttonPanel.add(add);
		remove = new JButton("Remove");
		remove.addActionListener(new removeListener(this));
		remove.setEnabled(false); 
		buttonPanel.add(remove);
		save = new JButton("Save");
		save.addActionListener(new saveListener(this));
		buttonPanel.add(save);
		load = new JButton("Load");
		load.addActionListener(new loadListener());
		buttonPanel.add(load);

		// Adds the button panel to DisplayPanel
		buttonPanel.setBackground(Color.RED);
		add(buttonPanel, BorderLayout.EAST);
		
	}
	
	/** 
	 * Method update attaches a SelectListener to all JTextFields in jsonPanel.
	 * This is used when loading in a JSON file.
	 */
	public void update() {
		//Iterate through the JTextFields in jsonPanel and adds a FocusListener to each.
		for (Component c : jsonPanel.getComponents()) {
			if (c instanceof JTextField)
				c.addFocusListener(new SelectListener());
			else {
				for (Component c_ : ((JPanel)c).getComponents())
					c_.addFocusListener(new SelectListener());
			}
				
		}
	}
	
	/** 
	 * The SelectListener class implements interface FocusListener and keeps
	 * track of which JTextField in the jsonPanel JPanel is currently selected.
	 */
	private class SelectListener implements FocusListener{
		
		/** 
		 * Method focusGained updates the private fields "selected," "selectInner," and "innerIndex," keeping
		 * track of the currently selected JTextField.
		 */
		public void focusGained(FocusEvent e) {
			//Checks whether the selected component is a key or part of a value.
			if (!(e.getComponent().getParent().getParent() instanceof DisplayPanel)) {
				selected = jsonPanel.getComponentZOrder(e.getComponent().getParent());
				selectInner = true;
				innerIndex = e.getComponent().getParent().getComponentZOrder(e.getComponent());
			}
			else {
				selected = jsonPanel.getComponentZOrder(e.getComponent());
				selectInner = false;
			}
			remove.setEnabled(true);
		}
		
		/** 
		 * Method focusLost does nothing, but we must implement it because 
		 * it is a method in FocusListener.
		 */
		public void focusLost(FocusEvent e) {}
	}
	
	
	
	/**
	 * The class addListener is an ActionListener for the JButton "add."
	 * It will add a new pair of JTextFields to jsonPanel.
	 */
	private class addListener implements ActionListener{
		
		/** The DisplayPanel which the "add" JButton lies on.*/
		private JPanel panel;
		
		private JTextField length;
		
		private boolean json;
		
		/**
		 * The constructor for an addListener object takes in the DisplayPanel
		 * which the "add" JButton lies on as an argument and assigns it to the "panel" field.
		 */
		private addListener(JPanel panel_) {
			panel = panel_;
		}
		
		/**
		 * Method actionPerformed is inherited from interface ActionListener
		 * and adds a pair of JTextfields to jsonPanel.
		 */
		public void actionPerformed(ActionEvent e) {
			//Prompts user for single or JSON value.
			JRadioButton single = new JRadioButton("Single Value");
			single.addActionListener(new singleListener());
			JRadioButton jsonButton = new JRadioButton("JSON Value");
			jsonButton.addActionListener(new jsonListener());
			length = new JTextField(5);
			length.setEnabled(false);
		    JPanel myPanel = new JPanel();
			myPanel.add(single);
			myPanel.add(Box.createHorizontalStrut(15));
			myPanel.add(jsonButton);
			ButtonGroup group = new ButtonGroup();
			group.add(single);
			group.add(jsonButton);
			myPanel.add(length);
			if (selectInner) {
				Component target = jsonPanel.getComponents()[selected];
				JTextField textfield = new JTextField();
				textfield.addFocusListener(new SelectListener());
				((JPanel)target).add(textfield);
				textfield = new JTextField();
				textfield.addFocusListener(new SelectListener());
				((JPanel)target).add(textfield);
				((JPanel)target).setLayout(new GridLayout(((JPanel)target).getComponentCount()/2, 2));
			}
			else {
				json = false;
				int dimension = 0;
			    int result = JOptionPane.showConfirmDialog(null, myPanel,
			    		"Please Select", JOptionPane.OK_CANCEL_OPTION);
			    
				if (result == JOptionPane.OK_OPTION) {
					if(json)
						dimension = Integer.parseInt(length.getText());
				}
				
				//Instantiates JTextFields and adds SelectListeners to them.
				JTextField textfield = new JTextField("", 40);
				textfield.addFocusListener(new SelectListener());
				jsonPanel.add(textfield);
				
				//If user selected JSON object as value.
				if (json) {
					JPanel inner = new JPanel();
					inner.setLayout(new GridLayout(dimension,2));
					for (int i=0; i<dimension*2; i++) {
						textfield = new JTextField("", 20);
						textfield.addFocusListener(new SelectListener());
						inner.add(textfield);
					}
					
					jsonPanel.add(inner);
				}
				//If user selected single value as value.
				else {
					textfield = new JTextField("", 40);
					textfield.addFocusListener(new SelectListener());
					jsonPanel.add(textfield);
				}
			}
			
			//Reorganize the jsonPanel so that it is in 2 columns.
			jsonPanel.setLayout(new GridLayout(jsonPanel.getComponentCount()/2, 2));
			
			//Refresh the JFrame with the updated DisplayPanel.
			frame.setContentPane(panel);
			
			//Pressing a button unselects all JTextFields, so disable JButton "remove."
			remove.setEnabled(false);
			
		}
		/** 
		 * The class singleListener is an ActionListener for the "single" 
		 * JRadioButton.
		 */
		private class singleListener implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				length.setEnabled(false);
				json = false;
			}
		}
		/** 
		 * The class singleListener is an ActionListener for the "jsonButton" 
		 * JRadioButton.
		 */
		private class jsonListener implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				length.setEnabled(true);
				json = true;
			}
		}
	}
	
	/**
	 * The class removeListener is an ActionListener for the JButton "remove."
	 * It will remove the selected row of JTextfields from jsonPanel.
	 */
	private class removeListener implements ActionListener{
		/** The DisplayPanel which the "remove" JButton lies on.*/
		private JPanel panel;
		
		/**
		 * The constructor for an removeListener object takes in the DisplayPanel
		 * which the "remove" JButton lies on as an argument and assigns it to 
		 * the "panel" field.
		 */
		private removeListener(JPanel panel_) {
			panel = panel_;
		}
		
		/**
		 * Method actionPerformed is inherited from interface ActionListener
		 * and removes the selected pair of JTextfields from jsonPanel.
		 */
		public void actionPerformed(ActionEvent e) {
			/* 
			 * If the selected JTextField is in the left column, remove the
			 * next JTextField, then the one selected.
			 */
			if (selectInner&&((JPanel)jsonPanel.getComponent(selected)).getComponentCount()>2) {
				Component target = jsonPanel.getComponent(selected);
				if (innerIndex%2 == 0) {
					((JPanel) target).remove(innerIndex+1);
					((JPanel) target).remove(innerIndex);
				}
				/*
				 * If the innerIndex JTextField is in the right column, remove the
				 * innerIndex one, then the previous one.
				 */
				else if (innerIndex%2 == 1) {
					((JPanel) target).remove(innerIndex);
					((JPanel) target).remove(innerIndex-1);
				}
				((JPanel)target).setLayout(new GridLayout(((JPanel)target).getComponentCount()/2, 2));
			}
			else {
				if (selected%2 == 0) {
					jsonPanel.remove(selected+1);
					jsonPanel.remove(selected);
				}
				/*
				 * If the selected JTextField is in the right column, remove the
				 * selected one, then the previous one.
				 */
				else if (selected%2 == 1) {
					jsonPanel.remove(selected);
					jsonPanel.remove(selected-1);
				}
				
				//Reorganize the jsonPanel to fit the remaining JTextFields in 2 columns.
				jsonPanel.setLayout(new GridLayout(jsonPanel.getComponentCount()/2, 2));
			}
			
			//Refresh the JFrame with the updated DisplayPanel
			frame.setContentPane(panel);
			
			//Pressing a button unselects all JTextFields, so disable JButton "remove."
			remove.setEnabled(false);
		}
	}
	
	/**
	 * The class saveListener is an ActionListener for the JButton "save."
	 * It will save the inputs in the JTextFields within jsonPanel to a selected JSON 
	 * file.
	 */
	private class saveListener implements ActionListener{
		/** The DisplayPanel which the "save" JButton lies on.*/
		private JPanel panel;
		
		/**
		 * The constructor for an save Listener object takes in the DisplayPanel
		 * which the "save" JButton lies on as an argument and assigns it to the 
		 * "panel" field.
		 */
		private saveListener(JPanel panel_) {
			panel = panel_;
		}
		
		/**
		 * Method actionPerformed is inherited from interface ActionListener
		 * and saves the inputs into a selected JSON file.
		 */
		public void actionPerformed(ActionEvent e) {
			//Instantiate an empty JSON object.
			JSONObject obj = new JSONObject();
			
			/*
			 * Iterate through the JTextFields in jsonPanel and add the pairs to the 
			 * JSON object.
			 */
			Component[] components = jsonPanel.getComponents();
			for(int i=0; i<components.length; i+=2) {
				if (obj.containsKey(((JTextField)components[i]).getText())){
					JOptionPane.showMessageDialog(panel, "Error: Found identical keys.");
					return;
				}
				if(components[i+1] instanceof JPanel) {
					JSONObject inner = new JSONObject();
					Component[] innerComponents = ((JPanel)components[i+1]).getComponents();
					for(int j=0; j<innerComponents.length; j+=2) {
						if (inner.containsKey(((JTextField)innerComponents[j]).getText())){
							JOptionPane.showMessageDialog(panel, "Error: Found identical keys.");
							return;
						}
						inner.put(((JTextField)innerComponents[j]).getText(), ((JTextField)innerComponents[j+1]).getText());
					}
					obj.put(((JTextField)components[i]).getText(), inner);
				}
				else if (components[i+1] instanceof JTextField)
					obj.put(((JTextField)components[i]).getText(), ((JTextField)components[i+1]).getText());
				else{
					System.out.println("why");
				}
			}
			System.out.println(obj);
			
			//Allow user to select the saved file name and location.
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			int returnValue = jfc.showOpenDialog(null);
			File selectedFile = null;
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				selectedFile = jfc.getSelectedFile();
			}
			try (FileWriter file = new FileWriter(selectedFile.getAbsolutePath())) {
	            file.write(obj.toJSONString());
	            file.flush();

	        } catch (IOException e_) {
	            e_.printStackTrace();
	        }
			
			System.out.println("Saved.");
			
			//Pressing a button unselects all JTextFields, so disable JButton "remove."
			remove.setEnabled(false);
		}
	}
	
	/**
	 * The class loadListener is an ActionListener for the JButton "load."
	 * It will load a selected JSON file into jsonPanel.
	 */
	private class loadListener implements ActionListener{
		/**
		 * Method actionPerformed is inherited from interface ActionListener
		 * and loads a selected JSON file into jsonPanel.
		 */
		public void actionPerformed(ActionEvent e) {
			//Instantiate a new DisplayPanel.
			DisplayPanel newPanel = new DisplayPanel(frame);
			
			//Allow user to select the desired JSON file.
			JSONParser parser = new JSONParser();
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			int returnValue = jfc.showOpenDialog(null);
			File selectedFile = null;
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				selectedFile = jfc.getSelectedFile();
			}
			try {
				//Parse the selected JSON file into a JSON object.
				Object obj = parser.parse(new FileReader(selectedFile));
				JSONObject jsonObject = (JSONObject) obj;
		        System.out.println(jsonObject);
		        /*
		         * Iterate through the JSON object and add pairs of of JTextFields/JPanels
		         * to the new jsonPanel with its keys and values.
		         */
		        for (Object key : jsonObject.keySet()) {
		        	JTextField textfield = new JTextField(key.toString(), 40);
		        	newPanel.jsonPanel.add(textfield);
		        	//If it is a JSONObject, iterate through it and put it on another JPanel.
		        	if (jsonObject.get(key.toString()) instanceof JSONObject) {
		        		JPanel inner = new JPanel();
		        		obj = jsonObject.get(key.toString());
		        		for (Object k : ((JSONObject)obj).keySet()) {
		        			inner.add(new JTextField(k.toString(), 20));
		        			inner.add(new JTextField(((JSONObject)obj).get(k).toString(), 20));
		        		}
		        		inner.setLayout(new GridLayout(((JSONObject)obj).size(),2));
		        		newPanel.jsonPanel.add(inner);
		        	}
		        	//If it is a single value, just put it on a JTextField.
		        	else{
			        	textfield = new JTextField(jsonObject.get(key.toString()).toString(), 40);
						newPanel.jsonPanel.add(textfield);
		        	}
		        }
		        //Assign SelectListeners to each of the added JTextFields.
		        newPanel.update();
		        
		        //Organize the new jsonPanel into 2 columns.
		        newPanel.jsonPanel.setLayout(new GridLayout(jsonObject.keySet().size(), 2));
		        
		        } catch (FileNotFoundException e_) {
		            e_.printStackTrace();
		        } catch (IOException e_) {
		            e_.printStackTrace();
		        } catch (ParseException e_) {
		            e_.printStackTrace();
		        }
			
			//Refresh the JFrame with the new DisplayPanel.
			frame.setContentPane(newPanel);
			frame.revalidate();
		}
	}
	
}
