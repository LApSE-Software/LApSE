/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drawingtagger;

import drawingtagger.model.TaggedLine;
import drawingtagger.model.TaggedRectangle;
import drawingtagger.view.RootLayoutController;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

/**
 *
 * @author Burhanuddin
 */
public class MainApp extends Application {
    
    public static final String TITLE = "Drawing Tagger";
    
    private Stage primaryStage;
    
    private final ObservableList<String> beforeLines;
    private final ObservableList<String> afterLines;
    private final ObservableList<TaggedLine> taggedLines;
    private final ObservableMap<String, ObservableList<String>> tags;
    private final ObservableList<String> drawingTypeList;
    private final ObservableList<TaggedRectangle> taggedRectangles;
    private final ObservableList<WritableImage> backupStates;
    
    /**
     * Constructor.
     */
    public MainApp() {
        beforeLines = FXCollections.observableArrayList();
        afterLines = FXCollections.observableArrayList();
        taggedLines = FXCollections.observableArrayList();
        tags = FXCollections.observableHashMap();
        drawingTypeList = FXCollections.observableArrayList();
        taggedRectangles = FXCollections.observableArrayList();
        backupStates = FXCollections.observableArrayList();
        loadTags("tags.txt");
    }
    
    /**
     * Clear the data for previous file. Usually called before loading
     * a new file.
     */
    public void clearData() {
        beforeLines.clear();
        afterLines.clear();
        taggedLines.clear();
        taggedRectangles.clear();
        backupStates.clear();
    }
    
    /**
     * Return the list of string before the line coordinates from the file.
     * @return beforeLines
     */
    public ObservableList<String> getBeforeLines() {
        return beforeLines;
    }
    
    /**
     * Return the list of string after the line coordinates from the file.
     * @return afterLines
     */
    public ObservableList<String> getAfterLines() {
        return afterLines;
    }
    
    /**
     * Return the list of line coordinates.
     * @return taggedLines
     */
    public ObservableList<TaggedLine> getTaggedLines() {
        return taggedLines;
    }
    
    /**
     * Return the list of all available tagging labels according to drawing
     * type.
     * @return tags
     */
    public ObservableMap<String, ObservableList<String>> getTags() {
        return tags;
    }
    
    /**
     * Return the list of all drawing types.
     * @return drawingTypeList
     */
    public ObservableList<String> getDrawingTypeList() {
        return drawingTypeList;
    }
    
    /**
     * Return a list of tagged rectangles.
     * @return taggedRectangles
     */
    public ObservableList<TaggedRectangle> getTaggedRectangles() {
        return taggedRectangles;
    }
    
    /**
     * Return the list of backup states of canvas.
     * @return backupStates
     */
    public ObservableList<WritableImage> getBackupStates() {
        return backupStates;
    }
    
    /**
     * Load all available tagging label from specified file.
     * @param fileName 
     */
    private void loadTags(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String temp;
            String currentType = null;
            while ((temp = reader.readLine()) != null) {
                if (temp.startsWith("#")) {
                    currentType = temp.substring(1);
                    drawingTypeList.add(currentType);
                    tags.put(currentType, FXCollections.observableArrayList());
                    continue;
                }
                if (currentType != null && !temp.isEmpty()) {
                    ObservableList<String> tagList = tags.get(currentType);
                    tagList.add(temp);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(RootLayoutController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        initRootLayout();
    }
    
    /**
     * Initialize root layout.
     */
    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/RootLayout.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            
            primaryStage.setTitle(TITLE);
            primaryStage.setMaximized(true);
            primaryStage.setScene(scene);
            
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);
            
            primaryStage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Return the primary stage of the application.
     * @return primaryStage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
