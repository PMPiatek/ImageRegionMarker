package ImageRegionMarker;

import javafx.event.Event;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Shape;
import javafx.stage.Popup;
import javafx.stage.WindowEvent;

import static java.lang.Math.max;

public class CustomCell extends ListCell<Shape>
{
    @Override protected void updateItem(Shape item, boolean empty)
    {
        super.updateItem(item, empty);

        if(item == null || empty)
        {
            setGraphic(null);
            setText(null);
        }
        else
        {
            String name;
            int x;
            int y;
            int width;
            int height;

            if (item instanceof MyRectangle) {
                MyRectangle rectangle = (MyRectangle) item;
                name = rectangle.getName();
                x = (int) rectangle.getX();
                y = (int) rectangle.getY();
                width = max((int) rectangle.getWidth(), 1);
                height = max((int) rectangle.getHeight(), 1);
            } else if (item instanceof MyEllipse) {
                MyEllipse ellipse = (MyEllipse) item;
                name = ellipse.getName();
                x = (int) (ellipse.getCenterX() - ellipse.getRadiusX());
                y = (int) (ellipse.getCenterY() - ellipse.getRadiusY());
                width = max((int) (ellipse.getRadiusX() * 2), 1);
                height = max((int) (ellipse.getRadiusY() * 2), 1);
            } else {
                MyPolygon polygon = (MyPolygon) item;
                name = polygon.getName();
                x = (int) polygon.getLayoutBounds().getMinX();
                y = (int) polygon.getLayoutBounds().getMinY();
                width = max((int) polygon.getLayoutBounds().getWidth(), 1);
                height = max((int) polygon.getLayoutBounds().getHeight(), 1);
            }

            ImageView imageView = (ImageView) getParent().getScene().lookup("#imageView");
            Image image = imageView.getImage();

            WritableImage snapshotBackground = new WritableImage(image.getPixelReader(), x, y, width, height);

            SnapshotParameters params = new SnapshotParameters();
            params.setFill(new ImagePattern(snapshotBackground));

            WritableImage snapshot = item.snapshot(params, null);

            ImageView graphic = new ImageView(snapshot);
            graphic.setFitHeight(80);
            graphic.setFitWidth(80);
            graphic.setPreserveRatio(true);

            MenuItem delete = new MenuItem("Delete");
            delete.setOnAction(event -> ((Pane) getScene().lookup("#shapesPane")).getChildren().remove(item));


            MenuItem rename = new MenuItem("Rename");
            rename.setOnAction(event ->
            {
                Popup popup = new Popup();
                TextField textField = new TextField(getText());
                popup.getContent().add(textField);
                textField.setOnKeyPressed((KeyEvent event1) ->
                {
                    if(event1.getCode() == KeyCode.ENTER)
                    {
                        setText(((TextField) popup.getContent().get(0)).getText());
                        if(item instanceof  MyRectangle)
                        {
                            ((MyRectangle) item).setName(((TextField) popup.getContent().get(0)).getText());
                        }
                        else if(item instanceof MyEllipse)
                        {
                            ((MyEllipse) item).setName(((TextField) popup.getContent().get(0)).getText());
                        }
                        else
                        {
                            ((MyPolygon) item).setName(((TextField) popup.getContent().get(0)).getText());
                        }
                        textField.fireEvent(new Event(WindowEvent.WINDOW_CLOSE_REQUEST));
                    }
                    else if(event1.getCode() == KeyCode.ESCAPE)
                    {
                        textField.fireEvent(new Event(WindowEvent.WINDOW_CLOSE_REQUEST));
                    }

                });
                popup.show(getScene().getWindow());
            });

            setContextMenu(new ContextMenu(delete, rename));

            setGraphic(graphic);

            setText(name);
        }
    }
}
