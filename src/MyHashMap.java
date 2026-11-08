/** Hash map with String keys using separate chaining and prime-sized resizing. */
@SuppressWarnings("unchecked")
public class MyHashMap<T> {

    private static final int[] PRIMES = {37, 79, 163, 331, 673, 1361, 2729, 5471, 10949, 21911, 43853, 87719, 175447, 350899, 701819, 1403641, 2807303
    };

    int capacity = 37;
    int capacityIndex = 0;

    int size = 0;
    double loadFactor = 0.75;
    HashNode<T>[] hashList = new HashNode[capacity];


    /** A single key-value entry in a bucket chain. */
    public static class HashNode<T> {
        String key;
        T value;
        HashNode<T> next;

        public HashNode(String key, T value){
            this.key = key;
            this.value = value;
        }
    }

    /** Grows the table to the next prime capacity and redistributes all entries. */
    private void rehash() {
        if (capacityIndex < PRIMES.length - 1) {
            capacityIndex++;
            capacity = PRIMES[capacityIndex];
        } else {
            capacity = capacity * 2 + 1;
        }

        HashNode<T>[] newHashList = new HashNode[capacity];

        for (HashNode<T> headNode : hashList) {
            HashNode<T> curr = headNode;
            while (curr != null) {
                HashNode<T> nextNode = curr.next;

                int newIndex = getHash(curr.key);


                curr.next = newHashList[newIndex];
                newHashList[newIndex] = curr;

                curr = nextNode;
            }
        }

        hashList = newHashList;
    }





    /** Polynomial string hash mapped to a bucket index. */
    public int getHash(String key){
        if(key == null) return 0;

        int prime = 31;
        int hashVal = 0;

        for(int i = 0; i< key.length(); i++) {
            hashVal = key.charAt(i) + prime * hashVal;
        }

        return (hashVal & 0x7fffffff) % capacity;
    }

    /** Returns true if the key exists in the map. */
    public boolean containsKey(String key){
        int keyHash = getHash(key);
        HashNode<T> h = hashList[keyHash];
        if (h == null) return false;

        while (h != null){
            if (h.key.equals(key)) return true;
            h = h.next;
        }

        return false;
    }

    /** Returns the value for the key, or null if absent. */
    public T get(String key){
        int keyHash = getHash(key);
        HashNode<T> h = hashList[keyHash];

        while(h != null){
            if(h.key.equals(key)) return h.value;
            h = h.next;
        }

        return null;
    }

    /** Inserts the key or overwrites its value if it already exists. */
    public void put(String key, T value){

        int keyHash = getHash(key);
        HashNode<T> node = new HashNode<>(key, value);

        if(hashList[keyHash] == null) {
            hashList[keyHash] = node;
            size += 1;
            if(size >= capacity * loadFactor) rehash();
        }
        else {
            HashNode<T> h = hashList[keyHash];
            while (true){
                if(h.key.equals(key)){
                    h.value = value;
                    break;
                }
                if(h.next == null){
                    h.next = node;
                    size += 1;
                    if(size >= capacity * loadFactor) rehash();
                    break;
                }
                h = h.next;
            }
        }

    }

    /** Removes the key from the map if present. */
    public void remove(String key){
        int keyHash = getHash(key);
        HashNode<T> h = hashList[keyHash];
        if (h == null) return;

        if(h.key.equals(key)){
            hashList[keyHash] = h.next;
            size -= 1;
            return;
        }

        while (h.next != null){
            if(h.next.key.equals(key)){
                h.next = h.next.next;
                size -= 1;
                return;
            }
            h = h.next;
        }
    }

    public int size(){
        return size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }
}