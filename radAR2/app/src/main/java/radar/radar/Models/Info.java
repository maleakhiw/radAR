package radar.radar.Models;

import java.util.ArrayList;

/**
 * Created by kenneth on 6/9/17.
 */

public class Info {
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

    private String name;
    private ArrayList<Integer> members;
    private String description;
    private int chatID;
}
