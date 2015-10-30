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
package lapse.view;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lapse.MainApp;
import lapse.model.TaggedLine;
import lapse.model.TaggedRectangle;
import lapse.util.ExceptionFormatter;
import lapse.util.FileChooserType;
import lapse.util.TemporaryDataHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Burhanuddin
 */
public class RootLayoutController implements Initializable {
    
    private static final Logger logger = LogManager.getLogger();
    
    @FXML
    private MenuItem undoMenu;
    @FXML
    private MenuItem redoMenu;
    @FXML
    private CheckMenuItem drawingSequenceMenu;
    @FXML
    private CheckMenuItem lineLabelMenu;
    @FXML
    private AnchorPane drawingPane;
    
    private MainApp mainApp;
    private File file;
    private DrawingController drawingController;
    private TemporaryDataHolder tempDataHolder;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tempDataHolder = new TemporaryDataHolder();
        
        initDrawing();
    }
    
    /**
     * Initialize drawing tab pane.
     */
    private void initDrawing() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Drawing.fxml"));
            Parent drawing = loader.load();
            
            drawingController = loader.getController();
//            drawingController.setMainApp(mainApp);
            drawingController.setRootLayout(this);
            
            drawingPane.getChildren().setAll(drawing);
        } catch (IOException ex) {
            logger.error(ExceptionFormatter.format(ex));
        }
    }
    
    /**
     * Called from 'Open...' menu. Open TRACE file, and load it to canvas.
     * @param event 
     */
    @FXML
    private void open(ActionEvent event) {
        chooseFile(FileChooserType.OPEN);
        loadFile();
    }
    
    /**
     * Save tagging to current file.
     * @param event 
     */
    @FXML
    private void save(ActionEvent event) {
        saveFile();
    }
    
    /**
     * Save tagging to the selected file.
     * @param event 
     */
    @FXML
    private void saveAs(ActionEvent event) {
        chooseFile(FileChooserType.SAVE);
        saveFile();
    }
    
    /**
     * Called from 'Quit' menu. Close the program.
     * @param event 
     */
    @FXML
    private void quit(ActionEvent event) {
        mainApp.getPrimaryStage().close();
    }
    
    /**
     * Called from 'Undo' menu.
     * 
     * @param event 
     */
    @FXML
    private void undo(ActionEvent event) {
        drawingController.undo(event);
    }
    
    /**
     * Called from 'Redo' menu.
     * 
     * @param event 
     */
    @FXML
    private void redo(ActionEvent event) {
        drawingController.redo(event);
    }
    
    /**
     * Called from 'Clear' menu.
     * @param event 
     */
    @FXML
    private void clear(ActionEvent event) {
        drawingController.clearTags(event);
    }
    
    /**
     * Called from 'About' menu. Show program info.
     * @param event 
     */
    @FXML
    private void about(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About LApSE");
        alert.setHeaderText("LApSE version 0.1.1-alpha");
        alert.setContentText("Copyright \u24D2 Dr Unaizah Hanum binti Obaidellah\r\n\r\n"
                + "Author: Burhanuddin Baharuddin");
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(mainApp.getPrimaryStage().getIcons().get(0));
        alert.show();
    }
    
    /**
     * Load TRACE file.
     */
    public void loadFile() {
        if (file == null) {
            return;
        }
        
        tempDataHolder.clearAll();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String temp;
            boolean foundLine = false;
            while ((temp = reader.readLine()) != null) {
                if (temp.equals("<<Extracted_Lines>>")) {
                    foundLine = true;
                    tempDataHolder.insertTempData("beforeLines", temp);
                    continue;
                }
                if (foundLine) {
                    if (temp.startsWith("<<")) {
                        tempDataHolder.insertTempData("afterLines", temp);
                        break;
                    }
                    tempDataHolder.insertTempData("taggedLines", temp);
                } else {
                    tempDataHolder.insertTempData("beforeLines", temp);
                }
            }
            while ((temp = reader.readLine()) != null) {
                tempDataHolder.insertTempData("afterLines", temp);
            }

            if (tempDataHolder.isEmpty("taggedLines")) {
                showWarningCorruptedFile();
            } else {
                drawingController.loadProgram(tempDataHolder);
            }
        } catch (IOException ex) {
            logger.error(ExceptionFormatter.format(ex));
        } catch (Exception ex) {
            logger.error(ExceptionFormatter.format(ex));
        }
    }
    
    /**
     * Load TRACE file.
     * 
     * @param filePath
     */
    public void loadFile(String filePath) {
        file = new File(filePath);
        
        loadFile();
    }
    
    /**
     * Save to file with appended tagging at the end of line coordinates.
     */
    private void saveFile() {
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
                mainApp.getBeforeLines().stream()
                        .forEach((line) -> {
                            writer.println(line);
                        });

                for (TaggedLine taggedLine : mainApp.getTaggedLines()) {
                    writer.print(taggedLine.id + "," +
                            (int) taggedLine.getStartX() + "," +
                            (int) taggedLine.getEndX() + "," +
                            (int) taggedLine.getStartY() + "," +
                            (int) taggedLine.getEndY() + "," +
                            taggedLine.timeStart + ",");
                    
                    if (taggedLine.tag.isEmpty()) {
                        found: {
                            for (TaggedRectangle taggedRectangle : mainApp.getTaggedRectangles()) {
                                if (isInRectangle(taggedLine.asLine(), taggedRectangle.rect)) {
                                    writer.println(taggedLine.timeEnd + "," +
                                            taggedRectangle.tag);
                                    break found;
                                }
                            }
                            writer.println(taggedLine.timeEnd);
                        }
                    } else {
                        writer.println(taggedLine.timeEnd + "," +
                                taggedLine.tag);
                    }
                }

                mainApp.getAfterLines().stream()
                        .forEach((line) -> {
                            writer.println(line);
                        });

                loadFile(); // load again to refresh
            } catch (IOException ex) {
                logger.error(ExceptionFormatter.format(ex));
            }
        }
    }
    
    /**
     * Open FileChooser to select external file.
     * 
     * @param type
     */
    public void chooseFile(FileChooserType type) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TRACE Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        
        if (type == FileChooserType.OPEN) {
            fileChooser.setTitle("Open...");
            file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
        } else if (type == FileChooserType.SAVE) {
            fileChooser.setTitle("Save...");
            file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
        }
    }
    
    /**
     * Check if line is in the rectangle
     * @param line
     * @param rect
     * @return true if the line is in rectangle, false otherwise
     */
    private boolean isInRectangle(Line line, Rectangle2D rect) {
        return line.getStartX() >= rect.getMinX() && line.getStartX() <= rect.getMaxX()
                && line.getEndX() >= rect.getMinX() && line.getEndX() <= rect.getMaxX()
                && line.getStartY() >= rect.getMinY() && line.getStartY() <= rect.getMaxY()
                && line.getEndY() >= rect.getMinY() && line.getEndY() <= rect.getMaxY();
    }
    
    /**
     * Show dialog stating that the file is either corrupted or is not a TRACE file.
     */
    private void showWarningCorruptedFile() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("The file is either corrupted or is not a TRACE file");
        alert.showAndWait();
    }
    
    /**
     * Called by main application to make a reference back to itself.
     * 
     * @param mainApp 
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        
        drawingController.initCheckMenuItem();
    }
    
    /**
     * Return file loaded for current session.
     * 
     * @return File
     */
    public File getFile() {
        return file;
    }
    
    /**
     * Return file path.
     * 
     * @return File path string.
     */
    public String getFilePath() {
        return file.getPath();
    }
    
    /**
     * Return drawing sequence menu item.
     * 
     * @return CheckMenuItem for drawing sequence
     */
    public CheckMenuItem getDrawingSequenceMenu() {
        return drawingSequenceMenu;
    }
    
    /**
     * Return line label menu item.
     * 
     * @return CheckMenuItem for line label.
     */
    public CheckMenuItem getLineLabelMenu() {
        return lineLabelMenu;
    }
    
    /**
     * Return undo menu item.
     * 
     * @return MenuItem for undo.
     */
    public MenuItem getUndoMenu() {
        return undoMenu;
    }
    
    /**
     * Return redo menu item.
     * 
     * @return MenuItem for redo.
     */
    public MenuItem getRedoMenu() {
        return redoMenu;
    }
    
}
