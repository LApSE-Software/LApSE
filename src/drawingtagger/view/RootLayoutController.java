package drawingtagger.view;

import drawingtagger.util.FileChooserType;
import drawingtagger.MainApp;
import drawingtagger.model.TaggedLine;
import drawingtagger.model.TaggedRectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Burhanuddin
 */
public class RootLayoutController implements Initializable {
    
    private static final int ID = 0;
    private static final int X_START = 1;
    private static final int X_END = 2;
    private static final int Y_START = 3;
    private static final int Y_END = 4;
    private static final int TIME_START = 5;
    private static final int TIME_END = 6;
    private static final int NUMBER_OF_TOKENS = TIME_END + 1;
    private static final int TAG = NUMBER_OF_TOKENS;
    
    private static final int GAP = 20;
    
    @FXML
    private AnchorPane drawingPane;
    @FXML
    private MenuItem undoMenu;
    @FXML
    private CheckMenuItem drawingSequenceMenu;
    @FXML
    private CheckMenuItem lineLabelMenu;
    
    private MainApp mainApp;
    private Group mainGroup;
    private Group lineLabelGroup;
    private Group drawingSequenceGroup;
    private Canvas canvas;
    private Rectangle rect;
    private GraphicsContext gc;
    
    private int minWidth, minHeight;
    private double startX, startY;
    private WritableImage image;
    
    /**
     * Called from 'Quit' menu. Close the program obviously.
     * @param event 
     */
    @FXML
    private void quit(ActionEvent event) {
        mainApp.getPrimaryStage().close();
    }
    
    /**
     * Called from 'Open...' menu. Open TRACE file, and load it to canvas.
     * @param event 
     */
    @FXML
    private void openFile(ActionEvent event) {
        File file = chooseFile(FileChooserType.OPEN);
        loadFile(file);
        findMinimumSize();
        loadCanvas();
    }
    
    /**
     * Open FileChooser to select external file.
     * @param type
     * @return file
     */
    private File chooseFile(FileChooserType type) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = null;
        if (type == FileChooserType.OPEN) {
            fileChooser.setTitle("Open...");
            file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
        } else if (type == FileChooserType.SAVE) {
            fileChooser.setTitle("Save...");
            file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
        }
        
