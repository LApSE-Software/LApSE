package drawingtagger.view;

import drawingtagger.MainApp;
import drawingtagger.model.TaggedRectangle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Burhanuddin
 */
public class TaggingController implements Initializable {

    private static final int PADDING = 2;
    
    @FXML
    private AnchorPane topPane;
    @FXML
    private ComboBox<String> drawingType;
    @FXML
    private ComboBox<String> tag;
    
    private MainApp mainApp;
    private RootLayoutController root;
    private Stage taggingStage;
    private Canvas mainCanvas;
    private GraphicsContext gc;
    private WritableImage image;
    private Rectangle2D rect;
    private int x, y;
    
    /**
     * Called when a value from Drawing Type combo box is selected. Load all
     * available tags for that particular drawing type.
     * @param event 
     */
    @FXML
    private void loadTags(ActionEvent event) {
        String selectedDrawingType = drawingType.getValue();
        ObservableList<String> tagList = mainApp.getTags().get(selectedDrawingType);
        tag.setItems(tagList);
    }
    
    /**
     * Called from Tag button. Add tagged rectangle to the drawing.
     * @param event 
     */
    @FXML
    private void tag(ActionEvent event) {
        String selectedTag = tag.getValue();
        if (selectedTag != null &&  !selectedTag.isEmpty()) {
            TaggedRectangle taggedArea = new TaggedRectangle(rect, selectedTag);
            mainApp.getTaggedRectangles().add(taggedArea);
            
            Group lineLabel = new Group();
            mainApp.getTaggedLines().stream().filter((taggedLine) 
                    -> (isInRectangle(taggedLine.asLine(), rect))).map((taggedLine) 
                            -> new Point2D(taggedLine.getStartX(), taggedLine.getStartY())
                                    .midpoint(taggedLine.getEndX(), taggedLine.getEndY())).map((midPoint) 
                                    -> new Text(midPoint.getX(), midPoint.getY(), selectedTag)).map((text) 
                                            -> {
                text.setFill(Color.BLUE);
                return text;
            }).forEach((text) -> {
                lineLabel.getChildren().add(text);
            });
            root.getLineLabelGroup().add(lineLabel);
            
            taggingStage.close();
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
     * Called from Cancel button. Cancel the tagging for current rectangle.
     * @param event 
     */
    @FXML
    private void cancel(ActionEvent event) {
        taggingStage.close();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    /**
     * Called from RootLayoutController to make reference to the stage. Useful
     * to make a button to close the stage from within.
     * @param taggingStage 
     */
    public void setTaggingStage(Stage taggingStage) {
        this.taggingStage = taggingStage;
    }
    
    /**
     * Called from root layout to make reference to itself.
     * @param root 
     */
    public void setRootLayout(RootLayoutController root) {
        this.root = root;
        this.mainApp = root.mainApp;
    }
    
    /**
     * Called from RootLayoutController to draw the selected image from
     * main canvas on this stage.
     * @param rectangle 
     */
    public void setSelectedImage(Rectangle rectangle) {
        this.mainCanvas = root.getMainCanvas();
        this.gc = mainCanvas.getGraphicsContext2D();
        this.rect = new Rectangle2D(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        SnapshotParameters param = new SnapshotParameters();
        param.setViewport(rect);
        this.image = mainCanvas.snapshot(param, null);
        loadImage();
        loadDrawingType();
    }
    
    /**
     * Draw the selected image on new canvas.
     */
    private void loadImage() {
        Canvas canvas = new Canvas(rect.getWidth(), rect.getHeight());
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.drawImage(image, 0, 0);
        topPane.getChildren().add(canvas);
    }
    
    /**
     * Load items for Drawing Type combo box.
     */
    private void loadDrawingType() {
        drawingType.setItems(mainApp.getDrawingTypeList());
    }
    
}
