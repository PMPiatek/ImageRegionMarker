package ImageRegionMarker;

import javafx.scene.shape.Polygon;

public class MyPolygon extends Polygon
{
    private String name;
    private String description;

    public MyPolygon()
    {
        super();
    }

    public MyPolygon(double... points)
    {
        super(points);
    }

    public MyPolygon(String name)
    {
        super();
        this.name = name;
    }

    public MyPolygon(String name, double... points)
    {
        super(points);
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
