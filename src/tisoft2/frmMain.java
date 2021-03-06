/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tisoft2;

import relatoriomarcasetor.InitConfigController;
import java.awt.Color;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import tcpcom.TcpClient;

/**
 *
 * @author ASUS
 */
public class frmMain extends javax.swing.JFrame {

    static ConnectionFactoryPostgre con = null;
    InitConfigController initconfig;
    static TcpClient client;
    private String pattern[] = {".*\\< \\d{2}\\+REON\\+000\\+0\\](\\d{20})\\](\\d{1,2}\\/\\d{1,2}\\/\\d{4})\\s(\\d{2}\\:\\d{2}\\:\\d{2})\\]2\\]2\\]2.*",
        ".*- 01\\+RH\\+000\\+\\d{2}\\/\\d{2}\\/\\d{2} \\d{2}\\:\\d{2}:\\d{2}\\]\\d{2}\\/\\d{2}\\/\\d{2}\\]\\d{2}\\/\\d{2}\\/\\d{2}.*"};
    Pattern r = Pattern.compile(pattern[0]);
    ComandaController controller = new ComandaController();

    public frmMain() {
        initComponents();
        initconfig = new InitConfigController();
        otherInit();
        socketCatraca();
        task();

    }

    public void task() {
        Timer time = new Timer(); // Instantiate Timer Object
        ScheduledTask st = new ScheduledTask(); // Instantiate SheduledTask class
        time.schedule(st, 0, 1000 * 5); // 
    }

    public void respostaSistema(String pergunta) {
        Matcher m = r.matcher(pergunta);
        try {
            m.find();
            String chip = m.group(1);
            int rs = controller.isFinalizada(chip);
            if (rs == 1) {
                System.out.println("liberado");
                sendComand("01+REON+00+6]5]ACESSO LIBERADO]5");
            } else if (rs == 0) {
                System.out.println("nao");
                sendComand("01+REON+00+30]7]Dirija-se ao}CAIXA]");
            }else if(rs==-1){
              sendComand("01+REON+00+30]7]Comanda não}ENCONTRADA]");
            }

        } catch (Exception e) {

        }
    }

    public static void sendComand(String comando) {
        char[] data;
        data = comando.toCharArray();
        String str = "", aux = "";
        str = textFormat(data);//formatando texto (cabeçalho, checksum e Byte final)
        aux = stringHexFormat(str);//formatando para numero Hexadecimal
        client.sendData(str.toCharArray());   //enviando dados
//                    jtaDadosBytesEnv.append(aux+"\n\n");
    }

    public static String stringHexFormat(String str) {
        String aux = "", temp = "";
        for (char ch : str.toCharArray()) {
            temp += Integer.toHexString(ch).toUpperCase();
            //Converte Hexa em String
            if (temp.length() == 1) {
                aux += "0" + temp + " ";//se tiver 1 digito complementa com 0
            } else {
                aux += temp + " ";
            }
            temp = new String();
        }
        return aux;
    }

    public static String textFormat(char[] data) {
        String aux = "", aux2 = "", str = "";
        char BYTE_INIT, BYTE_END, BYTE_TAM[] = {0, 0}, BYTE_CKSUM;
        BYTE_INIT = (char) Integer.valueOf("2", 16).intValue();//conf. bit inicial
        BYTE_END = (char) Integer.valueOf("3", 16).intValue();//conf. bit final
        BYTE_TAM[0] = (char) data.length;//conf. tamanho dos dados
        BYTE_TAM[1] = (char) Integer.valueOf("0", 16).intValue();
        aux2 += BYTE_INIT; //Inserindo byte inicial
        aux2 += BYTE_TAM[0]; //Inserindo byte do tamanho
        aux2 += BYTE_TAM[1];
        for (char chr : data) {
            str += chr;
        }
        aux = new String(aux2 + str); // concatenando com a informação

        BYTE_CKSUM = aux.charAt(1);//Calculo do Checksum
        for (int a = 2; a < aux.length(); a++) {
            BYTE_CKSUM = (char) (BYTE_CKSUM ^ aux.charAt(a));
        }
        aux += BYTE_CKSUM; //Inserindo Checksum
        aux += BYTE_END; //Inserindo byte Final
        return aux;

    }

