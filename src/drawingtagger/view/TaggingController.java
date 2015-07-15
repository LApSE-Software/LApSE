package drawingtagger.view;

import drawingtagger.MainApp;
import drawingtagger.model.TaggedRectangle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
    private Stage taggingStage;
    private Canvas mainCanvas;
    private GraphicsContext gc;
    private WritableImage image;
    private Rectangle2D rect;
    private int x, y;
    
    /**
     * Add backup state of canvas after tagged.
     */
    private void addBackupState() {
        WritableImage wi = mainCanvas.snapshot(null, null);
        mainApp.getBackupStates().add(wi);
    }
    
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
            mainApp.getTaggedRectangles().add(new TaggedRectangle(rect, selectedTag));
            gc.setFill(Color.RED);
            gc.fillText(selectedTag, rect.getMinX(), rect.getMinY() - PADDING);
            addBackupState();
            taggingStage.close();
        }
    }
    
    /**
     * Called from Cancel button. Cancel the tagging for current rectangle.
     * @param event 
     */
    @FXML
    private void cancel(ActionEvent event) {
        ObservableList<WritableImage> backupStates = mainApp.getBackupStates();
        gc.drawImage(backupStates.get(backupStates.size() - 1), 0, 0);
        taggingStage.close();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    /**
     * Called by main application to make a reference back to itself.
     * @param mainApp 
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
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
     * Called from RootLayoutController to draw the selected image from
     * main canvas on this stage.
     * @param mainCanvas
     * @param rectangle 
     */
    public void setSelectedImage(Canvas mainCanvas, Rectangle rectangle) {
        this.mainCanvas = mainCanvas;
        gc = mainCanvas.getGraphicsContext2D();
        this.rect = new Rectangle2D(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        SnapshotParameters param = new SnapshotParameters();
        param.setViewport(rect);
        image = mainCanvas.snapshot(param, null);
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
