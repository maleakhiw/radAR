package Model;

/**
 * Created by keyst on 9/09/2017.
 */

/** Class that are used for each item on the List View */

/** This class is for data model class */
public class ListItem {
    private String name;
    private String description;

    public ListItem(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
