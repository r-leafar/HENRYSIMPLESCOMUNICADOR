/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tisoft2;

/**
 *
 * @author ASUS
 */
public class ComandaModel {

    private int comanda;
    private String chip;

    public ComandaModel() {
    }

    public int getComanda() {
        return comanda;
    }

    public void setComanda(int comanda) {
        this.comanda = comanda;
    }

    public String getChip() {
        return chip;
    }

    public void setChip(String chip) {
        this.chip = chip.trim();
    }

    public ComandaModel(int comanda, String chip) {
        this.comanda = comanda;
        this.chip = chip;
    }
    public ComandaModel(String comanda, String chip) {
        this.comanda = Integer.parseInt(comanda);
        this.chip = chip;
    }

    @Override
    public String toString() {
        return "ComandaModel{" + "comanda=" + comanda + ", chip=" + chip + '}';
    }
}
