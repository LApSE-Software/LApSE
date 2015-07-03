/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drawingtagger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Burhanuddin
 */
public class DrawingTagger extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DrawingTagger.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        
        stage.setTitle("Drawing Tagger");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
        
        DrawingTaggerController controller = (DrawingTaggerController) loader.getController();
        controller.setMainStage(stage);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
