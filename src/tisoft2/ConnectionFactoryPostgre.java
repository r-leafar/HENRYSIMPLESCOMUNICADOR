/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tisoft2;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author admin
 */
public class ConnectionFactoryPostgre {

    private Connection con = null;
    private String ip = "127.0.0.1";
    private String porta = "5432";
    private String banco = "";
    private String user = "postgres";
    private String passwd = "123456";

    public ConnectionFactoryPostgre() throws SQLException {
        //  this.getConnectionFactory();
    }

    public boolean getConnectionFactory() throws SQLException {
        try {
            //String conexao = "jdbc:postgresql://" + this.getIp() + ":" + this.getPorta() + "/" + this.getBanco()+" - "+this.getUser()+" - "+this.getPasswd();
            //  System.out.println(conexao);
            Class.forName("org.postgresql.Driver");
            this.con = DriverManager.getConnection("jdbc:postgresql://" + this.getIp() + ":" + this.getPorta() + "/" + this.getBanco(), this.getUser(), this.getPasswd());
//            this.con.setAutoCommit(false);
            return true;
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            return false;
            //throw new SQLException(e.getMessage());
        } catch (org.postgresql.util.PSQLException ex) {
            return false;
        }
    }

    public boolean verificaTabelas() throws SQLException {

        PreparedStatement ps_post = this.getCon().prepareStatement("SELECT count(*)as total FROM produto limit 10");
        ResultSet rs = ps_post.executeQuery();
        String mensagem = "";
        boolean valido = true;

        if (rs.next()) {
            if (rs.getInt("total") > 0) {
                mensagem += "| Produto |";
                valido = false;
            }

        }
        ps_post = this.getCon().prepareStatement("SELECT count(*)as total FROM cliente limit 10");
        rs = ps_post.executeQuery();

        if (rs.next()) {
            if (rs.getInt("total") > 0) {
                mensagem += "| cliente |";
                valido = false;
            }

        }

        ps_post = this.getCon().prepareStatement("SELECT count(*)as total FROM fornecedor limit 10");
        rs = ps_post.executeQuery();

        if (rs.next()) {
            if (rs.getInt("total") > 0) {
                mensagem += "| fornecedor |";
                valido = false;
            }

        }
        ps_post = this.getCon().prepareStatement("SELECT count(*)as total FROM produto_marca limit 10");
        rs = ps_post.executeQuery();

        if (rs.next()) {
            if (rs.getInt("total") > 0) {
                mensagem += "| produto_marca |";
                valido = false;
            }

        }
        if (!valido) {
            JOptionPane.showMessageDialog(null, "Verifique a(s) tabela(s): \n " + mensagem + " \n ela(s) deve(m) estar vazia(s).");
        }
        return valido;

    }

    public void close() throws SQLException {
        if (this.getCon() != null) {
            this.getCon().close();
        }
    }

    public Connection getCon() {
        return con;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPorta() {
        return porta;
    }

    public void setPorta(String porta) {
        this.porta = porta;
    }

    public void setPorta(int porta) {
        this.porta = String.valueOf(porta);
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

}
