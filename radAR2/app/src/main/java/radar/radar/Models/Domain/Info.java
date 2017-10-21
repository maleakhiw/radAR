package radar.radar.Models.Domain;

import java.util.ArrayList;

/**
 * Data model for information i.e on tracking group
 */
public class Info {
    private String name;
    private ArrayList<Integer> members;
    private String description;
    private int chatID;

    /**
     * Getter and setter
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Integer> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<Integer> members) {
        this.members = members;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getChatID() {
        return chatID;
    }

    public void setChatID(int chatID) {
        this.chatID = chatID;
    }
}
