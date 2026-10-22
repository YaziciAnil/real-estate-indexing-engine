import java.util.ArrayList;

public class RealEstateSystem {
    MyHashMap<House> houseList;
    MyHashMap<Boolean> retiredIDList; // HashSet yerine MyHashMap<Boolean>
    MyHashMap<SearchSession> activeSessionList;
    MyTree houseTree;
    private long globalTimer = 0;

    public RealEstateSystem() {
        houseList = new MyHashMap<>();
        retiredIDList = new MyHashMap<>();
        activeSessionList = new MyHashMap<>();
        houseTree = new MyTree();
    }

    public boolean addHouse(String id, int price, int size) {
        if (houseList.containsKey(id) || retiredIDList.containsKey(id)) return false;
        House house = new House(id, price, size);
        house.setStatus("active");
        house.setInsertionTime(globalTimer++);
        houseList.put(id, house);
        houseTree.insert(house);
        return true;
    }

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

    public boolean updatePrice(String id, int newPrice) {
        if (!houseList.containsKey(id)) return false;

        House h = houseList.get(id);

        houseTree.delete(h);
        h.setPrice(newPrice);
        h.increaseVersion();
        h.setInsertionTime(globalTimer++);
        houseTree.insert(h);
        return true;
    }

    public int count() {
        return houseList.size();
    }

    public SearchSession search(String searchID, int minSize, int maxPrice, int count) {
        if (activeSessionList.containsKey(searchID)) return null;
        if (houseList.isEmpty()) return null;

        SearchSession session = new SearchSession(searchID);

        ArrayList<House> result = new ArrayList<>();
        ArrayList<String> priorityList = new ArrayList<>();

        // 1. P1 Havuzu
        ArrayList<House> p1 = houseTree.findP1(maxPrice, minSize, count);
        for (House h : p1) {
            result.add(h);
            priorityList.add("P1");
        }

        // 2. P2 Havuzu
        int needed = count - result.size();
        if (needed > 0) {
            ArrayList<House> p2 = houseTree.findP2(maxPrice, minSize, needed);
            for (House h : p2) {
                result.add(h);
                priorityList.add("P2");
            }
        }

        // 3. P3 Havuzu
        needed = count - result.size();
        if (needed > 0) {
            ArrayList<House> p3 = houseTree.findP3(maxPrice, minSize, needed);
            for (House h : p3) {
                result.add(h);
                priorityList.add("P3");
            }
        }

        // 4. P4 Havuzu
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

    public House buy(String searchID, int index) {
        if (!activeSessionList.containsKey(searchID)) return null;
        SearchSession session = activeSessionList.get(searchID);
        if (session.houseList.size() <= index) return null;

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

    public boolean closeSearch(String searchID) {
        if (activeSessionList.containsKey(searchID)) {
            activeSessionList.remove(searchID);
            return true;
        }
        return false;
    }

    public long totalPrice(int maxPrice, int minSize) {
        return houseTree.calculateTotalPrice(maxPrice, minSize);
    }
}
