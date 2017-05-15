package ImageRegionMarker;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class MyRectangle extends Rectangle
{
    private String name;
    private String description;

    public MyRectangle()
    {
        super();
    }

    public MyRectangle(double width, double height)
    {
        super(width, height);
    }

    public MyRectangle(double x, double y, double width, double height)
    {
        super(x, y, width, height);
    }

    public MyRectangle(double width, double height, Paint fill)
    {
        super(width, height, fill);
    }

    public MyRectangle(String name)
    {
        super();
        this.name = name;
    }

    public MyRectangle(double width, double height, String name)
    {
        super(width, height);
        this.name = name;
    }

    public MyRectangle(double x, double y, double width, double height, String name)
    {
        super(x, y, width, height);
        this.name = name;
    }

    public MyRectangle(double width, double height, Paint fill, String name)
    {
        super(width, height, fill);
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setName(String newName)
    {
        name = newName;
    }

    public void setDescription(String newDescription)
    {
        description = newDescription;
    }

}