    public void socketCatraca() {
        client = new TcpClient(initconfig.init.getIpcatraca(),
                initconfig.init.getPortacatraca(), 1000);
        Matcher m;
        if (client.connect()) {
            lblStatus.setText("ATIVO");
            lblStatus.setBackground(Color.GREEN);
            lblStatus.setOpaque(true);
            if (client.isConnected()) {

                Thread t1 = new Thread(() -> {
                    try {
                        while (client.isConnected()) {
                            if (client.availableData() > 0) {
                                char[] temp = client.receiveData(client.
                                        availableData()); //recebendo dados
                                String msgCatraca = "";

                                for (char chr : temp) {
                                    msgCatraca += chr;
                                }

                                if (r.matcher(msgCatraca).matches()) {

                                    Matcher mi = r.matcher(msgCatraca);
                                    mi.find();
                                    String chip = mi.group(1).replaceFirst("^0+(?!$)", "");
                                    String data = mi.group(2);
                                    String hora = mi.group(3);

                                    txtLog.append(chip + " " + data + " " + hora + "\n");
                                } else {
                                    if (frmTimeLog.ativo) {
                                        Matcher ma = r.compile(pattern[1]).matcher(msgCatraca);
                                        if (ma.matches()) {
                                            frmTimeLog.txtLog.append(msgCatraca + "\n");
                                        }
                                    }
                                }
                                respostaSistema(msgCatraca);

                            }
                            Thread.sleep(500);  //esperando resposta
                        }

                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage() + "erro");
                    }
                });
                t1.start();
            }
        } else {
            lblStatus.setText("INATIVO");
            lblStatus.setBackground(Color.RED);
            lblStatus.setOpaque(true);
        }

    }

    public void otherInit() {
        String msg = initconfig.mensagemArquivo();
        if (msg != null) {
            JOptionPane.showMessageDialog(null, "Por favor, verifique o arquivo de configuração.\nEstão faltando as seguinte(s) informação(ões):\n" + msg, "Arquivo de Configuração", JOptionPane.OK_OPTION);
            System.exit(0);
        } else {
            try {
                this.con = new ConnectionFactoryPostgre();
                con.setBanco(initconfig.init.getBanco());
                con.setUser(initconfig.init.getUsuariobanco());
                con.setPasswd(initconfig.init.getSenhabanco());
                con.setPorta(initconfig.init.getPortabanco());
                con.setIp(initconfig.init.getIpbanco());

                if (!con.getConnectionFactory()) {
                    JOptionPane.showMessageDialog(null, "Não foi possível conectar ao banco de dados.\nO Programa irá finalizar.");
                    System.exit(0);
                } else {

                    con.close();
                }
                controller.setConnection(con);

            } catch (SQLException ex) {
                Logger.getLogger(frmMain.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblStatus = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        lblStatus.setBackground(new java.awt.Color(0, 204, 51));
        lblStatus.setFont(new java.awt.Font("Times New Roman", 1, 36)); // NOI18N
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatus.setText("jLabel1");

        txtLog.setColumns(20);
        txtLog.setRows(5);
        txtLog.setEnabled(false);
        jScrollPane1.setViewportView(txtLog);

        jMenu1.setText("Arquivo");

        jMenuItem1.setText("Comanda");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Catraca");

        jMenuItem2.setText("Incluir comandas");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuItem4.setText("Excluir comandas");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Status");

        jMenuItem5.setText("Status");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem5);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 99, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        new frmComanda(this, true).setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        System.exit(0);
    }//GEN-LAST:event_formWindowClosed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        List<ComandaModel> lista = controller.getList();
        lista.forEach((comanda) -> {

            try {
                Thread.sleep(100);
                sendComand("05+ECAR+00+1+I[[" + comanda.getChip().trim() + "[[[1[4[1[[[[[2[[[[[0[COMANDA[" + comanda.getComanda() + "[");
            } catch (InterruptedException ex) {
                Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null, ex);
            }

        });

    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        List<ComandaModel> lista = controller.getList();
        lista.forEach((comanda) -> {

            try {
                Thread.sleep(100);
                sendComand("05+ECAR+00+1+E[[" + comanda.getChip().trim() + "[[[1[4[1[[[[[2[[[[[0[COMANDA[" + comanda.getComanda() + "[");
            } catch (InterruptedException ex) {
                Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null, ex);
            }

        });
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        new frmTimeLog(this, true).setVisible(true);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(frmMain.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmMain.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmMain.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmMain.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmMain().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTextArea txtLog;
    // End of variables declaration//GEN-END:variables
}
