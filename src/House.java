/** A house listing, ordered by price asc, size desc, then insertion time asc. */
public class House implements Comparable<House> {
    String id;
    int price;
    int size;
    long insertionTime;
    int version; // incremented on every price update
    String status; // active, sold, removed

    public House(String id, int price, int area) {
        this.id = id;
        this.price = price;
        this.size = area;
    }

    /** Compares by price ascending, size descending, then insertion time ascending. */
    @Override
    public int compareTo(House h) {

        if (this.price != h.price) {
            return Integer.compare(this.price, h.price);
        }

        if (this.size != h.size) {
            return Integer.compare(h.size, this.size);
        }

        return Long.compare(this.insertionTime, h.insertionTime);
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setInsertionTime(long insertionTime) {
        this.insertionTime = insertionTime;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }

    public int getSize() {
        return size;
    }

    public long getInsertionTime() {
        return insertionTime;
    }

    public int getVersion() {
        return version;
    }

    public String getStatus() {
        return status;
    }

    /** Marks the house as updated so stale search results can detect it. */
    public void increaseVersion() {
        version += 1;
    }
}