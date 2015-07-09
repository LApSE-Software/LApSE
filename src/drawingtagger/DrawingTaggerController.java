package drawingtagger;

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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Burhanuddin
 */
public class DrawingTaggerController implements Initializable {
    
    private static final int ID = 0;
    private static final int X_START = 1;
    private static final int X_END = 2;
    private static final int Y_START = 3;
    private static final int Y_END = 4;
    private static final int TIME_START = 5;
    private static final int TIME_END = 6;
    
    private static final int GAP = 20;
    
    @FXML
    private AnchorPane drawingPane;
    
    private Stage mainStage;
    private Group group;
    private Canvas canvas;
    private Rectangle rect;
    private GraphicsContext gc;
    
    private ObservableList<String> beforeLines;
    private ObservableList<String> afterLines;
    private ObservableList<WritableImage> backupStates;
    
    private ObservableList<TaggedLine> taggedLines;
    private ObservableList<TaggedRectangle> taggedRectangles;
    private ObservableMap<String, ObservableList<String>> tags;
    private ObservableList<String> drawingTypeList;
    private int minWidth, minHeight;
    private double startX, startY;
    private WritableImage image;
    
    @FXML
    private void openFile(ActionEvent event) {
        File file = chooseFile(FileChooserType.OPEN);
        loadFile(file);
        findMinimumSize();
        loadCanvas();
    }
    
    private File chooseFile(FileChooserType type) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = null;
        if (type == FileChooserType.OPEN) {
            fileChooser.setTitle("Open...");
            file = fileChooser.showOpenDialog(mainStage);
        } else if (type == FileChooserType.SAVE) {
            fileChooser.setTitle("Save...");
            file = fileChooser.showSaveDialog(mainStage);
        }
        
