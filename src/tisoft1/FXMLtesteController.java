package tisoft1;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import tcpcom.TcpClient;


/**
 * FXML Controller class
 *
 * @author ASUS
 */
public class FXMLtesteController implements Initializable {

    @FXML
    public Button btnEnviar;
    @FXML
    public Button btnReceber;
    @FXML
    public TextField txtComando;
    public TextArea txtRecebido;
    public TextArea txtEnviado;
    TcpClient client;
    boolean isconectar = true;


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        String ip = "192.168.1.110";;
        int porta = 3000;
        if (isconectar) {
            client = new TcpClient(ip,
                    porta);
            client.connect();
            if (client.isConnected()) {
                new Thread(new Runnable() {
                    public void run() {
                        String aux2 = "";
                        try {
                            while (isconectar) {
                                if (client.availableData() > 0) {
                                    char[] temp = client.receiveData(client.
                                            availableData()); //recebendo dados
                                    String str = "", aux = "";
                                    for (char chr : temp) {
                                        str += chr;
                                    }
                                    txtRecebido.appendText(str + "\n\n");
                                    aux = stringHexFormat(str);
                                  //  jtaDadosBytesRec.append(aux + "\n\n");

                                }
                                Thread.sleep(500);  //esperando resposta
//                                jlbConexao.setText("Conectado à: " + jtfIp.getText());
                            }
                        } catch (Exception e) {

                        }
//                        jchkConectar.setText("Conectado");
//                        System.out.println(aux2);
//                        jlbConexao.setText(aux2);
                    }
                }).start();
            } else {
//                jlbConexao.setText("Conexão não Estabelecida");
//                jchkConectar.setSelected(false);
            }
        }
    }

    public void limpar() {

        txtEnviado.clear();
        txtRecebido.clear();
    }

    public void sendComand() {
                 char[] data;
                    data = txtComando.getText().toCharArray();
                    String str = "", aux = "";
                    str = textFormat(data);//formatando texto (cabeçalho, checksum e Byte final)
                    aux = stringHexFormat(str);//formatando para numero Hexadecimal
                    client.sendData(str.toCharArray());   //enviando dados
                    txtEnviado.appendText(str+"\n\n");
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
}
