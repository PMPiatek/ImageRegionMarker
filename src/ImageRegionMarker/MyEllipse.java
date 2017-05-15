package ImageRegionMarker;

import javafx.scene.shape.Ellipse;

public class MyEllipse extends Ellipse
{
    private String name;
    private String description;

    public MyEllipse()
    {
        super();
    }

    public MyEllipse(double radiusX, double radiusY)
    {
        super(radiusX, radiusY);
    }

    public MyEllipse(double centerX, double centerY, double radiusX, double radiusY)
    {
        super(centerX, centerY, radiusX, radiusY);
    }

    public MyEllipse(String name)
    {
        super();
        this.name = name;
    }

    public MyEllipse(double radiusX, double radiusY, String name)
    {
        super(radiusX, radiusY);
        this.name = name;
    }

    public MyEllipse(double centerX, double centerY, double radiusX, double radiusY, String name)
    {
        super(centerX, centerY, radiusX, radiusY);
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