        return file;
    }
    
    /**
     * Load TRACE file.
     * @param file 
     */
    private void loadFile(File file) {
        if (file != null) {
            mainApp.clearData();
            lineLabelGroup.getChildren().clear();
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String temp;
                boolean foundLine = false;
                
                while ((temp = reader.readLine()) != null) {
                    if (temp.equals("<<Extracted_Lines>>")) {
                        foundLine = true;
                        mainApp.getBeforeLines().add(temp);
                        continue;
                    }
                    
                    if (foundLine) {
                        if (temp.startsWith("<<")) {
                            mainApp.getAfterLines().add(temp);
                            break;
                        }
                        
                        TaggedLine taggedLine = loadLineFromString(temp);
                        mainApp.getTaggedLines().add(taggedLine);
                        if (!taggedLine.tag.isEmpty()) {  // if tag exists
                            Point2D ptA = new Point2D(taggedLine.line.getStartX(), taggedLine.line.getStartY());
                            Point2D midPoint = ptA.midpoint(taggedLine.line.getEndX(), taggedLine.line.getEndY());
                            Text text = new Text(midPoint.getX(), midPoint.getY(), taggedLine.tag);
                            text.setFill(Color.RED);
                            lineLabelGroup.getChildren().add(text);
                        }
                    } else {
                        mainApp.getBeforeLines().add(temp);
                    }
                }
                
                while ((temp = reader.readLine()) != null) {
                    mainApp.getAfterLines().add(temp);
                }
                
                mainApp.getPrimaryStage().setTitle(file.getPath() + " - " + MainApp.TITLE);
            } catch (IOException ex) {
                Logger.getLogger(RootLayoutController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Load line coordinate from string.
     * @param temp
     * @return taggedLine
     */
    private TaggedLine loadLineFromString(String temp) {
        String[] value = temp.split(",");
        int id = Integer.parseInt(value[ID]);
        int xStart = Integer.parseInt(value[X_START]);
        int xEnd = Integer.parseInt(value[X_END]);
        int yStart = Integer.parseInt(value[Y_START]);
        int yEnd = Integer.parseInt(value[Y_END]);
        long startTime = Long.parseLong(value[TIME_START]);
        long endTime = Long.parseLong(value[TIME_END]);
        // if there is extra token (tag)
        String tag = (value.length == NUMBER_OF_TOKENS + 1) ? value[TAG] : "";
        
        Line line = new Line(xStart, yStart, xEnd, yEnd);
        return new TaggedLine(id, line, startTime, endTime, tag);
    }
    
    /**
     * Find minimum size of canvas.
     */
    private void findMinimumSize() {
        minWidth = 0;
        minHeight = 0;
        mainApp.getTaggedLines().stream().forEach((taggedLine) -> {
            int xPref = (int) Math.max(taggedLine.line.getStartX(), taggedLine.line.getEndX());
            int yPref = (int) Math.max(taggedLine.line.getStartY(), taggedLine.line.getEndY());
            
            minWidth = Math.max(minWidth, xPref);
            minHeight = Math.max(minHeight, yPref);
        });
    }
    
    /**
     * Load canvas and initialize its event handler.
     */
    private void loadCanvas() {
        canvas.setWidth(minWidth + GAP);
        canvas.setHeight(minHeight + GAP);
        mainGroup.getChildren().clear();
        mainGroup.getChildren().add(canvas);
        drawingPane.getChildren().clear();
        drawingPane.getChildren().add(mainGroup);
        if (lineLabelMenu.selectedProperty().getValue()) {  // if lineLabelMenu is selected
            mainGroup.getChildren().add(lineLabelGroup);
        }
        
        gc = canvas.getGraphicsContext2D();
        image = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        draw();
        addBackupState();
        
        canvas.setOnMousePressed((MouseEvent event) -> {
            mainGroup.getChildren().add(rect);
            startX = event.getX();
            startY = event.getY();
            rect.setX(event.getX());
            rect.setY(event.getY());
            rect.setWidth(0);
            rect.setHeight(0);
            canvas.snapshot(null, image);
        });
        canvas.setOnMouseDragged((MouseEvent event) -> {
            double x, y, width, height;
            if (event.getX() > startX) {
                x = startX;
                if (event.getX() > canvas.getWidth()) {
                    width = canvas.getWidth() - startX - 1.0;
                } else {
                    width = event.getX() - startX;
                }
            } else {
                if (event.getX() < 0.0) {
                    x = 1.0;
                    width = startX;
                } else {
                    x = event.getX();
                    width = startX - event.getX();
                }
            }
            if (event.getY() > startY) {
                y = startY;
                if (event.getY() > canvas.getHeight()) {
                    height = canvas.getHeight() - startY - 1.0;
                } else {
                    height = event.getY() - startY;
                }
            } else {
                if (event.getY() < 0.0) {
                    y = 1.0;
                    height = startY;
                } else {
                    y = event.getY();
                    height = startY - event.getY();
                }
            }

            rect.setX(x);
            rect.setY(y);
            rect.setWidth(width);
            rect.setHeight(height);
        });
        canvas.setOnMouseReleased((MouseEvent event) -> {
            if (rect.getWidth() != 0 && rect.getHeight() != 0) {
                openTagging(rect);

                gc.setStroke(Color.RED);
                gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
            }
            mainGroup.getChildren().remove(rect);
        });
    }
    
    /**
     * Draw lines according to the line coordinate from TRACE file.
     */
    private void draw() {
        gc.setStroke(Color.BLACK);
        mainApp.getTaggedLines().stream().forEach((taggedLine) -> {
            gc.strokeLine(taggedLine.line.getStartX(), taggedLine.line.getStartY(),
                    taggedLine.line.getEndX(), taggedLine.line.getEndY());
        });
    }
    
    /**
     * Add backup state for canvas to roll to on undo.
     */
    private void addBackupState() {
        WritableImage wi = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, wi);
        mainApp.getBackupStates().add(wi);
        Rectangle2D rectangle2d = new Rectangle2D(0, 0, 0, 0);
        mainApp.getTaggedRectangles().add(new TaggedRectangle(rectangle2d, "dummy"));
    }
    
    /**
     * Called on mouse released from canvas. Open Tagging window.
     * @param rect 
     */
    private void openTagging(Rectangle rect) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Tagging.fxml"));
        try {
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Tagging");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(mainApp.getPrimaryStage());
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            
            TaggingController controller = loader.getController();
            controller.setMainApp(mainApp);
            controller.setTaggingStage(stage);
            controller.setSelectedImage(canvas, rect);
            
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(RootLayoutController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Save tagging to the selected file. The tagging will be appended at the
     * end of the line coordinate.
     * @param event 
     */
    @FXML
    private void saveFile(ActionEvent event) {
        File file = chooseFile(FileChooserType.SAVE);
        
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            mainApp.getBeforeLines().stream().forEach((line) -> {
                writer.println(line);
            });
            
            for (TaggedLine taggedLine : mainApp.getTaggedLines()) {
                writer.print(taggedLine.id + ",");
                writer.print((int) taggedLine.line.getStartX() + ",");
                writer.print((int) taggedLine.line.getEndX() + ",");
                writer.print((int) taggedLine.line.getStartY() + ",");
                writer.print((int) taggedLine.line.getEndY() + ",");
                writer.print(taggedLine.timeStart + ",");
                found: {
                    for (TaggedRectangle taggedRectangle : mainApp.getTaggedRectangles()) {
                        if (isInRectangle(taggedLine.line, taggedRectangle.rect)) {
                            writer.print(taggedLine.timeEnd + ",");
                            writer.println(taggedRectangle.tag);
                            break found;
                        }
                    }
                    writer.println(taggedLine.timeEnd);
                }
            }
            
            mainApp.getAfterLines().stream().forEach((line) -> {
                writer.println(line);
            });
            
            showFinishedSaving();
        } catch (IOException ex) {
            Logger.getLogger(RootLayoutController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Show dialog box confirming that file have been saved.
     */
    private void showFinishedSaving() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Save");
        alert.setHeaderText(null);
        alert.setContentText("Done!");

        alert.showAndWait();
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
     * Called from 'Undo' menu. Remove the latest rectangle and revert
     * the canvas back to previous state.
     * @param event 
     */
    @FXML
    private void undo(ActionEvent event) {
        ObservableList<WritableImage> backupStates = mainApp.getBackupStates();
        ObservableList<TaggedRectangle> taggedRectangles = mainApp.getTaggedRectangles();
        
        if (backupStates.size() > 1) {
            backupStates.remove(backupStates.size() - 1);
            taggedRectangles.remove(taggedRectangles.size() - 1);
            gc.drawImage(backupStates.get(backupStates.size() - 1), 0, 0);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mainGroup = new Group();
        lineLabelGroup = new Group();
        drawingSequenceGroup = new Group();
        canvas = new Canvas();
        rect = new Rectangle();
        rect.setFill(null);
        rect.getStrokeDashArray().addAll(5.0);
        rect.setStroke(Color.RED);
        minWidth = 0;
        minHeight = 0;
        initCheckMenuItem();
    }
    
    /**
     * Initialize ChangeListener for Line Label menu and Drawing Sequence menu.
     */
    private void initCheckMenuItem() {
        drawingSequenceMenu.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isSelected) -> {
            if (isSelected) {
                mainGroup.getChildren().add(drawingSequenceGroup);
            } else {
                mainGroup.getChildren().remove(drawingSequenceGroup);
            }
        });
        lineLabelMenu.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isSelected) -> {
            if (isSelected) {
                mainGroup.getChildren().add(lineLabelGroup);
            } else {
                mainGroup.getChildren().remove(lineLabelGroup);
            }
        });
    }
    
    /**
     * Called by main application to make a reference back to itself. At the
     * same time initialize backup states listener to only enable undo button
     * when there are backup states.
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        mainApp.getBackupStates().addListener((ListChangeListener.Change<? extends WritableImage> c) -> {
            if (mainApp.getBackupStates().size() > 1) {
                undoMenu.setDisable(false);
            } else {
                undoMenu.setDisable(true);
            }
        });
    }
    
}
