package ImageRegionMarker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));

        ColorPicker picker = (ColorPicker) root.lookup("#colorPicker");
        picker.setValue(new Color(0, 0, 0, 1));

        primaryStage.setTitle("Lista 2");
        primaryStage.setScene(new Scene(root, 820, 640));
        primaryStage.setMinHeight(640);
        primaryStage.setMinWidth(820);
        primaryStage.setMaximized(true);

        primaryStage.show();

        Pane shapesPane = (Pane) root.lookup("#shapesPane");
        shapesPane.getTransforms().add(Transform.scale(1, 1));

        ListView list = (ListView) root.lookup("#list");
        list.setCellFactory(param -> new CustomCell());
    }


    public static void main(String[] args)
    {
        launch(args);
    }
}
