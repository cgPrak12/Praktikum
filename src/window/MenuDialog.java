package window;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JCheckBox;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import org.lwjgl.util.vector.Vector4f;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class MenuDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textExposure;
	private JTextField textBrightness;
	private JTextField textBloom;
	
	/**
	 * Launch the application.
	 */
	public void run() {
		try {
			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			this.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public MenuDialog() {
		setTitle("Graphic Menu");
		setBounds(100, 100, 400, 400);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{36, 50, 200, 54, 0};
		gbl_contentPanel.rowHeights = new int[]{36, 23, 23, 23, 31, 23, 2, 23, 23, 2, 23, 23, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblExposure = new JLabel("Exposure");
			GridBagConstraints gbc_lblExposure = new GridBagConstraints();
			gbc_lblExposure.fill = GridBagConstraints.HORIZONTAL;
			gbc_lblExposure.insets = new Insets(0, 0, 5, 5);
			gbc_lblExposure.gridx = 1;
			gbc_lblExposure.gridy = 1;
			contentPanel.add(lblExposure, gbc_lblExposure);
		}
		
		// Slider and label
		{
			final JSlider slideExposure = new JSlider();
			slideExposure.setSnapToTicks(true);
			slideExposure.setMajorTickSpacing(20);
			slideExposure.setValue(10);
			slideExposure.setMinorTickSpacing(1);
			slideExposure.setMaximum(200);
			GridBagConstraints gbc_slideExposure = new GridBagConstraints();
			gbc_slideExposure.anchor = GridBagConstraints.NORTH;
			gbc_slideExposure.fill = GridBagConstraints.HORIZONTAL;
			gbc_slideExposure.insets = new Insets(0, 0, 5, 5);
			gbc_slideExposure.gridx = 2;
			gbc_slideExposure.gridy = 1;
			contentPanel.add(slideExposure, gbc_slideExposure);
			slideExposure.addChangeListener(new ChangeListener(){
	            public void stateChanged(ChangeEvent e) {
	            	main.TerrainMain.setExposure(((float) slideExposure.getValue()) / 10.0f);
	            }
	        });
		}
		{
			textExposure = new JTextField();
			textExposure.setBorder(null);
			textExposure.setBackground(UIManager.getColor("Button.background"));
			GridBagConstraints gbc_textExposure = new GridBagConstraints();
			gbc_textExposure.anchor = GridBagConstraints.WEST;
			gbc_textExposure.insets = new Insets(0, 0, 5, 0);
			gbc_textExposure.gridx = 3;
			gbc_textExposure.gridy = 1;
			contentPanel.add(textExposure, gbc_textExposure);
			textExposure.setColumns(6);
			textExposure.setEditable(false);
		}
		
		{
			JLabel lblBrightness = new JLabel("Brightness");
			GridBagConstraints gbc_lblBrightness = new GridBagConstraints();
			gbc_lblBrightness.anchor = GridBagConstraints.WEST;
			gbc_lblBrightness.insets = new Insets(0, 0, 5, 5);
			gbc_lblBrightness.gridx = 1;
			gbc_lblBrightness.gridy = 2;
			contentPanel.add(lblBrightness, gbc_lblBrightness);
		}
		{
			final JSlider slideBrightness = new JSlider();
			slideBrightness.setSnapToTicks(true);
			slideBrightness.setMajorTickSpacing(20);
			slideBrightness.setMinorTickSpacing(1);
			slideBrightness.setMaximum(200);
			slideBrightness.setValue(10);
			GridBagConstraints gbc_slideBrightness = new GridBagConstraints();
			gbc_slideBrightness.anchor = GridBagConstraints.NORTH;
			gbc_slideBrightness.fill = GridBagConstraints.HORIZONTAL;
			gbc_slideBrightness.insets = new Insets(0, 0, 5, 5);
			gbc_slideBrightness.gridx = 2;
			gbc_slideBrightness.gridy = 2;
			contentPanel.add(slideBrightness, gbc_slideBrightness);
			slideBrightness.addChangeListener(new ChangeListener(){
	            public void stateChanged(ChangeEvent e) {
	            	float v = ((float) slideBrightness.getValue()) / 10.0f;
	            	main.TerrainMain.setBrightnessFactor(new Vector4f(v, v, v, v));
	            }
	        });
		}
		{
			textBrightness = new JTextField();
			textBrightness.setBorder(null);
			textBrightness.setBackground(UIManager.getColor("Button.background"));
			GridBagConstraints gbc_textBrightness = new GridBagConstraints();
			gbc_textBrightness.anchor = GridBagConstraints.WEST;
			gbc_textBrightness.insets = new Insets(0, 0, 5, 0);
			gbc_textBrightness.gridx = 3;
			gbc_textBrightness.gridy = 2;
			contentPanel.add(textBrightness, gbc_textBrightness);
			textBrightness.setColumns(6);
			textBrightness.setEditable(false);
		}
		
		{
			JLabel lblBloom = new JLabel("Bloom");
			GridBagConstraints gbc_lblBloom = new GridBagConstraints();
			gbc_lblBloom.fill = GridBagConstraints.HORIZONTAL;
			gbc_lblBloom.insets = new Insets(0, 0, 5, 5);
			gbc_lblBloom.gridx = 1;
			gbc_lblBloom.gridy = 3;
			contentPanel.add(lblBloom, gbc_lblBloom);
		}
		{
			final JSlider slideBloom = new JSlider();
			slideBloom.setMajorTickSpacing(20);
			slideBloom.setMinorTickSpacing(1);
			slideBloom.setValue(10);
			slideBloom.setMaximum(200);
			slideBloom.setSnapToTicks(true);
			GridBagConstraints gbc_slideBloom = new GridBagConstraints();
			gbc_slideBloom.anchor = GridBagConstraints.NORTH;
			gbc_slideBloom.fill = GridBagConstraints.HORIZONTAL;
			gbc_slideBloom.insets = new Insets(0, 0, 5, 5);
			gbc_slideBloom.gridx = 2;
			gbc_slideBloom.gridy = 3;
			contentPanel.add(slideBloom, gbc_slideBloom);
			slideBloom.addChangeListener(new ChangeListener(){
	            public void stateChanged(ChangeEvent e) {
	            	main.TerrainMain.setBloomFactor(((float) slideBloom.getValue()) / 10.0f);
	            }
	        });
		}
		{
			textBloom = new JTextField();
			textBloom.setBorder(null);
			textBloom.setEditable(false);
			textBloom.setBackground(UIManager.getColor("Button.background"));
			GridBagConstraints gbc_textBloom = new GridBagConstraints();
			gbc_textBloom.anchor = GridBagConstraints.WEST;
			gbc_textBloom.insets = new Insets(0, 0, 5, 0);
			gbc_textBloom.gridx = 3;
			gbc_textBloom.gridy = 3;
			contentPanel.add(textBloom, gbc_textBloom);
			textBloom.setColumns(6);
		}
		
		// Checkboxes
		{
			JCheckBox chckbxLightRotation = new JCheckBox("Light Rotation");
			chckbxLightRotation.setIconTextGap(10);
			GridBagConstraints gbc_chckbxLightRotation = new GridBagConstraints();
			gbc_chckbxLightRotation.anchor = GridBagConstraints.NORTH;
			gbc_chckbxLightRotation.fill = GridBagConstraints.HORIZONTAL;
			gbc_chckbxLightRotation.insets = new Insets(0, 0, 5, 5);
			gbc_chckbxLightRotation.gridx = 2;
			gbc_chckbxLightRotation.gridy = 5;
			contentPanel.add(chckbxLightRotation, gbc_chckbxLightRotation);
			chckbxLightRotation.addItemListener(new ItemListener() {
			    public void itemStateChanged(ItemEvent e) {
			    	boolean temp;
			    	if (e.getStateChange() == 1)
			    		temp = true;
			    	else
			    		temp = false;
			        main.TerrainMain.setRotatelight(temp);
			    }
			});
			chckbxLightRotation.setSelected(main.TerrainMain.isRotatelight());
		}
		{
			JSeparator separator = new JSeparator();
			GridBagConstraints gbc_separator = new GridBagConstraints();
			gbc_separator.anchor = GridBagConstraints.NORTH;
			gbc_separator.fill = GridBagConstraints.HORIZONTAL;
			gbc_separator.insets = new Insets(0, 0, 5, 5);
			gbc_separator.gridx = 2;
			gbc_separator.gridy = 6;
			contentPanel.add(separator, gbc_separator);
		}
		
		{
			JCheckBox chckbxBloom = new JCheckBox("Bloom");
			chckbxBloom.setIconTextGap(10);
			GridBagConstraints gbc_chckbxBloom = new GridBagConstraints();
			gbc_chckbxBloom.anchor = GridBagConstraints.NORTH;
			gbc_chckbxBloom.fill = GridBagConstraints.HORIZONTAL;
			gbc_chckbxBloom.insets = new Insets(0, 0, 5, 5);
			gbc_chckbxBloom.gridx = 2;
			gbc_chckbxBloom.gridy = 7;
			contentPanel.add(chckbxBloom, gbc_chckbxBloom);
			chckbxBloom.addItemListener(new ItemListener() {
			    public void itemStateChanged(ItemEvent e) {
			    	boolean temp;
			    	if (e.getStateChange() == 1)
			    		temp = true;
			    	else
			    		temp = false;
			        main.TerrainMain.setBloom(temp);
			    }
			});
			chckbxBloom.setSelected(main.TerrainMain.isBloom());
		}
		
		{
			JCheckBox chckbxToneMapping = new JCheckBox("Tone Mapping");
			chckbxToneMapping.setIconTextGap(10);
			GridBagConstraints gbc_chckbxToneMapping = new GridBagConstraints();
			gbc_chckbxToneMapping.anchor = GridBagConstraints.NORTH;
			gbc_chckbxToneMapping.fill = GridBagConstraints.HORIZONTAL;
			gbc_chckbxToneMapping.insets = new Insets(0, 0, 5, 5);
			gbc_chckbxToneMapping.gridx = 2;
			gbc_chckbxToneMapping.gridy = 8;
			contentPanel.add(chckbxToneMapping, gbc_chckbxToneMapping);
			chckbxToneMapping.addItemListener(new ItemListener() {
			    public void itemStateChanged(ItemEvent e) {
			    	boolean temp;
			    	if (e.getStateChange() == 1)
			    		temp = true;
			    	else
			    		temp = false;
			        main.TerrainMain.setTonemapping(temp);
			    }
			});
			chckbxToneMapping.setSelected(main.TerrainMain.isTonemapping());
		}
		{
			JSeparator separator = new JSeparator();
			GridBagConstraints gbc_separator = new GridBagConstraints();
			gbc_separator.anchor = GridBagConstraints.NORTH;
			gbc_separator.fill = GridBagConstraints.HORIZONTAL;
			gbc_separator.insets = new Insets(0, 0, 5, 5);
			gbc_separator.gridx = 2;
			gbc_separator.gridy = 9;
			contentPanel.add(separator, gbc_separator);
		}
		
		{
			JCheckBox chckbxWireFrame = new JCheckBox("Wire Frame");
			chckbxWireFrame.setIconTextGap(10);
			GridBagConstraints gbc_chckbxWireFrame = new GridBagConstraints();
			gbc_chckbxWireFrame.anchor = GridBagConstraints.NORTH;
			gbc_chckbxWireFrame.fill = GridBagConstraints.HORIZONTAL;
			gbc_chckbxWireFrame.insets = new Insets(0, 0, 5, 5);
			gbc_chckbxWireFrame.gridx = 2;
			gbc_chckbxWireFrame.gridy = 10;
			contentPanel.add(chckbxWireFrame, gbc_chckbxWireFrame);
			chckbxWireFrame.addItemListener(new ItemListener() {
			    public void itemStateChanged(ItemEvent e) {
			    	boolean temp;
			    	if (e.getStateChange() == 1)
			    		temp = true;
			    	else
			    		temp = false;
			        main.TerrainMain.setWireframe(temp);
			    }
			});
			chckbxWireFrame.setSelected(main.TerrainMain.isWireframe());
		}
		
		{
			JCheckBox chckbxCulling = new JCheckBox("Culling");
			chckbxCulling.setIconTextGap(10);
			GridBagConstraints gbc_chckbxCulling = new GridBagConstraints();
			gbc_chckbxCulling.anchor = GridBagConstraints.NORTH;
			gbc_chckbxCulling.fill = GridBagConstraints.HORIZONTAL;
			gbc_chckbxCulling.insets = new Insets(0, 0, 0, 5);
			gbc_chckbxCulling.gridx = 2;
			gbc_chckbxCulling.gridy = 11;
			contentPanel.add(chckbxCulling, gbc_chckbxCulling);
			chckbxCulling.addItemListener(new ItemListener() {
			    public void itemStateChanged(ItemEvent e) {
			    	boolean temp;
			    	if (e.getStateChange() == 1)
			    		temp = true;
			    	else
			    		temp = false;
			        main.TerrainMain.setCulling(temp);
			    }
			});
			chckbxCulling.setSelected(main.TerrainMain.isCulling());
		}
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener(new ActionListener() {		        	 
		            public void actionPerformed(ActionEvent e)
		            {
		                close();
		            }
		        });
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		this.run();
	}
	
	public void close() {
		this.dispose();
	}

}
