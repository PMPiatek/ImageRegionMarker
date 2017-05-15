package ImageRegionMarker;

import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Math.abs;

public class Controller
{
    private File projectFile;
    private File imageFile;

    @FXML private BorderPane root;
    @FXML private MenuBar menu;
    @FXML private Menu menuFile;
    @FXML private MenuItem menuNew;
    @FXML private MenuItem menuOpen;
    @FXML private MenuItem menuSave;
    @FXML private MenuItem menuSaveAs;
    @FXML private MenuItem menuExport;
    @FXML private MenuItem menuClose;
    @FXML private MenuItem menuQuit;
//    @FXML private Menu menuEdit;
//    @FXML private MenuItem menuDelete;
//    @FXML private MenuItem menuCopy;
//    @FXML private MenuItem menuCut;
//    @FXML private MenuItem menuPaste;
    @FXML private ToggleButton togglePointer;
//    @FXML private ToggleButton toggleHand;
    @FXML private ToggleButton toggleRect;
    @FXML private ToggleButton toggleEllipse;
    @FXML private ToggleButton togglePolygon;
    @FXML private ColorPicker colorPicker;
    @FXML private ScrollPane scrollPane;
    @FXML private Pane mainPane;
    @FXML private ImageView imageView;
    @FXML private Pane shapesPane;
    @FXML private Label labelZoom;
    @FXML private Button buttonLessZoom;
    @FXML private Slider sliderZoom;
    @FXML private Button buttonMoreZoom;
    @FXML private ListView list;

    private enum Mode {pointer, hand, rectangle, ellipse, polygon}
    private Mode activeMode = Mode.pointer;
    private Color activeColor = Color.BLACK;

    private double zoomFactor = 1;

    private Shape newShape;
    private double newShape_startX = 0;
    private double newShape_startY = 0;

    @FXML protected void onMouseClicked_mainPane(Event e)
    {
        MouseEvent event = (MouseEvent) e;

        if(newShape != null && activeMode == Mode.polygon && event.getClickCount() > 1)
        {
            ObservableList<Double> points = ((MyPolygon) newShape).getPoints();
            points.remove(points.size() - 3, points.size() - 1);

            list.refresh();

            newShape = null;
            newShape_startX = 0;
            newShape_startY = 0;
        }
    }

    @FXML protected void onMousePressed_mainPane(Event e)
    {
        MouseEvent event = (MouseEvent) e;

        if(newShape == null)
        {
            newShape = getShape(activeMode, event.getX()/zoomFactor, event.getY()/zoomFactor);
            if (newShape != null)
            {
                newShape_startX = event.getX()/zoomFactor;
                newShape_startY = event.getY()/zoomFactor;

                newShape.setStroke(activeColor);
                newShape.setStrokeWidth(3);
                newShape.setFill(activeColor.deriveColor(0, 1, 1, 0.35));

                shapesPane.getChildren().add(newShape);


                if(activeMode == Mode.polygon)
                {
                    ((MyPolygon) newShape).getPoints().addAll(newShape_startX, newShape_startY);
                }
            }
        }
        else if(activeMode == Mode.polygon)
        {
            ObservableList<Double> points = ((MyPolygon) newShape).getPoints();

            double epsilon = 2;
            double deltaX = abs(points.get(points.size() - 2) - points.get(points.size() - 4));
            double deltaY = abs(points.get(points.size() - 1) - points.get(points.size() - 3));

            if(deltaX > epsilon
                    || deltaY > epsilon)
            {
                points.addAll(event.getX() / zoomFactor, event.getY() / zoomFactor);
            }
        }
    }

    private Shape getShape(Mode mode, double x, double y)
    {
        switch (mode)
        {
            case rectangle:
                return new MyRectangle(x, y, 1, 1, "Rectangle");
            case ellipse:
                return new MyEllipse(x, y, 1, 1, "Ellipse");
            case polygon:
                return new MyPolygon("Polygon", x, y);
        }

        return null;
    }

    @FXML protected void onMouseMoved_mainPane(Event e)
    {
        MouseEvent event = (MouseEvent) e;

        if(newShape != null && activeMode == Mode.polygon)
        {
            ObservableList<Double> points = ((MyPolygon) newShape).getPoints();
            points.set(points.size() - 2, event.getX()/zoomFactor);
            points.set(points.size() - 1, event.getY()/zoomFactor);
        }
    }