        return file;
    }
    
    private void loadFile(File file) {
        if (file != null) {
            beforeLines.clear();
            afterLines.clear();
            taggedLines.clear();
            taggedRectangles.clear();
            backupStates.clear();
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String temp;
                boolean foundLine = false;
                
                while ((temp = reader.readLine()) != null) {
                    if (temp.equals("<<Extracted_Lines>>")) {
                        foundLine = true;
                        beforeLines.add(temp);
                        continue;
                    }
                    
                    if (foundLine) {
                        if (temp.startsWith("<<")) {
                            afterLines.add(temp);
                            break;
                        }
                        
                        TaggedLine line = loadLineFromString(temp);
                        taggedLines.add(line);
                    } else {
                        beforeLines.add(temp);
                    }
                }
                
                while ((temp = reader.readLine()) != null) {
                    afterLines.add(temp);
                }
                
                mainStage.setTitle(file.getPath() + " - " + DrawingTagger.TITLE);
            } catch (IOException ex) {
                Logger.getLogger(DrawingTaggerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private TaggedLine loadLineFromString(String temp) {
        String[] value = temp.split(",");
        int id = Integer.parseInt(value[ID]);
        int xStart = Integer.parseInt(value[X_START]);
        int xEnd = Integer.parseInt(value[X_END]);
        int yStart = Integer.parseInt(value[Y_START]);
        int yEnd = Integer.parseInt(value[Y_END]);
        long startTime = Long.parseLong(value[TIME_START]);
        long endTime = Long.parseLong(value[TIME_END]);
        
        Line line = new Line(xStart, yStart, xEnd, yEnd);
        return new TaggedLine(id, line, startTime, endTime);
    }
    
    private void findMinimumSize() {
        taggedLines.stream().forEach((taggedLine) -> {
            int xPref = (int) Math.max(taggedLine.line.getStartX(), taggedLine.line.getEndX());
            int yPref = (int) Math.max(taggedLine.line.getStartY(), taggedLine.line.getEndY());
            
            minWidth = Math.max(minWidth, xPref);
            minHeight = Math.max(minHeight, yPref);
        });
    }
    
    private void loadCanvas() {
        group = new Group();
        canvas = new Canvas(minWidth + GAP, minHeight + GAP);
        group.getChildren().add(canvas);
        drawingPane.getChildren().clear();
        drawingPane.getChildren().add(group);
        
        gc = canvas.getGraphicsContext2D();
        image = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        draw();
        addBackupState();
        
        canvas.setOnMousePressed((MouseEvent event) -> {
            group.getChildren().add(rect);
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
            openTagging(rect);
            
            gc.setStroke(Color.RED);
            gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
            group.getChildren().remove(rect);
        });
    }
    
//    private Rectangle2D createRectangleFromMouse(MouseEvent event) {
//        int x, y, width, height;
//        if (event.getX() > startX) {
//            x = startX;
//            width = (int) (event.getX() - startX);
//        } else {
//            x = (int) event.getX();
//            width = (int) (startX - event.getX());
//        }
//        if (event.getY() > startY) {
//            y = startY;
//            height = (int) (event.getY() - startY);
//        } else {
//            y = (int) event.getY();
//            height = (int) (startY - event.getY());
//        }
//        
//        return new Rectangle2D(x, y, width, height);
//    }
    
    private void draw() {
        gc.setStroke(Color.BLACK);
        taggedLines.stream().forEach((taggedLine) -> {
            gc.strokeLine(taggedLine.line.getStartX(), taggedLine.line.getStartY(),
                    taggedLine.line.getEndX(), taggedLine.line.getEndY());
        });
    }
    
    private void addBackupState() {
        WritableImage wi = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, wi);
        backupStates.add(wi);
        Rectangle2D rectangle2d = new Rectangle2D(0, 0, 0, 0);
        taggedRectangles.add(new TaggedRectangle(rectangle2d, "dummy"));
    }
    
    private void openTagging(Rectangle rect) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Tagging.fxml"));
        try {
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Tagging");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(mainStage);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
            
            TaggingController controller = (TaggingController) loader.getController();
            controller.setStage(stage);
            controller.setGraphicsContext(gc);
            controller.setSelectedImage(canvas, rect);
            controller.setBackupStates(backupStates);
            controller.setRectangleList(taggedRectangles);
            controller.setTags(tags, drawingTypeList);
            controller.loadImage();
            controller.loadDrawingType();
        } catch (IOException ex) {
            Logger.getLogger(DrawingTaggerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void saveFile(ActionEvent event) {
        File file = chooseFile(FileChooserType.SAVE);
        
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            beforeLines.stream().forEach((line) -> {
                writer.println(line);
            });
            
            for (TaggedLine taggedLine : taggedLines) {
                writer.print(taggedLine.id + ",");
                writer.print((int) taggedLine.line.getStartX() + ",");
                writer.print((int) taggedLine.line.getEndX() + ",");
                writer.print((int) taggedLine.line.getStartY() + ",");
                writer.print((int) taggedLine.line.getEndY() + ",");
                writer.print(taggedLine.timeStart + ",");
                found: {
                    for (TaggedRectangle taggedRectangle : taggedRectangles) {
                        if (isInRectangle(taggedLine.line, taggedRectangle.rect)) {
                            writer.print(taggedLine.timeEnd + ",");
                            writer.println(taggedRectangle.tag);
                            break found;
                        }
                    }
                    writer.println(taggedLine.timeEnd);
                }
            }
            
            afterLines.stream().forEach((line) -> {
                writer.println(line);
            });
            
            showFinishedSaving();
        } catch (IOException ex) {
            Logger.getLogger(DrawingTaggerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void showFinishedSaving() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Save");
        alert.setHeaderText(null);
        alert.setContentText("Done!");

        alert.showAndWait();
    }
    
    private boolean isInRectangle(Line line, Rectangle2D rect) {
        return line.getStartX() >= rect.getMinX() && line.getStartX() <= rect.getMaxX()
                && line.getEndX() >= rect.getMinX() && line.getEndX() <= rect.getMaxX()
                && line.getStartY() >= rect.getMinY() && line.getStartY() <= rect.getMaxY()
                && line.getEndY() >= rect.getMinY() && line.getEndY() <= rect.getMaxY();
    }
    
    @FXML
    private void undo(ActionEvent event) {
        if (backupStates.size() > 1) {
            backupStates.remove(backupStates.size() - 1);
            taggedRectangles.remove(taggedRectangles.size() - 1);
            gc.drawImage(backupStates.get(backupStates.size() - 1), 0, 0);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        beforeLines = FXCollections.observableArrayList();
        afterLines = FXCollections.observableArrayList();
        backupStates = FXCollections.observableArrayList();
        taggedLines = FXCollections.observableArrayList();
        taggedRectangles = FXCollections.observableArrayList();
        tags = FXCollections.observableHashMap();
        drawingTypeList = FXCollections.observableArrayList();
        rect = new Rectangle();
        rect.setFill(null);
        rect.getStrokeDashArray().addAll(5.0);
        rect.setStroke(Color.RED);
        minWidth = 0;
        minHeight = 0;
        loadTags("tags.txt");
    }
    
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
            Logger.getLogger(DrawingTaggerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }
    
}
