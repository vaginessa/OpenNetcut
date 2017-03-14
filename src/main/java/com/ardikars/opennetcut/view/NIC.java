/**
 * Copyright (C) 2017  Ardika Rommy Sanjaya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ardikars.opennetcut.view;

import static com.ardikars.jxnet.Jxnet.*;

import com.ardikars.jxnet.*;
import com.ardikars.jxnet.exception.JxnetException;
import com.ardikars.jxnet.util.AddrUtils;
import com.ardikars.opennetcut.app.LoggerHandler;
import com.ardikars.opennetcut.app.LoggerStatus;
import com.ardikars.opennetcut.app.StaticField;
import com.ardikars.opennetcut.app.Utils;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("unchecked")
public class NIC extends javax.swing.JFrame {

    public NIC() throws JxnetException {
        initComponents();
        setLocationRelativeTo(null);
        
        lbl_dev_name.setText(StaticField.SOURCE);
        SpinnerBufferSize.setValue(StaticField.TIMEOUT);
        SliderSnaplen.setValue(StaticField.SNAPLEN);
        lbl_snaplen.setText(String.valueOf(StaticField.SNAPLEN));
        cb_promisc.setSelected((StaticField.PROMISC == 1) ? true : false);
        refresh();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        NICPanel = new javax.swing.JPanel();
        NICSP = new javax.swing.JScrollPane();
        NICTable = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        cb_promisc = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        SpinnerBufferSize = new javax.swing.JSpinner();
        SliderSnaplen = new javax.swing.JSlider();
        btn_ok = new javax.swing.JButton();
        btn_cancel = new javax.swing.JButton();
        lbl_snaplen = new javax.swing.JLabel();
        lbl_dev_name = new javax.swing.JLabel();
        btn_refresh_devices = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Network Interface Card Configuration");
        setMinimumSize(new java.awt.Dimension(900, 350));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        NICTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        NICTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                NICTableMouseClicked(evt);
            }
        });
        NICSP.setViewportView(NICTable);

        javax.swing.GroupLayout NICPanelLayout = new javax.swing.GroupLayout(NICPanel);
        NICPanel.setLayout(NICPanelLayout);
        NICPanelLayout.setHorizontalGroup(
            NICPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NICPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(NICSP)
                .addContainerGap())
        );
        NICPanelLayout.setVerticalGroup(
            NICPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NICPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(NICSP, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                .addContainerGap())
        );

        cb_promisc.setText("Capture packets in promiscuous mode");

        jLabel2.setText("Limit packets");

        jLabel4.setText("Buffer size");

        jLabel3.setText("Name");

        SpinnerBufferSize.setModel(new javax.swing.SpinnerNumberModel(300, 0, 1800, 1));
        SpinnerBufferSize.setValue(25);

        SliderSnaplen.setMaximum(65535);
        SliderSnaplen.setValue(300);
        SliderSnaplen.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                SliderSnaplenStateChanged(evt);
            }
        });

        btn_ok.setText("Apply");
        btn_ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_okActionPerformed(evt);
            }
        });

        btn_cancel.setText("Close");
        btn_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelActionPerformed(evt);
            }
        });

        lbl_snaplen.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_snaplen.setText("65535");

        lbl_dev_name.setText("-");

        btn_refresh_devices.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/16x16/view-refresh.png"))); // NOI18N
        btn_refresh_devices.setText("Refresh");
        btn_refresh_devices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_refresh_devicesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(cb_promisc)
                    .addComponent(jLabel3))
                .addGap(27, 27, 27)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 110, Short.MAX_VALUE)
                        .addComponent(btn_refresh_devices, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_ok, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lbl_dev_name)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(SliderSnaplen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbl_snaplen, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(SpinnerBufferSize))
                        .addGap(12, 12, 12))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(lbl_dev_name))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SpinnerBufferSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SliderSnaplen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(lbl_snaplen))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cb_promisc)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_cancel)
                        .addComponent(btn_ok)
                        .addComponent(btn_refresh_devices)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(NICPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(NICPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void SliderSnaplenStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_SliderSnaplenStateChanged
        lbl_snaplen.setText(String.valueOf(SliderSnaplen.getValue()));
    }//GEN-LAST:event_SliderSnaplenStateChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        MainWindow.main_windows.setEnabled(true);
        dispose();
    }//GEN-LAST:event_formWindowClosing

    private void btn_refresh_devicesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_refresh_devicesActionPerformed
        refresh();
    }//GEN-LAST:event_btn_refresh_devicesActionPerformed

    private void btn_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelActionPerformed
        this.dispose();
        MainWindow.main_windows.setEnabled(true);
    }//GEN-LAST:event_btn_cancelActionPerformed

    private void btn_okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_okActionPerformed
        StringBuilder errbuf = new StringBuilder();
        String device = lbl_dev_name.getText();
        int newSnaplen = Integer.valueOf(lbl_snaplen.getText());
        int newPromisc = (cb_promisc.isSelected() ? 1 : 0);
        int newToMs = Integer.valueOf(SpinnerBufferSize.getValue().toString());
        try {
            Utils.initialize(device, newSnaplen, newPromisc, newToMs, "arp");
        } catch (JxnetException ex) {
            StaticField.LOGGER.log(LoggerStatus.COMMON, "[ WARNING ] :: " + ex.toString());
        }
    }//GEN-LAST:event_btn_okActionPerformed

    private void NICTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_NICTableMouseClicked
        int selectedRow = NICTable.getSelectedRow();
        lbl_dev_name.setText(NICTable.getValueAt(selectedRow, 1).toString());
    }//GEN-LAST:event_NICTableMouseClicked

    private void refresh() {
        DefaultTableModel dtm = new DefaultTableModel(null, new String[] {
                "No", "Name", "IPv4 Address", "IPv6 Address", "MAC Address", "Description"
                }) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
        };
        
        List<PcapIf> alldevsp = new ArrayList<PcapIf>();
        if(PcapFindAllDevs(alldevsp, StaticField.ERRBUF) != 0) {
            StaticField.LOGGER.log(LoggerStatus.COMMON, "[ WARNING ] :: " + StaticField.ERRBUF.toString());
        }
        String[] list = new String[6];
        int no = 1;
        for(PcapIf devs : alldevsp) {
            list[0] = String.valueOf(no);
            list[1] = devs.getName();
            for(PcapAddr dev : devs.getAddresses()) {
                if(dev.getAddr().getSaFamily() == SockAddr.Family.AF_INET) {
                    list[2] = dev.getAddr().toString();
                }
                if(dev.getAddr().getSaFamily() == SockAddr.Family.AF_INET6) {
                    list[3] = dev.getAddr().toString().toUpperCase();
                }
            }
            byte[] mac = AddrUtils.GetMACAddress(devs.getName());
            if (mac == null) continue;
            MacAddress macAddr = MacAddress.valueOf(mac);
            list[4] = (macAddr == null ? "" : macAddr.toString());
            list[5] = devs.getDescription();
            if(macAddr != null) {
                dtm.addRow(list);
                no++;
            }
        }
        NICTable.setModel(dtm);
        NICTable.getColumnModel().getColumn(0).setMaxWidth(50);
        NICTable.getColumnModel().getColumn(0).setMinWidth(50);
        NICTable.getColumnModel().getColumn(2).setMaxWidth(125);
        NICTable.getColumnModel().getColumn(2).setMinWidth(125);
        NICTable.getColumnModel().getColumn(3).setMaxWidth(200);
        NICTable.getColumnModel().getColumn(3).setMinWidth(200);
        NICTable.getColumnModel().getColumn(4).setMaxWidth(150);
        NICTable.getColumnModel().getColumn(4).setMinWidth(150);
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel NICPanel;
    private javax.swing.JScrollPane NICSP;
    private javax.swing.JTable NICTable;
    private javax.swing.JSlider SliderSnaplen;
    private javax.swing.JSpinner SpinnerBufferSize;
    private javax.swing.JButton btn_cancel;
    private javax.swing.JButton btn_ok;
    private javax.swing.JButton btn_refresh_devices;
    private javax.swing.JCheckBox cb_promisc;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lbl_dev_name;
    private javax.swing.JLabel lbl_snaplen;
    // End of variables declaration//GEN-END:variables
}
