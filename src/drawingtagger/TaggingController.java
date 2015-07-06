package drawingtagger;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
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
    
    private Stage stage;
    private ObservableList<TaggedRectangle> taggedRectangles;
    private ObservableList<WritableImage> backupStates;
    private ObservableMap<String, ObservableList<String>> tags;
    private ObservableList<String> drawingTypeList;
    private Canvas mainCanvas;
    private GraphicsContext gc;
    private WritableImage image;
    private Rectangle2D rect;
    private int x, y;
    
    private void addBackupState() {
        WritableImage wi = new WritableImage((int) mainCanvas.getWidth(), (int) mainCanvas.getHeight());
        mainCanvas.snapshot(null, wi);
        backupStates.add(wi);
    }
    
    @FXML
    private void loadTags(ActionEvent event) {
        String selectedDrawingType = drawingType.getValue();
        ObservableList<String> tagList = tags.get(selectedDrawingType);
        tag.setItems(tagList);
    }
    
    @FXML
    private void tag(ActionEvent event) {
        String selectedTag = tag.getValue();
        if (selectedTag != null &&  !selectedTag.isEmpty()) {
            taggedRectangles.add(new TaggedRectangle(rect, selectedTag));
            gc.setFill(Color.RED);
            gc.fillText(selectedTag, rect.getMinX(), rect.getMinY() - PADDING);
            addBackupState();
            stage.close();
        }
    }
    
    @FXML
    private void cancel(ActionEvent event) {
        Rectangle2D dummy = new Rectangle2D(0, 0, 0, 0);
        taggedRectangles.add(new TaggedRectangle(dummy, "dummy"));
        addBackupState();
        stage.close();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public void setGraphicsContext(GraphicsContext gc) {
        this.gc = gc;
    }
    
    public void setSelectedImage(Canvas canvas, Rectangle2D rect) {
        this.mainCanvas = canvas;
        this.rect = rect;
        image = new WritableImage((int) rect.getWidth(), (int) rect.getHeight());
        SnapshotParameters param = new SnapshotParameters();
        param.setViewport(rect);
        canvas.snapshot(param, image);
    }
    
    public void setBackupStates(ObservableList<WritableImage> backupStates) {
        this.backupStates = backupStates;
    }
    
    public void setRectangleList(ObservableList<TaggedRectangle> taggedRectangles) {
        this.taggedRectangles = taggedRectangles;
    }
    
    public void setTags(ObservableMap<String, ObservableList<String>> tags,
            ObservableList<String> drawingTypeList) {
        this.tags = tags;
        this.drawingTypeList = drawingTypeList;
    }
    
    public void loadImage() {
        Canvas canvas = new Canvas(rect.getWidth(), rect.getHeight());
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.drawImage(image, 0, 0);
        topPane.getChildren().add(canvas);
    }
    
    public void loadDrawingType() {
        drawingType.setItems(drawingTypeList);
    }
    
}
