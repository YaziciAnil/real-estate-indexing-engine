//
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.*;
//
//public class RealEstateSystem {
//    HashMap<String, House> houseList;
//    HashSet<String> retiredIDList;
//    HashMap<String, SearchSession> activeSessionList;
//    TreeSet<House> houseTree;
//    private long globalTimer = 0;
//
//    public RealEstateSystem(){
//        houseList = new HashMap<>();
//        retiredIDList = new HashSet<>();
//        activeSessionList = new HashMap<>();
//        houseTree = new TreeSet<>();
//
//    }
//
//
//    public boolean addHouse(String id, int price, int size){
//        if(houseList.containsKey(id) || retiredIDList.contains(id)) return false;
//        House house = new House(id,price,size);
//        house.setStatus("active");
//        house.setInsertionTime(globalTimer++);
//        houseList.put(id,house);
//        houseTree.add(house);
//        return true;
//    }
//
//    public boolean removeHouse(String id){
//        if(houseList.containsKey(id)){
//            House currentHouse = houseList.get(id);
//            currentHouse.setStatus("removed");
//            houseList.remove(id);
//            retiredIDList.add(id);
//            houseTree.remove(currentHouse);
//            return true;
//        }
//        return false;
//    }
//
//    public boolean updatePrice(String id, int newPrice){
//        if(!houseList.containsKey(id)) return false;
//
//        House h = houseList.get(id);
//
//        houseTree.remove(h);
//        h.setPrice(newPrice);
//        h.increaseVersion();
//        h.setInsertionTime(globalTimer++);
//        houseTree.add(h);
//        return true;
//    }
//
//    public int count(){
//        return houseList.size();
//    }
//
//
//    public SearchSession search(String searchID, int minSize, int maxPrice, int count){
//        if(activeSessionList.containsKey(searchID)) return null;
//        if(houseList.isEmpty()) return null;
//
//        SearchSession session = new SearchSession(searchID);
//
//        ArrayList<House> p1 = new ArrayList<>();
//        ArrayList<House> p2 = new ArrayList<>();
//        ArrayList<House> p3 = new ArrayList<>();
//        ArrayList<House> p4 = new ArrayList<>();
//
//        // price < max and size >= min p1
//        // price < max and size < min p2
//        // price > max and size >= min p3
//        // price > max and size < min = p4
//        for(House h: houseTree){
//            if(h.price <= maxPrice){ // p1 or p2
//                if(h.size >= minSize) { // p1
//                    p1.add(h);
//                    if(p1.size() >= count) break; // if there is enough p1 break
//                }
//                else p2.add(h);
//            }
//            else{
//                if (p1.size() + p2.size() >= count) break; // if enough p1 + p2 break
//                if(h.size >= minSize){
//                    p3.add(h); // p3
//                    if(p3.size() + p2.size() + p1.size() >= count) break; // if enough p1 + p2 + p3 break
//                }
//                else p4.add(h);
//            }
//
//        }
//
//        ArrayList<House> result = new ArrayList<>();
//
//        ArrayList<House> p0 = new ArrayList<>();
//
//        p0.addAll(p1);
//        p0.addAll(p2);
//        p0.addAll(p3);
//        p0.addAll(p4);
//
//        if(count >= p0.size()){
//            result = p0;
//        }
//        else {
//            for(int i = 0; i<count; i++){
//                House currentHouse = p0.get(i);
//                result.add(currentHouse);
//            }
//        }
//
//        for(int i = 0; i<result.size(); i++){
//            House currentHouse = p0.get(i);
//            if(currentHouse.getPrice() <= maxPrice && currentHouse.getSize() >= minSize) session.addPriorityList("P1");
//            if(currentHouse.getPrice() <= maxPrice && currentHouse.getSize() < minSize) session.addPriorityList("P2");
//            if(currentHouse.getPrice() > maxPrice && currentHouse.getSize() >= minSize) session.addPriorityList("P3");
//            if(currentHouse.getPrice() > maxPrice && currentHouse.getSize() < minSize) session.addPriorityList("P4");
//        }
//
//
//
//        ArrayList<Integer> versionList = new ArrayList<>();
//        for(House h: result){
//            versionList.add(h.version);
//        }
//
//        session.setVersions(versionList);
//        session.setHouseList(result);
//
//        activeSessionList.put(searchID,session);
//
//        return session;
//
//    }
//
//
//    public House buy(String searchID, int index){
//
//        if(!activeSessionList.containsKey(searchID)) return null;
//        if(activeSessionList.get(searchID).houseList.size() < index + 1) return null;
//
//        House requestedHouse = activeSessionList.get(searchID).houseList.get(index);
//
//        int oldVersion = activeSessionList.get(searchID).versions.get(index);
//        int currentVersion = requestedHouse.version;
//
//
//        if(oldVersion != currentVersion) return null;
//        if(!requestedHouse.status.equals("active") || !houseList.containsKey(requestedHouse.id)) return null;
//
//
//        houseList.remove(requestedHouse.getId());
//        houseTree.remove(requestedHouse);
//        retiredIDList.add(requestedHouse.getId());
//        requestedHouse.setStatus("sold");
//
//        return requestedHouse;
//    }
//
//    public boolean closeSearch(String searchID){
//        if(activeSessionList.containsKey(searchID)){
//            activeSessionList.remove(searchID);
//            return true;
//        }
//        return false;
//    }
//
//    public long totalPrice(long maxPrice, long minSize){
//        long total = 0;
//        for(House h: houseTree){
//            if(h.price > maxPrice) return total;
//            if(h.size >= minSize) total += h.price;
//        }
//        return total;
//    }
//
//}
//
//
