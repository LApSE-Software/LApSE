/*
 * The MIT License
 *
 * Copyright 2015 Burhanuddin.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package lapse;

import lapse.model.TaggedLine;
import lapse.model.TaggedRectangle;
import lapse.util.ExceptionFormatter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lapse.view.RootLayoutController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Burhanuddin
 */
public class MainApp extends Application {
    
    private static final Logger logger = LogManager.getLogger();
    public static final String TITLE = "LApSE";
    
    private Stage primaryStage;
    
    private final ObservableList<String> beforeLines;
    private final ObservableList<String> afterLines;
    private final ObservableList<TaggedLine> taggedLines;
    private final ObservableMap<String, ObservableList<String>> tags;
    private final ObservableList<String> drawingTypeList;
    private final ObservableList<TaggedRectangle> taggedRectangles;
    
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
        
        loadTags("data/tags.txt");
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
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("style/lapse_64x64.png")));
            primaryStage.setMaximized(true);
            primaryStage.setScene(scene);
            
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);
            
            primaryStage.show();
        } catch (IOException ex) {
            logger.error(ExceptionFormatter.format(ex));
        }
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
            logger.error(ExceptionFormatter.format(ex));
        }
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
