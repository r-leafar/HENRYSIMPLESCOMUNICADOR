package tisoft1;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author ASUS
 */
public class Comanda extends Application {
    
  @Override
    public void start(Stage primaryStage) throws Exception {
//        Pane root = FXMLLoader.load(getClass().getResource("FXMLcomanda.fxml"));
        Pane root = FXMLLoader.load(getClass().getResource("FXMLteste.fxml"));
        
        Scene scene = new Scene(root,491,332);
        primaryStage.setScene(scene);
        
        primaryStage.setTitle("TISOFT");
        primaryStage.show();
    
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
