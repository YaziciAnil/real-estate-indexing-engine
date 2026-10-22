import java.util.ArrayList;

public class SearchSession {
    String searchID;
    ArrayList<House> houseList = new ArrayList<>();
    ArrayList<Integer> versions = new ArrayList<>();
    ArrayList<String> priorityList = new ArrayList<>();

    public ArrayList<House> getHouseList() {
        return houseList;
    }

    public ArrayList<Integer> getVersions() {
        return versions;
    }

    public String getSearchID() {
        return searchID;
    }

    public ArrayList<String> getPriorityList() {
        return priorityList;
    }

    public void setHouseList(ArrayList<House> houseList) {
        this.houseList = houseList;
    }

    public void setPriorityList(ArrayList<String> priorityList) {
        this.priorityList = priorityList;
    }

    public void setSearchID(String searchID) {
        this.searchID = searchID;
    }

    public void setVersions(ArrayList<Integer> versions) {
        this.versions = versions;
    }

    public void addPriorityList(String p){
        priorityList.add(p);
    }

    public SearchSession(String searchID){
        this.searchID = searchID;
    }

}