    @FXML protected void onMouseDragged_mainPane(Event e)
    {
        MouseEvent event = (MouseEvent) e;

        if(newShape != null)
        {
            if(activeMode == Mode.rectangle || activeMode == Mode.ellipse)
            {
                double width = (event.getX() / zoomFactor - newShape_startX);
                double height = (event.getY() / zoomFactor - newShape_startY);

                resizeShape(newShape, abs(width), abs(height));

                if (width < 0)
                {
                    newShape.setTranslateX(width);
                }

                if (height < 0)
                {
                    newShape.setTranslateY(height);
                }
            }
            else if(activeMode == Mode.polygon)
            {
                ObservableList<Double> points = ((MyPolygon) newShape).getPoints();
                points.set(points.size() - 2, event.getX()/zoomFactor);
                points.set(points.size() - 1, event.getY()/zoomFactor);
            }
        }
    }

    private void resizeShape(Shape shape, double width, double height)
    {
        if (shape instanceof MyRectangle)
        {
            MyRectangle rect = (MyRectangle) shape;
            rect.setWidth(width);
            rect.setHeight(height);
        }
        else if (shape instanceof MyEllipse)
        {
            MyEllipse ellipse = (MyEllipse) shape;
            ellipse.setRadiusX(width/2);
            ellipse.setRadiusY(height/2);
            ellipse.setCenterX(newShape_startX + width/2);
            ellipse.setCenterY(newShape_startY + height/2);
        }
    }

    @FXML protected void onMouseReleased_mainPane(Event e)
    {
        MouseEvent event = (MouseEvent) e;

        if(newShape != null && activeMode == Mode.rectangle || activeMode == Mode.ellipse)
        {
            newShape.relocate(newShape_startX + newShape.getTranslateX(), newShape_startY + newShape.getTranslateY());
            newShape.setTranslateX(0);
            newShape.setTranslateY(0);

            list.refresh();

            newShape = null;
            newShape_startX = 0;
            newShape_startY = 0;
        }
        else if(newShape != null && activeMode == Mode.polygon)
        {
            ObservableList<Double> points = ((MyPolygon) newShape).getPoints();

            double epsilon = 2;
            double deltaX = abs(points.get(points.size() - 2) - points.get(points.size() - 4));
            double deltaY = abs(points.get(points.size() - 1) - points.get(points.size() - 3));

            if(deltaX > epsilon
                    || deltaY > epsilon)
            {
                points.addAll(event.getX() / zoomFactor, event.getY() / zoomFactor);
            }
        }
    }

    @FXML protected void onAction_menuNew(ActionEvent actionEvent)
    {
        sliderZoom.setValue(100);
        labelZoom.setText("100%");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.bmp"));
        imageFile = fileChooser.showOpenDialog(root.getScene().getWindow());
        if(imageFile != null)
        {
            try
            {
                Image image = new Image(imageFile.toURI().toURL().toString());
                imageView.setImage(image);
                imageView.setFitWidth(image.getWidth());
                imageView.setFitHeight(image.getHeight());

                shapesPane.resizeRelocate(imageView.getX(), imageView.getY(), imageView.getFitWidth(), imageView.getFitHeight());
                mainPane.resize(imageView.getFitWidth(), imageView.getFitHeight());

                recenter();
            } catch (MalformedURLException e1)
            {
                e1.printStackTrace();
            }
        }

        list.setItems(shapesPane.getChildren());

        onAction_togglePointer(actionEvent);
        menuSave.setDisable(false);
        menuSaveAs.setDisable(false);
        menuExport.setDisable(false);
        toggleRect.setDisable(false);
        toggleEllipse.setDisable(false);
        togglePolygon.setDisable(false);
        buttonLessZoom.setDisable(false);
        buttonMoreZoom.setDisable(false);
    }

