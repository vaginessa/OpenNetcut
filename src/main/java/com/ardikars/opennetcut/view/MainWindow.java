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

import com.ardikars.jxnet.*;
import com.ardikars.jxnet.exception.*;
import com.ardikars.jxnet.packet.arp.ARPOperationCode;
import com.ardikars.jxnet.util.FormatUtils;
import com.ardikars.opennetcut.app.*;
import com.ardikars.jxnet.packet.Packet;
import com.ardikars.jxnet.packet.PacketHandler;
import com.ardikars.jxnet.packet.arp.ARP;
import com.ardikars.opennetcut.util.Utils;
import static com.ardikars.opennetcut.util.Language.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MainWindow extends javax.swing.JFrame {   
    
    public static MainWindow main_windows = new MainWindow();

    private DefaultTableModel DtmScanTable;
    private DefaultTableModel DtmTargetTable;

    private Map<String, MacAddress> target = new HashMap<String, MacAddress>();

    private MainWindow() {
        initComponents();
        StaticField.LOGGER = (LoggerStatus status, String message) -> {
            switch (status) {
                case PROGRESS:
                    int value = Integer.parseInt(message);
                    this._progressBar.setValue(value);
                    if (value == MainWindow.main_windows._progressBar.getMaximum()) {
                        this.scanOp = true;
                        this._btnScan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/16x16/media-playback-start.png")));
                    }
                    break;
                case COMMON:
                    this._txt_logs.append(message+"\n");
                    break;
                default:
                    return;

            }
        };
        Utils.initialize(null, StaticField.SNAPLEN, StaticField.PROMISC, StaticField.TIMEOUT);
        DtmScanTable = Utils.createDefaultTableModel(new String[] {NO, ADD, IPV4_ADDRESS, MAC_ADDRESS, MANUFACTURE});
        DtmTargetTable = Utils.createDefaultTableModel(new String[] {IPV4_ADDRESS, ADD});
        setScanTableModel(DtmScanTable);
        setTargetTableModel(DtmTargetTable);
        setLocationRelativeTo(null);
        initMyComponents();
    }

    public void initMyComponents() {
        TxtNicName.setText(StaticField.SOURCE);
        TxtHwAddr.setText(StaticField.MAC_ADDRESS.toString());
        TxtGwIpAddr.setText(StaticField.GATEWAY_ADDRESS.toString());
        TxtGwHwAddr.setText(StaticField.GATEWAY_MAC_ADDRESS.toString());
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setIconImage(StaticField.ICON_IMAGE);
        _TargetPanel = new javax.swing.JPanel();
        _TargetSP = new javax.swing.JScrollPane();
        TblTarget = new javax.swing.JTable();
        _btnCut = new javax.swing.JButton();
        _btnMITM = new javax.swing.JButton();
        _btnDeleteTarget = new javax.swing.JButton();
        _btnAddTarget = new javax.swing.JButton();
        ScanPanel = new javax.swing.JPanel();
        _ScanSP = new javax.swing.JScrollPane();
        TblScan = new javax.swing.JTable();
        _btnScan = new javax.swing.JButton();
        _cbScanBy = new javax.swing.JComboBox();
        _lblFindBy = new javax.swing.JLabel();
        _txtInputFind = new javax.swing.JTextField();
        _btnScanAddToTarget = new javax.swing.JButton();
        _btnMoveToRight = new javax.swing.JButton();
        _lblNICName = new javax.swing.JLabel();
        _lblHwAddr = new javax.swing.JLabel();
        TxtNicName = new javax.swing.JTextField();
        TxtHwAddr = new javax.swing.JTextField();
        _LogsSP = new javax.swing.JScrollPane();
        _txt_logs = new javax.swing.JTextArea();
        _LogoPanel = new javax.swing.JPanel();
        _lblLogo = new javax.swing.JLabel();
        _lblJxnet = new javax.swing.JLabel();
        _progressBar = new javax.swing.JProgressBar();
        _Toolbar = new javax.swing.JToolBar();
        _filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10));
        _OpenIcon = new javax.swing.JLabel();
        _filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10));
        _SaveIcon = new javax.swing.JLabel();
        _filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10));
        _SettingIcon = new javax.swing.JLabel();
        _filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10));
        //_WifiIcon = new javax.swing.JLabel();
        //_filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10));
        _AboutIcon = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        TxtGwIpAddr = new javax.swing.JTextField();
        TxtGwIpAddr.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (Inet4Address.isValidAddress(TxtGwIpAddr.getText())) {
                    TxtGwIpAddr.setBackground(Color.WHITE);
                } else {
                    TxtGwIpAddr.setBackground(Color.RED);
                }
            }
        });
        TxtGwHwAddr = new javax.swing.JTextField();
        TxtGwHwAddr.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (MacAddress.isValidAddress(TxtGwHwAddr.getText())) {
                    TxtGwHwAddr.setBackground(Color.WHITE);
                } else {
                    TxtGwHwAddr.setBackground(Color.RED);
                }
            }
        });
        LblGwIpAddr = new javax.swing.JLabel();
        LblGwHwAddr = new javax.swing.JLabel();
        _MenuBar = new javax.swing.JMenuBar();
        _FileMenu = new javax.swing.JMenu();
        _OpenMenu = new javax.swing.JMenuItem();
        _SaveMenu = new javax.swing.JMenuItem();
        _ExitMenu = new javax.swing.JMenuItem();
        _EditMenu = new javax.swing.JMenu();
        _NICMenu = new javax.swing.JMenuItem();
        /*_PluginsMenu = new javax.swing.JMenu();
        _DNSSpoofPluginMenu = new javax.swing.JCheckBoxMenuItem();
        _SSLSniffPluginMenu = new javax.swing.JCheckBoxMenuItem();
        _NDPSupport = new javax.swing.JCheckBoxMenuItem();*/
        _HelpMenu = new javax.swing.JMenu();
        _UpdateMenu1 = new javax.swing.JMenuItem();
        _UpdateMenu = new javax.swing.JMenuItem();
        _AboutMenu = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(APPLICATION_NAME);
        setMinimumSize(new java.awt.Dimension(1000, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        _TargetPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("[ "+ ATTACK_MODE +" ]"));

        TblTarget.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        _TargetSP.setViewportView(TblTarget);

        _btnCut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/16x16/edit-cut.png"))); // NOI18N
        _btnCut.setText("Cut");
        _btnCut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _btnCutActionPerformed(evt);
            }
        });

        _btnMITM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/16x16/network-idle.png"))); // NOI18N
        _btnMITM.setText("MITM");
        _btnMITM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _btnMITMActionPerformed(evt);
            }
        });

        _btnDeleteTarget.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/16x16/window-close.png"))); // NOI18N
        _btnDeleteTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _btnDeleteTargetActionPerformed(evt);
            }
        });

        _btnAddTarget.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/16x16/zoom-in.png"))); // NOI18N
        _btnAddTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _btnAddTargetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout _TargetPanelLayout = new javax.swing.GroupLayout(_TargetPanel);
        _TargetPanel.setLayout(_TargetPanelLayout);
        _TargetPanelLayout.setHorizontalGroup(
            _TargetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(_TargetPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(_TargetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(_TargetSP, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(_TargetPanelLayout.createSequentialGroup()
                        .addGroup(_TargetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(_btnCut, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(_btnDeleteTarget, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(_TargetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(_btnMITM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(_btnAddTarget, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        _TargetPanelLayout.setVerticalGroup(
            _TargetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, _TargetPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(_TargetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(_btnCut)
                    .addComponent(_btnMITM))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(_TargetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(_btnDeleteTarget)
                    .addComponent(_btnAddTarget))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(_TargetSP, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                .addContainerGap())
        );

        ScanPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("[ " + NETWORK_CLIENT +" ]"));

        TblScan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        _ScanSP.setViewportView(TblScan);

        _btnScan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/16x16/media-playback-start.png"))); // NOI18N
        _btnScan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _btnScanActionPerformed(evt);
            }
        });

        _cbScanBy.setModel(new javax.swing.DefaultComboBoxModel(new String[] { SCAN_BY_AUTOMATICLY, SCAN_BY_IPV4_ADDRESS }));
        _cbScanBy.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                _cbScanByItemStateChanged(evt);
            }
        });

        _lblFindBy.setText(SCAN_BY);

        _txtInputFind.setEditable(false);

        _btnScanAddToTarget.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/16x16/zoom-in.png"))); // NOI18N
        _btnScanAddToTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _btnScanAddToTargetActionPerformed(evt);
            }
        });

        _btnMoveToRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/16x16/go-next.png"))); // NOI18N
        _btnMoveToRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _btnMoveToRightActionPerformed(evt);
            }
        });

        _lblNICName.setText(NIC_NAME);

        _lblHwAddr.setText(MAC_ADDRESS);

        TxtNicName.setEditable(false);

        TxtHwAddr.setEditable(false);

        javax.swing.GroupLayout ScanPanelLayout = new javax.swing.GroupLayout(ScanPanel);
        ScanPanel.setLayout(ScanPanelLayout);
        ScanPanelLayout.setHorizontalGroup(
            ScanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ScanPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ScanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(_ScanSP)
                    .addGroup(ScanPanelLayout.createSequentialGroup()
                        .addGroup(ScanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(_lblFindBy)
                            .addComponent(_lblNICName))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(ScanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ScanPanelLayout.createSequentialGroup()
                                .addComponent(_cbScanBy, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(_txtInputFind, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
                            .addComponent(TxtNicName))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(ScanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(ScanPanelLayout.createSequentialGroup()
                                .addComponent(_btnScan, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(_btnScanAddToTarget, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(_btnMoveToRight, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(ScanPanelLayout.createSequentialGroup()
                                .addComponent(_lblHwAddr)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(TxtHwAddr)))))
                .addContainerGap())
        );
        ScanPanelLayout.setVerticalGroup(
            ScanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ScanPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ScanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(_lblNICName)
                    .addComponent(_lblHwAddr)
                    .addComponent(TxtNicName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TxtHwAddr, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ScanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(_btnScan)
                    .addComponent(_cbScanBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_lblFindBy)
                    .addComponent(_txtInputFind, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_btnScanAddToTarget)
                    .addComponent(_btnMoveToRight))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(_ScanSP, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                .addContainerGap())
        );

        _txt_logs.setEditable(false);
        _txt_logs.setColumns(20);
        _txt_logs.setRows(5);
        _LogsSP.setViewportView(_txt_logs);

        _lblLogo.setFont(new java.awt.Font("Arial Black", 1, 22)); // NOI18N
        _lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        _lblLogo.setText(" [ " + APPLICATION_NAME + "  ] ");

        _lblJxnet.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        _lblJxnet.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        //_lblJxnet.setText("Powered by Jxnet");

        javax.swing.GroupLayout _LogoPanelLayout = new javax.swing.GroupLayout(_LogoPanel);
        _LogoPanel.setLayout(_LogoPanelLayout);
        _LogoPanelLayout.setHorizontalGroup(
            _LogoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(_LogoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(_LogoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(_lblLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(_lblJxnet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        _LogoPanelLayout.setVerticalGroup(
            _LogoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(_LogoPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(_lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(_lblJxnet)
                .addGap(6, 6, 6))
        );

        _progressBar.setString("");
        _progressBar.setStringPainted(true);

        _Toolbar.setFloatable(false);
        _Toolbar.setBorderPainted(false);
        _Toolbar.add(_filler1);

        _OpenIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/32x32/document-open.png"))); // NOI18N
        _OpenIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                _OpenMenuActionPerformed(null);
            }
        });
        _Toolbar.add(_OpenIcon);
        _Toolbar.add(_filler2);

        _SaveIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/32x32/document-save.png"))); // NOI18N
        _SaveIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                _SaveMenuActionPerformed(null);
            }
        });
        _Toolbar.add(_SaveIcon);
        _Toolbar.add(_filler4);

        _SettingIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/32x32/applications-system.png"))); // NOI18N
        _SettingIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                _NICMenuActionPerformed(null);
            }
        });
        _Toolbar.add(_SettingIcon);
        _Toolbar.add(_filler5);

        /*_WifiIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/32x32/network-wireless.png"))); // NOI18N
        _Toolbar.add(_WifiIcon);
        _Toolbar.add(_filler6);*/

        _AboutIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/32x32/help-browser.png"))); // NOI18N
        _AboutIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new About().setVisible(true);
            }
        });
        _Toolbar.add(_AboutIcon);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(NETWORK_INFORMATION));

        LblGwIpAddr.setText(GATEWAY_IPV4_ADDRESS);

        LblGwHwAddr.setText(GATEWAY_MAC_ADDRESS);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(LblGwHwAddr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(LblGwIpAddr, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TxtGwIpAddr)
                    .addComponent(TxtGwHwAddr))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(TxtGwIpAddr, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(LblGwIpAddr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TxtGwHwAddr, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LblGwHwAddr, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        _FileMenu.setText(FILE);

        _OpenMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        _OpenMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/16x16/document-open.png"))); // NOI18N
        _OpenMenu.setText(FILE_OPEN);
        _OpenMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _OpenMenuActionPerformed(evt);
            }
        });
        _FileMenu.add(_OpenMenu);

        _SaveMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        _SaveMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/16x16/document-save.png"))); // NOI18N
        _SaveMenu.setText(FILE_SAVE);
        _SaveMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _SaveMenuActionPerformed(evt);
            }
        });
        _FileMenu.add(_SaveMenu);

        _ExitMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/16x16/window-close.png"))); // NOI18N
        _ExitMenu.setText(FILE_EXIT);
        _ExitMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _ExitMenuActionPerformed(evt);
            }
        });
        _FileMenu.add(_ExitMenu);

        _MenuBar.add(_FileMenu);

        _EditMenu.setText(EDIT);

        _NICMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        _NICMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/16x16/applications-system.png"))); // NOI18N
        _NICMenu.setText(EDIT_NIC);
        _NICMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _NICMenuActionPerformed(evt);
            }
        });
        _EditMenu.add(_NICMenu);

        _MenuBar.add(_EditMenu);

        /*_PluginsMenu.setText("Plugins");

        _DNSSpoofPluginMenu.setSelected(true);
        _DNSSpoofPluginMenu.setText("DNSSpoof");
        _PluginsMenu.add(_DNSSpoofPluginMenu);

        _SSLSniffPluginMenu.setSelected(true);
        _SSLSniffPluginMenu.setText("SSLSniff");
        _PluginsMenu.add(_SSLSniffPluginMenu);

        _NDPSupport.setSelected(true);
        _NDPSupport.setText("NDPSupport");
        _PluginsMenu.add(_NDPSupport);

        _MenuBar.add(_PluginsMenu);*/

        _HelpMenu.setText(ABOUT);

        _UpdateMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/16x16/system-software-update.png"))); // NOI18N
        _UpdateMenu1.setText(ABOUT_UPDATE_APPLICATION);
        _UpdateMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _UpdateMenu1ActionPerformed(evt);
            }
        });
        _HelpMenu.add(_UpdateMenu1);

        _UpdateMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/16x16/system-software-update.png"))); // NOI18N
        _UpdateMenu.setText(ABOUT_UPDATE_OUI);
        _UpdateMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _UpdateMenuActionPerformed(evt);
            }
        });
        _HelpMenu.add(_UpdateMenu);

        _AboutMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        _AboutMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/16x16/help-browser.png"))); // NOI18N
        _AboutMenu.setText(ABOUT_APPLICATION);
        _AboutMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _AboutMenuActionPerformed(evt);
            }
        });
        _HelpMenu.add(_AboutMenu);

        _MenuBar.add(_HelpMenu);

        setJMenuBar(_MenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(_LogsSP)
                    .addComponent(_progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ScanPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(_LogoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(_TargetPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
            .addComponent(_Toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(_Toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_LogoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(_TargetPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ScanPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(_LogsSP, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(_progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }

    private boolean scanOp = true;
    private NetworkScanner scanner = null;
    private static int counter = 0;
    private void _btnScanActionPerformed(java.awt.event.ActionEvent evt) {

        counter = 0;

        DtmScanTable = Utils.createDefaultTableModel(new String[] {NO, ADD, IPV4_ADDRESS, MAC_ADDRESS, MANUFACTURE});
        
        PacketHandler<String> handler = (String str, PcapPktHdr pktHdr, Map<Class, Packet> packets) -> {
            ARP arp = (ARP) packets.get(ARP.class);
            if (arp == null) return;
            if (arp.getOperationCode() == ARPOperationCode.ARP_REPLY) {
                boolean isAvaible = false;
                for (int i=0; i<TblScan.getRowCount(); i++) {
                    if (TblScan.getValueAt(i, 2).equals(arp.getSenderProtocolAddress().toString().toUpperCase())) {
                        isAvaible = true;
                    }
                }
                if (!isAvaible && !arp.getSenderProtocolAddress().equals(Inet4Address.valueOf(TxtGwIpAddr.getText()))) {
                            DtmScanTable.addRow(new Object[] {
                                Integer.toString(++counter),
                                false,
                                arp.getSenderProtocolAddress().toString().toUpperCase(),
                                arp.getSenderHardwareAddress().toString().toUpperCase(),
                                OUI.searchVendor(arp.getSenderHardwareAddress().toString().toUpperCase())
                            });
                            setScanTableModel(DtmScanTable);
                }
            }
        };
        if (scanOp) {
            setScanTableModel(DtmScanTable);
            scanner = null;
            switch (_cbScanBy.getSelectedIndex()) {
                case 0:
                    scanner = new NetworkScanner(handler);
                    scanner.start();
                    scanOp = false;
                    _btnScan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/16x16/media-playback-stop.png")));
                    break;
                case 1:
                    Inet4Address ip = Inet4Address.valueOf(_txtInputFind.getText());
                    scanner = new NetworkScanner(handler, ip);
                    scanner.start();
                    scanOp = true;
                    _btnScan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/16x16/media-playback-stop.png")));
                    break;
                default:
                    return;
            } 
        } else {
            scanner.stopThread();
            scanOp = true;
            _btnScan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/16x16/media-playback-start.png")));
            try {
                Thread.sleep(StaticField.TIMEOUT * StaticField.TIMEOUT_MULTIPLIER);
            } catch (InterruptedException ex) {
                if (StaticField.LOGGER != null)
                    StaticField.LOGGER.log(LoggerStatus.COMMON, "[ " + WARNING + " ] :: " + ex.getMessage());
            }
        }
    }

    private void _NICMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__NICMenuActionPerformed
        try {
            MainWindow.main_windows.setEnabled(false);
            new NIC().setVisible(true);
        } catch (JxnetException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void _btnMoveToRightActionPerformed(java.awt.event.ActionEvent evt) {
        int rowCount = TblScan.getRowCount();
        if (rowCount < 1) {
            return;
        }
        DtmTargetTable = Utils.createDefaultTableModel(new String[] {IPV4_ADDRESS, ADD});
        target.clear();
        for (int i=0; i<rowCount; i++) {
            if (TblScan.getValueAt(i, 1).equals(Boolean.TRUE) && 
                    !TblScan.getValueAt(i, 2).toString().equals(TxtGwHwAddr.getText())) {
                if (target.containsKey(TblScan.getValueAt(i, 2).toString())) {
                    continue;
                } else {
                    target.put(TblScan.getValueAt(i, 2).toString(),
                            MacAddress.valueOf(TblScan.getValueAt(i, 3).toString()));
                }
            }
        }
        for (Map.Entry<String, MacAddress> entry : target.entrySet()) {
            DtmTargetTable.addRow(new Object[] {
                entry.getKey().toString(), false
            });
        }
        setTargetTableModel(DtmTargetTable);
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        if (StaticField.PCAP == null || StaticField.PCAP.isClosed()) {
            System.exit(0);
        } else {
            Jxnet.PcapClose(StaticField.PCAP);
            System.exit(0);
        }
    }

    private void _btnDeleteTargetActionPerformed(java.awt.event.ActionEvent evt) {
        DefaultTableModel model = (DefaultTableModel) TblTarget.getModel();
        for (int i=0; i<model.getRowCount(); i++) {
            if (model.getValueAt(i, 1).equals(Boolean.TRUE)) {
                model.removeRow(i);
            }
        }
        setTargetTableModel(model);
    }

    private boolean addOpScan = true;
    private void _btnScanAddToTargetActionPerformed(java.awt.event.ActionEvent evt) {
        int rowCount = TblScan.getRowCount();
        if (rowCount < 1) {
            return;
        }
        if (addOpScan) {
            for (int i=0; i<rowCount; i++) {
                TblScan.setValueAt(Boolean.TRUE, i, 1);
            }
            addOpScan = false;
            _btnScanAddToTarget.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/16x16/zoom-out.png")));
        } else {
            for (int i=0; i<rowCount; i++) {
                TblScan.setValueAt(Boolean.FALSE, i, 1);
            }
            addOpScan = true;
            _btnScanAddToTarget.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/16x16/zoom-in.png")));
        }
    }

    private boolean addOpTarget = true;
    private void _btnAddTargetActionPerformed(java.awt.event.ActionEvent evt) {
        int rowCount = TblTarget.getRowCount();
        if (rowCount < 1) {
            return;
        }
        if (addOpTarget) {
            for (int i=0; i<rowCount; i++) {
                TblTarget.setValueAt(Boolean.TRUE, i, 1);
            }
            addOpTarget = false;
            _btnAddTarget.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/16x16/zoom-out.png")));
        } else {
            for (int i=0; i<rowCount; i++) {
                TblTarget.setValueAt(Boolean.FALSE, i, 1);
            }
            addOpTarget = true;
            _btnAddTarget.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/ardikars/opennetcut/images/16x16/zoom-in.png")));
        }
    }

    private List<NetworkSpoofer> nss = new ArrayList<NetworkSpoofer>();
    
    private void _btnCutActionPerformed(java.awt.event.ActionEvent evt) {
        if (TblTarget.getRowCount() < 1) {
            JOptionPane.showMessageDialog(null, NO_VICTIM);
            return;
        }
	
        if (!Inet4Address.isValidAddress(TxtGwIpAddr.getText())) {
            StaticField.LOGGER.log(LoggerStatus.COMMON, "[ " + WARNING + " ] :: " + INVALID_GATEWAY_ADDRESS);
            return;
        }
        if (!MacAddress.isValidAddress(TxtGwHwAddr.getText())) {
            StaticField.LOGGER.log(LoggerStatus.COMMON, "[ " + WARNING + " ] :: " + INVALID_GATEWAY_MAC_ADDRESS);
            return;
        }
        Inet4Address gwIp = Inet4Address.valueOf(TxtGwIpAddr.getText());
        MacAddress gwHw = MacAddress.valueOf(TxtGwHwAddr.getText());
        if (_btnCut.getText().equals("Cut")) {
            nss.clear();
            for (int i=0; i<TblTarget.getRowCount(); i++) {
                if (TblTarget.getValueAt(i, 1).equals(Boolean.TRUE)) {
                    MacAddress victimMac = target.get(TblTarget.getValueAt(i, 0).toString());
                    nss.add(new NetworkSpoofer(
                            victimMac,
                            Inet4Address.valueOf(TblTarget.getValueAt(i, 0).toString()), 
                            Utils.randomMacAddress(), 
                            gwIp,
                            StaticField.TIMEOUT * StaticField.TIMEOUT_MULTIPLIER));
                    nss.add(new NetworkSpoofer(
                            gwHw,
                            gwIp,
                            Utils.randomMacAddress(), 
                            Inet4Address.valueOf(TblTarget.getValueAt(i, 0).toString()),
                            StaticField.TIMEOUT * StaticField.TIMEOUT_MULTIPLIER));
                }
            }
            if (nss.size() < 1) {
                JOptionPane.showMessageDialog(null, NO_VICTIM);
		return;
            }
            for (NetworkSpoofer ns : nss) {
                ns.start();
            } 
            statusTargetButton(false);
            _btnMITM.setEnabled(false);
            _btnCut.setText("Stop");
        } else {
            for (NetworkSpoofer ns : nss) {
                ns.stopThread();
            }
            try {
                Thread.sleep(StaticField.TIMEOUT * StaticField.TIMEOUT_MULTIPLIER);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            byte[] toGw = null;
            byte[] toVic = null;
            for (int i=0; i<TblTarget.getRowCount(); i++) {
                if (TblTarget.getValueAt(i, 1).equals(Boolean.TRUE)) {
                    MacAddress victimMac = target.get(TblTarget.getValueAt(i, 0).toString());
                    toGw = PacketBuilder.arpBuilder(gwHw, ARPOperationCode.ARP_REPLY,
                            victimMac, Inet4Address.valueOf(TblTarget.getValueAt(i, 0).toString()), gwHw, gwIp)
                            .toBytes();
                    toVic = PacketBuilder.arpBuilder(victimMac, ARPOperationCode.ARP_REPLY,
                            gwHw, gwIp, victimMac, Inet4Address.valueOf(TblTarget.getValueAt(i, 0).toString()))
                            .toBytes();
                    for (int z=0; z<5; z++) {
                        Jxnet.PcapSendPacket(StaticField.PCAP, FormatUtils.toDirectBuffer(toGw), toGw.length);
                        Jxnet.PcapSendPacket(StaticField.PCAP, FormatUtils.toDirectBuffer(toVic), toVic.length);
                        try {
                            Thread.sleep(StaticField.TIMEOUT);
                        } catch (InterruptedException e) {
                    		e.printStackTrace();
                        }
		            }
                    
                }
            }
            statusTargetButton(true);
            _btnMITM.setEnabled(true);
            _btnCut.setText("Cut");
        }
    }

    private List<NetworkSpoofer> nssMITM = new ArrayList<NetworkSpoofer>();
    private void _btnMITMActionPerformed(java.awt.event.ActionEvent evt) {
	if (TblTarget.getRowCount() < 1) {
		JOptionPane.showMessageDialog(null, NO_VICTIM);
		return;
	}
	if (!Inet4Address.isValidAddress(TxtGwIpAddr.getText())) {
            StaticField.LOGGER.log(LoggerStatus.COMMON, "[ " + WARNING + " ] :: " + INVALID_GATEWAY_ADDRESS);
            return;
        }
        if (!MacAddress.isValidAddress(TxtGwHwAddr.getText())) {
            StaticField.LOGGER.log(LoggerStatus.COMMON, "[ " + WARNING + " ] :: " + INVALID_GATEWAY_MAC_ADDRESS);
            return;
        }
        Inet4Address gwIp = Inet4Address.valueOf(TxtGwIpAddr.getText());
        MacAddress gwHw = MacAddress.valueOf(TxtGwHwAddr.getText());
        if (_btnMITM.getText().equals("MITM")) {
            nssMITM.clear();
            for (int i=0; i<TblTarget.getRowCount(); i++) {
                if (TblTarget.getValueAt(i, 1).equals(Boolean.TRUE)) {
                    MacAddress victimMac = target.get(TblTarget.getValueAt(i, 0).toString());
                    //GW
                    nssMITM.add(new NetworkSpoofer(
                            gwHw,
                            gwIp,
                            StaticField.MAC_ADDRESS,
                            Inet4Address.valueOf(TblTarget.getValueAt(i, 0).toString()),
                            StaticField.TIMEOUT * StaticField.TIMEOUT_MULTIPLIER));
                    //Vic
                    nssMITM.add(new NetworkSpoofer(
                            victimMac,
                            Inet4Address.valueOf(TblTarget.getValueAt(i, 0).toString()), 
                            StaticField.MAC_ADDRESS,
                            gwIp,
                            StaticField.TIMEOUT * StaticField.TIMEOUT_MULTIPLIER));
                }
            }
            if (nssMITM.size() < 1) {
                JOptionPane.showMessageDialog(null, NO_VICTIM);
                return;
            }
            for (NetworkSpoofer ns : nssMITM) {
                ns.start();
            }
            statusTargetButton(false);
            _btnCut.setEnabled(false);
            _btnMITM.setText("Stop");
        } else { 
            for (NetworkSpoofer ns : nssMITM) {
                ns.stopThread();
            }
            try {
                Thread.sleep(StaticField.TIMEOUT * StaticField.TIMEOUT_MULTIPLIER);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            byte[] toGw = null;
            byte[] toVic = null;
            for (int i=0; i<TblTarget.getRowCount(); i++) {
                if (TblTarget.getValueAt(i, 1).equals(Boolean.TRUE)) {
                    MacAddress victimMac = target.get(TblTarget.getValueAt(i, 0).toString());
                    toGw = PacketBuilder.arpBuilder(gwHw, ARPOperationCode.ARP_REPLY,
                            victimMac, Inet4Address.valueOf(TblTarget.getValueAt(i, 0).toString()), gwHw, gwIp)
                            .toBytes();
                    toVic = PacketBuilder.arpBuilder(victimMac, ARPOperationCode.ARP_REPLY,
                            gwHw, gwIp, victimMac, Inet4Address.valueOf(TblTarget.getValueAt(i, 0).toString()))
                            .toBytes();

		    for (int z=0; z<5; z++) {
                    	Jxnet.PcapSendPacket(StaticField.PCAP, FormatUtils.toDirectBuffer(toGw), toGw.length);
                   	Jxnet.PcapSendPacket(StaticField.PCAP, FormatUtils.toDirectBuffer(toVic), toVic.length); 
			try {
                		Thread.sleep(StaticField.TIMEOUT);
			} catch (InterruptedException e) {
                		e.printStackTrace();
            		}
		    }
                }
            }
            statusTargetButton(true);
            _btnCut.setEnabled(true);
            _btnMITM.setText("MITM");
        }
        
    }

    private void _SaveMenuActionPerformed(java.awt.event.ActionEvent evt) {
        if (StaticField.RANDOM_STRING == null || TblScan.getRowCount() <= 0) {
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                String fileName = fileChooser.getSelectedFile().getAbsolutePath();
                if (!fileName.endsWith(".pcap") || !fileName.endsWith(".pcapng")) {
                    fileName = fileName.concat(".pcap");
                }
                Utils.copyFileUsingFileChannels(new File(StaticField.RANDOM_STRING), new File(fileName));
            } catch (IOException ex) {
                if (StaticField.LOGGER != null)
                    StaticField.LOGGER.log(LoggerStatus.COMMON, "[ " + WARNING + " ] :: " + ex.getMessage());
                return;
            }
        }
        StaticField.RANDOM_STRING = null;
    }

    private static int openNo = 0;
    private void _OpenMenuActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        String fileName = null;
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            fileName = fileChooser.getSelectedFile().getAbsolutePath();
            if (fileName == null) return;
        } else {
            return;
        }
        
        DtmScanTable = Utils.createDefaultTableModel(new String[] {NO, ADD, IPV4_ADDRESS, MAC_ADDRESS, MANUFACTURE});

        PacketHandler handler = (no, pcapPktHdr, map) -> {
            ARP arp = (ARP) map.get(ARP.class);
            if (arp != null) {
                if (arp.getOperationCode() == ARPOperationCode.ARP_REPLY) {
                    openNo++;
                    DtmScanTable.addRow(new Object[] {
                            String.valueOf(openNo),
                            false,
                            arp.getSenderProtocolAddress().toString().toUpperCase(),
                            arp.getSenderHardwareAddress().toString().toUpperCase(),
                            OUI.searchVendor(arp.getSenderHardwareAddress().toString().toUpperCase())
                    });
                };
            }
        };
        Utils.openPcapFile(handler, StaticField.LOGGER, fileName);
        setScanTableModel(DtmScanTable);
        openNo = 0;
    }
    
    private void _ExitMenuActionPerformed(java.awt.event.ActionEvent evt) {
        if (StaticField.PCAP == null || StaticField.PCAP.isClosed()) {
            System.exit(0);
        } else {
            Jxnet.PcapClose(StaticField.PCAP);
            System.exit(0);
        }
    }

    private void _AboutMenuActionPerformed(java.awt.event.ActionEvent evt) {
        new About().setVisible(true);
    }

    private void _UpdateMenuActionPerformed(java.awt.event.ActionEvent evt) {
        Thread oui = new Thread(new OUI(StaticField.LOGGER));
        oui.start();
    }

    private void _cbScanByItemStateChanged(java.awt.event.ItemEvent evt) {
        if (_cbScanBy.getSelectedIndex() == 0) {
            _txtInputFind.setEditable(false);
            _txtInputFind.setText("");
        } else {
            _txtInputFind.setEditable(true);
            _txtInputFind.setText("");
        }
    }

    private void _UpdateMenu1ActionPerformed(java.awt.event.ActionEvent evt) {
        JOptionPane.showMessageDialog(null, "");
    }

    private void statusTargetButton(boolean status) {
        _btnAddTarget.setEnabled(status);
        _btnDeleteTarget.setEnabled(status);
        _btnMoveToRight.setEnabled(status);
    }
    
    private void setScanTableModel(DefaultTableModel model) {
        TblScan.setModel(model);
        TblScan.getColumnModel().getColumn(0).setMaxWidth(50);
        TblScan.getColumnModel().getColumn(0).setMinWidth(50);
        TblScan.getColumnModel().getColumn(1).setMaxWidth(50);
        TblScan.getColumnModel().getColumn(1).setMinWidth(50);
        TblScan.getColumnModel().getColumn(2).setMaxWidth(200);
        TblScan.getColumnModel().getColumn(2).setMinWidth(125);
        TblScan.getColumnModel().getColumn(3).setMaxWidth(150);
        TblScan.getColumnModel().getColumn(3).setMinWidth(150);    
    }
    
    private void setTargetTableModel(DefaultTableModel model) {
        TblTarget.setModel(model);
        TblTarget.getColumnModel().getColumn(1).setMaxWidth(50);
        TblTarget.getColumnModel().getColumn(1).setMinWidth(50);
    }

    private javax.swing.JPanel ScanPanel;
    private javax.swing.JTable TblScan;
    private javax.swing.JTable TblTarget;
    private javax.swing.JTextField TxtGwHwAddr;
    private javax.swing.JTextField TxtHwAddr;
    private javax.swing.JTextField TxtGwIpAddr;
    private javax.swing.JTextField TxtNicName;
    private javax.swing.JMenuItem _AboutMenu;
    //private javax.swing.JCheckBoxMenuItem _DNSSpoofPluginMenu;
    private javax.swing.JMenu _EditMenu;
    private javax.swing.JMenuItem _ExitMenu;
    private javax.swing.JMenu _FileMenu;
    private javax.swing.JLabel _AboutIcon;
    private javax.swing.JMenu _HelpMenu;
    private javax.swing.JPanel _LogoPanel;
    private javax.swing.JScrollPane _LogsSP;
    private javax.swing.JMenuBar _MenuBar;
    //private javax.swing.JCheckBoxMenuItem _NDPSupport;
    private javax.swing.JMenuItem _NICMenu;
    private javax.swing.JLabel _OpenIcon;
    private javax.swing.JMenuItem _OpenMenu;
    //private javax.swing.JMenu _PluginsMenu;
    //private javax.swing.JCheckBoxMenuItem _SSLSniffPluginMenu;
    private javax.swing.JLabel _SaveIcon;
    private javax.swing.JMenuItem _SaveMenu;
    private javax.swing.JScrollPane _ScanSP;
    private javax.swing.JLabel _SettingIcon;
    private javax.swing.JPanel _TargetPanel;
    private javax.swing.JScrollPane _TargetSP;
    private javax.swing.JToolBar _Toolbar;
    private javax.swing.JMenuItem _UpdateMenu;
    private javax.swing.JMenuItem _UpdateMenu1;
    //private javax.swing.JLabel _WifiIcon;
    private javax.swing.JButton _btnAddTarget;
    private javax.swing.JButton _btnCut;
    private javax.swing.JButton _btnDeleteTarget;
    private javax.swing.JButton _btnMITM;
    private javax.swing.JButton _btnMoveToRight;
    private javax.swing.JButton _btnScan;
    private javax.swing.JButton _btnScanAddToTarget;
    private javax.swing.JComboBox _cbScanBy;
    private javax.swing.Box.Filler _filler1;
    private javax.swing.Box.Filler _filler2;
    private javax.swing.Box.Filler _filler4;
    private javax.swing.Box.Filler _filler5;
    private javax.swing.Box.Filler _filler6;
    private javax.swing.JLabel _lblFindBy;
    private javax.swing.JLabel _lblHwAddr;
    private javax.swing.JLabel _lblJxnet;
    private javax.swing.JLabel _lblLogo;
    private javax.swing.JLabel _lblNICName;
    private javax.swing.JProgressBar _progressBar;
    private javax.swing.JTextField _txtInputFind;
    private javax.swing.JTextArea _txt_logs;
    private javax.swing.JLabel LblGwIpAddr;
    private javax.swing.JLabel LblGwHwAddr;
    private javax.swing.JPanel jPanel1;

}
