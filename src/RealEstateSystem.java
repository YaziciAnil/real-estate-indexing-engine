import java.util.ArrayList;

/** Core logic: stores houses, search sessions and retired IDs. */
public class RealEstateSystem {
    MyHashMap<House> houseList;
    MyHashMap<Boolean> retiredIDList;
    MyHashMap<SearchSession> activeSessionList;
    MyTree houseTree;
    private long globalTimer = 0; // insertion order counter

    public RealEstateSystem() {
        houseList = new MyHashMap<>();
        retiredIDList = new MyHashMap<>();
        activeSessionList = new MyHashMap<>();
        houseTree = new MyTree();
    }

    /** Adds a new house; fails if the ID is active or retired. */
    public boolean addHouse(String id, int price, int size) {
        if (houseList.containsKey(id) || retiredIDList.containsKey(id)) return false;
        House house = new House(id, price, size);
        house.setStatus("active");
        house.setInsertionTime(globalTimer++);
        houseList.put(id, house);
        houseTree.insert(house);
        return true;
    }

    /** Removes an active house and retires its ID. */
    public boolean removeHouse(String id) {
        if (houseList.containsKey(id)) {
            House currentHouse = houseList.get(id);
            currentHouse.setStatus("removed");
            houseList.remove(id);
            retiredIDList.put(id, true);
            houseTree.delete(currentHouse);
            return true;
        }
        return false;
    }

    /** Changes a house's price; fails if missing or price is unchanged. */
    public boolean updatePrice(String id, int newPrice) {
        if (!houseList.containsKey(id)) return false;

        House h = houseList.get(id);
        if (h.getPrice() == newPrice) return false;

        houseTree.delete(h);
        h.setPrice(newPrice);
        h.increaseVersion();
        h.setInsertionTime(globalTimer++);
        houseTree.insert(h);
        return true;
    }

    /** Returns the number of active houses. */
    public int count() {
        return houseList.size();
    }

    /** Builds a ranked result from P1-P4 pools and stores it as a new session. */
    public SearchSession search(String searchID, int minSize, int maxPrice, int count) {
        if (activeSessionList.containsKey(searchID)) return null;
        if (houseList.isEmpty()) return null;

        SearchSession session = new SearchSession(searchID);

        ArrayList<House> result = new ArrayList<>();
        ArrayList<String> priorityList = new ArrayList<>();

        // 1. P1
        ArrayList<House> p1 = houseTree.findP1(maxPrice, minSize, count);
        for (House h : p1) {
            result.add(h);
            priorityList.add("P1");
        }

        // 2. P2
        int needed = count - result.size();
        if (needed > 0) {
            ArrayList<House> p2 = houseTree.findP2(maxPrice, minSize, needed);
            for (House h : p2) {
                result.add(h);
                priorityList.add("P2");
            }
        }

        // 3. P3
        needed = count - result.size();
        if (needed > 0) {
            ArrayList<House> p3 = houseTree.findP3(maxPrice, minSize, needed);
            for (House h : p3) {
                result.add(h);
                priorityList.add("P3");
            }
        }

        // 4. P4
        needed = count - result.size();
        if (needed > 0) {
            ArrayList<House> p4 = houseTree.findP4(maxPrice, minSize, needed);
            for (House h : p4) {
                result.add(h);
                priorityList.add("P4");
            }
        }

        ArrayList<Integer> versionList = new ArrayList<>(result.size());
        for (House h : result) {
            versionList.add(h.version);
        }

        for (String prio : priorityList) {
            session.addPriorityList(prio);
        }

        session.setVersions(versionList);
        session.setHouseList(result);

        activeSessionList.put(searchID, session);
        return session;
    }

    /** Buys the house at index if it is still active and not updated since the search. */
    public House buy(String searchID, int index) {
        if (!activeSessionList.containsKey(searchID)) return null;
        SearchSession session = activeSessionList.get(searchID);
        if (index < 0 || session.houseList.size() <= index) return null;

        House requestedHouse = session.houseList.get(index);

        int oldVersion = session.versions.get(index);
        int currentVersion = requestedHouse.version;

        if (oldVersion != currentVersion) return null;
        if (!requestedHouse.status.equals("active") || !houseList.containsKey(requestedHouse.id)) return null;

        houseList.remove(requestedHouse.getId());
        houseTree.delete(requestedHouse);
        retiredIDList.put(requestedHouse.getId(), true);
        requestedHouse.setStatus("sold");

        return requestedHouse;
    }

    /** Closes an open search session. */
    public boolean closeSearch(String searchID) {
        if (activeSessionList.containsKey(searchID)) {
            activeSessionList.remove(searchID);
            return true;
        }
        return false;
    }

    /** Returns the total price of matching active houses. */
    public long totalPrice(int maxPrice, int minSize) {
        return houseTree.calculateTotalPrice(maxPrice, minSize);
    }
}