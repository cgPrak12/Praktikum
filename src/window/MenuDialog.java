package window;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JCheckBox;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import org.lwjgl.util.vector.Vector4f;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.ItemSelectable;


public class MenuDialog extends JDialog {

	//strings for check boxes (split screen)
	private static final String ENLIGHTENED = 		"Enlightened";
	private static final String TONEMAPPEDBLOOMED = "Tone Mapped Bloomed";
	private static final String TONEMAPPED = 		"Tone Mapped";
	private static final String BLURED = 			"Blured";
	private static final String BLOOMED = 			"Bloomed";
	private static final String BRIGHTMAP = 		"Bright Map";
	private static final String SHADOWMAP = 		"Shadow Map";
	
	//consts for split screen view
	private static final int TOPLEFT = 		0;
	private static final int TOPRIGHT = 	1;
	private static final int BOTTOMLEFT = 	2;
	private static final int BOTTOMRIGHT = 	3;
	
	// Singleton object
	private static MenuDialog mDial;
	private static final long serialVersionUID = 1L;
    private final JTabbedPane tabs = new JTabbedPane();
	private final JPanel graphSettingsPanel = new JPanel();
	private final JPanel splitScreenPanel = new JPanel();
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
	@SuppressWarnings("unchecked")
	private MenuDialog() {
	    try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch (Exception e) { }
		
		setResizable(false);
		 
        Action closeAction = new AbstractAction(){
			private static final long serialVersionUID = 2L;
			public void actionPerformed(ActionEvent e) {
                destroyInstance();
            }
        };

        //***********************
        // Graphic Settings
        //***********************
        
	    graphSettingsPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeAction");
	    graphSettingsPanel.getActionMap().put("closeAction", closeAction);
	    
		setTitle("Graphic Settings");
		setBounds(100, 100, 425, 520);
		getContentPane().setLayout(new BorderLayout());
		graphSettingsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(graphSettingsPanel, BorderLayout.CENTER);
		
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{36, 50, 200, 54, 0};
		gbl_contentPanel.rowHeights = new int[]{36, 23, 23, 23, 31, 23, 2, 23, 23, 23, 23, 23, 23, 2, 23, 23, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		
		graphSettingsPanel.setLayout(gbl_contentPanel);
		
		// Slider and label
		{
			JLabel lblExposure = new JLabel("Exposure");
			GridBagConstraints gbc_lblExposure = new GridBagConstraints();
			gbc_lblExposure.fill = GridBagConstraints.HORIZONTAL;
			gbc_lblExposure.insets = new Insets(0, 0, 5, 5);
			gbc_lblExposure.gridx = 1;
			gbc_lblExposure.gridy = 1;
			graphSettingsPanel.add(lblExposure, gbc_lblExposure);
		}
		
		// Exposure Slider
		{
			final JSlider slideExposure = new JSlider();
			slideExposure.setValue((int) (main.TerrainMain.getExposure() * 100.0f));
			slideExposure.setSnapToTicks(true);
			slideExposure.setMajorTickSpacing(20);
			slideExposure.setMinorTickSpacing(1);
			slideExposure.setMaximum(200);
			GridBagConstraints gbc_slideExposure = new GridBagConstraints();
			gbc_slideExposure.anchor = GridBagConstraints.NORTH;
			gbc_slideExposure.fill = GridBagConstraints.HORIZONTAL;
			gbc_slideExposure.insets = new Insets(0, 0, 5, 5);
			gbc_slideExposure.gridx = 2;
			gbc_slideExposure.gridy = 1;
			graphSettingsPanel.add(slideExposure, gbc_slideExposure);
			slideExposure.addChangeListener(new ChangeListener(){
	            public void stateChanged(ChangeEvent e) {
	            	float newValue = ((float) slideExposure.getValue()) / 100.0f;
	            	main.TerrainMain.setExposure(newValue);
	    			String txt = Float.toString(newValue);
	    			textExposure.setText(txt);	            	
	            }
	        });
		}
		{
			textExposure = new JTextField(5);
			textExposure.setBorder(null);
			textExposure.setBackground(UIManager.getColor("Button.background"));
			GridBagConstraints gbc_textExposure = new GridBagConstraints();
			gbc_textExposure.anchor = GridBagConstraints.EAST;
			gbc_textExposure.fill = GridBagConstraints.HORIZONTAL;
			gbc_textExposure.insets = new Insets(0, 0, 5, 0);
			gbc_textExposure.gridx = 3;
			gbc_textExposure.gridy = 1;
			graphSettingsPanel.add(textExposure, gbc_textExposure);
			textExposure.setEditable(false);
			String txt = Float.toString(main.TerrainMain.getExposure());
			textExposure.setText(txt);
		}
		
		// Brightness slider
		{
			JLabel lblBrightness = new JLabel("Brightness");
			GridBagConstraints gbc_lblBrightness = new GridBagConstraints();
			gbc_lblBrightness.anchor = GridBagConstraints.WEST;
			gbc_lblBrightness.insets = new Insets(0, 0, 5, 5);
			gbc_lblBrightness.gridx = 1;
			gbc_lblBrightness.gridy = 2;
			graphSettingsPanel.add(lblBrightness, gbc_lblBrightness);
		}
		{
			final JSlider slideBrightness = new JSlider();
			slideBrightness.setValue((int) (main.TerrainMain.getBrightnessFactor().x * 100.0f));
			slideBrightness.setSnapToTicks(true);
			slideBrightness.setMajorTickSpacing(20);
			slideBrightness.setMinorTickSpacing(1);
			slideBrightness.setMaximum(200);
			GridBagConstraints gbc_slideBrightness = new GridBagConstraints();
			gbc_slideBrightness.anchor = GridBagConstraints.NORTH;
			gbc_slideBrightness.fill = GridBagConstraints.HORIZONTAL;
			gbc_slideBrightness.insets = new Insets(0, 0, 5, 5);
			gbc_slideBrightness.gridx = 2;
			gbc_slideBrightness.gridy = 2;
			graphSettingsPanel.add(slideBrightness, gbc_slideBrightness);
			slideBrightness.addChangeListener(new ChangeListener(){
	            public void stateChanged(ChangeEvent e) {
	            	float newValue = ((float) slideBrightness.getValue()) / 100.0f;
	            	main.TerrainMain.setBrightnessFactor(new Vector4f(newValue, newValue, newValue, newValue));
	    			String txt = Float.toString(newValue);
	    			textBrightness.setText(txt);
	            }
	        });
		}
		{
			textBrightness = new JTextField(5);
			textBrightness.setBorder(null);
			textBrightness.setBackground(UIManager.getColor("Button.background"));
			GridBagConstraints gbc_textBrightness = new GridBagConstraints();
			gbc_textBrightness.anchor = GridBagConstraints.EAST;
			gbc_textBrightness.fill = GridBagConstraints.HORIZONTAL;
			gbc_textBrightness.insets = new Insets(0, 0, 5, 0);
			gbc_textBrightness.gridx = 3;
			gbc_textBrightness.gridy = 2;
			graphSettingsPanel.add(textBrightness, gbc_textBrightness);
			textBrightness.setEditable(false);
			String txt = Float.toString(main.TerrainMain.getBrightnessFactor().x);
			textBrightness.setText(txt);
		}
		
		
		// Bloom slider
		{
			JLabel lblBloom = new JLabel("Bloom");
			GridBagConstraints gbc_lblBloom = new GridBagConstraints();
			gbc_lblBloom.fill = GridBagConstraints.HORIZONTAL;
			gbc_lblBloom.insets = new Insets(0, 0, 5, 5);
			gbc_lblBloom.gridx = 1;
			gbc_lblBloom.gridy = 3;
			graphSettingsPanel.add(lblBloom, gbc_lblBloom);
		}
		{
			final JSlider slideBloom = new JSlider();
			slideBloom.setValue((int) (main.TerrainMain.getBloomFactor() * 100.0f));
			slideBloom.setMajorTickSpacing(20);
			slideBloom.setMinorTickSpacing(1);
			slideBloom.setMaximum(200);
			slideBloom.setSnapToTicks(true);
			GridBagConstraints gbc_slideBloom = new GridBagConstraints();
			gbc_slideBloom.anchor = GridBagConstraints.NORTH;
			gbc_slideBloom.fill = GridBagConstraints.HORIZONTAL;
			gbc_slideBloom.insets = new Insets(0, 0, 5, 5);
			gbc_slideBloom.gridx = 2;
			gbc_slideBloom.gridy = 3;
			graphSettingsPanel.add(slideBloom, gbc_slideBloom);
			slideBloom.addChangeListener(new ChangeListener(){
	            public void stateChanged(ChangeEvent e) {
	            	float newValue = ((float) slideBloom.getValue()) / 100.0f;
	            	main.TerrainMain.setBloomFactor(newValue);
	    			String txt = Float.toString(newValue);
	    			textBloom.setText(txt);
	            }
	        });
		}
		{
			textBloom = new JTextField(5);
			textBloom.setBorder(null);
			textBloom.setEditable(false);
			textBloom.setBackground(UIManager.getColor("Button.background"));
			GridBagConstraints gbc_textBloom = new GridBagConstraints();
			gbc_textBloom.anchor = GridBagConstraints.EAST;
			gbc_textBloom.fill = GridBagConstraints.HORIZONTAL;
			gbc_textBloom.insets = new Insets(0, 0, 5, 0);
			gbc_textBloom.gridx = 3;
			gbc_textBloom.gridy = 3;
			graphSettingsPanel.add(textBloom, gbc_textBloom);
			String txt = Float.toString(main.TerrainMain.getBloomFactor());
			textBloom.setText(txt);
		}
		
		// Checkboxes
		{
			JCheckBox chckbxLightRotation = new JCheckBox("Animation");
			chckbxLightRotation.setIconTextGap(10);
			GridBagConstraints gbc_chckbxLightRotation = new GridBagConstraints();
			gbc_chckbxLightRotation.anchor = GridBagConstraints.NORTH;
			gbc_chckbxLightRotation.fill = GridBagConstraints.HORIZONTAL;
			gbc_chckbxLightRotation.insets = new Insets(0, 0, 5, 5);
			gbc_chckbxLightRotation.gridx = 2;
			gbc_chckbxLightRotation.gridy = 5;
			graphSettingsPanel.add(chckbxLightRotation, gbc_chckbxLightRotation);
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
			graphSettingsPanel.add(separator, gbc_separator);
		}
		
		{
			JCheckBox chckbxToneMapping = new JCheckBox("Tone Mapping");
			chckbxToneMapping.setIconTextGap(10);
			GridBagConstraints gbc_chckbxToneMapping = new GridBagConstraints();
			gbc_chckbxToneMapping.anchor = GridBagConstraints.NORTH;
			gbc_chckbxToneMapping.fill = GridBagConstraints.HORIZONTAL;
			gbc_chckbxToneMapping.insets = new Insets(0, 0, 5, 5);
			gbc_chckbxToneMapping.gridx = 2;
			gbc_chckbxToneMapping.gridy = 7;
			graphSettingsPanel.add(chckbxToneMapping, gbc_chckbxToneMapping);
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
			JCheckBox chckbxBloom = new JCheckBox("Bloom");
			chckbxBloom.setIconTextGap(10);
			GridBagConstraints gbc_chckbxBloom = new GridBagConstraints();
			gbc_chckbxBloom.anchor = GridBagConstraints.NORTH;
			gbc_chckbxBloom.fill = GridBagConstraints.HORIZONTAL;
			gbc_chckbxBloom.insets = new Insets(0, 0, 5, 5);
			gbc_chckbxBloom.gridx = 2;
			gbc_chckbxBloom.gridy = 8;
			graphSettingsPanel.add(chckbxBloom, gbc_chckbxBloom);
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
			JCheckBox chckbxAmbientOcclusion = new JCheckBox("Ambient Occlusion");
			chckbxAmbientOcclusion.setIconTextGap(10);
			GridBagConstraints gbc_chckbxGlareFading = new GridBagConstraints();
			gbc_chckbxGlareFading.anchor = GridBagConstraints.WEST;
			gbc_chckbxGlareFading.insets = new Insets(0, 0, 5, 5);
			gbc_chckbxGlareFading.gridx = 2;
			gbc_chckbxGlareFading.gridy = 9;
			graphSettingsPanel.add(chckbxAmbientOcclusion, gbc_chckbxGlareFading);
			chckbxAmbientOcclusion.addItemListener(new ItemListener() {
			    public void itemStateChanged(ItemEvent e) {
			    	boolean temp;
			    	if (e.getStateChange() == 1)
			    		temp = true;
			    	else
			    		temp = false;
			        main.TerrainMain.setAmbientocclusion(temp);
			    }
			});
			chckbxAmbientOcclusion.setSelected(main.TerrainMain.isAmbientocclusion());
		}
		{
			JCheckBox chckbxNormalMapping = new JCheckBox("Normal Mapping");
			chckbxNormalMapping.setIconTextGap(10);
			GridBagConstraints gbc_chckbxNormalMapping = new GridBagConstraints();
			gbc_chckbxNormalMapping.anchor = GridBagConstraints.WEST;
			gbc_chckbxNormalMapping.insets = new Insets(0, 0, 5, 5);
			gbc_chckbxNormalMapping.gridx = 2;
			gbc_chckbxNormalMapping.gridy = 10;
			graphSettingsPanel.add(chckbxNormalMapping, gbc_chckbxNormalMapping);
			chckbxNormalMapping.addItemListener(new ItemListener() {
			    public void itemStateChanged(ItemEvent e) {
			    	boolean temp;
			    	if (e.getStateChange() == 1)
			    		temp = true;
			    	else
			    		temp = false;
			        main.TerrainMain.setNormalmapping(temp);
			    }
			});
			chckbxNormalMapping.setSelected(main.TerrainMain.isNormalmapping());
		}
		{
			JCheckBox chckbxGodRays = new JCheckBox("God Rays");
			chckbxGodRays.setIconTextGap(10);
			GridBagConstraints gbc_chckbxGodRays = new GridBagConstraints();
			gbc_chckbxGodRays.anchor = GridBagConstraints.WEST;
			gbc_chckbxGodRays.insets = new Insets(0, 0, 5, 5);
			gbc_chckbxGodRays.gridx = 2;
			gbc_chckbxGodRays.gridy = 11;
			graphSettingsPanel.add(chckbxGodRays, gbc_chckbxGodRays);
			chckbxGodRays.addItemListener(new ItemListener() {
			    public void itemStateChanged(ItemEvent e) {
			    	boolean temp;
			    	if (e.getStateChange() == 1)
			    		temp = true;
			    	else
			    		temp = false;
			        main.TerrainMain.setGodRays(temp);
			    }
			});
			chckbxGodRays.setSelected(main.TerrainMain.isGodRays());
		}
		{
			JCheckBox chckbxShadows = new JCheckBox("Shadows");
			chckbxShadows.setIconTextGap(10);
			GridBagConstraints gbc_chckbxShadows = new GridBagConstraints();
			gbc_chckbxShadows.anchor = GridBagConstraints.WEST;
			gbc_chckbxShadows.insets = new Insets(0, 0, 5, 5);
			gbc_chckbxShadows.gridx = 2;
			gbc_chckbxShadows.gridy = 12;
			graphSettingsPanel.add(chckbxShadows, gbc_chckbxShadows);
			chckbxShadows.addItemListener(new ItemListener() {
			    public void itemStateChanged(ItemEvent e) {
			    	boolean temp;
			    	if (e.getStateChange() == 1)
			    		temp = true;
			    	else
			    		temp = false;
			        main.TerrainMain.setShadows(temp);
			    }
			});
			chckbxShadows.setSelected(main.TerrainMain.isShadows());
		}
		
		{
			JSeparator separator = new JSeparator();
			GridBagConstraints gbc_separator = new GridBagConstraints();
			gbc_separator.anchor = GridBagConstraints.NORTH;
			gbc_separator.fill = GridBagConstraints.HORIZONTAL;
			gbc_separator.insets = new Insets(0, 0, 5, 5);
			gbc_separator.gridx = 2;
			gbc_separator.gridy = 13;
			graphSettingsPanel.add(separator, gbc_separator);
		}		
		{
			JCheckBox chckbxCulling = new JCheckBox("Culling");
			chckbxCulling.setIconTextGap(10);
			GridBagConstraints gbc_chckbxCulling = new GridBagConstraints();
			gbc_chckbxCulling.anchor = GridBagConstraints.NORTH;
			gbc_chckbxCulling.fill = GridBagConstraints.HORIZONTAL;
			gbc_chckbxCulling.insets = new Insets(0, 0, 5, 5);
			gbc_chckbxCulling.gridx = 2;
			gbc_chckbxCulling.gridy = 14;
			graphSettingsPanel.add(chckbxCulling, gbc_chckbxCulling);
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
			JCheckBox chckbxWireFrame = new JCheckBox("Wire Frame");
			chckbxWireFrame.setIconTextGap(10);
			GridBagConstraints gbc_chckbxWireFrame = new GridBagConstraints();
			gbc_chckbxWireFrame.anchor = GridBagConstraints.NORTH;
			gbc_chckbxWireFrame.fill = GridBagConstraints.HORIZONTAL;
			gbc_chckbxWireFrame.insets = new Insets(0, 0, 5, 5);
			gbc_chckbxWireFrame.gridx = 2;
			gbc_chckbxWireFrame.gridy = 15;
			graphSettingsPanel.add(chckbxWireFrame, gbc_chckbxWireFrame);
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
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setSize(new Dimension(75, 23));
				okButton.setPreferredSize(new Dimension(75, 23));
				okButton.setMinimumSize(new Dimension(75, 23));
				okButton.setMaximumSize(new Dimension(75, 23));
				okButton.setActionCommand("OK");
				okButton.addActionListener(new ActionListener() {		        	 
		            public void actionPerformed(ActionEvent e)
		            {
		                destroyInstance();
		            }
		        });
				buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
        tabs.addTab("Graphic Settings", graphSettingsPanel);
		
        
		// *****************
		// Split Screen Tab
		// *****************
		
        splitScreenPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeAction");
        splitScreenPanel.getActionMap().put("closeAction", closeAction);
        
        tabs.addTab("SplitScreen", splitScreenPanel);
        
		GridBagLayout gbl_splitScreen = new GridBagLayout();
		gbl_splitScreen.columnWidths = new int[]{36, 50, 200, 54, 0};
		gbl_splitScreen.rowHeights = new int[]{36, 23, 23, 23, 31, 23, 2, 23, 23, 2, 0, 0, 0, 23, 0, 23, 0, 0, 0};
		gbl_splitScreen.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_splitScreen.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		
		splitScreenPanel.setLayout(gbl_splitScreen);
		
		{
			JCheckBox chckbxEnableSplitScreen = new JCheckBox("Enable Split Screen");
			chckbxEnableSplitScreen.setIconTextGap(10);
			GridBagConstraints gbc_splitScreen = new GridBagConstraints();
			gbc_splitScreen.anchor = GridBagConstraints.NORTH;
			gbc_splitScreen.fill = GridBagConstraints.HORIZONTAL;
			gbc_splitScreen.insets = new Insets(0, 0, 5, 5);
			gbc_splitScreen.gridx = 2;
			gbc_splitScreen.gridy = 1;
			splitScreenPanel.add(chckbxEnableSplitScreen, gbc_splitScreen);
			chckbxEnableSplitScreen.addItemListener(new ItemListener() {
			    public void itemStateChanged(ItemEvent e) {
			    	boolean temp;
			    	if (e.getStateChange() == 1)
			    		temp = true;
			    	else
			    		temp = false;
			        main.TerrainMain.setSplitScreen(temp);
			    }
			});
			chckbxEnableSplitScreen.setSelected(main.TerrainMain.isSplitScreen());
		}
		
		String[] cbxStrings = { ENLIGHTENED, TONEMAPPEDBLOOMED, TONEMAPPED, BLURED, BLOOMED, BRIGHTMAP, SHADOWMAP };
		
		{
			JLabel lblTopLeft = new JLabel("Top Left:");
			GridBagConstraints gbc_lblTopLeft = new GridBagConstraints();
			gbc_lblTopLeft.fill = GridBagConstraints.HORIZONTAL;
			gbc_lblTopLeft.insets = new Insets(0, 0, 5, 5);
			gbc_lblTopLeft.gridx = 1;
			gbc_lblTopLeft.gridy = 5;
			splitScreenPanel.add(lblTopLeft, gbc_lblTopLeft);
		}
		
		// combo boxes	
		{
			@SuppressWarnings("rawtypes")
			JComboBox cbxTopLeft = new JComboBox(cbxStrings);
			GridBagConstraints gbc_cbxStrings = new GridBagConstraints();
			gbc_cbxStrings.anchor = GridBagConstraints.NORTH;
			gbc_cbxStrings.fill = GridBagConstraints.HORIZONTAL;
			gbc_cbxStrings.insets = new Insets(0, 0, 5, 5);
			gbc_cbxStrings.gridx = 2;
			gbc_cbxStrings.gridy = 5;
			cbxTopLeft.setSelectedIndex(main.TerrainMain.getSplitScreenVal(TOPLEFT));
			splitScreenPanel.add(cbxTopLeft, gbc_cbxStrings);
			ActionListener actionListener = new ActionListener() {
			      public void actionPerformed(ActionEvent actionEvent) {
			        ItemSelectable is = (ItemSelectable)actionEvent.getSource();
			        int val = string2Value(selectedString(is));
			        main.TerrainMain.setSplitScreenVal(val, TOPLEFT);
			      }
			};
			cbxTopLeft.addActionListener(actionListener);
		}

		{
			JLabel lblTopRight = new JLabel("Top Right:");
			GridBagConstraints gbc_lblTopRight = new GridBagConstraints();
			gbc_lblTopRight.fill = GridBagConstraints.HORIZONTAL;
			gbc_lblTopRight.insets = new Insets(0, 0, 5, 5);
			gbc_lblTopRight.gridx = 1;
			gbc_lblTopRight.gridy = 6;
			splitScreenPanel.add(lblTopRight, gbc_lblTopRight);
		}
        
		{
			@SuppressWarnings("rawtypes")
			JComboBox cbxTopRight = new JComboBox(cbxStrings);
			GridBagConstraints gbc_cbxStrings = new GridBagConstraints();
			gbc_cbxStrings.anchor = GridBagConstraints.NORTH;
			gbc_cbxStrings.fill = GridBagConstraints.HORIZONTAL;
			gbc_cbxStrings.insets = new Insets(0, 0, 5, 5);
			gbc_cbxStrings.gridx = 2;
			gbc_cbxStrings.gridy = 6;
			cbxTopRight.setSelectedIndex(main.TerrainMain.getSplitScreenVal(TOPRIGHT));
			splitScreenPanel.add(cbxTopRight, gbc_cbxStrings);
			ActionListener actionListener = new ActionListener() {
			      public void actionPerformed(ActionEvent actionEvent) {
			        ItemSelectable is = (ItemSelectable)actionEvent.getSource();
			        int val = string2Value(selectedString(is));
			        main.TerrainMain.setSplitScreenVal(val, TOPRIGHT);
			      }
			};
			cbxTopRight.addActionListener(actionListener);
		}
		
		{
			JLabel lblBottomLeft = new JLabel("Bottom Left:");
			GridBagConstraints gbc_lblBottomLeft = new GridBagConstraints();
			gbc_lblBottomLeft.fill = GridBagConstraints.HORIZONTAL;
			gbc_lblBottomLeft.insets = new Insets(0, 0, 5, 5);
			gbc_lblBottomLeft.gridx = 1;
			gbc_lblBottomLeft.gridy = 8;
			splitScreenPanel.add(lblBottomLeft, gbc_lblBottomLeft);
		}
        
		{
			@SuppressWarnings("rawtypes")
			JComboBox cbxBottomLeft = new JComboBox(cbxStrings);
			GridBagConstraints gbc_cbxStrings = new GridBagConstraints();
			gbc_cbxStrings.anchor = GridBagConstraints.NORTH;
			gbc_cbxStrings.fill = GridBagConstraints.HORIZONTAL;
			gbc_cbxStrings.insets = new Insets(0, 0, 5, 5);
			gbc_cbxStrings.gridx = 2;
			gbc_cbxStrings.gridy = 8;
			cbxBottomLeft.setSelectedIndex(main.TerrainMain.getSplitScreenVal(BOTTOMLEFT));
			splitScreenPanel.add(cbxBottomLeft, gbc_cbxStrings);
			ActionListener actionListener = new ActionListener() {
			      public void actionPerformed(ActionEvent actionEvent) {
			        ItemSelectable is = (ItemSelectable)actionEvent.getSource();
			        int val = string2Value(selectedString(is));
			        main.TerrainMain.setSplitScreenVal(val, BOTTOMLEFT);
			      }
			};
			cbxBottomLeft.addActionListener(actionListener);
		}
		
		{
			JLabel lblBottomRight = new JLabel("Bottom Right:");
			GridBagConstraints gbc_lblBottomRight = new GridBagConstraints();
			gbc_lblBottomRight.fill = GridBagConstraints.HORIZONTAL;
			gbc_lblBottomRight.insets = new Insets(0, 0, 5, 5);
			gbc_lblBottomRight.gridx = 1;
			gbc_lblBottomRight.gridy = 9;
			splitScreenPanel.add(lblBottomRight, gbc_lblBottomRight);
		}
        
		{
			@SuppressWarnings("rawtypes")
			JComboBox cbxBottomRight = new JComboBox(cbxStrings);
			GridBagConstraints gbc_cbxStrings = new GridBagConstraints();
			gbc_cbxStrings.anchor = GridBagConstraints.NORTH;
			gbc_cbxStrings.fill = GridBagConstraints.HORIZONTAL;
			gbc_cbxStrings.insets = new Insets(0, 0, 5, 5);
			gbc_cbxStrings.gridx = 2;
			gbc_cbxStrings.gridy = 9;
			cbxBottomRight.setSelectedIndex(main.TerrainMain.getSplitScreenVal(BOTTOMRIGHT));
			splitScreenPanel.add(cbxBottomRight, gbc_cbxStrings);
			ActionListener actionListener = new ActionListener() {
			      public void actionPerformed(ActionEvent actionEvent) {
			        ItemSelectable is = (ItemSelectable)actionEvent.getSource();
			        int val = string2Value(selectedString(is));
			        main.TerrainMain.setSplitScreenVal(val, BOTTOMRIGHT);
			      }
			};
			cbxBottomRight.addActionListener(actionListener);
		}
		
        this.add(tabs);
		this.run();
	}
	
	static private String selectedString(ItemSelectable is) {
		    Object selected[] = is.getSelectedObjects();
		    return ((selected.length == 0) ? "null" : (String)selected[0]);
	}
	
	static private int string2Value(String s) {
		
		switch (s) {
			case ENLIGHTENED: 		return 0; 
			case TONEMAPPEDBLOOMED: return 1; 
			case TONEMAPPED: 		return 2; 
			case BLURED: 			return 3;  
			case BLOOMED: 			return 4; 
			case BRIGHTMAP: 		return 5; 
			case SHADOWMAP: 		return 6; 
		}
		return 0;
	}
	
	public void close() {
		this.dispose();
		mDial = null;
	}
	
	/**
	 * @brief returns instance of the object if not already existing (singleton) 
	 */
	public static MenuDialog getInstance() {
		if(mDial != null) {
			mDial.toFront();
			return mDial;
		}
		else {
			mDial = new MenuDialog();
			return mDial;
		}
		
	}
	public static void destroyInstance() {
		if(mDial != null) {
			mDial.close();
		}
	}

}
