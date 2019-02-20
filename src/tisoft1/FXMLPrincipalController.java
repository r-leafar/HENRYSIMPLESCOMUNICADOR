package tisoft1;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author ASUS
 */
public class FXMLPrincipalController implements Initializable {

    @FXML
    Label lblStatus;
    String styleAtivo = "-fx-background-color:#07f113;";
    String styleInativo = "-fx-background-color:#07f113;";

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    public void showComandaDialog() {

        try {
            System.out.println(getClass().getResource("FXMLcomanda.fxml"));
            FXMLLoader load = new FXMLLoader(getClass().getResource("tisoft/view/FXMLcomanda.fxml"));

            Parent root = (Parent)load.load();

            //CRiando um estágio dialog
            Stage dialogStage = new Stage();

            dialogStage.setTitle("Cadastro de Comandas");

            dialogStage.setScene(new Scene(root));

          //  dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.show();
        } catch (IOException ex) {
            //Logger.getLogger(FXMLPrincipalController.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("erro: "+ex.getMessage());
        }

    }

}
