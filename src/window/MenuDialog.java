package window;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JCheckBox;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

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
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		{
			JLabel lblExposure = new JLabel("Exposure");
			contentPanel.add(lblExposure, "4, 4");
		}
		{
			JSlider slideExposure = new JSlider();
			slideExposure.setSnapToTicks(true);
			slideExposure.setMajorTickSpacing(20);
			slideExposure.setValue(10);
			slideExposure.setMinorTickSpacing(1);
			slideExposure.setMaximum(200);
			contentPanel.add(slideExposure, "6, 4, fill, default");
		}
		{
			textExposure = new JTextField();
			textExposure.setBackground(UIManager.getColor("Button.background"));
			contentPanel.add(textExposure, "8, 4, left, default");
			textExposure.setColumns(6);
			textExposure.setEditable(false);
			textExposure.setText("dummy");
		}
		{
			JLabel lblBrightness = new JLabel("Brightness");
			contentPanel.add(lblBrightness, "4, 6");
		}
		{
			JSlider slideBrightness = new JSlider();
			slideBrightness.setSnapToTicks(true);
			slideBrightness.setMajorTickSpacing(20);
			slideBrightness.setMinorTickSpacing(1);
			slideBrightness.setMaximum(200);
			slideBrightness.setValue(10);
			contentPanel.add(slideBrightness, "6, 6, fill, default");
		}
		{
			textBrightness = new JTextField();
			textBrightness.setBackground(UIManager.getColor("Button.background"));
			contentPanel.add(textBrightness, "8, 6, left, default");
			textBrightness.setColumns(6);
			textBrightness.setEditable(false);
		}
		{
			JLabel lblBloom = new JLabel("Bloom");
			contentPanel.add(lblBloom, "4, 8");
		}
		{
			JSlider slideBloom = new JSlider();
			slideBloom.setMajorTickSpacing(20);
			slideBloom.setMinorTickSpacing(1);
			slideBloom.setValue(10);
			slideBloom.setMaximum(200);
			slideBloom.setSnapToTicks(true);
			contentPanel.add(slideBloom, "6, 8, fill, default");
		}
		{
			textBloom = new JTextField();
			textBloom.setEditable(false);
			textBloom.setBackground(UIManager.getColor("Button.background"));
			contentPanel.add(textBloom, "8, 8, left, default");
			textBloom.setColumns(6);
		}
		{
			JCheckBox chckbxLightRotation = new JCheckBox("Light Rotation");
			contentPanel.add(chckbxLightRotation, "6, 12");
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
		}
		{
			JSeparator separator = new JSeparator();
			contentPanel.add(separator, "6, 14");
		}
		{
			JCheckBox chckbxBloom = new JCheckBox("Bloom");
			chckbxBloom.setSelected(true);
			contentPanel.add(chckbxBloom, "6, 16");
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
		}
		{
			JCheckBox chckbxToneMapping = new JCheckBox("Tone Mapping");
			chckbxToneMapping.setSelected(true);
			contentPanel.add(chckbxToneMapping, "6, 18");
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
		}
		{
			JSeparator separator = new JSeparator();
			contentPanel.add(separator, "6, 20");
		}
		{
			JCheckBox chckbxWireFrame = new JCheckBox("Wire Frame");
			contentPanel.add(chckbxWireFrame, "6, 22");
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
		}
		{
			JCheckBox chckbxCulling = new JCheckBox("Culling");
			chckbxCulling.setSelected(true);
			contentPanel.add(chckbxCulling, "6, 24");
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
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
//			{
//				JButton cancelButton = new JButton("Cancel");
//				cancelButton.setActionCommand("Cancel");
//				buttonPane.add(cancelButton);
//			}
		}
		this.run();
	}

}
