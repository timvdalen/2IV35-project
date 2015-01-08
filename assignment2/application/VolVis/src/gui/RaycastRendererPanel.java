/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.BorderLayout;
import javax.swing.DefaultComboBoxModel;
import volvis.RaycastRenderer;

/**
 *
 * @author michel
 */
public class RaycastRendererPanel extends javax.swing.JPanel {

    RaycastRenderer renderer;
    TransferFunctionEditor tfEditor;
	
    /**
     * Creates new form RaycastRendererPanel
     */
    public RaycastRendererPanel(RaycastRenderer renderer) {
        initComponents();
        this.renderer = renderer;
        this.tfPanel.setLayout(new BorderLayout());
    }

    public void setSpeedLabel(String text) {
        renderingSpeedLabel.setText(text);
    }
    
    public void setTransferFunctionEditor(TransferFunctionEditor ed) {
        if (tfEditor != null) {
            tfPanel.remove(tfEditor);
        }
        tfEditor = ed;
        tfPanel.add(ed, BorderLayout.CENTER);
        tfPanel.repaint();
        repaint();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        renderingSpeedLabel = new javax.swing.JLabel();
        tfPanel = new javax.swing.JPanel();
        chckQuality = new javax.swing.JCheckBox();
        chckBackground = new javax.swing.JCheckBox();
        cmbRenderer = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        chckInterpolate = new javax.swing.JCheckBox();

        jLabel1.setText("Rendering time (ms):");

        renderingSpeedLabel.setText("jLabel2");

        javax.swing.GroupLayout tfPanelLayout = new javax.swing.GroupLayout(tfPanel);
        tfPanel.setLayout(tfPanelLayout);
        tfPanelLayout.setHorizontalGroup(
            tfPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 489, Short.MAX_VALUE)
        );
        tfPanelLayout.setVerticalGroup(
            tfPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 434, Short.MAX_VALUE)
        );

        chckQuality.setText("Force quality");
        chckQuality.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chckQualityStateChanged(evt);
            }
        });

        chckBackground.setSelected(true);
        chckBackground.setText("Calculate in background");
        chckBackground.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chckBackgroundStateChanged(evt);
            }
        });

        cmbRenderer.setModel(new DefaultComboBoxModel(RaycastRenderer.RaycastMethod.values()));
		cmbRenderer.setSelectedIndex(1);
        cmbRenderer.setToolTipText("");
        cmbRenderer.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbRendererItemStateChanged(evt);
            }
        });

        jLabel2.setText("Rendering method:");

        chckInterpolate.setSelected(true);
        chckInterpolate.setText("Interpolate");
        chckInterpolate.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chckInterpolateStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tfPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chckInterpolate)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(renderingSpeedLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chckQuality, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbRenderer, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chckBackground, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(renderingSpeedLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chckQuality)
                    .addComponent(chckBackground))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbRenderer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chckInterpolate)
                .addGap(18, 18, 18)
                .addComponent(tfPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chckQualityStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chckQualityStateChanged
        this.renderer.forceQuality(((javax.swing.JCheckBox) evt.getSource()).isSelected());
    }//GEN-LAST:event_chckQualityStateChanged

    private void chckBackgroundStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chckBackgroundStateChanged
        this.renderer.runInBackground(((javax.swing.JCheckBox) evt.getSource()).isSelected());
    }//GEN-LAST:event_chckBackgroundStateChanged

    private void chckInterpolateStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chckInterpolateStateChanged
        this.renderer.setInterpolate(((javax.swing.JCheckBox) evt.getSource()).isSelected());
    }//GEN-LAST:event_chckInterpolateStateChanged

    private void cmbRendererItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbRendererItemStateChanged
        this.renderer.setRaycastMethod((RaycastRenderer.RaycastMethod) ((javax.swing.JComboBox<RaycastRenderer.RaycastMethod>) evt.getSource()).getSelectedItem());
    }//GEN-LAST:event_cmbRendererItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chckBackground;
    private javax.swing.JCheckBox chckInterpolate;
    private javax.swing.JCheckBox chckQuality;
    private javax.swing.JComboBox cmbRenderer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel renderingSpeedLabel;
    private javax.swing.JPanel tfPanel;
    // End of variables declaration//GEN-END:variables
}