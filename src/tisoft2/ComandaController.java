/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tisoft2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ASUS
 */
public class ComandaController {

    private ConnectionFactoryPostgre postgre;

    public List<ComandaModel> getList() {
        List<ComandaModel> lista = new ArrayList<>();
        try {
            postgre.getConnectionFactory();
            PreparedStatement ps = postgre.getCon().prepareStatement("SELECT * FROM comanda_chip ORDER BY comanda ASC");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new ComandaModel(rs.getInt("comanda"), rs.getString("chip")));
            }
            ps.close();
            rs.close();
            postgre.close();
            return lista;

        } catch (SQLException ex) {
            Logger.getLogger(ComandaController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public ComandaModel getByChip(String chip) {
        try {
            postgre.getConnectionFactory();
            //remove os zeros a esquerda
            chip = chip.replaceFirst("^0+(?!$)", "");
            PreparedStatement ps = postgre.getCon().prepareStatement("SELECT * FROM comanda_chip WHERE chip=?");
            ps.setString(1, chip);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new ComandaModel(rs.getInt("comanda"), rs.getString("chip"));
            } else {
                ps.close();
                rs.close();
                postgre.close();
                return null;
            }

        } catch (SQLException ex) {
            Logger.getLogger(ComandaController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    ;

public int isFinalizada(String chip) {
        ComandaModel comanda = this.getByChip(chip);
        if (this.getByChip(chip) == null) {
            //comanda não encontrada
            return -1;
        }
        try {
            postgre.getConnectionFactory();
            PreparedStatement ps = postgre.getCon().prepareStatement("SELECT * FROM comandas_abertas WHERE idcomanda=?");
            ps.setInt(1, comanda.getComanda());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                boolean resposta = rs.getBoolean("status");;
                ps.close();
                rs.close();
                postgre.close();
                if (!resposta) {

                    return 1;
                } else {
                    return 0;
                }
            } else {

                ps.close();
                rs.close();
                postgre.close();
                return -1;
            }

        } catch (SQLException ex) {
            Logger.getLogger(ComandaController.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

    }

    public boolean Add(ComandaModel cm) {
        try {
            postgre.getConnectionFactory();
            PreparedStatement ps = postgre.getCon().prepareStatement("INSERT INTO comanda_chip (comanda,chip) VALUES(?,?)");

            ps.setInt(1, cm.getComanda());
            ps.setString(2, cm.getChip());

            int rs = ps.executeUpdate();
            ps.close();
            postgre.close();
            return rs == 1;

        } catch (SQLException ex) {
            Logger.getLogger(ComandaController.class
                    .getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean removeByComanda(ComandaModel cm) {
        return removeByComanda(cm.getComanda());
    }

    public boolean updateChipByComanda(int comanda, String chip) {
        try {
            postgre.getConnectionFactory();
            PreparedStatement ps = postgre.getCon().prepareStatement("UPDATE comanda_chip SET chip=? WHERE comanda=?");

            ps.setString(1, chip);
            ps.setInt(2, comanda);

            int rs = ps.executeUpdate();
            ps.close();
            postgre.close();
            return rs == 1;

        } catch (SQLException ex) {
            Logger.getLogger(ComandaController.class
                    .getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean removeByComanda(int cm) {
        try {
            postgre.getConnectionFactory();
            PreparedStatement ps = postgre.getCon().prepareStatement("DELETE FROM comanda_chip WHERE comanda = ?");

            ps.setInt(1, cm);
            int rs = ps.executeUpdate();
            ps.close();
            postgre.close();
            return rs == 1;

        } catch (SQLException ex) {
            Logger.getLogger(ComandaController.class
                    .getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    public boolean removeByChip(ComandaModel cm) {
        return removeByChip(cm.getChip());
    }

    public boolean removeByChip(String cm) {
        try {
            postgre.getConnectionFactory();
            PreparedStatement ps = postgre.getCon().prepareStatement("DELETE FROM comanda_chip WHERE chip = ?");

            ps.setString(1, cm);
            int rs = ps.executeUpdate();
            ps.close();
            postgre.close();
            return rs == 1;

        } catch (SQLException ex) {
            Logger.getLogger(ComandaController.class
                    .getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    public void setConnection(ConnectionFactoryPostgre postgre) {
        this.postgre = postgre;
    }

}