    @FXML protected void onAction_menuOpen(ActionEvent actionEvent)
    {
        onAction_menuClose(actionEvent);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Project File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Project File", "*.xml"));

        projectFile = fileChooser.showOpenDialog(root.getScene().getWindow());

        try
        {
            Scanner s = new Scanner(projectFile).useDelimiter("\\n");
            ArrayList<String> lines = new ArrayList<>();
            while(s.hasNext())
            {
                lines.add(s.next());
            }

            String[] slines = new String[lines.size()];
            slines =  lines.toArray(slines);
            parse(slines);
        } catch (Exception e)
        {
            onAction_menuClose(actionEvent);

            Popup errorPopup = new Popup();

            Button button = new Button("Ok");
            button.setOnAction(event -> button.fireEvent(new Event(WindowEvent.WINDOW_CLOSE_REQUEST)));

            errorPopup.getContent().add(
                    new VBox(
                            new Label("Niepoprawny plik projektu"),
                            button
                    )
            );

            errorPopup.show(root.getScene().getWindow());
        }

        list.setItems(shapesPane.getChildren());

        onAction_togglePointer(actionEvent);
        menuSave.setDisable(false);
        menuSaveAs.setDisable(false);
        menuExport.setDisable(false);
        toggleRect.setDisable(false);
        toggleEllipse.setDisable(false);
        togglePolygon.setDisable(false);
        buttonLessZoom.setDisable(false);
        buttonMoreZoom.setDisable(false);
    }

    private void parse(String[] xmlStringLines) throws Exception
    {
        for (String line : xmlStringLines)
        {
            line = line.replaceAll("[<>/\t]", "");

            if (line.startsWith("Image"))
            {
                String[] tokens = line.split(" ");
                tokens[1] = tokens[1].replace("filePath=", "");
                tokens[1] = tokens[1].replaceAll("\"", "");
                imageFile = new File(tokens[1]);
                imageView.setImage(new Image(imageFile.toURI().toURL().toString()));
            }
            else if(line.startsWith("Shapes")) {}
            else if(line.startsWith("Rectangle"))
            {
                String[] tokens = line.split(" ");
                double x = Double.parseDouble(tokens[2].split("\"")[1]);
                double y = Double.parseDouble(tokens[3].split("\"")[1]);
                double width = Double.parseDouble(tokens[4].split("\"")[1]);
                double height = Double.parseDouble(tokens[5].split("\"")[1]);
                MyRectangle rectangle = new MyRectangle(x, y, width, height);
                rectangle.setStroke(Color.valueOf(tokens[6].split("\"")[1]));
                rectangle.setStrokeWidth(Double.parseDouble(tokens[7].split("\"")[1]));
                rectangle.setFill(Color.valueOf(tokens[8].split("\"")[1]));
                rectangle.setName(tokens[9].split("\"")[1]);

                shapesPane.getChildren().add(rectangle);
            }
            else if(line.startsWith("Ellipse"))
            {
                String[] tokens = line.split(" ");
                double centerX = Double.parseDouble(tokens[2].split("\"")[1]);
                double centerY = Double.parseDouble(tokens[3].split("\"")[1]);
                double radiusX = Double.parseDouble(tokens[4].split("\"")[1]);
                double radiusY = Double.parseDouble(tokens[5].split("\"")[1]);
                MyEllipse ellipse = new MyEllipse(centerX, centerY, radiusX, radiusY);
                ellipse.setStroke(Color.valueOf(tokens[6].split("\"")[1]));
                ellipse.setStrokeWidth(Double.parseDouble(tokens[7].split("\"")[1]));
                ellipse.setFill(Color.valueOf(tokens[8].split("\"")[1]));
                ellipse.setName(tokens[9].split("\"")[1]);

                shapesPane.getChildren().add(ellipse);
            } else if(line.startsWith("Polygon"))
            {
                String[] sPoints = line.split("\\[")[1].split("\\]")[0].split(", ");

                Double[] points = new Double[sPoints.length];
                for(int i = 0; i < sPoints.length; i++)
                {
                    points[i] = Double.parseDouble(sPoints[i]);
                }

                MyPolygon polygon = new MyPolygon();
                polygon.getPoints().addAll(points);

                String[] properties = line.split("\\]")[1].split(" ");

                polygon.setStroke(Color.valueOf(properties[1].split("\"")[1]));
                polygon.setStrokeWidth(Double.parseDouble(properties[2].split("\"")[1]));
                polygon.setFill(Color.valueOf(properties[3].split("\"")[1]));
                polygon.setName(properties[4].split("\"")[1]);

                shapesPane.getChildren().add(polygon);
            }
            else throw new Exception();
        }
    }

