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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lapse.MainApp;
import lapse.model.TaggedLine;
import lapse.model.TaggedRectangle;
import lapse.shape.Arrow;
import lapse.util.ExceptionFormatter;
import lapse.util.GT;
import lapse.util.TemporaryDataHolder;
import lapse.util.ZoomHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Burhanuddin
 */
public class DrawingController implements Initializable {

    private static final Logger logger = LogManager.getLogger();
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
    private ScrollPane scrollPane;
    
    public MainApp mainApp;
    
    private RootLayoutController root;
    private Group mainGroup;
    private Group lineLabelGroup;
    private ObservableList<Node> lineLabelBackup;
    private ObservableList<TaggedRectangle> taggedRectangleBackup;
    private Group lineSequenceGroup;
    private Group drawingSequenceGroup;
    private Group arrowGroup;
    private Group circleGroup;
    private Canvas canvas;
    private Rectangle rect;
    private GraphicsContext gc;
    
    private int minWidth, minHeight;
    private double startX, startY;
    
    /**
     * Show that copy of file is allowed indicated by the icon at mouse cursor.
     * @param event 
     */
    @FXML
    private void acceptFileTransferByDragAndDrop(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        } else {
            event.consume();
        }
    }
    
    /**
     * Called when file is drag and drop onto the pane.
     * @param event 
     */
    @FXML
    private void openFileByDragAndDrop(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            success = true;
            String filePath = db.getFiles().get(0).getAbsolutePath();
            root.loadFile(filePath);
        }
        event.setDropCompleted(success);
        event.consume();
    }
    
    /**
     * Clear all data. Called before opening new file.
     */
    public void clearData() {
        mainApp.clearData();
        lineSequenceGroup.getChildren().clear();
        lineLabelGroup.getChildren().clear();
        arrowGroup.getChildren().clear();
        circleGroup.getChildren().clear();
        drawingSequenceGroup.getChildren().clear();
        lineLabelBackup.clear();
        taggedRectangleBackup.clear();
    }
    
    /**
     * Load program after loading file.
     * @param tempDataHolder
     * @throws java.lang.Exception
     */
    public void loadProgram(TemporaryDataHolder tempDataHolder) throws Exception {
        clearData();
        loadDataFromTemporaryData(tempDataHolder);
        FXCollections.sort(mainApp.getTaggedLines());
        loadCanvas();
        generateLinesAndLabels();
        generateDrawingSequence();
        mainApp.getPrimaryStage().setTitle(root.getFilePath() + " - " + MainApp.TITLE);
    }
    
    /**
     * Load data to program from temporary data holder. Clears temporary data
     * afterwards.
     * @param tempDataHolder Temporary data holder.
     */
    public void loadDataFromTemporaryData(TemporaryDataHolder tempDataHolder) {
        if (!tempDataHolder.isEmpty("beforeLines")) {
            mainApp.getBeforeLines().addAll(tempDataHolder.retrieveTempData("beforeLines"));
        }
        if (!tempDataHolder.isEmpty("afterLines")) {
            mainApp.getAfterLines().addAll(tempDataHolder.retrieveTempData("afterLines"));
        }
        if (!tempDataHolder.isEmpty("taggedLines")) {
            tempDataHolder.retrieveTempData("taggedLines").stream()
                    .map((line) -> loadLineFromString(line))
                    .forEach((taggedLine) -> {
                        mainApp.getTaggedLines().add(taggedLine);
                    });
        }
    }
    
    /**
     * Generate lines and its label.
     */
    public void generateLinesAndLabels() {
        ObservableList<TaggedLine> taggedLines = mainApp.getTaggedLines();
        Group lineLabelFromFile = new Group();
        taggedLines.stream()
                .forEach((taggedLine) -> {
                    Point2D ptA = new Point2D(taggedLine.getStartX(), taggedLine.getStartY());
                    Point2D midPoint = ptA.midpoint(taggedLine.getEndX(), taggedLine.getEndY());
                    loadSequencePoint(lineSequenceGroup, midPoint);
                    if (!taggedLine.tag.isEmpty()) {    // if tag exists
                        Text text = new Text(midPoint.getX(), midPoint.getY(), taggedLine.tag);
                        text.setFill(Color.RED);
                        lineLabelFromFile.getChildren().add(text);
                    }
                });
        loadSequencePoint(lineSequenceGroup, null);    // remove last curve
        makeCurves(lineSequenceGroup);
        lineLabelGroup.getChildren().add(lineLabelFromFile);
    }
    
    /**
     * Generate drawing sequence from line sequence by grouping the same label.
     */
    public void generateDrawingSequence() {
        ObservableList<TaggedLine> curves = FXCollections.observableArrayList();
        String currentTag = mainApp.getTaggedLines().get(0).tag;
        for (TaggedLine taggedLine : mainApp.getTaggedLines()) {
            if (taggedLine.tag.equals(currentTag)) {
                curves.add(taggedLine);
            } else {
                currentTag = taggedLine.tag;
                Point2D midPoint = calculateMidPointOfLineGroup(curves);
                loadSequencePoint(drawingSequenceGroup, midPoint);
                curves.clear();
                curves.add(taggedLine);
            }
        }
        loadSequencePoint(drawingSequenceGroup, calculateMidPointOfLineGroup(curves));
        loadSequencePoint(drawingSequenceGroup, null);  // remove last curve
        makeCurves(drawingSequenceGroup);
        addArrows(drawingSequenceGroup);
        addCircles(drawingSequenceGroup);
    }
    
    /**
     * Calculate the mid point of grouped lines.
     * @param lines
     * @return 
     */
    private Point2D calculateMidPointOfLineGroup(ObservableList<TaggedLine> lines) {
        ObservableList<Point2D> midPoints = FXCollections.observableArrayList();
        lines.stream()
                .forEach((line) -> {
                    midPoints.add(new Point2D(line.getStartX(), line.getStartY())
                            .midpoint(line.getEndX(), line.getEndY()));
                });
        
        double totalX = 0.0, totalY = 0.0;
        for (Point2D pt : midPoints) {
            totalX += pt.getX();
            totalY += pt.getY();
        }
        
        return new Point2D(totalX / midPoints.size(), totalY / midPoints.size());
    }
    
    /**
     * Load cubic curve from two mid-points.
     * @param targetGroup
     * @param pt 
     */
    private void loadSequencePoint(Group targetGroup, Point2D pt) {
        ObservableList<Node> curves = targetGroup.getChildren();
        if (curves.isEmpty()) {
            CubicCurve curve = new CubicCurve();
            curve.setStartX(pt.getX());
            curve.setStartY(pt.getY());
            curve.setControlX1(pt.getX());
            curve.setControlY1(pt.getY());
            curve.setFill(null);
            curve.setStroke(Color.GREEN);
            curves.add(curve);
        } else if (pt != null) {
            CubicCurve curveA = (CubicCurve) curves.get(curves.size() - 1);     // get last curve
            curveA.setEndX(pt.getX());
            curveA.setEndY(pt.getY());
            curveA.setControlX2(pt.getX());
            curveA.setControlY2(pt.getY());
            
            CubicCurve curveB = new CubicCurve();
            curveB.setStartX(pt.getX());
            curveB.setStartY(pt.getY());
            curveB.setControlX1(pt.getX());
            curveB.setControlY1(pt.getY());
            curveB.setFill(null);
            curveB.setStroke(Color.GREEN);
            curves.add(curveB);
        } else {    // if null
            curves.remove(curves.size() - 1);   // remove last curve
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
        
        // add tag if there is extra token
        String tag = (value.length == NUMBER_OF_TOKENS + 1) ? value[TAG] : "";
        
        Line line = new Line(xStart, yStart, xEnd, yEnd);
        return new TaggedLine(id, line, startTime, endTime, tag);
    }
    
    /**
     * Find minimum size of canvas.
     */
    private void findMinimumCanvasSize() {
        minWidth = 0;
        minHeight = 0;
        mainApp.getTaggedLines().stream()
                .forEach((taggedLine) -> {
                    int xPref = (int) Math.max(taggedLine.getStartX(), taggedLine.getEndX());
                    int yPref = (int) Math.max(taggedLine.getStartY(), taggedLine.getEndY());
                    minWidth = Math.max(minWidth, xPref);
                    minHeight = Math.max(minHeight, yPref);
                });
    }
    
    /**
     * Load canvas and initialize its event handler.
     */
    public void loadCanvas() {
        findMinimumCanvasSize();
        canvas.setWidth(minWidth + GAP);
        canvas.setHeight(minHeight + GAP);
        
        mainGroup.getChildren().clear();
        mainGroup.getChildren().add(canvas);
        drawingPane.getChildren().clear();
        drawingPane.getChildren().add(mainGroup);
        
        if (root.getLineLabelMenu().selectedProperty().getValue()) {  // if lineLabelMenu is selected
            mainGroup.getChildren().add(lineLabelGroup);
        }
        if (root.getDrawingSequenceMenu().selectedProperty().getValue()) {  // if drawingSequenceMenu is selected
            mainGroup.getChildren().add(drawingSequenceGroup);
            mainGroup.getChildren().add(arrowGroup);
            mainGroup.getChildren().add(circleGroup);
        }
        
        gc = canvas.getGraphicsContext2D();
        
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        drawLines();
        
        canvas.setOnMousePressed((MouseEvent event) -> {
            initSelectionRectangle(event);
        });
        canvas.setOnMouseDragged((MouseEvent event) -> {
            resizeSelectionRectangle(event);
        });
        canvas.setOnMouseReleased((MouseEvent event) -> {
            finishSelectionRectangle(event);
        });
    }
    
    /**
     * Allow panning of scroll pane.
     * @param event 
     */
    @FXML
    private void enablePan(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            scrollPane.setPannable(true);
        }
    }
    
    /**
     * Disallow panning of scroll pane.
     * @param event 
     */
    @FXML
    private void disablePan(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            scrollPane.setPannable(false);
        }
    }
    
    /**
     * Called when mouse pressed on canvas.
     * @param event 
     */
    private void initSelectionRectangle(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            mainGroup.getChildren().add(rect);
            startX = event.getX();
            startY = event.getY();
            rect.setX(event.getX());
            rect.setY(event.getY());
            rect.setWidth(0);
            rect.setHeight(0);
        }
    }
    
    /**
     * Called when mouse dragged on canvas.
     * @param event 
     */
    private void resizeSelectionRectangle(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
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
        }
    }
    
    /**
     * Called when mouse released on canvas.
     * @param event 
     */
    private void finishSelectionRectangle(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            if (rect.getWidth() != 0 && rect.getHeight() != 0) {
                openTaggingWindow(rect);
            }
            mainGroup.getChildren().remove(rect);
        }
    }
    
    /**
     * Draw lines according to the line coordinate from TRACE file.
     */
    private void drawLines() {
        gc.setStroke(Color.BLACK);
        mainApp.getTaggedLines().stream()
                .forEach((taggedLine) -> {
                    gc.strokeLine(taggedLine.getStartX(),
                                    taggedLine.getStartY(),
                                    taggedLine.getEndX(),
                                    taggedLine.getEndY());
                });
    }
    
    /**
     * Called on mouse released from canvas. Open Tagging window.
     * @param rect 
     */
    private void openTaggingWindow(Rectangle rect) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Tagging.fxml"));
        try {
            Parent taggingWindowRoot = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Tagging");
            stage.getIcons().add(mainApp.getPrimaryStage().getIcons().get(0));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(mainApp.getPrimaryStage());
            Scene scene = new Scene(taggingWindowRoot);
            stage.setScene(scene);
            
            TaggingController controller = loader.getController();
            controller.setRootLayout(this);
            controller.setTaggingStage(stage);
            controller.setSelectedImage(rect);
            
            stage.show();
        } catch (IOException ex) {
            logger.error(ExceptionFormatter.format(ex));
        }
    }
    
    /**
     * Return main canvas.
     * @return 
     */
    public Canvas getMainCanvas() {
        return canvas;
    }
    
    /**
     * Return a list of line label groups.
     * @return 
     */
    public ObservableList<Node> getLineLabelGroup() {
        return lineLabelGroup.getChildren();
    }
    
    /**
     * Remove the latest rectangle and revert the canvas back to previous state.
     * 
     * @param event 
     */
    public void undo(ActionEvent event) {
        ObservableList<Node> lineLabels = lineLabelGroup.getChildren();
        ObservableList<TaggedRectangle> taggedRectangles = mainApp.getTaggedRectangles();
        
        if (lineLabels.size() > 1) {
            Node removedNode = lineLabels.remove(lineLabels.size() - 1);   // remove last
            TaggedRectangle removedRect = taggedRectangles.remove(taggedRectangles.size() - 1);   // remove last
            if (removedNode != null && removedRect != null) {
                lineLabelBackup.add(removedNode);
                taggedRectangleBackup.add(removedRect);
            }
        }
    }
    
    /**
     * Remove last label from backup and add to current labels.
     * 
     * @param event 
     */
    public void redo(ActionEvent event) {
        ObservableList<Node> lineLabels = lineLabelGroup.getChildren();
        ObservableList<TaggedRectangle> taggedRectangles = mainApp.getTaggedRectangles();
        
        if (lineLabelBackup.size() > 0) {
            Node removedNode = lineLabelBackup.remove(lineLabelBackup.size() - 1); // remove last
            TaggedRectangle removedRect = taggedRectangleBackup.remove(taggedRectangleBackup.size() - 1);  // remove last
            if (removedNode != null && removedRect != null) {
                lineLabels.add(removedNode);
                taggedRectangles.add(removedRect);
            }
        }
    }
    
    /**
     * Clear all tags including tags from file.
     * 
     * @param event 
     */
    public void clearTags(ActionEvent event) {
        mainApp.getTaggedLines().stream()
                .filter((taggedLine) -> (!taggedLine.tag.isEmpty()))
                .forEach((taggedLine) -> {
                    taggedLine.tag = "";
                });
        lineLabelGroup.getChildren().clear();
        lineLabelGroup.getChildren().add(new Text());   // dummy
        drawingSequenceGroup.getChildren().clear();
        arrowGroup.getChildren().clear();
        circleGroup.getChildren().clear();
    }
    
    /**
     * Make curves from the drawing sequence.
     */
    private void makeCurves(Group targetGroup) {
        ObservableList<Node> curves = targetGroup.getChildren();
        if (curves.size() < 3) {
            return;
        }
        double ratio = 0.1;
        
        CubicCurve curve1, curve2, curve3;
        double angle12, angle23, theta12, theta23;
        
        curve1 = (CubicCurve) curves.get(0);
        curve2 = (CubicCurve) curves.get(1);
        
        angle12 = GT.angle(curve1, curve2);
        theta12 = (Math.PI - Math.abs(angle12)) / 2;
        
        GT.scale(curve1, ratio);
        if (angle12 < 0) {
            GT.rotate(curve1, -theta12, false);
        } else {
            GT.rotate(curve1, theta12, false);
        }
        
        for (int i = 0; i < curves.size() - 2; i++) {
            curve1 = (CubicCurve) curves.get(i);
            curve2 = (CubicCurve) curves.get(i + 1);
            curve3 = (CubicCurve) curves.get(i + 2);
            
            angle12 = GT.angle(curve1, curve2);
            angle23 = GT.angle(curve2, curve3);
            theta12 = (Math.PI - Math.abs(angle12)) / 2;
            theta23 = (Math.PI - Math.abs(angle23)) / 2;
            
            GT.scale(curve2, ratio);
            if (angle12 < 0) {
                GT.rotate(curve2, theta12, true);
            } else {
                GT.rotate(curve2, -theta12, true);
            }
            
            if (angle23 < 0) {
                GT.rotate(curve2, -theta23, false);
            } else {
                GT.rotate(curve2, theta23, false);
            }
        }
        
        curve2 = (CubicCurve) curves.get(curves.size() - 2);
        curve3 = (CubicCurve) curves.get(curves.size() - 1);
        
        angle23 = GT.angle(curve2, curve3);
        theta23 = (Math.PI - Math.abs(angle23)) / 2;
        
        GT.scale(curve3, ratio);
        if (angle23 < 0) {
            GT.rotate(curve3, theta23, true);
        } else {
            GT.rotate(curve3, -theta23, true);
        }
    }
    
    /**
     * Add arrow image on cubic curve group.
     * @param targetGroup 
     */
    private void addArrows(Group targetGroup) {
        ObservableList<Node> curves = targetGroup.getChildren();
        ObservableList<Node> arrows = arrowGroup.getChildren();
        curves.stream()
                .map((node) -> (CubicCurve) node)
                .forEach((curve) -> {
                    arrows.add(new Arrow(curve, 0.5f));
                });
    }
    
    /**
     * Add circles to mark the start and the end of the curve.
     * @param targetGroup 
     */
    private void addCircles(Group targetGroup) {
        if (targetGroup.getChildren().size() == 0) {
            return;
        }
        
        ObservableList<Node> curves = targetGroup.getChildren();
        ObservableList<Node> circles = circleGroup.getChildren();
        double radius = 10d;
        
        CubicCurve firstCurve = (CubicCurve) curves.get(0);
        Circle firstCircle = new Circle(firstCurve.getStartX(), firstCurve.getStartY(), radius);
        firstCircle.setStroke(Color.GREEN);
        firstCircle.setFill(Color.LIGHTCORAL);
        circles.add(firstCircle);
        
        curves.stream()
                .map((node) -> (CubicCurve) node)
                .map((curve) -> new Circle(curve.getEndX(), curve.getEndY(), radius))
                .map((circle) -> {
                    circle.setStroke(Color.GREEN);
                    return circle;
                })
                .map((circle) -> {
                    circle.setFill(Color.LIGHTBLUE);
                    return circle;
                })
                .forEach((circle) -> {
                    circles.add(circle);
                });
        
        Circle lastCircle = (Circle) circles.get(circles.size() - 1);
        lastCircle.setFill(Color.LIGHTGREEN);
    }
    
    /**
     * Clear backup.
     */
    public void clearBackup() {
        lineLabelBackup.clear();
        taggedRectangleBackup.clear();
    }
    
    /**
     * Called from Animate Sequence menu. Show drawing sequence using animation.
     * @param event 
     */
    @FXML
    private void animateSequence(ActionEvent event) {
        // TODO
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mainGroup = new Group();
        lineLabelGroup = new Group();
        lineLabelBackup = FXCollections.observableArrayList();
        taggedRectangleBackup = FXCollections.observableArrayList();
        lineSequenceGroup = new Group();
        drawingSequenceGroup = new Group();
        arrowGroup = new Group();
        circleGroup = new Group();
        canvas = new Canvas();
        rect = new Rectangle();
        rect.setFill(null);
        rect.getStrokeDashArray().addAll(5.0);
        rect.setStroke(Color.RED);
        minWidth = 0;
        minHeight = 0;
    }
    
    /**
     * Initialize zoom handling for scroll pane.
     */
    private void initZoomHandling() {
        scrollPane.setContent(drawingPane);
        scrollPane.addEventFilter(ScrollEvent.ANY, new ZoomHandler(mainGroup, drawingPane, scrollPane));
        scrollPane.viewportBoundsProperty()
                .addListener((ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) -> {
                    drawingPane.setPrefSize(
                            Math.max(mainGroup.getBoundsInParent().getMaxX(), newValue.getWidth()),
                            Math.max(mainGroup.getBoundsInParent().getMaxY(), newValue.getHeight())
                    );
                });
        mainGroup.boundsInParentProperty()
                .addListener((ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) -> {
                    drawingPane.setPrefSize(
                            Math.max(newValue.getMaxX(), scrollPane.getViewportBounds().getWidth()),
                            Math.max(newValue.getMaxY(), scrollPane.getViewportBounds().getHeight())
                    );
                });
    }
    
    /**
     * Called by main application to make a reference back to itself. At the
     * same time initialize backup states listener to only enable undo and redo
     * button when there are backup states.
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        lineLabelGroup.getChildren()
                .addListener((ListChangeListener.Change<? extends Node> c) -> {
                    if (lineLabelGroup.getChildren().size() > 1) {  // 1 is loaded from file
                        root.getUndoMenu().setDisable(false);
                    } else {
                        root.getUndoMenu().setDisable(true);
                    }
                });
        lineLabelBackup.addListener((ListChangeListener.Change<? extends Node> c) -> {
            if (lineLabelBackup.size() > 0) {
                root.getRedoMenu().setDisable(false);
            } else {
                root.getRedoMenu().setDisable(true);
            }
        });
        
        initZoomHandling();
    }
    
    /**
     * Make a reference to the root layout.
     * 
     * @param root RootLayoutController.
     */
    public void setRootLayout(RootLayoutController root) {
        this.root = root;
    }
    
    /**
     * Initialize ChangeListener for Line Label menu and Drawing Sequence menu.
     */
    public void initCheckMenuItem() {
        root.getLineLabelMenu().selectedProperty()
                .addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isSelected) -> {
                    if (isSelected) {
                        mainGroup.getChildren().add(lineLabelGroup);
                    } else {
                        mainGroup.getChildren().remove(lineLabelGroup);
                    }
                });
        root.getDrawingSequenceMenu().selectedProperty()
                .addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isSelected) -> {
                    if (isSelected) {
                        mainGroup.getChildren().add(drawingSequenceGroup);
                        mainGroup.getChildren().add(arrowGroup);
                        mainGroup.getChildren().add(circleGroup);
                    } else {
                        mainGroup.getChildren().remove(drawingSequenceGroup);
                        mainGroup.getChildren().remove(arrowGroup);
                        mainGroup.getChildren().remove(circleGroup);
                    }
                });
    }
    
    /**
     * Return main group.
     * 
     * @return Main group.
     */
    public Group getMainGroup() {
        return mainGroup;
    }
    
}
