package drawingtagger;

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
    
    private Stage stage;
    private ObservableList<TaggedRectangle> taggedRectangles;
    private ObservableList<WritableImage> backupStates;
    private Canvas mainCanvas;
    private GraphicsContext gc;
    private WritableImage image;
    private Rectangle2D rect;
    private int x, y;
    
    @FXML
    private void tagHash(ActionEvent event) {
        taggedRectangles.add(new TaggedRectangle(rect, "Hash"));
        gc.setFill(Color.RED);
        gc.fillText("Hash", rect.getMinX(), rect.getMinY() - PADDING);
        addBackupState();
        stage.close();
    }
    
    @FXML
    private void tagAppleTree(ActionEvent event) {
        taggedRectangles.add(new TaggedRectangle(rect, "AppleTree"));
        gc.setFill(Color.RED);
        gc.fillText("AppleTree", rect.getMinX(), rect.getMinY() - PADDING);
        addBackupState();
        stage.close();
    }
    
    @FXML
    private void tagBbqPit(ActionEvent event) {
        taggedRectangles.add(new TaggedRectangle(rect, "BbqPit"));
        gc.setFill(Color.RED);
        gc.fillText("BbqPit", rect.getMinX(), rect.getMinY() - PADDING);
        addBackupState();
        stage.close();
    }
    
    @FXML
    private void tagBee(ActionEvent event) {
        taggedRectangles.add(new TaggedRectangle(rect, "Bee"));
        gc.setFill(Color.RED);
        gc.fillText("Bee", rect.getMinX(), rect.getMinY() - PADDING);
        addBackupState();
        stage.close();
    }
    
    @FXML
    private void tagBicycle(ActionEvent event) {
        taggedRectangles.add(new TaggedRectangle(rect, "Bicycle"));
        gc.setFill(Color.RED);
        gc.fillText("Bicycle", rect.getMinX(), rect.getMinY() - PADDING);
        addBackupState();
        stage.close();
    }
    
    @FXML
    private void tagChair(ActionEvent event) {
        taggedRectangles.add(new TaggedRectangle(rect, "Chair"));
        gc.setFill(Color.RED);
        gc.fillText("Chair", rect.getMinX(), rect.getMinY() - PADDING);
        addBackupState();
        stage.close();
    }
    
    @FXML
    private void tagFork(ActionEvent event) {
        taggedRectangles.add(new TaggedRectangle(rect, "Fork"));
        gc.setFill(Color.RED);
        gc.fillText("Fork", rect.getMinX(), rect.getMinY() - PADDING);
        addBackupState();
        stage.close();
    }
    
    @FXML
    private void tagFountain(ActionEvent event) {
        taggedRectangles.add(new TaggedRectangle(rect, "Fountain"));
        gc.setFill(Color.RED);
        gc.fillText("Fountain", rect.getMinX(), rect.getMinY() - PADDING);
        addBackupState();
        stage.close();
    }
    
    @FXML
    private void tagGardenTable(ActionEvent event) {
        taggedRectangles.add(new TaggedRectangle(rect, "GardenTable"));
        gc.setFill(Color.RED);
        gc.fillText("GardenTable", rect.getMinX(), rect.getMinY() - PADDING);
        addBackupState();
        stage.close();
    }
    
    @FXML
    private void tagSaw(ActionEvent event) {
        taggedRectangles.add(new TaggedRectangle(rect, "Saw"));
        gc.setFill(Color.RED);
        gc.fillText("Saw", rect.getMinX(), rect.getMinY() - PADDING);
        addBackupState();
        stage.close();
    }
    
    @FXML
    private void tagSeesaw(ActionEvent event) {
        taggedRectangles.add(new TaggedRectangle(rect, "Seesaw"));
        gc.setFill(Color.RED);
        gc.fillText("Seesaw", rect.getMinX(), rect.getMinY() - PADDING);
        addBackupState();
        stage.close();
    }
    
    @FXML
    private void tagShears(ActionEvent event) {
        taggedRectangles.add(new TaggedRectangle(rect, "Shears"));
        gc.setFill(Color.RED);
        gc.fillText("Shears", rect.getMinX(), rect.getMinY() - PADDING);
        addBackupState();
        stage.close();
    }
    
    @FXML
    private void tagShedLamp(ActionEvent event) {
        taggedRectangles.add(new TaggedRectangle(rect, "ShedLamp"));
        gc.setFill(Color.RED);
        gc.fillText("ShedLamp", rect.getMinX(), rect.getMinY() - PADDING);
        addBackupState();
        stage.close();
    }
    
    @FXML
    private void tagShovel(ActionEvent event) {
        taggedRectangles.add(new TaggedRectangle(rect, "Shovel"));
        gc.setFill(Color.RED);
        gc.fillText("Shovel", rect.getMinX(), rect.getMinY() - PADDING);
        addBackupState();
        stage.close();
    }
    
    @FXML
    private void tagSwing(ActionEvent event) {
        taggedRectangles.add(new TaggedRectangle(rect, "Swing"));
        gc.setFill(Color.RED);
        gc.fillText("Swing", rect.getMinX(), rect.getMinY() - PADDING);
        addBackupState();
        stage.close();
    }
    
    @FXML
    private void tagTrampoline(ActionEvent event) {
        taggedRectangles.add(new TaggedRectangle(rect, "Trampoline"));
        gc.setFill(Color.RED);
        gc.fillText("Trampoline", rect.getMinX(), rect.getMinY() - PADDING);
        addBackupState();
        stage.close();
    }
    
    @FXML
    private void tagTulipFlower(ActionEvent event) {
        taggedRectangles.add(new TaggedRectangle(rect, "TulipFlower"));
        gc.setFill(Color.RED);
        gc.fillText("TulipFlower", rect.getMinX(), rect.getMinY() - PADDING);
        addBackupState();
        stage.close();
    }
    
    @FXML
    private void tagOther(ActionEvent event) {
        taggedRectangles.add(new TaggedRectangle(rect, "Other"));
        gc.setFill(Color.RED);
        gc.fillText("Other", rect.getMinX(), rect.getMinY() - PADDING);
        addBackupState();
        stage.close();
    }
    
    @FXML
    private void tagError(ActionEvent event) {
        taggedRectangles.add(new TaggedRectangle(rect, "Error"));
        gc.setFill(Color.RED);
        gc.fillText("Error", rect.getMinX(), rect.getMinY() - PADDING);
        addBackupState();
        stage.close();
    }
    
    @FXML
    private void close(ActionEvent event) {
        Rectangle2D dummy = new Rectangle2D(0, 0, 0, 0);
        taggedRectangles.add(new TaggedRectangle(dummy, "dummy"));
        addBackupState();
        stage.close();
    }
    
    private void addBackupState() {
        WritableImage wi = new WritableImage((int) mainCanvas.getWidth(), (int) mainCanvas.getHeight());
        mainCanvas.snapshot(null, wi);
        backupStates.add(wi);
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
    
    public void loadImage() {
        Canvas canvas = new Canvas(rect.getWidth(), rect.getHeight());
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.drawImage(image, 0, 0);
        topPane.getChildren().add(canvas);
    }
    
}