    @FXML protected void onAction_menuSave(ActionEvent actionEvent)
    {
        if(projectFile == null)
        {
            onAction_menuSaveAs(actionEvent);
        }
        else
        {
            try
            {
                FileWriter fileWriter = new FileWriter(projectFile, false);
                fileWriter.write(getSaveData());
                fileWriter.flush();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @FXML protected void onAction_menuSaveAs(ActionEvent actionEvent)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Directory");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML File", "*.xml"));

        if(projectFile != null)
        {
            fileChooser.setInitialDirectory(projectFile.getParentFile());
            fileChooser.setInitialFileName(projectFile.getName());
        }

        File selectedFile = fileChooser.showSaveDialog(root.getScene().getWindow());

        if(selectedFile != null)
        {
            projectFile = selectedFile;
            onAction_menuSave(actionEvent);
        }
    }

    private String getSaveData()
    {
        StringBuilder saveData = new StringBuilder();

        saveData.append("<Image filePath=\"");
        saveData.append(imageFile.getPath());
        saveData.append("\"/>\n");

        saveData.append("<Shapes>\n");

        ObservableList<Node> shapes = shapesPane.getChildren();
        for(Node n : shapes)
        {
            saveData.append("\t<");
            saveData.append(getShapeData((Shape) n));
            saveData.append("/>\n");
        }

        saveData.append("</Shapes>\n");

        return saveData.toString();
    }

    private String getShapeData(Shape shape)
    {
        StringBuilder shapeData = new StringBuilder();

        if(shape instanceof MyRectangle)
        {
            MyRectangle rectangle = (MyRectangle) shape;
            shapeData.append("Rectangle ");
            shapeData.append(" x=\"");
            shapeData.append(rectangle.getX());
            shapeData.append("\" y=\"");
            shapeData.append(rectangle.getY());
            shapeData.append("\" width=\"");
            shapeData.append(rectangle.getWidth());
            shapeData.append("\" height=\"");
            shapeData.append(rectangle.getHeight());
            shapeData.append("\" stroke=\"");
            shapeData.append(rectangle.getStroke().toString());
            shapeData.append("\" strokeWidth=\"");
            shapeData.append(rectangle.getStrokeWidth());
            shapeData.append("\" fill=\"");
            shapeData.append(rectangle.getFill().toString());
            shapeData.append("\" name=\"");
            shapeData.append(rectangle.getName());
            shapeData.append("\"");
        }
        else if(shape instanceof MyEllipse)
        {
            MyEllipse ellipse = (MyEllipse) shape;
            shapeData.append("Ellipse ");
            shapeData.append(" centerX=\"");
            shapeData.append(ellipse.getCenterX());
            shapeData.append("\" centerY=\"");
            shapeData.append(ellipse.getCenterY());
            shapeData.append("\" radiusX=\"");
            shapeData.append(ellipse.getRadiusX());
            shapeData.append("\" radiusY=\"");
            shapeData.append(ellipse.getRadiusY());
            shapeData.append("\" stroke=\"");
            shapeData.append(ellipse.getStroke());
            shapeData.append("\" strokeWidth=\"");
            shapeData.append(ellipse.getStrokeWidth());
            shapeData.append("\" fill=\"");
            shapeData.append(ellipse.getFill());
            shapeData.append("\" name=\"");
            shapeData.append(ellipse.getName());
            shapeData.append("\"");
        }
        else
        {
            MyPolygon polygon = (MyPolygon) shape;
            shapeData.append("Polygon ");
            shapeData.append("\" points=\"");
            shapeData.append(polygon.getPoints().toString());
            shapeData.append("\" stroke=\"");
            shapeData.append(polygon.getStroke());
            shapeData.append("\" strokeWidth=\"");
            shapeData.append(polygon.getStrokeWidth());
            shapeData.append("\" fill=\"");
            shapeData.append(polygon.getFill());
            shapeData.append("\" name=\"");
            shapeData.append(polygon.getName());
            shapeData.append("\"");
        }

        return shapeData.toString();
    }

