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
package lapse.util;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Burhanuddin
 */
public class ZoomHandler implements EventHandler<ScrollEvent> {

    private static final double MIN_SCALE = .5d;
    
    private final Node nodeToZoom;
    private final AnchorPane nodeContainer;
    private final ScrollPane scrollPane;
    
    public ZoomHandler(Node nodeToZoom, AnchorPane nodeContainer, ScrollPane pane) {
        this.nodeToZoom = nodeToZoom;
        this.nodeContainer = nodeContainer;
        this.scrollPane = pane;
    }
    
    @Override
    public void handle(ScrollEvent event) {
        final double scale = calculateScale(event);
        nodeToZoom.setScaleX(scale);
        nodeToZoom.setScaleY(scale);
        
        Platform.runLater(() -> {
            nodeContainer.setPrefSize(
                    Math.max(nodeContainer.getBoundsInParent().getMaxX(), scrollPane.getViewportBounds().getWidth()),
                    Math.max(nodeContainer.getBoundsInParent().getMaxY(), scrollPane.getViewportBounds().getHeight())
            );
        });
        
        event.consume();
    }
    
    private double calculateScale(ScrollEvent event) {
        double scale = nodeToZoom.getScaleX() + event.getDeltaY() / 200;
        
        if (scale <= MIN_SCALE) {
            scale = MIN_SCALE;
        }
        return scale;
    }
    
}