    @FXML protected void onAction_menuExport(ActionEvent actionEvent)
    {
        double activeZoomFactor = zoomFactor;
        zoomFactor = 1;
        zoom();
        Image image = mainPane.snapshot(null, null);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Save Directory");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg")
                );
        fileChooser.setInitialFileName(projectFile.getName());
        fileChooser.setInitialDirectory(projectFile.getParentFile());

        File saveFile = fileChooser.showSaveDialog(root.getScene().getWindow());
        try {
            saveFile.createNewFile();
            BufferedImage bufIm = SwingFXUtils.fromFXImage(image, null);
            BufferedImage bufImRGB = new BufferedImage(bufIm.getWidth(), bufIm.getHeight(), BufferedImage.OPAQUE);
            Graphics2D graphics = bufImRGB.createGraphics();
            graphics.drawImage(bufIm, 0, 0, null);
            ImageIO.write(bufImRGB, "jpg", saveFile);
            graphics.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }

        zoomFactor = activeZoomFactor;
    }

    @FXML protected void onAction_menuClose(ActionEvent actionEvent)
    {
        imageView.setImage(null);
        sliderZoom.setValue(100);
        labelZoom.setText("100%");
        shapesPane.getChildren().clear();
        imageFile = null;
        projectFile = null;

        onAction_togglePointer(actionEvent);
        menuSave.setDisable(true);
        menuSaveAs.setDisable(true);
        menuExport.setDisable(true);
        toggleRect.setDisable(true);
        toggleEllipse.setDisable(true);
        togglePolygon.setDisable(true);
        buttonLessZoom.setDisable(true);
        buttonMoreZoom.setDisable(true);
    }

    @FXML protected void onAction_menuQuit(ActionEvent actionEvent)
    {
        root.fireEvent(new WindowEvent(root.getScene().getWindow() ,WindowEvent.WINDOW_CLOSE_REQUEST));
    }

//    @FXML protected void onAction_menuDelete(ActionEvent actionEvent)
//    {
//    }
//
//    @FXML protected void onAction_menuCopy(ActionEvent actionEvent)
//    {
//    }
//
//    @FXML protected void onAction_menuCut(ActionEvent actionEvent)
//    {
//    }
//
//    @FXML protected void onAction_menuPaste(ActionEvent actionEvent)
//    {
//    }

    @FXML protected void onAction_colorPicker(ActionEvent actionEvent)
    {
        activeColor = colorPicker.getValue();
    }

    @FXML protected void onAction_togglePointer(ActionEvent actionEvent)
    {
        togglePointer.setSelected(true);
        activeMode = Mode.pointer;
    }

//    @FXML protected void onAction_toggleHand(ActionEvent actionEvent)
//    {
//        toggleHand.setSelected(true);
//        activeMode = Mode.hand;
//    }

    @FXML protected void onAction_toggleRect(ActionEvent actionEvent)
    {
        toggleRect.setSelected(true);
        activeMode = Mode.rectangle;
    }

    @FXML protected void onAction_toggleEllipse(ActionEvent actionEvent)
    {
        toggleEllipse.setSelected(true);
        activeMode = Mode.ellipse;
    }

    @FXML protected void onAction_togglePolygon(ActionEvent actionEvent)
    {
        togglePolygon.setSelected(true);
        activeMode = Mode.polygon;
    }

    @FXML protected void onAction_buttonLessZoom(ActionEvent actionEvent)
    {
        sliderZoom.decrement();

        zoom();
    }

    @FXML protected void onAction_buttonMoreZoom(ActionEvent actionEvent)
    {
        sliderZoom.increment();

        zoom();
    }

    private void zoom() {
        labelZoom.setText(sliderZoom.getValue() + "%");
        zoomFactor = sliderZoom.getValue()/100;

        Image image = imageView.getImage();

        mainPane.resize(image.getWidth()*zoomFactor, image.getHeight()*zoomFactor);

        imageView.setFitHeight(image.getHeight()*zoomFactor);
        imageView.setFitWidth(image.getWidth()*zoomFactor);

        shapesPane.getTransforms().set(0, Transform.scale(zoomFactor, zoomFactor));

        recenter();
    }
    private void recenter()
    {
        if(mainPane.getWidth() < scrollPane.getWidth())
        {
            mainPane.setTranslateX(scrollPane.getWidth() / 2 - mainPane.getWidth() / 2);
        }
        else
        {
            mainPane.setTranslateX(0);
        }
        if(mainPane.getHeight() < scrollPane.getHeight())
        {
            mainPane.setTranslateY(scrollPane.getHeight() / 2 - mainPane.getHeight() / 2);
        }
        else
        {
            mainPane.setTranslateY(0);
        }
    }
}
